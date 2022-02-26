package org.zebias.lsautoconfig.main;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.zebias.lsautoconfig.model.ResultInfo;
import org.zebias.lsautoconfig.model.TableColumnModel;
import org.zebias.lsautoconfig.service.App;
import org.zebias.lsautoconfig.utils.IPUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(value = "MainController2")
public class MainController {

    public static final String WIFI_CONFIG_URL = "";
    public static final String WIFI_PREFIX = "";
    public static final String WIFI_SSID = "";
    public static final String WIFI_DEFAULT_PASSWORD = "";
    public static final String WIFI_DEFAULT_MANAGER_PASSWORD = "";
    public static final String DEFUALT_IF = "";

    private int status = 0;

    public void start(TableColumnModel cm) {
        cm.setProgress(0);
        // 判断网络状态
        boolean isPing = true;
        cm.setProgress(cm.getProgress()+0.1);
        while (isPing) {
            boolean state = !IPUtil.ping("10.168.1.1", 2, 1000);
            log.info("ping ip 10.168.1.1 state is {}", !state);
            isPing = state;
        }

        // 判断端口是否正常
        boolean isPortOpen = true;
        log.info("测试网络端口80是否正常");
        while (isPortOpen) {
//            isPortOpen = !IPUtil.isSuccess("10.168.1.1", 80);
            isPortOpen = !IPUtil.checkUrl(WIFI_CONFIG_URL);
            log.info("测试网络端口通讯：{}", !isPortOpen);
        }
        cm.setProgress(cm.getProgress()+0.2);
        // 设置驱动参数
        System.setProperty("webdriver.edge.driver", App.getInstance().getBasePath()+"edgedriver_win64\\msedgedriver.exe");
        EdgeDriver edge = new EdgeDriver(new EdgeOptions()); // 实例化edge驱动
        System.out.println("edge drvier 初始化成功");
        //
        cm.setProgress(cm.getProgress()+0.1);
//        edge.manage().window().setSize(new Dimension(600, 400));
//        edge.get("http://10.168.1.1/cgi-bin/orayboxmgr/admin/system/info"); // 访问网站
        edge.get(WIFI_CONFIG_URL);
        waitTimeout(edge, 500);
        // 显示等待
        Integer isInit = new WebDriverWait(edge, 20).until(driver -> isInitDevice(edge));
        // 判断是否是初始化设备
//        boolean isInit = isInitDevice(edge);
        if (isInit==0) {
            // 直接配置wifi账号密码
            log.info("配置wifi名称和密码");
            WebElement ssidEle = edge.findElementByName("ssid");
            ssidEle.clear();
//            ssidEle.sendKeys(WIFI_PREFIX+cm.getId());
            ssidEle.sendKeys(WIFI_SSID);
            WebElement pwdEle = edge.findElement(By.name("wifi-pwd"));
            pwdEle.clear();
            pwdEle.sendKeys(WIFI_DEFAULT_PASSWORD);
            edge.findElement(By.xpath("//button[contains(.,'下一步')]")).click();
            waitTimeout(edge, 500);
            // 配置管理密码
            WebElement pwdManagerEle = new WebDriverWait(edge, 10).until(ExpectedConditions.visibilityOfElementLocated(By.name("pwd")));
            pwdManagerEle.clear();
            pwdManagerEle.sendKeys(WIFI_DEFAULT_MANAGER_PASSWORD);
            WebElement configCompile = new WebDriverWait(edge, 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(.,'配置完成')]")));
//            edge.findElement(By.xpath("//button[contains(.,'配置完成')]")).click();
            configCompile.click();
            waitTimeout(edge, 500);
            // 获取元素
            WebElement systemInfo = null;
            try {
                systemInfo = new WebDriverWait(edge, 50).until(ExpectedConditions.visibilityOfElementLocated(By.linkText("系统信息")));
            } catch (Exception e) {
                log.error("超时仍未完成配置");
                return;
            }
            systemInfo.click();
            status = 1;
        }
        // 不是初始化
        if(isInit==1 && status==0){
            login(edge, WIFI_DEFAULT_MANAGER_PASSWORD);
        }
        waitTimeout(edge,500);
        List<String> info = getInfo(edge);
        // import config file
        //mainController.importRestoreFile(edge, BASE_PATH+"backupFile\\system_config_020004753404_2022-02-19.backup");
        // wifi config
        cm.setProgress(cm.getProgress()+0.2);
        if(isInit == 1){
//            wifiConfig(edge, WIFI_PREFIX+cm.getId(), WIFI_DEFAULT_PASSWORD);
            wifiConfig(edge, WIFI_SSID, WIFI_DEFAULT_PASSWORD);
            waitTimeout(edge,500);
            log.info("wifi config success");
        }
        // info信息处理
        if (info.size() > 1) {
            log.info("获取到多条数据，暂时不处理 | {}", info);
        }
        ResultInfo resultInfo = getResultInfo(cm, info);
        ArrayList<ResultInfo> rows = CollUtil.newArrayList(resultInfo);
        ExcelWriter excelWriter = ExcelUtil.getWriter(App.getInstance().getBasePath()+"tables\\02.xlsx");
        log.info("读取已经完成列表");
        excelWriter.setCurrentRowToEnd();
        excelWriter.write(rows);
        excelWriter.close();
        edge.quit();
        cm.setProgress(cm.getProgress()+0.3);

    }

    /**
     * 获取返回值信息
     * @param cm
     * @param info
     * @return
     */
    private ResultInfo getResultInfo(TableColumnModel cm, List<String> info) {
        String baseInfo = info.get(0);
        String[] split = baseInfo.split("\\n");
        log.info("获取到的基本信息：{}", split);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setId(cm.getId());
        resultInfo.setStoreName(cm.getName());
        resultInfo.setIccid(cm.getIccid());
        // 判断是否传值
        String orayMac = "";
        if (StringUtils.isEmpty(cm.getMac())) {
            // 自己获取arp -a命令匹配的mac地址，地址前缀：a0-c5-f2
            orayMac = IPUtil.getMacInWindows(DEFUALT_IF);
            log.info("自动获取mac地址，mac地址前缀：a0-c5-f2， 获取到真实mac地址是:{}",orayMac );
        }else {
            orayMac = cm.getMac();
        }
        resultInfo.setMac(orayMac);
        resultInfo.setOperator(cm.getOperator());
        resultInfo.setStoreName(cm.getName());
        for (int i = 0; i < split.length; i++) {
            if(split[i].equals("SN码：")){
                resultInfo.setSn(split[i+1]);
            }else if(split[i].equals("设备型号：")){
                resultInfo.setModel(split[i+1].split(" ")[0]);
            }else if(split[i].equals("系统时间：")){
                resultInfo.setSystemTime(split[i+1].split(" ")[0]);
            }else if(split[i].equals("系统时区：")){
                resultInfo.setTimeZone(split[i+1].split(" ")[0]);
            }else if(split[i].equals("IMEI码：")){
                resultInfo.setImei(split[i+1]);
            }else if(split[i].equals("工作模式：")){
                resultInfo.setType(split[i+1].split(" ")[0]);
            }else if(split[i].equals("固件版本：")){
                resultInfo.setVersion(split[i+1]);
            }else{
                log.info("未知信息：{}", split[i]);
            }
        }
        log.info("配置成功导出信息内容： {}", resultInfo);
        return resultInfo;
    }

    private int isInitDevice(EdgeDriver driver) {
        WebElement ssid = null;
        try {
            ssid = driver.findElement(By.name("ssid"));
        } catch (Exception e) {
            log.error("ssid元素未找到",e);
            return isInitDevice2(driver);
        }
        log.info("初始化配置设备");
        return 0;
    }

    private int isInitDevice2(EdgeDriver driver) {
        WebElement ssid = null;
        try {
            ssid = driver.findElement(By.id("password"));
        } catch (Exception e) {
            log.error("password元素未找到",e);
            isInitDevice(driver);
        }
        log.info("后期配置设备");
        return 1;
    }

    /**
     * 等待超时
     * Establish Waiting Strategy
     * @param driver
     * @param timeout
     *
     * Synchronizing the code with the current state of the browser is one of the biggest challenges with Selenium, and doing it well is an advanced topic.
     * Essentially you want to make sure that the element is on the page before you attempt to locate it and the element is in an interactable state before you attempt to interact with it.
     * An implicit wait is rarely the best solution, but it’s the easiest to demonstrate here, so we’ll use it as a placeholder.
     */
    private void waitTimeout(EdgeDriver driver,int timeout) {
        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
    }


    /**
     * 查找元素
     * @param driver
     * @param element
     * @param flag
     * @example WebElement searchBox = driver.findElement(By.name("q"));
     *          WebElement searchButton = driver.findElement(By.name("btnK"));
     */
    private void findElement(EdgeDriver driver, String element, String flag) {
        System.out.println("查找元素["+element+"]");
        driver.findElement(By.id(element));
    }

    /**
     * 用户登录
     * @param driver
     * @param password
     */
    private void login(EdgeDriver driver, String password){
        driver.findElement(By.id("password")).sendKeys(StringUtils.isEmpty(password) ? "admin" : password);
        driver.findElement(By.id("login-btn")).click();
        // //span[contains(.,'密码错误，请重新输入...')]
        // driver.findElement(By.id("password")).sendKeys(Keys.ENTER); 回车键登录
    }

    public void importRestoreFile(EdgeDriver driver, String filePath){

        System.out.println("import restore file ["+filePath+"]");
        // 确保点击到 系统信息
//        driver.findElement(By.linkText("系统信息")).click();
        // 确保点击到 路由器信息
//        driver.findElement(By.cssSelector("li:nth-child(1) img")).click();
        // 导入且恢复
        driver.findElement(By.linkText("导入且恢复")).click();
        // 选择文件
//        driver.findElement(By.cssSelector(".file-sel")).click();
//        driver.findElementByCssSelector("input[type=\"file\"]").sendKeys(filePath);
        driver.findElement(By.id("backup-file")).sendKeys(filePath);
        waitTimeout(driver,5000);
        // 开始导入
        driver.findElement(By.linkText("开始导入")).click();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取基础信息
     * @param driver
     */
    private List<String> getInfo(EdgeDriver driver){
        driver.findElement(By.cssSelector(".eye-off")).click();
        List<WebElement> elements = driver.findElements(By.id("info-container"));
        List<String> list = new ArrayList<>();
        for (WebElement e : elements) {
            System.out.println(e.getText());
            list.add(e.getText());
        }
        return list;
    }

    private void wifiConfig(EdgeDriver driver, String ssid, String password){
        if (StringUtils.isEmpty(ssid) || StringUtils.isEmpty(password)) {
            log.warn("wifi config ssid or password is empty");
            return;
        }
        driver.findElement(By.linkText("网络设置")).click();
        driver.findElement(By.cssSelector("li:nth-child(3) p")).click();
        driver.findElement(By.linkText("修改")).click();
        waitTimeout(driver,5000);
        driver.findElement(By.xpath("//form[@id='wifi-form']/div[2]/div[3]/div[3]/div/label/span")).click();
        WebElement ssidEle = driver.findElementByName("ssid");
        ssidEle.clear();
        ssidEle.sendKeys(ssid);
        WebElement pwdEle = driver.findElement(By.id("wifi-pwd"));
        pwdEle.clear();
        pwdEle.sendKeys(password);
        // save
//        driver.findElement(By.linkText("保存")).click();
        WebElement save = new WebDriverWait(driver, 50).until(ExpectedConditions.visibilityOfElementLocated(By.linkText("保存")));
        save.click();
        try {
            Sleeper.SYSTEM_SLEEPER.sleep(Duration.ofSeconds(5));
        } catch (InterruptedException e) {
            log.error("sleep error", e);
        }
        waitTimeout(driver,1000);
    }
}

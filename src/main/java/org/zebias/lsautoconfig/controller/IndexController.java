package org.zebias.lsautoconfig.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zebias.lsautoconfig.constact.Event;
import org.zebias.lsautoconfig.main.MainController;
import org.zebias.lsautoconfig.model.IndexModel;
import org.zebias.lsautoconfig.model.Store;
import org.zebias.lsautoconfig.model.TableColumnModel;
import org.zebias.lsautoconfig.model.TableModel;
import org.zebias.lsautoconfig.service.App;
import org.zebias.lsautoconfig.service.TableService;
import org.zebias.lsautoconfig.utils.TableUtils;
import org.zebias.lsautoconfig.views.CustomDialogView;
import org.zebias.lsautoconfig.views.LogView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 主页控制器
 */
@FXMLController
@Slf4j
@Data
public class IndexController implements Initializable {

    @FXML
    private Button startBtn;
    @FXML
    private ProgressBar pBar;
    @FXML
    private Label pBarText;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem exitBtn;
    @FXML
    private MenuItem logInfo;
    @FXML
    private TextField nameInput;

    private String operatorName = null;

    @Autowired
    private MainController mMainContoller;

    @Autowired
    private CustomDialogView customDialogView;

    @Autowired
    private LogView logView;

    private IndexModel indexModel = new IndexModel();
    @FXML
    private TableView<TableColumnModel> tView;

    private TableModel tableModel = new TableModel();

    private TableService tableService = new TableService(tableModel);

    private List<Store> list2;

    /**
     * 点击按钮 【确认】
     * @param event
     */
    @FXML
    public void startEvent(ActionEvent event){
        log.info("startEvent");
        log.info("event: {}", event);
        // 默认选中第一行 或者 在未完成列表中第一行
        if(startBtn.getText().equals("确认")){
            nameInput.setText(nameInput.getText().trim());
            operatorName = nameInput.getText().trim();
            if(operatorName.equals("")){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("操作员用户名不能为空");
                alert.setContentText("请输入操作员姓名");
                alert.showAndWait();
            }else{
                nameInput.setDisable(true);
                startBtn.setText("取消");
                tView.getColumns().removeAll(tView.getColumns()); // 清空表格
                tableModel.getTableList().clear();
                init();
            }
        }else{
            nameInput.setDisable(false);
            nameInput.setText(nameInput.getText().trim());
            operatorName = "";
            startBtn.setText("确认");
        }
    }

    @FXML
    public void configPath(ActionEvent event){
        log.info("configPath");
        customDialogView.start(new Stage());
    }

    @FXML
    public void logEvent(ActionEvent event){
        log.info("logEvent");
        logView.start(new Stage());
    }

    @FXML
    public void exitAction(ActionEvent event){
        log.info("exitAction");
        App.getInstance().getStage().close();
        System.exit(0);
    }

    /**
     * 初始化
     */
    public void init() {
        log.info("清空原始表格开始");
        tView.getColumns().removeAll(tView.getColumns()); // 清空表格
        tableModel.getTableList().clear();
        log.info("清空原始表格结束");
        // 属性双向绑定
        pBarText.textProperty().bindBidirectional(indexModel.titleProperty());
        getCompileExcel();
        buildTableColumn();
        ObservableList<TableColumnModel> tableList = tableModel.getTableList();
        tView.setItems(tableList);
        showList();
        App.getInstance().setTotalCount(tableList.size());
        App.getInstance().setCompileCount(list2.size());
        pBar.setProgress((float)list2.size() / tableList.size());
        indexModel.setTitle("总数量:"+App.getInstance().getTotalCount() + " | 当前已完成: " + App.getInstance().getCompileCount());
        nameInput.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * JavaFX 初始化
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("IndexController initialize");
        log.info("location: {}", location);
        log.info("resources: {}", resources);
        init();
    }

    /**
     * tableView数据刷新
     */
    private void showList(){
        log.info("刷新tableView数据");
        if (operatorName == null) {
            tableService.loadTableList();
            return;
        }

        // 通过操作员名称ALL查询所有数据
        if("ALL".equals(operatorName)){
            tableService.loadTableList();
        }else {
            tableService.loadTableList(operatorName);
        }
    }

    /**
     * 初始化表格
     */
    private void buildTableColumn() {
        TableColumn<TableColumnModel, Integer> id = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumn("店铺号", "id", 80);
        TableColumn<TableColumnModel, String> title = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumn("店铺信息", "name", 100);
        //TableColumn<TableColumnModel, String> address = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumn("地址", "name", 100);
        TableColumn<TableColumnModel, String> type = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumnByType("方案类型", "type", 160);
        TableColumn<TableColumnModel, String> option = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumn("工程师", "operator", 100);
        TableColumn<TableColumnModel, String> iccid = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumnByIccid("ICCID", "iccid", 150);
        TableColumn<TableColumnModel, String> mac = org.zebias.lsautoconfig.service.TableColumnBuilder.textColumnByMac("Mac", "mac", 120);
        TableColumn<TableColumnModel, Double> progress = org.zebias.lsautoconfig.service.TableColumnBuilder.progressColumn("进度", "progress", 140);
        TableColumn<TableColumnModel, Integer> operator = org.zebias.lsautoconfig.service.TableColumnBuilder.operatorColumn("操作", "id", 80,this::operatorConsumer,list2);
        // 添加列
        editIccId(iccid);
        editMac(mac);
        tView.setEditable(true);
        tView.getColumns().addAll(id, title,iccid,mac,type,option, progress, operator);
        TableUtils.installCopyPasteHandler(tView); // 支持复制和粘贴
        // 获取到用户编辑的值
    }

    private void editMac(TableColumn<TableColumnModel, String> mac) {
        mac.setOnEditCommit(event -> {
            log.info("获取到用户编辑的mac值是：{}", event.getNewValue());
            int row = event.getTablePosition().getRow(); // 获取当前所在行
            // 获取到用户当前选择行
            setTextToExcel(event.getNewValue(), row+2,"F");
            // 配置好后，页面列表中添加值
            tableModel.getTableList().get(row).setMac(event.getNewValue());
        });
    }

    private void editIccId(TableColumn<TableColumnModel, String> iccid) {
        iccid.setOnEditCommit(event -> {
            log.info("获取到用户编辑的iccid值是：{}", event.getNewValue());
            int row = event.getTablePosition().getRow(); // 获取当前所在行
            // 获取到用户当前选择行
            setTextToExcel(event.getNewValue(), row+2,"E");
            // 配置好后，页面列表中添加值
            tableModel.getTableList().get(row).setIccid(event.getNewValue());
        });
    }

    // BiCousmer函数时接口
    private void operatorConsumer(Event event, Integer value) {
        switch (event) {
            case START:
                log.info("当前配置店铺号: {}", value);
                tableService.executeStartWork(value);
                // 获取当前选中行
                TableView.TableViewSelectionModel<TableColumnModel> selectionModel = tView.getSelectionModel();
                int rowByStoreId = getRowByStoreId(value);
                if (rowByStoreId >= 0) {
                    log.info("当前行数据: {}",tableModel.getTableList().get(rowByStoreId));
                    selectionModel.select(rowByStoreId); // 默认选中

                    log.info("获取到的数据是: {}",selectionModel.getSelectedItem().toString());
                    // 判断iccid是否是空值
                    if (StringUtils.isEmpty(selectionModel.getSelectedItem().getIccid())) {
                        showAlert( "配置ICCID","请先配置iccid");
                    }
                }
                break;
            case STOP:
                tableService.executeStopWork(value);
                break;
            default:
                log.warn("operatorConsumer default");
        }
    }

    /**
     * 获取当前已完成的数据
     */
    private void getCompileExcel(){
        list2 = ExcelUtil.getReader(App.getInstance().getBasePath()+"tables\\02.xlsx").readAll(Store.class);
    }

    /**
     * 显示对话框
     * @param title
     * @param content
     */
    public static void showAlert(String title, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void setTextToExcel(String text, int row,String cell){
        ExcelWriter excelWriter = ExcelUtil.getWriter(App.getInstance().getBasePath()+"tables\\342.xlsx");
        log.info("读取已经完成列表");
        excelWriter.setCurrentRow(row);
        excelWriter.writeCellValue(cell+row, text);
        log.info("通过页面配置可编辑参数并且存储到基础表格中,参数值:{}, 配置行数为:E{}", text, row);
        excelWriter.close();
    }

    /**
     * 获取索引
     */
    public int getRowByStoreId(int id){
        ObservableList<TableColumnModel> tableList = tableModel.getTableList();
        for (int i = 0; i <tableList.size() ; i++) {
            if (tableList.get(i).getId() == id) {
                return i;
            }
        }
        return  -1;
    }
}



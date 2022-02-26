package org.zebias.lsautoconfig.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class IPUtil {
    /**
     * IP地址以及端口是否正常
     *
     * @return
     */
    public static boolean isSuccess(String ip, int port) {
        int length = ip.toCharArray().length;
        Socket connect = new Socket();
        try {
            connect.connect(new InetSocketAddress(ip, port), 100);//建立连接
            boolean res = connect.isConnected();//通过现有方法查看连通状态
            return res;//true为连通
        } catch (IOException e) {
            return false;//当连不通时，直接抛异常，异常捕获即可
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * IP地址以及端口是否正常
     *
     * @return
     */
    public static boolean isYituSuccess(String ip, int port) {
        int length = ip.toCharArray().length;
        String substring = ip.substring(8, length);
        Socket connect = new Socket();
        try {
            connect.connect(new InetSocketAddress(substring, port), 100);//建立连接
            boolean res = connect.isConnected();//通过现有方法查看连通状态
            return res;//true为连通
        } catch (IOException e) {
            return false;//当连不通时，直接抛异常，异常捕获即可
        } finally {
            try {
                connect.close();
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * @param ipAddress  ip地址
     * @param pingTimes  次数(一次ping,对方返回的ping的结果的次数)
     * @param timeOut    超时时间 单位ms(ping不通,设置的此次ping结束时间)
     * @return
     */
    public static boolean ping(String ipAddress, int pingTimes, int timeOut) {
        BufferedReader in = null;
        String pingCommand = null;
        Runtime r = Runtime.getRuntime();
        String osName = System.getProperty("os.name");
        log.info("项目所在系统是:" +osName);
        if(osName.contains("Windows")){
            //将要执行的ping命令,此命令是windows格式的命令
            pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;
        }else{
            //将要执行的ping命令,此命令是Linux格式的命令
            //-c:次数,-w:超时时间(单位/ms)  ping -c 10 -w 0.5 192.168.120.206
            pingCommand = "ping " + " -c " + "4" + " -w " + "2 " + ipAddress;
        }
        try {
            //执行命令并获取输出
            Process p = r.exec(pingCommand);
            if (p == null) {
                return false;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line,osName);
            }
            //如果出现类似=23 ms ttl=64(TTL=64 Windows)这样的字样,出现的次数=测试次数则返回真
            //return connectedCount == pingTimes;
            log.info("ping通设备IP的次数为:" +connectedCount);
            return connectedCount >= 2 ? true : false;
        } catch (Exception ex) {
            ex.printStackTrace(); //出现异常则返回假
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //若line含有=18 ms ttl=64字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line,String osName) {
        if(osName.contains("Windows")){
            if(line.contains("TTL=")){
                return 1;
            }
        }else{
            if(line.contains("ttl=")){
                return 1;
            }
        }
        return 0;
    }

    public static boolean checkUrl(String url_s) {
        try {
            URL url = new URL(url_s);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
/**

 * public int getResponseCode()throws IOException 从 HTTP 响应消息获取状态码。

 * 例如，就以下状态行来说： HTTP/1.0 200 OK HTTP/1.0 401 Unauthorized 将分别返回 200

 * 和 401。 如果无法从响应中识别任何代码(即响应不是有效的 HTTP)，则返回 -1。

 *

 * 返回 HTTP 状态码或 -1

 */
            int state = conn.getResponseCode();
            System.out.println(state);
            if (state == 200) {
                return true;
            } else {
                System.out.println("invalid: " + url_s);
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static String getMacInWindows(final String ip) {

        String result = "";

        String[] cmd = {

                "cmd",

                "/c",

                "ping " + ip

        };

        String[] another = {

                "cmd",

                "/c",

                "arp -a"

        };

        String cmdResult = callCmd(cmd, another);

        result = filterMacAddress(ip, cmdResult, "-");

        return result;

    }

    /**
     *
     *
     *
     * @param ip
     *            目标ip,一般在局域网内
     *
     * @param sourceString
     *            命令处理的结果字符串
     *
     * @param macSeparator
     *            mac分隔符号
     *
     * @return mac地址，用上面的分隔符号表示
     */

    public static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {

        String result = "";

        String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";

        Pattern pattern = Pattern.compile(regExp);

        Matcher matcher = pattern.matcher(sourceString);

        while (matcher.find()) {

            result = matcher.group(1);

            // 针对于蒲公英
            if(result.contains("a0-c5-f2") || result.contains("fc-83-c6")){
                break;
            }

//            if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
//
//                break; // 如果有多个IP,只匹配本IP对应的Mac.
//
//            }

        }

        return result;

    }


    /**
     * 执行cmd命令
     *
     * @param cmd
     *            第一个命令
     * @param another
     *            第二个命令
     * @return
     *
     */
    public static String callCmd(String[] cmd, String[] another) {

        String result = "";

        String line = "";

        try {

            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(cmd);

            //proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令

            proc = rt.exec(another);

            InputStreamReader is = new InputStreamReader(proc.getInputStream());

            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null) {

                result += line;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }


    public static String callCmd(String[] cmd) {

        String result = "";

        String line = "";

        try {

            Process proc = Runtime.getRuntime().exec(cmd);

            InputStreamReader is = new InputStreamReader(proc.getInputStream());

            BufferedReader br = new BufferedReader(is);

            while ((line = br.readLine()) != null) {

                result += line;

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return result;

    }

    public static void main(String[] args) {
        String macInWindows = getMacInWindows("10.168.1.1");
        System.out.println(macInWindows);
    }


}

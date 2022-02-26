package org.zebias.lsautoconfig.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultInfo {

    private int id; //店铺号
    private String storeName; //店铺名称
    private String sn; //序列号
    private String imei; //IMEI
    private String iccid; //ICCID
    private String mac;
    private String operator; //操作员
    private String type; //模式
    private String model; // 型号
    private String version; //版本
    private String systemTime; //当前时间
    private String timeZone; // 系统时区
}

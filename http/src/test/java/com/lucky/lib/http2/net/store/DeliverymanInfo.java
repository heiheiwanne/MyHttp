package com.lucky.lib.http2.net.store;

import java.io.Serializable;

/**
 * @author xmq
 * @date 2017/7/27
 */

public class DeliverymanInfo implements Serializable {
    /**
     * 配送员id
     */
    public long empId;
    /**
     * 配送员昵称
     */
    public String aliasName;
    /**
     * 配送员手机
     */
    public String cellPhone;
    /**
     * 配送员邮箱
     */
    public String email;
    /**
     * 上班与否，0下班 1上班
     */
    public int workStatus;
    /**
     * 接单与否， 0暂停接单 1开始接单
     */
    public int orderStatus;
    /**
     * 是否离店 0未知 1到店 2离店送餐
     */
    public int positionStatus;
    /**
     * 离店时间
     */
    public String outshopTimeStr;
    /**
     * 员工性质 1.兼职员工， 0.全职员工
     */
    public int nature;
    /**
     * 员工所属公司id(本公司还是快递公司)
     */
    public int companyId;
    /**
     * 员工所属公司名称
     */
    public String companyName;
    /**
     * 未完成订单数
     */
    public int unFinishOrderCount;
    /**
     * 完成订单数
     */
    public int finishOrderCount;
    /**
     * 配送员照片url
     */
    public String logoUrl;
    /**
     * 配送员员工编号
     */
    public String empNo;
    /**
     * 配送员姓名
     */
    public String name;
    /**
     * 离店时间是否超过30分钟
     */
    public boolean isOutshopOverTime;
    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

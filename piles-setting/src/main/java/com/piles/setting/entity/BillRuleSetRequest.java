package com.piles.setting.entity;

import com.google.common.collect.Lists;
import com.piles.common.util.BytesUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 计费规则设置
 */
@Data
public class BillRuleSetRequest implements Serializable
{
    /**
     * 计费规则ID 4位 BIN 首次登录填0，后续登录填桩当前正在使用的计费规则ID
     */
    private int billingRuleId;
    /**
     * 计费规则版本号 4位 BIN 首次登录填0，后续登录填桩当前正在使用的计费规则版本号
     */
    private int billingRuleVersion;
    /**
     * 生效时间  6位 BCD 格式：YYMMDDHHMMSS
     */
    private String effectiveTime;
    /**
     * 预约费单价 2位 BIN  单位：元/次，精确度为0.001
     */
    private BigDecimal subscriptionPriceUnit;
    /**
     * 服务费单价 2位 BIN   单位：元/度，精确度为0.001
     */
    private BigDecimal servicePriceUnit;
    /**
     * 停车费单价 2位 BIN   单位：元/小时，精确度为0.001
     */
    private BigDecimal parkingPriceUnit;
    /**
     * 尖时电价 2位 BIN   单位：元，精确度为0.001
     */
    private BigDecimal pointElectricPrice;
    /**
     * 峰时电价 2位 BIN   单位：元，精确度为0.001
     */
    private BigDecimal peakElectricPrice;
    /**
     * 平时电价 2位 BIN   单位：元，精确度为0.001
     */
    private BigDecimal ordinaryElectricPrice;
    /**
     * 谷时电价 2位 BIN   单位：元，精确度为0.001
     */
    private BigDecimal dipElectricPrice;
    /**
     * 时段数 1位 BIN   范围：1-48
     */
    private int periodCount;
    /**
     * 计费规则时段
     */
    private List<BillRulePeriod> billRulePeriodList;


    /**
     * 解析报文并封装request体
     * @param msg
     * @return
     */
    public static BillRuleSetRequest packEntity(byte[] msg){
        BillRuleSetRequest request=new BillRuleSetRequest();
        request.setBillingRuleId(Integer.parseInt(BytesUtil.binary(BytesUtil.copyBytes(msg,0,4),10)));
        request.setBillingRuleVersion(Integer.parseInt(BytesUtil.binary(BytesUtil.copyBytes(msg,4,4),10)));
        request.setEffectiveTime(BytesUtil.bcd2Str(BytesUtil.copyBytes(msg,8,6)));
        request.setSubscriptionPriceUnit(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 14, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setServicePriceUnit(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 16, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setParkingPriceUnit(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 18, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setPointElectricPrice(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 20, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setPeakElectricPrice(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 22, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setOrdinaryElectricPrice(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 24, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setDipElectricPrice(BigDecimal.valueOf(BytesUtil.bytesToInt(BytesUtil.copyBytes(msg, 26, 2), 0)).divide(new BigDecimal(1000),3,BigDecimal.ROUND_HALF_UP));
        request.setPeriodCount(Integer.parseInt(BytesUtil.binary(BytesUtil.copyBytes(msg,28,1),10)));
        ArrayList<BillRulePeriod> billRulePeriodList = Lists.newArrayList();
        int startIndex = 29;
        int periodCount = 5;
        for(int i =0;i<request.getPeriodCount();i++){
            BillRulePeriod billRulePeriod = BillRulePeriod.packEntity(BytesUtil.copyBytes(msg, startIndex, periodCount));
            billRulePeriodList.add(billRulePeriod);
            startIndex = startIndex+periodCount;
        }
        request.setBillRulePeriodList(billRulePeriodList);

        return request;
    }


    public static void main(String[] args) {
        byte[] msg= new byte[]{0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x01,0x17,0x07,0x04,0x10,0x59,0x09,0x13,(byte)0x88,0x02,0x58,0x07,(byte)0xD0,0x05,0x14,0x05,0x14,0x05,0x14,0x05,0x14,0x01,0x00,0x00,0x23,0x59,0x03,0x65,(byte)0x93};
//        byte[] msg= new byte[]{0x10,0x00,0x02,0x54,(byte)0x84,0x56,0x18,0x35,0x02,0x02,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x01,0x00,0x01,0x02,0x00,0x00,0x00,0x04,0x00,0x00,0x00,0x01};
        BillRuleSetRequest billRuleSetRequest = packEntity(msg);
        System.out.println(billRuleSetRequest);

    }



}

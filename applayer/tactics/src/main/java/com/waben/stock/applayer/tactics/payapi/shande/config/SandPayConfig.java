package com.waben.stock.applayer.tactics.payapi.shande.config;

public class SandPayConfig {
    /**
     * 秘钥
     */
    public static final String  key = "0f7acd3d53920783c61ed1a99ed58c89";
    /**
     * 商户号
     */
    public static final String  mchNo = "MER1000157";
    /**
     * 商户类型
     */
    public static final String mchType = "1";
    /**
     * 支付渠道
     */
    public static final String payChannel = "1_jhdr";
    /**
     * 支付渠道支付类型编号
     */
    public static final String payChannelTypeNo = "7";
    /**
     * 商品名称
     */
    public static final String goodsName = "支付";
    /**
     * 商品描述
     */
    public static final String goodsDesc = "快捷";
//    /**
//     * 回调地址
//     */
//    public static final String notifyUrl = "https://m.youguwang.com.cn/tactics/quickpay/sdpaycallback";
//    /**
//     * 页面通知地址
//     */
//    public static final String fontUrl = "https://m.youguwang.com.cn/tactics/quickpay/sdpayreturn";

    /**
     * 回调地址
     */
    public static final String notifyUrl = "http://egdajc.natappfree.cc/quickpay/sdpaycallback";
    /**
     * 页面通知地址
     */
    public static final String fontUrl = "http://egdajc.natappfree.cc/quickpay/sdpayreturn";

    //测试
//    public static final String notifyUrl = "https://m.youguwang.com.cn/tactics/payment/quickpaynotify";
}
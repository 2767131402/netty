package com.zhenglei.netty.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局常量
 * @Description: 
 * @Date: 2019年3月8日
 * @auther: zhenglei
 */
public final class TypeData {

	/** 心跳包 PING命令 */
	public static final byte PING = 1;
	/** 心跳包 PONG命令 */
	public static final byte PONG = 2;
	/** 非心跳包 逻辑处理 */
	public static final byte CUSTOME = 3;

	/**成功 */
    public static final String OK = "OK";
    /**成功 */
    public static final String SUCCESS = "SUCCESS";
    /**所有异常*/
    public static final Map<String, String> keyMap = new HashMap<String, String>();
    /** 封装消息的 key。 data属性:数据 */
    public static final String MESSAGE_DATA = "data";
    /**封装消息的 key。 type:心跳/业务*/
    public static final String MESSAGE_TYPE = "type";

    static {
        //保存所有出现的错误
        //1. 短信发送错误信息
        keyMap.put(OK, "发送成功");
        keyMap.put("isp.RAM_PERMISSION_DENY", "RAM权限DENY");
        keyMap.put("isv.OUT_OF_SERVICE", "业务停机");
        keyMap.put("isv.PRODUCT_UN_SUBSCRIPT", "未开通云通信产品的阿里云客户");
        keyMap.put("isv.PRODUCT_UNSUBSCRIBE", "产品未开通");
        keyMap.put("isv.ACCOUNT_NOT_EXISTS", "账户不存在");
        keyMap.put("isv.ACCOUNT_ABNORMAL", "账户异常");
        keyMap.put("isv.SMS_TEMPLATE_ILLEGAL", "短信模板不合法");
        keyMap.put("isv.SMS_SIGNATURE_ILLEGAL", "短信签名不合法");
        keyMap.put("isv.INVALID_PARAMETERS", "参数异常");
        keyMap.put("isp.SYSTEM_ERROR", "系统错误");
        keyMap.put("isv.MOBILE_NUMBER_ILLEGAL", "非法手机号");
        keyMap.put("isv.MOBILE_COUNT_OVER_LIMIT", "手机号码数量超过限制");
        keyMap.put("isv.TEMPLATE_MISSING_PARAMETERS", "模板缺少变量");
        keyMap.put("isv.BUSINESS_LIMIT_CONTROL", "业务限流");
        keyMap.put("isv.INVALID_JSON_PARAM", "JSON参数不合法，只接受字符串值");
        keyMap.put("isv.BLACK_KEY_CONTROL_LIMIT", "黑名单管控");
        keyMap.put("isv.PARAM_LENGTH_LIMIT", "参数超出长度限制");
        keyMap.put("isv.PARAM_NOT_SUPPORT_URL", "不支持URL");
        keyMap.put("isv.AMOUNT_NOT_ENOUGH", "账户余额不足");
    }
}
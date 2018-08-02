package com.waben.stock.applayer.admin.util;

import com.waben.stock.interfaces.util.StringUtil;

/**
 * 注释：
 *
 * @Author: zengzhiwei
 * @Date: 2018/7/27 14:24
 */
public class PhoneUtil {

    public static String encodedPhone(String phone) {
        if(!StringUtil.isEmpty(phone)) {
            String string = "****";
            StringBuffer stringBuffer = new StringBuffer(phone);
            stringBuffer.replace(7,11,string);
            return stringBuffer.toString();
        }
        return phone;
    }

}

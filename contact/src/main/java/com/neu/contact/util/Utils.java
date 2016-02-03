package com.neu.contact.util;

import java.lang.reflect.Field;

/**
 * Created by neuyuandaima on 2015/12/28.
 * 工具类
 */
public class Utils {
    /**
     * 校验给定对象是否为空及其属性是否为空
     * @param object    给定对象
     * @param message   抛出错误信息
     * @param <T>   返回类型
     * @return  返回对象
     */
   public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.get(object) == null) { //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
                    throw new NullPointerException(message);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return object;
    }


}

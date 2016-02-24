package com.neu.httpclient;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *Created by neu on 16/2/9.
 */
public abstract class TType<T> {

    /**
     * //由于泛型的擦试原因，无法在子类中获取T.class，因此需要通过反射机制获取
     *
     * @return
     */
    public final Type getTType() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

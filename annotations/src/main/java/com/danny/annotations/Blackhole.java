package com.danny.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 接口声明注解
 */
@Retention(value = RetentionPolicy.CLASS)
public @interface Blackhole {
    String value();
}
package com.danny.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 接口实现注解
 */
@Retention(value = RetentionPolicy.CLASS)
public @interface Whitehole {
    String value();
}
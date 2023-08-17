package com.danny.runtime;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by Danny 姜
 */
class WormholeFinder {

    private static final String WORM_PACKAGE_NAME = "com.danny.wormhole";

    /**
     * 使用 JavaPoet 生成中间文件
     * @param clazzName 需要生成的 class 文件名称
     * @param value     需要写入的全局变量 value 值
     */
    public static String generateMiddleClass(String clazzName, String value) {
        System.out.println("BifrostClassUtil generateMiddleClass, value: "
                + value + " clazzName: " + clazzName);
        FieldSpec valueSpec = FieldSpec.builder(String.class, "value")
                .initializer("$S", value)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
        TypeSpec classSpec = TypeSpec.classBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(valueSpec)
                .build();
        JavaFile javaFile = JavaFile.builder(WORM_PACKAGE_NAME, classSpec)
                .build();
        return javaFile.toString();
    }
}

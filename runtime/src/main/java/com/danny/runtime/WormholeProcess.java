package com.danny.runtime;

import com.danny.annotations.Blackhole;
import com.danny.annotations.Whitehole;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static com.danny.runtime.WormholeFinder.generateMiddleClass;

@SupportedAnnotationTypes({
        "com.danny.annotations.Blackhole",
        "com.danny.annotations.Whitehole"
})
@AutoService(Processor.class)
public class WormholeProcess extends AbstractProcessor {

    Elements elementUtils;
    Types typeUtils;
    Filer filer;
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set annotations, RoundEnvironment roundEnv) {
        processBlackhole(roundEnv);
        processWhitehole(roundEnv);
        return true;
    }

    private void processWhitehole(RoundEnvironment roundEnv) {
        Map<String, ElementHolder> protocolMap = collectClassInfo(roundEnv, Whitehole.class, ElementKind.CLASS);
        for (String value : protocolMap.keySet()) {
            try {
                JavaFileObject fileObject = filer.createSourceFile(value, (Element[]) null);
                Writer writer = fileObject.openWriter();
                writer.write(generateMiddleClass(value, protocolMap.get(value).clazzName));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.out.println("e: " + e.getMessage());
            }
        }
    }

    private void processBlackhole(RoundEnvironment roundEnv) {
        Map<String, ElementHolder> shadowMap = collectClassInfo(roundEnv, Blackhole.class, ElementKind.INTERFACE);
        for (String value : shadowMap.keySet()) {
            try {
                String simpleName = shadowMap.get(value).simpleName;
                JavaFileObject fileObject = filer.createSourceFile(simpleName + "Bridge", (Element[]) null);
                Writer writer = fileObject.openWriter();
                writer.write(generateMiddleClass(simpleName + "Bridge", value));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, ElementHolder> collectClassInfo(RoundEnvironment roundEnv, Class clazz, ElementKind kind) {
        System.out.println("collectClassInfo for " + clazz.getSimpleName());
        Map<String, ElementHolder> map = new HashMap<>();
        Set<Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(clazz);

        for (Element element : elementsAnnotatedWith) {
            if (element.getKind() != kind) {
                throw new IllegalStateException(
                        String.format("@%s annotation must be on a  %s.", element.getSimpleName(), kind.name()));
            }
            try {
                TypeElement typeElement = (TypeElement) element;
                Annotation annotation = element.getAnnotation(clazz);
                Method annotationMethod = clazz.getDeclaredMethod("value");
                String name = (String) annotationMethod.invoke(annotation);
                String clazzName = typeElement.getQualifiedName().toString();
                String simpleName = typeElement.getSimpleName().toString();
                map.put(name, new ElementHolder(typeElement, name, clazzName, simpleName));
                System.out.println("get Annotation from Class :" + simpleName + "-->name:"
                        + name + "-->clazzName:" + clazzName + "-->annotationMethodName:"
                        + annotationMethod.getName() + "-->map.zie():" + map.size());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return map;
    }

    private static class ElementHolder {
        TypeElement typeElement;
        String valueName;
        String clazzName;
        String simpleName;

        public ElementHolder(TypeElement typeElement, String valueName, String clazzName, String simpleName) {
            this.typeElement = typeElement;
            this.valueName = valueName;
            this.clazzName = clazzName;
            this.simpleName = simpleName;
        }
    }
}
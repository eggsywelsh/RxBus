package com.eggsy.rxbus;

import com.eggsy.rxbus.annotation.EventSubscribe;
import com.eggsy.rxbus.assist.ProxyClassInfo;
import com.eggsy.rxbus.assist.ProxyMethodInfo;
import com.eggsy.rxbus.assist.ProxyParameterInfo;
import com.eggsy.rxbus.util.ClassValidator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by eggsy on 17-1-25.
 */

public class RxBusProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    /**
     * key:full class name
     * value:ProxyClassInfo
     */
    private HashMap<String, ProxyClassInfo> proxyClassInfoMap = new HashMap<>();

    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(EventSubscribe.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        note("process RxBusHelper annotations...");

        if (!processAnnotations(roundEnv, EventSubscribe.class)) return false;

        return false;
    }

    private boolean processAnnotations(RoundEnvironment roundEnv, Class<? extends Annotation> clazz) {
        if (roundEnv.getElementsAnnotatedWith(clazz) != null && roundEnv.getElementsAnnotatedWith(clazz).size() > 0) {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(clazz)) {
                if (checkMethodValid(annotatedElement, clazz)) {
                    ExecutableElement methodElement = (ExecutableElement) annotatedElement;

                    ProxyClassInfo proxyClassInfo = extractClassInfo(methodElement);
                    ProxyMethodInfo proxyMethodInfo = extractMethodInfo(proxyClassInfo, methodElement, clazz);
                    extractMethodParametersInfo(proxyMethodInfo, methodElement);


                }
            }
        }

        return true;
    }

    /**
     * extract annotation method's class info into ProxyClassInfo
     *
     * @param methodElement
     * @return
     */
    private ProxyClassInfo extractClassInfo(ExecutableElement methodElement) {
        if (methodElement != null) {
            String fullClassName = getClassFullName(methodElement);
            ProxyClassInfo proxyClassInfo = proxyClassInfoMap.get(fullClassName);
            if (proxyClassInfo == null) {
                proxyClassInfo = new ProxyClassInfo();
                proxyClassInfo.setProxyClassFullName(fullClassName);
                proxyClassInfoMap.put(fullClassName, proxyClassInfo);
            }
            return proxyClassInfo;
        }

        return null;
    }

    /**
     * extract annotation method info into ProxyMethodInfo instance
     *
     * @param proxyClassInfo
     * @param methodElement
     * @param clazz
     * @return
     */
    private ProxyMethodInfo extractMethodInfo(ProxyClassInfo proxyClassInfo, ExecutableElement methodElement, Class<? extends Annotation> clazz) {
        if (proxyClassInfo != null && methodElement != null) {
            Annotation annotation = methodElement.getAnnotation(clazz);
            if (annotation instanceof EventSubscribe) {
                EventSubscribe eventSubscribe = (EventSubscribe) annotation;
                String methodName = methodElement.getSimpleName().toString();

                ProxyMethodInfo proxyMethodInfo = new ProxyMethodInfo();
                proxyMethodInfo.setMethodName(methodName);
                proxyMethodInfo.setThreadMode(eventSubscribe.tmode());

                HashMap<String, ProxyMethodInfo> proxyMethodInfoMap = proxyClassInfo.getProxyMethodInfoMap();
                if (proxyMethodInfoMap == null) {
                    proxyMethodInfoMap = new HashMap<>();
                    proxyMethodInfoMap.put(methodName, proxyMethodInfo);
                    proxyClassInfo.setProxyMethodInfoMap(proxyMethodInfoMap);
                }

                return proxyMethodInfo;
            }

        }
        return null;
    }

    /**
     * extract method's parameters into ProxyMethodInfo instance
     *
     * @param proxyMethodInfo
     * @param methodElement
     */
    private void extractMethodParametersInfo(ProxyMethodInfo proxyMethodInfo, ExecutableElement methodElement) {
        List<? extends VariableElement> methodParams = methodElement.getParameters();
        note("annotation method params size=" + methodParams.size());
        List<ProxyParameterInfo> proxyParameterInfos = new ArrayList<>();
        if (methodParams != null && methodParams.size() > 0) {
            for (VariableElement variableElement : methodParams) {
                ProxyParameterInfo proxyParameterInfo = new ProxyParameterInfo();
                if (isBasicType(variableElement)) {
                    proxyParameterInfo.setParameterClassName(variableElement.asType().getKind().name());
                } else {
                    Element element = typeUtils.asElement(variableElement.asType());
                    proxyParameterInfo.setParameterClassName(element.getSimpleName().toString());
                }
                proxyParameterInfos.add(proxyParameterInfo);
            }
        }
        proxyMethodInfo.setParameterInfos(proxyParameterInfos);
    }

    /**
     * extract ExecutableElement belong class full name(include package)
     *
     * @param methodElement
     * @return class full name
     */
    private String getClassFullName(ExecutableElement methodElement) {
        //class type
        TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
        //full class name
        String fqClassName = classElement.getQualifiedName().toString();

        return fqClassName;
    }

    /**
     * judge the java basic type
     *
     * @param element
     * @return
     */
    private boolean isBasicType(Element element) {
        return element.asType().getKind().isPrimitive();
    }

    private void note(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void error(String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args));
    }

    private boolean checkMethodValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.METHOD) {
            error(annotatedElement, "%s must be declared on method.", clazz.getSimpleName());
            return false;
        }
        if (ClassValidator.isPrivate(annotatedElement) || ClassValidator.isAbstract(annotatedElement)) {
            error(annotatedElement, "%s() must can not be abstract or private.", annotatedElement.getSimpleName());
            return false;
        }

        return true;
    }
}

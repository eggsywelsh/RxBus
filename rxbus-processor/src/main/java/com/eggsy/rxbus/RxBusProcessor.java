package com.eggsy.rxbus;

import com.eggsy.rxbus.annotation.EventSubscribe;
import com.eggsy.rxbus.util.ClassValidator;

import java.lang.annotation.Annotation;
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
                    String methodName = methodElement.getSimpleName().toString();
//                    note("annotation method name="+methodName);
                    List<? extends VariableElement> methodParams = methodElement.getParameters();
                    note("annotation method params size=" + methodParams.size());
                    if (methodParams != null && methodParams.size() > 0) {
                        for (VariableElement variableElement : methodParams) {
                            if (isBasicType(variableElement)) {
                                note(variableElement.asType().getKind().name());
                            } else {
                                Element element = typeUtils.asElement(variableElement.asType());
                                note(element.getSimpleName().toString());
                            }
                            /*TypeElement typeElement = (TypeElement)typeUtils.asElement(variableElement.asType());
                            if(typeElement!=null){
                                note(typeElement.getQualifiedName().toString());
                            }else{
                                error();
                            }*/

//                            note("variable type="+variableElement.getSimpleName().toString()+" , value="+variableElement.getConstantValue());
//                            note("vaiable ="+variableElement.toString());
//                            note("variable ="+variableElement.getEnclosingElement().toString());
//                            TypeElement paramsElement = (TypeElement)variableElement;
//                            note("ttttt"+paramsElement.getQualifiedName().toString());
                        }
                    }
                }
            }
        }

        return true;
    }

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

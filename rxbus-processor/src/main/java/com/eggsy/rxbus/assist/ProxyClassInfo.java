package com.eggsy.rxbus.assist;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.reactivex.disposables.CompositeDisposable;

import static com.eggsy.rxbus.RxBus.SUFFIX;

/**
 * Created by eggsy on 17-1-31.
 * <p>
 * record proxy class info
 */

public class ProxyClassInfo {

    /**
     * proxy class package path
     */
    private String packageName;

    /**
     * proxy source class full path name(include package)
     */
    private String proxyClassFullName;

    /**
     * proxy source class simple name,
     * if the class is internal class, the simple name include outter class name
     */
    private String proxyClassSimpleName;

    /**
     * generate target class simple name
     */
    private String generateTargetClassSimpleName;

    /**
     * proxy sorce class TypeElement instance
     */
    private TypeElement proxyClassElement;

    public GenerateCode generateCode;

    private HashMap<String, ProxyMethodInfo> proxyMethodInfoMap;

    public HashMap<String, ProxyMethodInfo> getProxyMethodInfoMap() {
        return proxyMethodInfoMap;
    }

    public ProxyClassInfo(Elements elementUtils, TypeElement classElement) {
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //classname
        classElement.getSimpleName();
//        String simpleClassName = classElement.getSimpleName().toString();

        this.proxyClassSimpleName = getClassName(classElement);
        this.proxyClassElement = classElement;
        this.packageName = packageName;
//        this.generateTargetClassSimpleName = simpleClassName + SUFFIX;
        this.generateTargetClassSimpleName = getGenerateTargetClassName(classElement);
        this.proxyClassFullName = classElement.getQualifiedName().toString();
        this.generateCode = new GenerateCode();
        isInternalClass(proxyClassElement);
    }

    public String getGenerateTargetClassName(TypeElement classElement){
        String className = "";
        if(isInternalClass(classElement)){
            className = getClassName((TypeElement)classElement.getEnclosingElement())+"$"+classElement.getSimpleName().toString();
        }else{
            className = classElement.getSimpleName().toString()+className;
        }
        return className+SUFFIX;
    }

    public String getClassName(TypeElement classElement){
        String className = "";
        if(isInternalClass(classElement)){
            className = getClassName((TypeElement)classElement.getEnclosingElement())+"."+classElement.getSimpleName().toString();
        }else{
            className = classElement.getSimpleName().toString()+className;
        }
        return className;
    }

    public boolean isInternalClass(TypeElement classElement){
        Element element = classElement.getEnclosingElement();
        return element instanceof TypeElement;
    }

    public void setProxyMethodInfoMap(HashMap<String, ProxyMethodInfo> proxyMethodInfoMap) {
        this.proxyMethodInfoMap = proxyMethodInfoMap;
    }

    public String getPackageName() {
        return packageName;
    }

    public class GenerateCode {

        private final static String COMPOSITE_DISPOSABLE_FIELD = "compositeDisposable";
        private final static String SOURCE_PROXY_FIELD = "sourceInstance";

        public TypeSpec generateProxyClassCode() {
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generateTargetClassSimpleName);
            classBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(
                            ParameterizedTypeName.get(ClassName.bestGuess("com.eggsy.rxbus.internal.RxBusProxy"),
                                    TypeVariableName.get(proxyClassSimpleName)));

            classBuilder.addField(generateCompositeDisposableField());
            classBuilder.addField(generateProxySourceInstanceCode());
            classBuilder.addMethod(generateRegisterMethodCode());
            classBuilder.addMethod(generateUnRegisterMethodCode());
            return classBuilder.build();
        }

        private FieldSpec generateCompositeDisposableField() {
            FieldSpec fieldSpec = FieldSpec
                    .builder(CompositeDisposable.class, COMPOSITE_DISPOSABLE_FIELD, Modifier.PROTECTED)
                    .build();
            return fieldSpec;
        }

        private FieldSpec generateProxySourceInstanceCode() {
            FieldSpec fieldSpec = FieldSpec
                    .builder(TypeName.get(proxyClassElement.asType()), SOURCE_PROXY_FIELD, Modifier.PROTECTED)
                    .build();
            return fieldSpec;
        }

        private MethodSpec generateRegisterMethodCode() {

            MethodSpec.Builder methodSpecBuilder = MethodSpec
                    .methodBuilder("register")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(TypeVariableName.get(proxyClassSimpleName), "source").build())
                    .returns(TypeName.get(CompositeDisposable.class));

            methodSpecBuilder.addStatement(SOURCE_PROXY_FIELD + "=source");

            for (Map.Entry<String, ProxyMethodInfo> entry : proxyMethodInfoMap.entrySet()) {
                String methodName = entry.getKey();
                ProxyMethodInfo methodInfo = entry.getValue();

                String parameterClassName = methodInfo.getParameterInfo().getParameterClassName();
                String registerClass = parameterClassName + ".class";
                ClassName event = ClassName.bestGuess(methodInfo.getParameterInfo().getParameterFullName());
                ClassName rxbusHelper = ClassName.bestGuess("com.eggsy.rxbus.RxBusHelper");
                ClassName consumer = ClassName.bestGuess("io.reactivex.functions.Consumer");
                ClassName disposable = ClassName.bestGuess("io.reactivex.disposables.Disposable");

                String disposableName = methodName + "_disposable";
                methodSpecBuilder.addStatement(
                        "$T " + disposableName + " = $T.getDefault().register(" + registerClass + "," +
                                generateConsumerCode(SOURCE_PROXY_FIELD, methodName) +
                                generateInvokeRegisterCode(methodInfo) +
                                generateBackpressureStrategyCode(methodInfo) + ")"
                        , disposable, rxbusHelper, consumer, event, event);
                methodSpecBuilder.beginControlFlow("if(compositeDisposable==null || compositeDisposable.isDisposed())");
                methodSpecBuilder.addStatement("compositeDisposable = new CompositeDisposable()");
                methodSpecBuilder.endControlFlow();
                methodSpecBuilder.addStatement("compositeDisposable.add(" + disposableName + ")");
            }
            methodSpecBuilder.addStatement("return " + COMPOSITE_DISPOSABLE_FIELD);

            return methodSpecBuilder.build();
        }

        private String generateInvokeRegisterCode(ProxyMethodInfo proxyMethodInfo) {
            StringBuilder codeBuilder = new StringBuilder();
            if (proxyMethodInfo != null) {
                switch (proxyMethodInfo.getThreadMode()) {
                    case MainThread:
                        codeBuilder.append(",io.reactivex.android.schedulers.AndroidSchedulers.mainThread()");
                        break;
                    case IoThread:
                        codeBuilder.append(",io.reactivex.schedulers.Schedulers.io()");
                        break;
                    case NewThread:
                        codeBuilder.append(",io.reactivex.schedulers.Schedulers.newThread()");
                        break;
                    case SingleThread:
                        codeBuilder.append(",io.reactivex.schedulers.Schedulers.single()");
                        break;
                    case ComputationThread:
                        codeBuilder.append(",io.reactivex.schedulers.Schedulers.computation()");
                        break;
                    case PostThread:
                        codeBuilder.append(",io.reactivex.schedulers.Schedulers.trampoline()");
                        break;
                    default:
                        break;
                }
            }
            return codeBuilder.toString();
        }

        private String generateBackpressureStrategyCode(ProxyMethodInfo proxyMethodInfo) {
            StringBuilder codeBuilder = new StringBuilder();
            switch (proxyMethodInfo.getBackpressureStrategy()) {
                case BUFFER:
                    codeBuilder.append(",io.reactivex.BackpressureStrategy.BUFFER");
                    break;
                case DROP:
                    codeBuilder.append(",io.reactivex.BackpressureStrategy.DROP");
                    break;
                case ERROR:
                    codeBuilder.append(",io.reactivex.BackpressureStrategy.ERROR");
                    break;
                case MISSING:
                    codeBuilder.append(",io.reactivex.BackpressureStrategy.MISSING");
                    break;
                case LATEST:
                    codeBuilder.append(",io.reactivex.BackpressureStrategy.LATEST");
                    break;
                default:
                    DEFAULT:
                    break;
            }
            return codeBuilder.toString();
        }

        private MethodSpec generateUnRegisterMethodCode() {
            MethodSpec.Builder methodSpecBuilder = MethodSpec
                    .methodBuilder("unRegister")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class);
            methodSpecBuilder.beginControlFlow(
                    "if (compositeDisposable != null && !compositeDisposable.isDisposed())");
            methodSpecBuilder.addStatement("compositeDisposable.dispose()");

            methodSpecBuilder.endControlFlow();

            return methodSpecBuilder.build();
        }

        private String generateConsumerCode(String sourceInstanceName, String sourceInstanceMethodName) {
            return
                    "new $T<$T>(){\n" +
                            "@Override\n" +
                            "public void accept($T o) throws Exception {\n" +
                            sourceInstanceName + "." + sourceInstanceMethodName + "(o);\n" +
                            "}\n" +
                            "}";
        }
    }

}

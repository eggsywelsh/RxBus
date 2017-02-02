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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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
     * proxy source class simple name
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
        String className = classElement.getSimpleName().toString();

        this.proxyClassSimpleName = className;
        this.proxyClassElement = classElement;
        this.packageName = packageName;
        this.generateTargetClassSimpleName = className + SUFFIX;
        this.proxyClassFullName = classElement.getQualifiedName().toString();
        this.generateCode = new GenerateCode();
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
                                    TypeVariableName.get(proxyClassElement.getSimpleName().toString())));

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
                    .returns(TypeName.get(Disposable.class));
            for (Map.Entry<String, ProxyMethodInfo> entry : proxyMethodInfoMap.entrySet()) {
                String methodName = entry.getKey();
                ProxyMethodInfo methodInfo = entry.getValue();

                String parameterClassName = methodInfo.getParameterInfo().getParameterClassName();
                String registerClass = parameterClassName + ".class";
                ClassName rxbusHelper = ClassName.bestGuess("com.eggsy.rxbus.RxBusHelper");
                ClassName consumer = ClassName.bestGuess("io.reactivex.functions.Consumer");
                methodSpecBuilder.addStatement(SOURCE_PROXY_FIELD+"=source");
                methodSpecBuilder.addStatement("Disposable disposable = $T.getDefault().register(" + registerClass + "," + generateConsumerCode(parameterClassName, SOURCE_PROXY_FIELD, methodName) + ")"
                        , rxbusHelper, consumer);
                methodSpecBuilder.beginControlFlow("if(compositeDisposable==null || compositeDisposable.isDisposed())");
                methodSpecBuilder.addStatement("compositeDisposable = new CompositeDisposable()");
                methodSpecBuilder.endControlFlow();
                methodSpecBuilder.addStatement("compositeDisposable.add(disposable)");
                methodSpecBuilder.addStatement("return disposable");

            }
            return methodSpecBuilder.build();
        }

        private MethodSpec generateUnRegisterMethodCode() {
            MethodSpec.Builder methodSpecBuilder = MethodSpec
                    .methodBuilder("unRegister")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class);
            methodSpecBuilder.addStatement(
                    "if (compositeDisposable != null && !compositeDisposable.isDisposed()) {\n" +
                            "compositeDisposable.dispose();\n" +
                            "}");
            return methodSpecBuilder.build();
        }
    }


    private String generateConsumerCode(String genericType, String sourceInstanceName, String sourceInstanceMethodName) {
        return
                "new $T<" + genericType + ">(){\n" +
                        "@Override\n" +
                        "public void accept(" + genericType + " o) throws Exception {\n" +
                        sourceInstanceName + "." + sourceInstanceMethodName + "(o);\n" +
                        "}\n" +
                        "}";
    }


}

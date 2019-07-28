package com.jeremyliao.dataloader.compiler.processor;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.jeremyliao.dataloader.base.annotation.DataLoad;
import com.jeremyliao.dataloader.base.utils.EncryptUtils;
import com.jeremyliao.dataloader.compiler.processor.base.LoaderType;
import com.jeremyliao.dataloader.compiler.processor.bean.LoaderInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.sun.tools.javac.code.Type;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Created by liaohailiang on 2019-07-19.
 */
@AutoService(Processor.class)
public class DataLoaderProcessor extends AbstractProcessor {

    private static final String TAG = "[DataLoaderProcessor]";
    private static final String DEFAULT_PKG_NAME = "com.jeremyliao.dataloader.generated";
    private static final String DATA_SOURCE_PACKAGE_NAME = "com.jeremyliao.dataloader.core.source";
    private static final String DATA_SOURCE_CLASS_NAME = "DataSource";
    private static final String OUTPUT_PATH = "META-INF/dataloader/loaderinfo/";

    Filer filer;
    Types types;
    Elements elements;

    Gson gson = new Gson();
    Map<String, List<LoaderInfo>> loaderInfoMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(DataLoad.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!roundEnvironment.processingOver()) {
            processAnnotations(roundEnvironment);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(DataLoad.class);
        if (elements != null && elements.size() > 0) {
            loaderInfoMap.clear();
            for (Element element : elements) {
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    LoaderInfo loaderInfo = new LoaderInfo();
                    loaderInfo.loaderClass = typeElement.getQualifiedName().toString();
                    loaderInfo.method = getAnnotation(element, DataLoad.class, "method");
                    loaderInfo.targetClass = getAnnotation(element, DataLoad.class, "target");
                    for (TypeMirror anInterface : typeElement.getInterfaces()) {
                        Element interfaceElement = asElement(anInterface);
                        if (interfaceElement instanceof TypeElement) {
                            if (isFromInterface((TypeElement) interfaceElement, LoaderType.DATA_LOADER_TYPE)) {
                                if (anInterface instanceof Type.ClassType) {
                                    Type.ClassType classType = (Type.ClassType) anInterface;
                                    if (classType.typarams_field != null && classType.typarams_field.size() > 0) {
                                        int size = classType.typarams_field.size();
                                        loaderInfo.returnType = classType.typarams_field.get(size - 1).toString();
                                        if (size > 1) {
                                            loaderInfo.paramTypes = new String[size - 1];
                                            for (int i = 0; i < size - 1; i++) {
                                                loaderInfo.paramTypes[i] = classType.typarams_field.get(i).toString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!loaderInfoMap.containsKey(loaderInfo.targetClass)) {
                        loaderInfoMap.put(loaderInfo.targetClass, new ArrayList<>());
                    }
                    loaderInfoMap.get(loaderInfo.targetClass).add(loaderInfo);
                    System.out.println(TAG + "loaderInfo: " + loaderInfo);
                }
            }
            generateInterfaceClass();
            generateOutput();
        }
    }

    private boolean isFromInterface(TypeElement typeElement, String interfaceName) {
        for (TypeMirror anInterface : typeElement.getInterfaces()) {
            if (anInterface.toString().equals(interfaceName)) {
                return true;
            } else {
                boolean isInterface = isFromInterface((TypeElement) asElement(anInterface), interfaceName);
                if (isInterface) {
                    return true;
                }
            }
        }
        return false;
    }

    private Element asElement(TypeMirror mirror) {
        return types.asElement(mirror);
    }

    private <T> T getAnnotation(Element element, Class<? extends Annotation> type, String name) {
        String canonicalName = type.getCanonicalName();
        List<? extends AnnotationMirror> annotationMirrors = elements.getAllAnnotationMirrors(element);
        if (annotationMirrors != null && annotationMirrors.size() > 0) {
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (canonicalName.equals(annotationMirror.getAnnotationType().toString())) {
                    if (annotationMirror.getElementValues() != null) {
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry :
                                annotationMirror.getElementValues().entrySet()) {
                            ExecutableElement annotationName = entry.getKey();
                            AnnotationValue annotationValue = entry.getValue();
                            if (annotationName.getSimpleName().toString().equals(name)) {
                                return (T) annotationValue.getValue();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void generateInterfaceClass() {
        if (loaderInfoMap.size() == 0) {
            return;
        }
        for (Map.Entry<String, List<LoaderInfo>> entry : loaderInfoMap.entrySet()) {
            String targetClassName = entry.getKey();
            System.out.println(TAG + "generateInterfaceClass: " + targetClassName);
            int index = targetClassName.lastIndexOf(".");
            String interfaceName = index >= 0 ? targetClassName.substring(index + 1) : targetClassName;
            TypeSpec.Builder builder = TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(Modifier.PUBLIC)
                    .addJavadoc("Auto generate code, do not modify!!!");
            for (LoaderInfo loaderInfo : entry.getValue()) {
                //添加每一个方法
                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(loaderInfo.method)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                //添加return
                ClassName baseClassName = ClassName.get(DATA_SOURCE_PACKAGE_NAME, DATA_SOURCE_CLASS_NAME);
                TypeName returnType = ParameterizedTypeName.get(baseClassName, getTypeName(loaderInfo.returnType));
                methodBuilder.returns(returnType);
                //添加参数
                if (loaderInfo.paramTypes != null && loaderInfo.paramTypes.length > 0) {
                    for (int i = 0; i < loaderInfo.paramTypes.length; i++) {
                        String paramType = loaderInfo.paramTypes[i];
                        methodBuilder.addParameter(getTypeName(paramType), "param" + (i + 1));
                    }
                }
                builder.addMethod(methodBuilder.build());
            }
            TypeSpec typeSpec = builder.build();
            String packageName = index >= 0 ? targetClassName.substring(0, index) : targetClassName;
            if (index >= 0) {
                packageName = targetClassName.substring(0, index);
            } else {
                if (entry.getValue().size() > 0) {
                    String loaderClass = entry.getValue().get(0).loaderClass;
                    int index1 = loaderClass.lastIndexOf(".");
                    if (index1 > 0) {
                        packageName = loaderClass.substring(0, index1);
                    }
                }
                if (packageName == null || packageName.length() == 0) {
                    packageName = DEFAULT_PKG_NAME;
                }
            }
            try {
                JavaFile.builder(packageName, typeSpec)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void generateOutput() {
        if (loaderInfoMap.size() == 0) {
            return;
        }
        Map<String, String> outputMap = new HashMap<>();
        for (List<LoaderInfo> loaderInfos : loaderInfoMap.values()) {
            for (LoaderInfo loaderInfo : loaderInfos) {
                String key = getKey(loaderInfo);
                outputMap.put(key, loaderInfo.loaderClass);
            }
        }
        writeToFile(OUTPUT_PATH + System.currentTimeMillis(), gson.toJson(outputMap));
    }

    private String getKey(LoaderInfo loaderInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(loaderInfo.targetClass);
        sb.append(loaderInfo.method);
        if (loaderInfo.paramTypes != null && loaderInfo.paramTypes.length > 0) {
            for (String paramType : loaderInfo.paramTypes) {
                sb.append(paramType);
            }
        }
        String feature = sb.toString();
        System.out.println(TAG + "feature: " + feature);
        return EncryptUtils.encryptMD5ToString(feature);
    }

    private TypeName getTypeName(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        java.lang.reflect.Type type = getType(name);
        if (type != null) {
            return ClassName.get(type);
        } else {
            return TypeVariableName.get(name);
        }
    }

    private java.lang.reflect.Type getType(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void writeToFile(String fileName, String content) {
        if (isEmpty(fileName) || isEmpty(content)) {
            return;
        }
        System.out.println(TAG + "writeToFile fileName: " + fileName + " content: " + content);
        try {
            FileObject res = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            OutputStream os = res.openOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(content);
            writer.flush();
            writer.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(TAG + e.toString());
        }
    }

    static boolean isEmpty(String path) {
        return path == null || path.length() == 0;
    }
}

package com.hyc.nav_compiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.hyc.nav_annotation.Destination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.hyc.nav_annotation.Destination")
public class NavProcessor extends AbstractProcessor {
    private static final String PAGE_TYPE_ACTIVITY = "Activity";
    private static final String PAGE_TYPE_FRAGMENT = "Fragment";
    private static final String PAGE_TYPE_DIALOG = "Dialog";

    private static final String OUT_PUT_FILE_NAME = "destination.json";

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "enter init....");

        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Destination.class);
        if (!elements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            handleDestination(elements, Destination.class, destMap);

            try {
                FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", OUT_PUT_FILE_NAME);
                //文件目录，app/build/intermediates/classes/目录下
                //但是我们需要生成最终文件在 app/main.assets/
                String resourcePath = resource.toUri().getPath();

                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets";
                File file = new File(assetsPath);
                if (file.exists()){
                    file.mkdir();
                }
                Files.createDirectories(file.toPath());

                String content = JSON.toJSONString(destMap);
                File outputFile = new File(assetsPath, OUT_PUT_FILE_NAME);
                if (outputFile.exists()){
                    outputFile.delete();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                OutputStreamWriter write = new OutputStreamWriter(fileOutputStream);
                write.write(content);
                write.flush();

                fileOutputStream.close();
                write.close();
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "生成文件失败:" +  e.getLocalizedMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        return false;
    }

    private void handleDestination(Set<? extends Element> elements, Class<Destination> destinationClass, HashMap<String, JSONObject> destMap) {
        elements.forEach(element -> {

            TypeElement typeElement = (TypeElement) element;
            //获取全类名
            String clazName = typeElement.getQualifiedName().toString();
            //获取注解
            Destination annotation = typeElement.getAnnotation(Destination.class);
            //取出注解中字段的值
            String pageUrl = annotation.pageUrl();
            //是否设置为启动页
            boolean asStarter = annotation.asStarter();
            //导航Id 设置为 我们注解所在的全类名的hash值
            int id = clazName.hashCode();
            //判断注解当前所在类 继承自哪个类。
            String destType = getDestinationType(typeElement);

            if (destMap.containsKey(pageUrl)){
                messager.printMessage(Diagnostic.Kind.ERROR,"不同的页面不允许使用相同的pageUrl:"+pageUrl);
            }else{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clazName",clazName);
                jsonObject.put("pageUrl",pageUrl);
                jsonObject.put("asStarter",asStarter);
                jsonObject.put("id",id);
                jsonObject.put("destType",destType);
                destMap.put(pageUrl,jsonObject);
            }
        });

    }

    private String getDestinationType(TypeElement typeElement) {
        TypeMirror superclass = typeElement.getSuperclass();
        String superClazName = superclass.toString();
        if (superClazName.contains(PAGE_TYPE_ACTIVITY.toLowerCase())){
            return PAGE_TYPE_ACTIVITY.toLowerCase();
        }else if (superClazName.contains(PAGE_TYPE_FRAGMENT.toLowerCase())){
            return PAGE_TYPE_FRAGMENT.toLowerCase();
        }else if (superClazName.contains(PAGE_TYPE_DIALOG.toLowerCase())){
            return PAGE_TYPE_DIALOG.toLowerCase();
        }
        //如果这个父类类型是类的类型，或者是接口的类型。
        if (superclass instanceof DeclaredType){
            getDestinationType(typeElement);
        }
        return null;
    }
}
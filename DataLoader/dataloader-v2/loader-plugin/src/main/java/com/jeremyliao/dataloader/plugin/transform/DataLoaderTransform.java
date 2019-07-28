package com.jeremyliao.dataloader.plugin.transform;

import com.android.SdkConstants;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jeremyliao.dataloader.plugin.common.Const;
import com.jeremyliao.dataloader.plugin.utils.GradleUtils;

import org.gradle.api.Project;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by liaohailiang on 2019-07-24.
 */
public class DataLoaderTransform extends Transform {

    private static final String TAG = "[DataLoaderTransform]";
    private static final String LOADER_INFO_PATH = "META-INF/dataloader/loaderinfo/";
    private static final String ASSETS_PATH = "dataloader/loaderinfo/";
    private static final String ASSETS_FILE = "loaderinfo.json";

    private final Project project;
    private Gson gson = new Gson();

    public DataLoaderTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return DataLoaderTransform.class.getSimpleName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        process(transformInvocation);
        copyInputToOutput(transformInvocation);
    }

    private void process(TransformInvocation transformInvocation) throws IOException {
        Map<String, String> loaderInfoMap = new HashMap<>();
        for (TransformInput input : transformInvocation.getInputs()) {
            for (JarInput jarInput : input.getJarInputs()) {
                File file = jarInput.getFile();
                JarFile jarFile = new JarFile(file);
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (isLoaderInfoEntry(entry)) {
                        System.out.println(TAG + "entry name: " + entry.getName());
                        String content = GradleUtils.getContent(jarFile, entry);
                        System.out.println(TAG + "content: " + content);
                        Type type = new TypeToken<Map<String, String>>() {
                        }.getType();
                        Map<String, String> map = gson.fromJson(content, type);
                        if (map != null) {
                            loaderInfoMap.putAll(map);
                        }
                    }
                }
            }
        }
        String json = gson.toJson(loaderInfoMap);
        generateInitClass(transformInvocation, json);
    }

    private void writeToAssets(Map<String, String> loaderInfoMap) {
        File assetsDir = new File(getAssetsPath(), ASSETS_PATH);
        if (assetsDir.isFile()) {
            assetsDir.delete();
        }
        if (!assetsDir.exists()) {
            assetsDir.mkdirs();
        }
        File assetsFile = new File(assetsDir, ASSETS_FILE);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(assetsFile);
            writer.write(gson.toJson(loaderInfoMap));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            GradleUtils.safeClose(writer);
        }
    }

    private void copyInputToOutput(TransformInvocation transformInvocation) throws IOException {
        for (TransformInput input : transformInvocation.getInputs()) {
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File dest = transformInvocation.getOutputProvider().getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
            for (JarInput jarInput : input.getJarInputs()) {
                String destName = jarInput.getName();
                File dest = transformInvocation.getOutputProvider().getContentLocation(destName,
                        jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                FileUtils.copyFile(jarInput.getFile(), dest);
            }
        }
    }

    private void generateInitClass(TransformInvocation transformInvocation, String content) {
        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            MethodVisitor mv;
            cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC,
                    Const.SERVICE_LOADER_INIT.replace('.', '/'),
                    null, "java/lang/Object", null);
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(6, l0);
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                        Const.INIT_METHOD, "()Ljava/lang/String;", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(8, l0);
                mv.visitLdcInsn(content);
                mv.visitInsn(Opcodes.ARETURN);
                mv.visitMaxs(1, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            File dir = transformInvocation.getOutputProvider().getContentLocation(
                    "dataloader", TransformManager.CONTENT_CLASS,
                    ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);
            File dest = new File(dir, Const.SERVICE_LOADER_INIT.replace('.', '/') + SdkConstants.DOT_CLASS);
            dest.getParentFile().mkdirs();
            new FileOutputStream(dest).write(cw.toByteArray());
            System.out.println(TAG + "init class dest: " + dest.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAssetsPath() {
        return FileUtils.join(getMainPath(), "assets");
    }

    private String getMainPath() {
        return FileUtils.join(getModuleRootDir().getAbsolutePath(),
                "src", "main");
    }

    private File getModuleRootDir() {
        return project.getBuildDir().getParentFile();
    }

    private boolean isLoaderInfoEntry(JarEntry entry) {
        return !entry.isDirectory() && entry.getName().startsWith(LOADER_INFO_PATH);
    }
}

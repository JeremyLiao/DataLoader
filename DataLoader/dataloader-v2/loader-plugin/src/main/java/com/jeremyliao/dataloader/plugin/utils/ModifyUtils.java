package com.jeremyliao.dataloader.plugin.utils;

import com.android.build.api.transform.TransformInvocation;
import com.jeremyliao.dataloader.plugin.common.Const;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by liaohailiang on 2019-08-29.
 */
public class ModifyUtils {

    private static final String TAG = "[DataLoaderTransform]";

    private static final String TARGET_CLASS = "com/jeremyliao/dataloader/core/utils/GenericsUtils.class";

    public static boolean isNeedModify(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (TARGET_CLASS.equals(entry.getName())) {
                return true;
            }
        }
        return false;
    }

    public static File replaceInJar(TransformInvocation transformInvocation, File file) throws IOException {
        if (isNeedModify(file)) {
            return modifyJar(file, transformInvocation.getContext().getTemporaryDir());
        }
        return null;
    }

    public static File modifyJar(File file, File tempDir) throws IOException {
        JarFile jarFile = new JarFile(file);
        System.out.println(TAG + "tempDir: " + tempDir.getAbsolutePath());
        File outputFile = new File(tempDir, file.getName());
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputFile));
        Enumeration enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            String entryName = jarEntry.getName();
            ZipEntry zipEntry = new ZipEntry(entryName);
            jarOutputStream.putNextEntry(zipEntry);
            byte[] modifiedClassBytes = null;
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream);
            if (TARGET_CLASS.equals(jarEntry.getName())) {
                ClassReader cr = new ClassReader(sourceClassBytes);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cr.accept(cw, 0);
                {
                    MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "test", "()V", null, null);
                    mv.visitCode();
                    Label l0 = new Label();
                    mv.visitLabel(l0);
                    mv.visitLineNumber(18, l0);
                    mv.visitInsn(Opcodes.RETURN);
                    mv.visitMaxs(0, 0);
                    mv.visitEnd();
                }
                cw.visitEnd();
                modifiedClassBytes = cw.toByteArray();
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes);
            } else {
                jarOutputStream.write(modifiedClassBytes);
            }
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        jarFile.close();
        return outputFile;
    }
}

package com.jeremyliao.dataloader.plugin.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by liaohailiang on 2019/2/22.
 */
public class GradleUtils {

    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getContent(JarFile jarFile, JarEntry entry) {
        InputStream is = null;
        BufferedReader reader = null;
        try {
            is = jarFile.getInputStream(entry);
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(reader);
            safeClose(is);
        }
        return null;
    }
}

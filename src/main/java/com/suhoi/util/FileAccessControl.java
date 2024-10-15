package com.suhoi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileAccessControl {

    static {
        System.loadLibrary("FileAccessControlNative"); // Загрузка библиотеки
    }

    // Нативные методы
    public static native boolean blockAccess();
    public static native boolean allowAccess();
    public static native boolean unblockSpecific(String path);
}

/**
 * код ниже вместо блока static для релиза
 */
//static {
//    try {
//        // Extract the DLL file from the JAR
//        String dllFileName = "FileAccessControlNative.dll";
//        File tempDll = extractDllFromJar(dllFileName);
//        // Load the extracted DLL file
//        System.load(tempDll.getAbsolutePath());
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
//
//private static File extractDllFromJar(String dllFileName) throws IOException {
//    // Create a temporary file
//    File tempFile = File.createTempFile(dllFileName, "");
//    tempFile.deleteOnExit();
//
//    // Open an input stream to the DLL file inside the JAR
//    try (InputStream is = FileAccessControl.class.getResourceAsStream("/" + dllFileName);
//         FileOutputStream fos = new FileOutputStream(tempFile)) {
//        if (is == null) {
//            throw new IOException("DLL file " + dllFileName + " not found in JAR");
//        }
//        // Copy the DLL file to the temporary file
//        byte[] buffer = new byte[1024];
//        int bytesRead;
//        while ((bytesRead = is.read(buffer)) != -1) {
//            fos.write(buffer, 0, bytesRead);
//        }
//    }
//
//    return tempFile;
//}
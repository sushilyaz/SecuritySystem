package com.suhoi.util;

public class FileAccessControl {

    static {
        System.loadLibrary("FileAccessControlNative"); // Загрузка библиотеки
    }

    // Нативные методы
    public static native boolean blockAccess();
    public static native boolean allowAccess();
    public static native boolean unblockSpecific(String path);
}


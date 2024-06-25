package com.suhoi.util;

public class FileAccessManager {
    static {
        System.loadLibrary("FileAccessControl");
    }

    private static native boolean blockFileAccess();
    private static native boolean allowDirectoryAccess(String folderPath);
    private static native boolean unblockFileAccess();

    public static boolean restrictAccess() {
        return blockFileAccess();
    }

    public static boolean grantAccess(String folderPath) {
        return allowDirectoryAccess(folderPath);
    }

    public static boolean restoreAccess() {
        return unblockFileAccess();
    }
}


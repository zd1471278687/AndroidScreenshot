package app.zd.androidscreenshot.util;

import java.io.File;

/**
 * file util
 * Created by zhangdong on 2018/2/26.
 */

public class FileUtil {
    /**
     * Don't let anyone instantiate this class
     */
    private FileUtil() {}

    /**
     * 删除目录下所有文件(自身不会被删除)
     *
     * @param file 文件
     */
    public static void deleteDir(File file) {
        if (file == null) {
            return;
        }
        File[] childFiles = file.listFiles();
        if (null != childFiles && childFiles.length > 0) {
            for (File child : childFiles) {
                if (null != child && child.exists()) {
                    deleteAllFileCache(child);
                }
            }
        }
    }

    /**
     * 递归删除文件
     *
     * @param file 文件
     */
    public static void deleteAllFileCache(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (File childFile : childFiles) {
                deleteAllFileCache(childFile);
            }
            file.delete();
        }
    }

    /**
     * 获取文件夹大小
     *
     * @return float 单位为M
     */
    public static float getFolderSize(File folder) {
        float size = 0;
        try {
            File[] fileList = folder.listFiles();
            if (null == fileList) {
                return 0;
            }
            for (File file : fileList) {
                if (file.isDirectory()) {
                    size = size + getFolderSize(file);
                } else {
                    size = size + file.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size / 1048576;
    }

    public static boolean isFile(String filePath) {
        return !StringUtil.isNullOrEmpty(filePath) && new File(filePath).exists();
    }
}

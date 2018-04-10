package com.ligf.androidutilslib.file;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 文件工具类，Android系统中获取系统文件路径的方法
 * Created by ligf on 2018/4/10.
 */

public class FileUtil {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 获取系统的外存路径（保存在SD卡中）<p>
     * example<p>
     * 传入TestFileDirectory，返回结果：/storage/emulated/0/TestFileDirectory
     * @param context
     * @param childPath
     * @return
     */
    public static File getExternalStorageDirectory(Context context, String childPath){
        File externalDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)){
            externalDir = new File(Environment.getExternalStorageDirectory(), childPath);
        }
        return externalDir;
    }

    /**
     * 获取外存中系统指定的文件路径<p>
     * 当应用被删除后，该路径中的文件也会被删除<p>
     * example<p>
     * 返回结果：/storage/emulated/0/Android/data/com.mtq.pndstatusmonitor/files/db
     * @param context
     * @return
     */
    public static File getExternalFilesDir(Context context){
        File returnFile = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            returnFile = context.getExternalFilesDir("db");
        }
        return returnFile;
    }

    /**
     * 返回外存中系统指定的文件缓存路径<p>
     * example<p>
     * 返回结果：/storage/emulated/0/Android/data/com.mtq.pndstatusmonitor/cache
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context){
        File returnFile = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            returnFile = context.getExternalCacheDir();
        }
        return returnFile;
    }

    /**
     * 获取系统外存的根目录，存入的参数作为子目录<p>
     * example<p>
     * log为传入的字符串参数<p>
     * 返回结果：/storage/emulated/0/log
     * @param childPath
     * @return
     */
    public static File getExternalStoragePublicDirectory(Context context, String childPath){
        File returnFile = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            returnFile = Environment.getExternalStoragePublicDirectory(childPath);
        }
        return returnFile;
    }

    /**
     * 获取文件系统中指定的应用文件缓存路径<p>
     * 读写此缓存路径的文件不需要特殊权限;当存储空间不足时，此文件路径中的文件可能会被删除<p>
     * example<p>
     * 返回的结果：/data/data/com.mtq.pndstatusmonitor/cache
     * @param context
     * @return
     */
    public static File getCacheDir(Context context){
        File returnFile = context.getCacheDir();
        return returnFile;
    }

    /**
     * 创建一个数据库，返回系统保存该数据库的路径<p>
     * example<p>
     * 传入参数：data.db,返回结果：/data/data/com.mtq.pndstatusmonitor/databases/data.db
     * @param context
     * @return
     */
    public static File getDatabasePath(Context context, String dbName){
        File returnFile = context.getDatabasePath(dbName);
        return returnFile;
    }

    /**
     * 根据文件父目录路径和文件名称创建新的文件
     * @param parentFileDirPath 文件父目录路径名称
     * @param fileName 文件名称
     * @return
     */
    public static File createNewFile(String parentFileDirPath, String fileName){
        File resultFile = null;
        resultFile = new File(parentFileDirPath, fileName);
        if (!resultFile.getParentFile().exists()) {
            resultFile.getParentFile().mkdirs();
        }
        if (!resultFile.exists()) {
            try {
                resultFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return resultFile;
    }

    /**
     * 判断是否有写入存储的权限
     * @param context
     * @return
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}

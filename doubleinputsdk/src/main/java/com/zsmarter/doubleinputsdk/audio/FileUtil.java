//package com.zsmarter.doubleinputsdk.audio;
//
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
///**
// * Created by wangxf on 16-12-26.
// */
//
//public class FileUtil {
//
//    public static String TAG = "FileUtil";
//    /**
//     * 删除单个文件
//     * @param   filePath    被删除文件的文件名
//     * @return 文件删除成功返回true，否则返回false
//     */
//    public  boolean deleteFile(String filePath) {
//        File file = new File(filePath);
//        if (file.isFile() && file.exists()) {
//            return file.delete();
//        }
//        return false;
//    }
//
//    /**
//     * 删除文件夹以及目录下的文件
//     * @param   filePath 被删除目录的文件路径
//     * @return  目录删除成功返回true，否则返回false
//     */
//    public boolean deleteDirectory(String filePath) {
//        boolean flag = false;
//        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
//        if (!filePath.endsWith(File.separator)) {
//            filePath = filePath + File.separator;
//        }
//        File dirFile = new File(filePath);
//        if (!dirFile.exists() || !dirFile.isDirectory()) {
//            return false;
//        }
//        flag = true;
//        File[] files = dirFile.listFiles();
//        //遍历删除文件夹下的所有文件(包括子目录)
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].isFile()) {
//                //删除子文件
//                flag = deleteFile(files[i].getAbsolutePath());
//                if (!flag) break;
//            } else {
//                //删除子目录
//                flag = deleteDirectory(files[i].getAbsolutePath());
//                if (!flag) break;
//            }
//        }
//        if (!flag) return false;
//        //删除当前空目录
//        return dirFile.delete();
//    }
//
//    /**
//     *  根据路径删除指定的目录或文件，无论存在与否
//     *@param filePath  要删除的目录或文件
//     *@return 删除成功返回 true，否则返回 false。
//     */
//    public boolean DeleteFolder(String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            return false;
//        } else {
//            if (file.isFile()) {
//                // 为文件时调用删除文件方法
//                return deleteFile(filePath);
//            } else {
//                // 为目录时调用删除目录方法
//                return deleteDirectory(filePath);
//            }
//        }
//    }
//
//    /**
//     * 生成文件夹
//     * @param filePath
//     */
//    public  void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//        } catch (Exception e) {
//            Log.i("error:", e + "");
//        }
//    }
//
//    /**
//     *创建文件夹 返回File对象
//     * @param filePath
//     */
//    public  File makeRootDirectoryReturn(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//        } catch (Exception e) {
//            Log.i("error:", e + "");
//        }
//        return file;
//    }
//
//    /**
//     * 复制单个文件
//     * @param oldPath String 原文件路径 如：c:/fqf.txt
//     * @param newPath String 复制后路径 如：f:/fqf.txt
//     * @return boolean
//     */
//    public boolean copyFile(String oldPath, String newPath) {
//        try {
//            int bytesum = 0;
//            int byteread = 0;
//            File oldfile = new File(oldPath);
//            File newfile = new File(newPath);
//            if (oldfile.exists()) { //文件存在时
//                Log.i(TAG,"oldfile.exists()");
//                if (!newfile.exists()){
//                    Log.i(TAG,"!newfile.exists()");
//                    makeRootDirectory(newPath.substring(0, newPath.lastIndexOf("/")));
//                }
//                InputStream inStream = new FileInputStream(oldPath); //读入原文件
//                FileOutputStream fs = new FileOutputStream(newPath);
//                byte[] buffer = new byte[1444];
//                int length;
//                while ( (byteread = inStream.read(buffer)) != -1) {
//                    bytesum += byteread; //字节数 文件大小
//                    System.out.println(bytesum);
//                    fs.write(buffer, 0, byteread);
//                }
//                inStream.close();
//                fs.close();
//                return true;
//            }
//            Log.e(TAG,"oldPath not exit");
//            return false;
//        }
//        catch (Exception e) {
//            //            System.out.println("复制单个文件操作出错");
//            Log.e(TAG,"copy failed");
//            e.printStackTrace();
//            return false;
//
//        }
//
//    }
//
//    /**
//     * 获取文件名称
//     * @param path
//     * @return
//     */
//    public String getFileName(String path){
//        String name = "";
//        File file = new File(path);
//        name = file.getName();
//        return name;
//    }
//
//    /**
//     * 获取文件大小
//     * @param path
//     */
//    public long getFileLength(String path){
//        File file = new File(path);
//        long length = file.length();
//        return length;
//    }
//
//    /**
//     * 判断是否为文件夹
//     * @param path
//     * @return
//     */
//    public boolean fileIsDirectory(String path){
//        File file = new File(path);
//        return file.isDirectory();
//    }
//
//    /**
//     * 获取文件对象
//     * @param path
//     * @return
//     */
//    public File getFile(String path){
//        File file = new File(path);
//        return file;
//    }
//}

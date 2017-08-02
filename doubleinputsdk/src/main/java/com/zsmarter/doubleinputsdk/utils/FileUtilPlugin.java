//package com.zsmarter.doubleinputsdk.utils;
//
//import android.os.Environment;
//import android.util.Log;
//import android.widget.Toast;
//
//import org.apache.cordova.CallbackContext;
//import org.apache.cordova.CordovaInterface;
//import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.CordovaWebView;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import static android.R.id.input;
//
///**
// * Created by hechengbin on 2017/3/20.
// */
//
//public class FileUtilPlugin extends CordovaPlugin {
//
//    public static String FILEUTILCOPY = "FileUtilCopy";
//    public static String FILEUTILDELETE= "FileUtilDelete";
//    public static String FILEUTILRENAME = "FileUtilRename";
//    public static String FILEUTILMAKEDIR = "FileUtilMakeDir";
//    public static String FILEUTILGETFILENAME = "FileUtilGetFileName";
//    public static String FILEUTILGETFILELENGTH= "FileUtilGetFileLength";
//    public static String FILEUTILGETFILE ="FileUtilGetFile";
//    public static String FILEUTILGETALLFILE ="FileUtilGetAllFile";
//    public static String TAG = "FileUtilPlugin";
//    private FileUtil fileUtil;
//    private String absolutePath = "";
//
//    @Override
//    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
//        super.initialize(cordova, webView);
//
//        Log.e("test","initialize");
//        fileUtil = new FileUtil();
//        //                absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();//获取SD卡绝对路径
//
//    }
//
//    @Override
//    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
//        JSONObject obj = args.getJSONObject(0);
//        if (action.equals(FILEUTILCOPY)){
//            //文件拷贝
//            String fileOldPath = obj.optString("fileOldPath");
//            String fileOldName = obj.optString("fileOldName");
//            String fileNewPath = obj.optString("fileNewPath");
//            String fileNewName = obj.optString("fileNewName");
//
//            Log.i(TAG,"action.equals(FILEUTILCOPY)");
//            Log.i(TAG,"fileOldPath+fileOldName "+fileOldPath+fileOldName);
//            Log.i(TAG,"fileNewPath+fileNewName "+fileNewPath+fileNewName);
//
//            //            fileUtil.makeRootDirectory(absolutePath+fileNewPath);
//            boolean copySuccess = fileUtil.copyFile(absolutePath+fileOldPath+fileOldName,absolutePath+fileNewPath+fileNewName);
//            //            Toast.makeText(cordova.getActivity(),copySuccess+"",Toast.LENGTH_SHORT).show();
//
//            callbackContext.success(new JSONObject().put("copySuccess",copySuccess));
//
//        }else if(action.equals(FILEUTILDELETE)){
//            JSONArray deleteArray = obj.getJSONArray("deleteArray");
//            //文件删除
//            Log.i(TAG,"action.equals(FILEUTILDELETE)");
//            for(int i = 0;i<deleteArray.length();i++){
//                String fileDeletePath = deleteArray.getString(i);
//
//                File file = new File(fileDeletePath);
//                if (!(file.isDirectory())){
//                    //如果删除的是具体文件则删除其父目录下全部文件
//                    //                                        fileDeletePath = Environment.getExternalStorageDirectory().getAbsolutePath() +file.getParent()+"/";
//                    File parentFile = new File(file.getParent()+"/");
//                    if(parentFile.list().length==1){
//                        fileDeletePath =  file.getParent()+"/";
//                    }
//
//                }
//                Log.i(TAG,"fileDeletePath "+absolutePath+fileDeletePath);
//                boolean deleteSuccess = fileUtil.DeleteFolder(absolutePath+fileDeletePath);
//                if (i+1 == deleteArray.length()){
//                    callbackContext.success(new JSONObject().put("deleteSuccess",deleteSuccess));
//                }
//            }
//
//        }else if (action.equals(FILEUTILRENAME)){
//            //文件重命名
//            String fileRenamePath = obj.optString("fileRenamePath");
//            String fileOldName = obj.optString("fileOldName");
//            String fileNewName = obj.optString("fileNewName");
//
//            Log.i(TAG,"action.equals(FILEUTILRENAME)");
//            Log.i(TAG,"fileRenamePath+fileOldName "+fileRenamePath+fileOldName);
//            Log.i(TAG,"fileRenamePath+fileNewName "+fileRenamePath+fileNewName);
//
//            boolean renameSuccess = fileUtil.copyFile(absolutePath+fileRenamePath+fileOldName,absolutePath+fileRenamePath+fileNewName);
//            //            Toast.makeText(cordova.getActivity(),renameSuccess+"",Toast.LENGTH_SHORT).show();
//            callbackContext.success(new JSONObject().put("renameSuccess",renameSuccess));
//
//        }else if (action.equals(FILEUTILMAKEDIR)){
//            //创建文件夹
//            String dirPath = obj.optString("dirPath");
//
//            Log.i(TAG,"action.equals(FILEUTILMAKEDIR)");
//            Log.i(TAG,"dirPath "+dirPath);
//
//            fileUtil.makeRootDirectory(absolutePath+dirPath);
//        }else if (action.equals(FILEUTILGETFILENAME)){
//            //获取文件名称
//            Log.i(TAG,"action.equals(FILEUTILMAKEDIR)");
//            String path = absolutePath+obj.optString("path");
//            String name = fileUtil.getFileName(path);
//            Log.i(TAG,"name is"+ name);
//            Log.i(TAG,"path is"+ path);
//            callbackContext.success(new JSONObject().put("name",name));
//
//        }else if(action.equals(FILEUTILGETFILELENGTH)){
//            //获取文件大小
//            Log.i(TAG,"action.equals(FILEUTILGETFILELENGTH)");
//            String path = absolutePath+obj.optString("path");
//            long length = fileUtil.getFileLength(path);
//            Log.i(TAG,"length is"+ length);
//            Log.i(TAG,"path is"+ path);
//            callbackContext.success(new JSONObject().put("length",length));
//
//        }else if (action.equals(FILEUTILGETFILE)){
//            //获取文件信息
//            Log.i(TAG,"action.equals(FILEUTILGETFILE)");
//            String path = absolutePath+obj.optString("path");
//            File file = fileUtil.getFile(path);
//            byte[] retBytes = new byte[0];
//
//            try {
//                FileInputStream fileInputStream = new FileInputStream(file); //建立数据通道
//
//                ByteArrayOutputStream output = new ByteArrayOutputStream();
//                byte[] buffer = new byte[4096];
//                int n = 0;
//                while (-1 != (n = fileInputStream.read(buffer))) {
//                    output.write(buffer, 0, n);
//                }
//
//                retBytes = output.toByteArray();
//
//                fileInputStream.close(); //关闭资源
//                output.close();
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//            Log.i(TAG,"path is"+ path);
//            JSONObject fileObj = new JSONObject();
//            fileObj.put("fileName",file.getName());
//            fileObj.put("fileLength",file.length());
//            //            fileObj.put("fileObj",file.getI());
//            fileObj.put("fileByte",retBytes);
//
//            callbackContext.success(retBytes);
//
//            //            callbackContext.success(new JSONObject().put("file",fileObj));
//
//        }else if (action.equals(FILEUTILGETALLFILE)){
//            //获取全部文件
//            Log.i(TAG,"action.equals(FILEUTILGETALLFILE)");
//            String path = obj.optString("path");
//            Log.i(TAG,"path " +path);
//            ArrayList<File> fileList = new ArrayList<>();
//            fileUtil.getAllFileNoDir(path,fileList);
//            JSONArray jsonArray = new JSONArray();
//            for(int i = 0;i<fileList.size();i++)
//            {
//                Log.i(TAG,fileList.get(i).getAbsolutePath());
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("absolutePath",fileList.get(i).getAbsolutePath());
//                jsonArray.put(i,jsonObject);
//            }
//            JSONObject backObject = new JSONObject();
//            backObject.put("fileList",jsonArray);
//            callbackContext.success(backObject);
//
//        }
//        return true;
//    }
//}

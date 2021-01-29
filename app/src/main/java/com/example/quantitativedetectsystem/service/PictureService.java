package com.example.quantitativedetectsystem.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.quantitativedetectsystem.Activity.FunctionSampleActivity;
import com.example.quantitativedetectsystem.view.MarkView;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureService {
    static String QDSDirectoryName = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM  + File.separator + "QDS" + File.separator;
    static File QDSDirectory=new File(QDSDirectoryName);

    public static final int FROM_CAMERA = 101,FROM_ALBUM = 102;
    //    如果手机指定的色彩检测保存路径不空，就创建该目录。
    static{
        if(!QDSDirectory.exists()){
            QDSDirectory.mkdirs();
        }
    }
    public static String takePhoto(FunctionSampleActivity functionSampleActivity){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        File photoFile = new File(QDSDirectory,fileName);
        Uri uri = Uri.fromFile(photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        functionSampleActivity.startActivityForResult(intent, FROM_CAMERA);
        return photoFile.getAbsolutePath();
    }
    public static void pickPhoto(FunctionSampleActivity functionSampleActivity){
        Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("/storage/emulated/0/DCIM/Camera/"));
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        functionSampleActivity.startActivityForResult(intent, FROM_ALBUM);
    }
    public static Bitmap getSmallBitmap(String filePath, int imageWidth, int imageHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);//自定义一个宽和高
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);//返回复制的可编辑的bitmap
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;//获取图片的高
        final int width = options.outWidth;//获取图片的框
        int inSampleSize = 4;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;//求出缩放值
    }

    //分析数据
    public static int[] analyse(Bitmap bitmap, MarkView mark){
        int[] pixels = new int[mark.getWidth()];
        int[] result = new int[mark.getHeight()];
        for (int i = (int) mark.getY(), index = 0; i < mark.getY()+mark.getHeight(); i++, index++){
            bitmap.getPixels(pixels,0,mark.getWidth(), (int) mark.getX(),i,mark.getWidth(),1);
            int length = 1, AGray = 0;
            for (int j = 0;j < pixels.length;j++){
                double Gray;
                if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE)
                     Gray = Color.red(pixels[j]);
                else{
                    Gray = (255-Color.green(pixels[j]))/2+(255-Color.blue(pixels[j]))/2;
                }
                AGray += (Gray - AGray) / length++ ;
            }
            result[index] = AGray;
        }
        return result;
    }

    public static int findIndex(int max,int[] result){
        for(int i = 0;i < result.length;i++)
            if(result[i] == max)
                return i;
        return result.length-1;
    }
    //以红色为特征值将图片转化为灰度图
    public static void greyInRed(Bitmap bitmap){
        int index = 0;
        int[] pixels = new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        for(int i = 0;i<bitmap.getWidth();i++){
            for (int j = 0;j<bitmap.getHeight();j++){
                if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
//                int Gray = Color.red(pixels[index]);
//                pixels[index] = Color.rgb(Gray,Gray,Gray);
                }
                else {
                    int R = 255-Color.red(pixels[index]);
                    int G = 255-Color.green(pixels[index]);
                    int B = 255-Color.blue(pixels[index]);
                    pixels[index] = Color.rgb(R,G,B);
                }
                index++;
            }
        }
        bitmap.setPixels(pixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
    }

    public static int[] getFeatures(int[] result){
        if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
            int[] features = new int[7];
            int leh = result.length/13;
            for(int i = leh,index = 0;i < result.length-leh && index < 6;i++){
                //判断扫描第一次范围的前半部分是否存在极值点
                if(index == 0){
                    int mayBe =getMax(cut(0,i-1,result));
                    int I = findIndex(mayBe,cut(0,i-1,result));
                    if(compare(cut(I+1,I+leh,result),mayBe)){
                        features[index++] = I;
                        continue;
                    }
                }
                if(compare(cut(i-leh,i-1,result),result[i])&& compare(cut(i+1,i+leh,result),result[i])){
                    if(index !=0 && i - features[index - 1] < leh/3*2)
                        continue;
                    features[index++] = i;
                }
            }
            //判断最后一次扫描的后半部分是否存在极值点
            int mayBe =getMax(cut(result.length-leh,result.length-1,result));
            int i;
            for(i = result.length - 1; i > result.length - leh;i--)
                if (result[i] == mayBe)
                    break;
            if(compare(cut(i-leh,i-1,result),mayBe)){
                features[features.length-1] = i;
            }
            int[] feature = new int[features.length-1];
            for(int j = 0;j < features.length-1;j++)
                feature[j] = features[j+1];
            return feature;
        }
        else {
            int[] features = new int[6];
            int leh = result.length/12;
            for(int i = leh,index = 0;i < result.length-leh && index < 6;i++){
                //判断扫描第一次范围的前半部分是否存在极值点
                if(index == 0){
                    int mayBe =getMax(cut(0,i-1,result));
                    int I = findIndex(mayBe,cut(0,i-1,result));
                    if(compare(cut(I+1,I+leh,result),mayBe)){
                        features[index++] = I;
                        continue;
                    }
                }
                if(compare(cut(i-leh,i-1,result),result[i])&& compare(cut(i+1,i+leh,result),result[i])){
                    if(index !=0 && i - features[index - 1] < leh/3*2)
                        continue;
                    features[index++] = i;
                }
            }
            return features;
        }
        //判断最后一次扫描的后半部分是否存在极值点
//        int mayBe =getMax(cut(result.length-leh,result.length-1,result));
//        int i;
//        for(i = result.length - 1; i > result.length - leh;i--)
//            if (result[i] == mayBe)
//                break;
//        if(compare(cut(i-leh,i-1,result),mayBe)){
//            features[features.length-1] = i;
//        }
    }
    public static int[] cut(int start,int end,int[] result){
        if(end < start){
            int[] error = new int[0];
            return error;
        }
        int[] sub = new int[end-start];
        for(int i = start;i < end;i++)
            sub[i-start] = result[i];
        return sub;
    }
    public static boolean compare(int[] sub,int max){
        for(int i = 0;i < sub.length;i++)
            if(max < sub[i])
                return false;
        return true;
    }
    public static int getMax(int[] points){
        int max = 0;
        for(int num:points){
            if(num > max)
                max = num;
        }
        return max;
    }
}

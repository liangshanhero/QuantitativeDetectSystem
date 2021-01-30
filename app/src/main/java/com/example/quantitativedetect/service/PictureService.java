package com.example.quantitativedetect.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.quantitativedetect.Activity.FunctionSampleActivity;
import com.example.quantitativedetect.domain.Line;
import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.view.MarkView;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static Mark analyse(Bitmap bitmap, MarkView markView){
        Mark mark =new Mark();
        int width = markView.getWidth();
        int height = markView.getHeight();
        int lines = width/mark.getLineWidthPixelQuantity();

        int[] pixels = new int[width];

        for(int i=(int) markView.getY(); i<markView.getY()+ height;i++){
            Line line = new Line();
            int length = 1, lineAvegGray = 0;
            bitmap.getPixels(pixels,0,markView.getWidth(), (int) markView.getX(),i,markView.getWidth(),1);


            for (int j=0;j<pixels.length;j++){
                double tmpGray;
                if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE)
                    tmpGray = Color.red(pixels[j]);
                else{
                    tmpGray = (255-Color.green(pixels[j]))/2+(255-Color.blue(pixels[j]))/2;
                }
                lineAvegGray += (tmpGray - lineAvegGray) / length++ ;
            }

            line.setGray(lineAvegGray);
            mark.getLineList().add(line);
        }
        return mark;
    }



//找到特征行
    public static int findIndex(int maxGray,List<Line> lineList){
        for(int i = 0;i < lineList.size();i++)
            if(lineList.get(i).getGray() == maxGray)
                return i;
        return lineList.size()-1;
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

    public static Mark getFeatures(/*int[] result*/Mark mark){
        if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
            int[] features = new int[7];
            List<Line> tempFeatureLineList = new ArrayList<>();
//将整个mark的分为13份
            int stepLength = mark.getLineList().size()/13;//result.length/13;
            for(int i = stepLength,index = 0;i < mark.getLineList().size()-stepLength && index < 6;index++,i++){
//            for(int i = length,index = 0;i < result.length-length && index < 6;i++){
                //判断扫描第一次范围的前半部分是否存在极值点
                if(index == 0){
                    int mayBeMaxGray = getMax(cut(0,i-1,mark.getLineList()));

                    int maxGrayLineIndex = findIndex(mayBeMaxGray,cut(0,i-1,mark.getLineList()));

                    if(compare(cut(maxGrayLineIndex+1,maxGrayLineIndex+stepLength,mark.getLineList()),mayBeMaxGray)){
                        features[index++] = maxGrayLineIndex;
                        continue;
                    }
                }
                if(compare(cut(i-stepLength,i-1,mark.getLineList()),mark.getLineList().get(i).getGray())&& compare(cut(i+1,i+stepLength,mark.getLineList()),mark.getLineList().get(i).getGray())){
                    if(index !=0 && i - features[index - 1] < stepLength/3*2)
                        continue;
//                    if(index !=0 && i - mark.getLineList().indexOf(mark.getFeatureLineList().get(index-1)) < length/3*2)
//                        continue;
                    features[index++] = i;
//                    tempFeatureLineList.add(mark.getLineList().get(i));
                }
            }
            //判断最后一次扫描的后半部分是否存在极值点
            int mayBeMaxGray =getMax(cut(mark.getLineList().size()-stepLength,mark.getLineList().size()-1,mark.getLineList()));
            int i;
            for(i = mark.getLineList().size() - 1; i > mark.getLineList().size() - stepLength;i--){
                if (mark.getLineList().get(i).getGray() == mayBeMaxGray)
                    break;
            }

            if(compare(cut(i-stepLength,i-1,mark.getLineList()),mayBeMaxGray)){
//                features[features.length-1] = i;
                mark.getFeatureLineList().add(mark.getLineList().get(i));
            }
//            int[] feature = new int[features.length-1];
//            for(int j = 0;j < features.length-1;j++)
//                feature[j] = features[j+1];
//            return feature;

            for(int j = 0;j < features.length-1;j++) {
                mark.getLineList().get(j + 1).setFeaturte(true);
                mark.getFeatureLineList().add(mark.getLineList().get(features[j + 1]));
            }

            return mark;
        }
        else {
            int[] features = new int[6];
            int stepLength = mark.getLineList().size()/12;
            for(int i = stepLength,index = 0;i < mark.getLineList().size()-stepLength && index < 6;i++){
                //判断扫描第一次范围的前半部分是否存在极值点
                if(index == 0){
                    int mayBeMaxGray =getMax(cut(0,i-1,mark.getLineList()));
                    int maxGrayLineIndex = findIndex(mayBeMaxGray,cut(0,i-1,mark.getLineList()));
                    if(compare(cut(maxGrayLineIndex+1,maxGrayLineIndex+stepLength,mark.getLineList()),mayBeMaxGray)){
                        features[index++] = maxGrayLineIndex;
                        continue;
                    }
                }
                if(compare(cut(i-stepLength,i-1,mark.getLineList()),mark.getLineList().get(i).getGray())&& compare(cut(i+1,i+stepLength,mark.getLineList()),mark.getLineList().get(i).getGray())){
                    if(index !=0 && i - features[index - 1] < stepLength/3*2)
                        continue;
                    features[index++] = i;
                }
            }
            for(int j = 0;j < features.length;j++) {
                mark.getLineList().get(j).setFeaturte(true);
                mark.getFeatureLineList().add(mark.getLineList().get(features[j]));
            }
            return mark;
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
    public static List<Line> cut(int start, int end, List<Line> lineList){
        if(end < start){
//            int[] error = new int[0];
//            return error;
            return new ArrayList<>();
        }
        int[] sub = new int[end-start];
        List<Line> cutLineList = new ArrayList<>();
        for(int i = start;i < end;i++){
            cutLineList.add(lineList.get(i));
        }
        return cutLineList;
//            sub[i-start] = result[i];
//        return sub;
    }
    public static boolean compare(List<Line> lineList,int maxGray){
        for(int i = 0;i < lineList.size();i++)
            if(maxGray < lineList.get(i).getGray())
                return false;
        return true;
    }
    public static int getMax(List<Line> lineList){
        int maxGray = 0;
        for(Line line:lineList){
            if(line.getGray() > maxGray)
                maxGray = line.getGray();
        }
        return maxGray;
    }
}

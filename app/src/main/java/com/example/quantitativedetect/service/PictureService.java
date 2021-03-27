package com.example.quantitativedetect.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.quantitativedetect.Activity.FunctionSampleActivity;
import com.example.quantitativedetect.domain.CheckPanel;
import com.example.quantitativedetect.domain.Line;
import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.view.CheckPanelView;
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
//    图片适应屏幕分辨率，将图片缩放至屏幕中imageView特定的大小
    public static Bitmap getAdaptedScreenBitmap(String filePath, int imageTargetWidth, int imageTargetHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, imageTargetWidth, imageTargetHeight);//自定义一个宽和高
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap.copy(Bitmap.Config.ARGB_8888, true);//返回复制的可编辑的bitmap
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int imageTrueHeight = options.outHeight;//获取图片的高
        final int imageTrueWidth = options.outWidth;//获取图片的框
        /*
        原来是4,即图片的长宽都缩小为原图的1/4,整个图片缩小为原来的1/16,
        为了扩大各特征线的间隔像素距离,现在改为2(即缩小1/4),可能会影响性能.
         */
        int inSampleSize = 4;
        if (imageTrueHeight > reqHeight || imageTrueWidth > reqWidth) {
            final int heightRatio = Math.round((float) imageTrueHeight/ (float) reqHeight);
            final int widthRatio = Math.round((float) imageTrueWidth / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;//求出缩放值
    }

    //分析数据，获取需要的颜色数据
    public static Mark analyse(int[] pixels,int markWidth,int adaptedY){
        Mark mark = new Mark();

        for (int i=0;i<pixels.length/markWidth;i++) {
            int length = 1,lineAvegGray = 0;
            Line line = new Line();
            double tmpGray;
            for (int j = 0; j < markWidth; j++) {
                if (FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
                    tmpGray = 255 - Color.red(pixels[i*markWidth+j]);
//                    tmpGray = (255 - Color.green(pixels[i]) + 255 - Color.blue(pixels[i]))/2;
                }
                else{
                    tmpGray = (255-Color.green(pixels[i*markWidth+j]))/2+(255-Color.blue(pixels[i*markWidth+j]))/2;
                }
                lineAvegGray += (tmpGray - lineAvegGray) / length++ ;
            }
            line.setGray(lineAvegGray);
            line.setAdaptedY(adaptedY+i);
            mark.getLineList().add(line);
        }
//TODO      2021-0222 featureLineList中的所有featureLine都是同一条Line,故gray都是一样的...待查
        return mark;
    }

    public static Mark analyse(MarkView markView){
//    public static Mark analyse(Bitmap bitmap, MarkView markView){
        Mark mark = new Mark();
        Bitmap bitmap = markView.getBitmap();

        int width = markView.getAdaptedWidth();
        int height = markView.getAdaptedHeight();
//        int width = markView.getLayoutParams().width;//.getWidth();
//        int height = markView.getLayoutParams().height;//.getHeight();

        int adaptedX = markView.getAdaptedX();
        int lines = width/mark.getLineWidthPixelQuantity();

        int[] pixels = new int[width];

        for(int i = (int) markView.getAdaptedY(); i<markView.getAdaptedY()+ height; i++){
            Line line = new Line();
            int length = 1, lineAvegGray = 0;
            bitmap.getPixels(pixels,0, width, adaptedX ,i, width,1);

            for (int j=0;j<pixels.length;j++) {
                double tmpGray;
                if (FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
                    tmpGray = 255 - Color.red(pixels[j]);
//                    tmpGray = (255 - Color.green(pixels[j]) + 255 - Color.blue(pixels[j]))/2;
                }
                else{
                    tmpGray = (255-Color.green(pixels[j]))/2+(255-Color.blue(pixels[j]))/2;
                }
                lineAvegGray += (tmpGray - lineAvegGray) / length++ ;
            }
//TODO      2021-0222 featureLineList中的所有featureLine都是同一条Line,故gray都是一样的...待查
            line.setGray(lineAvegGray);
            line.setAdaptedY(i);
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



    public static CheckPanel getFeatures(CheckPanel checkPanel) {
        if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){

            int stripeQuantity = checkPanel.getStripeQuantity();


            for (int i = 0; i < checkPanel.getMarkList().size(); i++) {
                Mark mark = checkPanel.getMarkList().get(i);
                int stripeHeight =mark.getLineList().size() / stripeQuantity;
                for (int j = 0; j < stripeQuantity; j++) {
                    List<Line> lineList;
                    if (j!=stripeQuantity-1){
                        lineList = mark.getLineList().subList(j * stripeHeight,(j+1) * stripeHeight);
                    }else{
                        lineList = mark.getLineList().subList(j * stripeHeight,mark.getLineList().size()-1);
                    }
                    Line maxGrayLine = lineList.get(0);
                    for (int k = 1; k < lineList.size(); k++) {
                        maxGrayLine = (lineList.get(k).getGray() <= maxGrayLine.getGray()) ? maxGrayLine : lineList.get(k);
                    }
                    mark.getFeatureLineList().add(maxGrayLine);
                }
            }
        }else{

        }

        return checkPanel;
    }
    public static Mark getFeatures(/*int[] result*/Mark mark){
        if(FunctionSampleActivity.CHECK_MODE == FunctionSampleActivity.FLUORESCENT_MICROSPHERE){
            List<Line> tempFeatureLineList = new ArrayList<>();
//            遍历mark的LineList，将其中灰度为峰值的Line加入到tempLineList，以备后续操作。
            for (int i = 0; i <mark.getLineList().size()-1; i++) {
                if (i==0) {
                    if (mark.getLineList().get(i).getGray() >= mark.getLineList().get(i+1).getGray()){
                        mark.getLineList().get(i).setValid(true);
                        tempFeatureLineList.add(mark.getLineList().get(i));
                    }
                }else {
                    if (mark.getLineList().get(i-1).getGray() < mark.getLineList().get(i).getGray() && mark.getLineList().get(i).getGray() >= mark.getLineList().get(i+1).getGray() ){
                        mark.getLineList().get(i).setValid(true);
                        tempFeatureLineList.add(mark.getLineList().get(i));
                    }
                }
            }
            /*
            对得到的tempList进行剔除，将相隔近的几条线放入一个分区，以备后续剔除，最终得到与图片相符的feature个数
            startIndex:特征区的开始索引列表
            endIndex:特征区的结束索引列表
            剔除操作：从tempList中取出startIndex（含）与endIndex（含）之间的行，然后将其中灰度值最大的行放入mark.featureLineList
            TODO 还有bug，
             */
//            List<Integer> startIndex = new ArrayList<>();
//            List<Integer> endIndex = new ArrayList<>();
//            for (int i = 0; i < tempFeatureLineList.size()-1; i++) {
////                TODO +4是因为所属不同的Tline（featureLine）的tempFeatureLine之间的间距，会比属于同一条Tline的featureLine之间的间距更大，
////                     但是间隔究竟取多大，还需要进一步确定（不想写成固定值，最好还是根据情况而定）
//                if (mark.getLineList().indexOf(tempFeatureLineList.get(i)) + 5 < mark.getLineList().indexOf(tempFeatureLineList.get(i+1))){
//                    if (endIndex.isEmpty()){
//                        startIndex.add(0);
//                        endIndex.add(i);
//                    }else {
//                        startIndex.add(endIndex.get(endIndex.size()-1)+1);
//                        endIndex.add(i);
//                    }
//                }
//            }
////            如果endIndex与startIndex不等长，则将tempList的最后一个的索引值放入endIndex
//            if (endIndex.size()!=startIndex.size()){
//                endIndex.add(tempFeatureLineList.size()-1);
//            }
//            for (int i = 0; i < startIndex.size(); i++) {
//                if (startIndex.get(i)==endIndex.get(i) && i!=startIndex.size()-1){
//                    startIndex.set(i+1,startIndex.get(i));
//                }else if (i==startIndex.size()-1) {
//                    mark.getFeatureLineList().add(tempFeatureLineList.get(i));
//                }else{
//                    mark.getFeatureLineList().add(getMaxGrayLine(cut(startIndex.get(i),endIndex.get(i),tempFeatureLineList)));
//                }
//
//            }


            mark.setFeatureLineList(tempFeatureLineList);
            /*
//            features[0]表示为试纸中的CLine(基准线)，即featureLine除开CLine以外，一共只有6条
            int[] features = new int[7];
//          将整个mark的分为13份
            int stepLength = mark.getLineList().size()/13;//result.length/13;
//          整个循环只有第一次能正常给到features（CLine）
            for(int i = stepLength,index = 0;i < mark.getLineList().size()-stepLength && index < 6;index++,i++){
//            for(int i = length,index = 0;i < result.length-length && index < 6;i++){
//                i和index的作用是？？
//                获取基准线
                if(index == 0){
                    Line maybeMaxGrayLine = getMaxLineGray(cut(0,i-1,mark.getLineList()));
                    int maxGrayLineIndex = mark.getLineList().indexOf(maybeMaxGrayLine);

                    if(isMaxInList(cut(maxGrayLineIndex+1,maxGrayLineIndex+stepLength,mark.getLineList()),maybeMaxGrayLine.getGray())){
                        features[index++] = maxGrayLineIndex;
                        continue;
                    }
                }
                if(isMaxInList(cut(i-stepLength,i-1,mark.getLineList()),mark.getLineList().get(i).getGray())&& isMaxInList(cut(i+1,i+stepLength,mark.getLineList()),mark.getLineList().get(i).getGray())){
                    if(index !=0 && i - features[index - 1] < stepLength/3*2)
                        continue;
//                    if(index !=0 && i - mark.getLineList().indexOf(mark.getFeatureLineList().get(index-1)) < length/3*2)
//                        continue;
                    features[index++] = i;
//                    tempFeatureLineList.add(mark.getLineList().get(i));
                }
            }


//            判断最后一次扫描的后半部分是否存在极值点
//            int mayBeMaxGray = getMaxLineGray(cut(mark.getLineList().size()-stepLength,mark.getLineList().size()-1,mark.getLineList()));
            Line mayBeMaxGrayLine = getMaxLineGray(cut(mark.getLineList().size()-stepLength,mark.getLineList().size()-1,mark.getLineList()));
            int i;
//          TODO 2021-0304 ？？这一个循环的作用是？
            for(i = mark.getLineList().size() - 1; i > mark.getLineList().size() - stepLength;i--){
                if (mark.getLineList().get(i).getGray() == mayBeMaxGrayLine.getGray())
                    break;
            }
            if(isMaxInList(cut(i-stepLength,i-1,mark.getLineList()),mayBeMaxGrayLine.getGray())){
//                features[features.length-1] = i;
                mark.getFeatureLineList().add(mark.getLineList().get(i));
            }
//            以下四行由后四行代替
//            int[] feature = new int[features.length-1];
//            for(int j = 0;j < features.length-1;j++)
//                feature[j] = features[j+1];
//            return feature;
            for(int j = 0;j < features.length-1;j++) {
                mark.getFeatureLineList().add(mark.getLineList().get(features[j + 1]));
            }*/
            return mark;
        }


        else {
            int[] features = new int[6];
            int stepLength = mark.getLineList().size()/12;
            for(int i = stepLength,index = 0;i < mark.getLineList().size()-stepLength && index < 6;i++){
                //判断扫描第一次范围的前半部分是否存在极值点
                if(index == 0){
//                    int mayBeMaxGray = getMaxLineGray(cut(0,i-1,mark.getLineList()));
                    Line mayBeMaxGrayLine = getMaxGrayLine(cut(0,i-1,mark.getLineList()));
                    int maxGrayLineIndex = findIndex(mayBeMaxGrayLine.getGray(),cut(0,i-1,mark.getLineList()));
                    if(isMaxInList(cut(maxGrayLineIndex+1,maxGrayLineIndex+stepLength,mark.getLineList()),mayBeMaxGrayLine.getGray())){
                        features[index++] = maxGrayLineIndex;
                        continue;
                    }
                }
                if(isMaxInList(cut(i-stepLength,i-1,mark.getLineList()),mark.getLineList().get(i).getGray())&& isMaxInList(cut(i+1,i+stepLength,mark.getLineList()),mark.getLineList().get(i).getGray())){
                    if(index !=0 && i - features[index - 1] < stepLength/3*2)
                        continue;
                    features[index++] = i;
                }
            }
            for(int j = 0;j < features.length;j++) {

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
////            sub[i-start] = result[i];
//        return sub;
    }
    public static boolean isMaxInList(List<Line> lineList, int maxGray){
        for(int i = 0;i < lineList.size();i++)
            if(maxGray < lineList.get(i).getGray())
                return false;
        return true;
    }
    public static Line/*int*/ getMaxGrayLine(List<Line> lineList){
        Line maxGrayLine=lineList.get(0);
        int maxGray = 0;
        for(Line line:lineList){
            if(line.getGray() > maxGray){
                maxGray = line.getGray();
                maxGrayLine = line;
            }
        }
        return maxGrayLine;
//        return maxGray;
    }
}

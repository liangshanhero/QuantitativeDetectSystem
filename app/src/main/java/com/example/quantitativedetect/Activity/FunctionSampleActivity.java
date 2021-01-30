package com.example.quantitativedetect.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.quantitativedetect.R;

import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.listener.MoveOnTouchListener;
import com.example.quantitativedetect.service.PictureService;
import com.example.quantitativedetect.view.MarkView;

import java.util.ArrayList;

import static com.example.quantitativedetect.service.PictureService.FROM_ALBUM;
import static com.example.quantitativedetect.service.PictureService.FROM_CAMERA;
import static com.example.quantitativedetect.service.PictureService.getFeatures;
import static com.example.quantitativedetect.service.PictureService.getSmallBitmap;

public class FunctionSampleActivity extends MainActivity {

    public static final String FUNCTION_FIRST_SAMPLE = "FirstSample";
    public static final String FUNCTION_SECOND_SAMPLE = "SecondSample";
    public static final String FUNCTION_CHECK = "Check";
    public static final int COLLOIDAL_GOLD = 10086;
    public static final int FLUORESCENT_MICROSPHERE = 10087;
    public static int CHECK_MODE = 10086;
    private String function = "FirstSample";
    private String testPath = "/storage/emulated/0/tencent/qq_images/61c6497fa1685240.jpg";
    private Bitmap bitmap;
    private ImageView imageView;
    private String picturePath;
    private RelativeLayout relativeLayout;
    private int markId = 999;     //给每一个标识圈添加一个Id
    private MoveOnTouchListener moveOnTouchListener;
    private ArrayList<MarkView> markViews = new ArrayList<MarkView>();
    private SeekBar seekBarW, seekBarH;
    private int selectingID = 0;
    private Button takePicture;
    private int imageWidth;
    private int imageHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_sample);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        init();
    }

    private void init(){
        Intent intent = getIntent();
        function = intent.getStringExtra("function");
        imageWidth = MainActivity.screenWidth;
        imageHeight = screenWidth*5/4;
        relativeLayout = findViewById(R.id.relative_layout);
        takePicture = findViewById(R.id.take_picture);
        moveOnTouchListener = new MoveOnTouchListener(this);
        imageViewInit();
        seekBarInit();
        if(function.equals(FUNCTION_CHECK)){
            Button buttona = findViewById(R.id.button_add);
            Button buttond = findViewById(R.id.button_det);
            buttona.setVisibility(View.INVISIBLE);
            buttond.setVisibility(View.INVISIBLE);
        }
//        bitmap = getSmallBitmap(testPath,imageWidth,imageHeight);
//        greyInRed(bitmap);
//        imageView.setImageBitmap(bitmap);
//        addMark(300,500,70,350);//测试用特征框
    }
    private void imageViewInit(){
        imageView = findViewById(R.id.show_sample);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = imageWidth;
        layoutParams.height = imageHeight;
        imageView.setLayoutParams(layoutParams);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSelectingID(0);
                return true;
            }
        });
    }
    public void mountBitmap(){
        //压缩拍摄到的图片以便在屏幕上显示
        bitmap = getSmallBitmap(picturePath,imageWidth,imageHeight);
        this.imageHeight = imageView.getHeight();
        MoveOnTouchListener.setImageHeight(imageHeight);
//        greyInRed(bitmap);
        imageView.setImageBitmap(bitmap);
        addMark(300,500,70,350);//测试用特征框
    }
    private void seekBarInit(){
        seekBarH = findViewById(R.id.seekBar_h);
        seekBarW = findViewById(R.id.seekBar_w);
        seekBarW.setProgress(10);
        seekBarH.setProgress(10);
        seekBarW.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int unit = imageWidth / 150;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(selectingID !=0 ){
                    MarkView markView = relativeLayout.findViewById(selectingID);
                    //设置宽度最小值为10
                    if(unit*progress <= 10)
                        return;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.width = progress*unit;  //设置宽高
                    layoutParams.height = markView.getHeight();
                    layoutParams.leftMargin = markView.getLeft();
                    layoutParams.topMargin = markView.getTop();
                    markView.setLayoutParams(layoutParams);
//                    markView.setWidth(progress*unit);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarH.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int unit = imageHeight / 150;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(selectingID !=0 ){
                    MarkView markView = relativeLayout.findViewById(selectingID);
                    //设置宽度最小值为10
                    if(progress*unit <=10)
                        return;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.width = markView.getWidth();  //设置宽高
                    layoutParams.height = progress*unit;
                    layoutParams.leftMargin = markView.getLeft();
                    layoutParams.topMargin = markView.getTop();
                    markView.setLayoutParams(layoutParams);
                    //markView.setHeight(progress*unit);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //采样框在图片中的坐标及宽高
    public MarkView getMark(MarkView markView){
        float rateX = markView.getX()/imageWidth;
        int x = (int)(bitmap.getWidth()*rateX);
        float rateWidth = (float) markView.getWidth()/imageWidth;
        int width = (int)(bitmap.getWidth()*rateWidth);
        float rateY = markView.getY()/imageHeight;
        int y = (int)(bitmap.getHeight()*rateY);
        float rateHeight = (float)markView.getHeight()/imageHeight;
        int height = (int)(bitmap.getHeight()*rateHeight);
        return new MarkView(this,width,height);
    }
    public void onSelected(int ID){
        MarkView markView = relativeLayout.findViewById(ID);
        markView.onSelected();
//        seekBarH.setProgress(markView.getHeight()/screenHeight*150);
//        seekBarW.setProgress(markView.getWidth()/screenWidth*150);
    }
    public void offSelected(int ID){
        MarkView markView = (MarkView)relativeLayout.findViewById(ID);
        markView.offSelected();
    }

    public RelativeLayout getRelativeLayout(){
        return this.relativeLayout;
    }
    public void setSelectingID(int viewId){
        if (selectingID !=0 )
            offSelected(selectingID);
        this.selectingID = viewId;
        if(viewId != 0)
            onSelected(viewId);
    }
    //添加标识圈
    public void addMark(int x, int y, int width, int height){

        View testView = new View(this);



        MarkView markView = new MarkView(this);
        markView.setId(markId++);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = width;  //设置宽高
        layoutParams.height = height;
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        markView.setLayoutParams(layoutParams);
        markView.setOnTouchListener(moveOnTouchListener);
        relativeLayout.addView(markView);

        testView.setLayoutParams(layoutParams);

        relativeLayout.addView(testView);


        markViews.add(markView);
        setSelectingID(markView.getId());
    }

    public void addMarkView(View view){
        addMark(200,200,70,120);
    }
    public void deleteMarkView(View view){
        if(selectingID != 0){
            MarkView markView = relativeLayout.findViewById(selectingID);
            relativeLayout.removeView(markView);
            for(int i = 0;i < markViews.size();i++){
                if(markViews.get(i) == markView){
                    markViews.remove(i);
                    break;
                }
            }
            selectingID = 0;
        }
    }
    private void sortMark(){
        for(int i = 0;i < markViews.size() - 1;i++)
            for (int j = 0;j < markViews.size() - i - 1;j ++){
                if(markViews.get(j).getX() > markViews.get(j+1).getX()){
                    MarkView markView = markViews.remove(j+1);
                    markViews.add(j,markView);
                }
            }
    }
    public void loadPicture(View view){
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle("Please choose a way to get the picture. ");
        //选择获取照片的方式
        String[] getPictures = {"Photograph","Photo Album"};
        listDialog.setItems(getPictures, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        picturePath = PictureService.takePhoto(FunctionSampleActivity.this);
                        break;
                    case 1:
                        PictureService.pickPhoto(FunctionSampleActivity.this);
                        break;
                }
            }
        });
        listDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        listDialog.show();
    }

//    获取灰度
    public void analyse(){
        Mark mark = null;


        for(int i = 0;i < markViews.size();i++){
            MarkView markView = getMark(markViews.get(i));//应该再MarkView中建立该方法
            mark = PictureService.analyse(bitmap, markView);
        }
//        跳转到fit界面
        Intent intent = new Intent(this,FunctionFittingActivity.class);
        intent.putExtra("length",markViews.size());
        for (int i = 0;i < markViews.size();i++){
            String str = "points" + String.valueOf(i);
            intent.putExtra(str, mark);
        }
        startActivity(intent);
    }

    public void secondSample(){
        Mark mark =null;

        for(int i = 0;i < markViews.size();i++){
            MarkView markView = getMark(markViews.get(i));
            mark = PictureService.analyse(bitmap, markView);
        }
        Intent intent = new Intent();
        intent.putExtra("length",markViews.size());
        for (int i = 0;i < markViews.size();i++){
            String str = "points" + String.valueOf(i);
            intent.putExtra(str, mark);
        }
        setResult(RESULT_OK,intent);
        this.finish();
    }

    public void detect(){

        Mark mark = PictureService.analyse(bitmap, getMark(markViews.get(0)));

/*TODO 2021-0129 mark的特征需要封装，*/
        //对mark中的featureList进行封装
        mark = getFeatures(mark);

        //mark.setFeatureIndexOnDotrowIndex(getFeatures(points));
        Intent intent = new Intent(this,FunctionInputDataActivity.class);
        intent.putExtra("function","check");
        intent.putExtra("length",5);
        intent.putExtra("VirginPoint",mark);
        startActivity(intent);
        this.finish();
    }

//    照片中选取了mark后，获取mark的灰度
    public void next(View view){
        sortMark();
        if(function.equals(FUNCTION_FIRST_SAMPLE))
//            if(markViews.size() < 3){
//                Toast.makeText(this,"样本数量不足以建立标曲，请重试",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else
                analyse();
        else if(function.equals(FUNCTION_SECOND_SAMPLE))
            secondSample();
        else if(function.equals(FUNCTION_CHECK))
            detect();
    }




    //根据Uri获取图片文件的真实路径
    public String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case FROM_CAMERA:
                    mountBitmap();
                    takePicture.setVisibility(View.INVISIBLE);
                    break;
                case FROM_ALBUM:
                    Uri uri = data.getData();
                    if (uri != null) {
                        picturePath = getRealPathFromURI(uri);
                        Log.w("Result",picturePath);
//                        boardFile = new File(path);
//                        picturePath = boardFile.getAbsolutePath();
                        mountBitmap();
                    }
                    takePicture.setVisibility(View.INVISIBLE);
                    break;
                default: break;
            }
    }
    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public static void setCheckMode(int mode){
        CHECK_MODE = mode;
    }
}

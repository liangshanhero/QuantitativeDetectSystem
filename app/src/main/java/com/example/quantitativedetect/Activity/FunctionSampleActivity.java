package com.example.quantitativedetect.Activity;

import android.Manifest;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.example.quantitativedetect.R;

import com.example.quantitativedetect.domain.CheckPanel;
import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.listener.MoveOnTouchListener;
import com.example.quantitativedetect.service.PictureService;
import com.example.quantitativedetect.view.CheckPanelView;
import com.example.quantitativedetect.view.MarkView;

import java.util.ArrayList;

import static com.example.quantitativedetect.service.PictureService.FROM_ALBUM;
import static com.example.quantitativedetect.service.PictureService.FROM_CAMERA;
import static com.example.quantitativedetect.service.PictureService.getFeatures;
import static com.example.quantitativedetect.service.PictureService.getAdaptedScreenBitmap;

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
    private int checkPanelViewId = 999;
    private MoveOnTouchListener moveOnTouchListener;
    private ArrayList<MarkView> markViews = new ArrayList<MarkView>();
    private CheckPanelView checkPanelView;
    private SeekBar widthSeekBar, heightSeekBar, markGapSeekBar;
    private int selectingID = 0;
    private Button takePicture;
    private int imageDisplayAreaWidth;
    private int imageDisplayAreaHeight;
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
        imageDisplayAreaWidth = MainActivity.screenWidth;
        imageDisplayAreaHeight = screenWidth*5/4;
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
        layoutParams.width = imageDisplayAreaWidth;
        layoutParams.height = imageDisplayAreaHeight;
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
        //压缩的似乎仅仅是显示的图片
        //压缩拍摄到的图片以便在屏幕上显示
        bitmap = getAdaptedScreenBitmap(picturePath, imageDisplayAreaWidth, imageDisplayAreaHeight);
        this.imageDisplayAreaHeight = imageView.getHeight();
        MoveOnTouchListener.setImageHeight(imageDisplayAreaHeight);
//        greyInRed(bitmap);
        imageView.setImageBitmap(bitmap);
//        seekBar与MarkView的宽高对应
        widthSeekBar.setProgress(96);
        heightSeekBar.setProgress(40);
        markGapSeekBar.setProgress(1);

//        addMarkView(new View(this));
        addCheckPanelView(new View(this));
//        似乎没有什么用，暂时屏蔽
//        addMark(300,500,70,350);//测试用特征框
    }

    private void seekBarInit() {
        markGapSeekBar = findViewById(R.id.gap_seekBar);
        heightSeekBar = findViewById(R.id.height_seekBar);
        widthSeekBar = findViewById(R.id.width_seekBar);
        markGapSeekBar.setProgress(0);
        widthSeekBar.setProgress(0);
        heightSeekBar.setProgress(0);

        markGapSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //            private int unit = imageDisplayAreaHeight / 150;
            private int unit = imageDisplayAreaHeight / 100;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (selectingID != 0) {
//                    MarkView markView = relativeLayout.findViewById(selectingID);
                    CheckPanelView checkPanelView = relativeLayout.findViewById(selectingID);
                    int maxGapWidth = (checkPanelView.getWidth() - checkPanelView.getMarkQuantity() * 10) / (checkPanelView.getMarkQuantity() - 1);//假设10为mark的最小宽度
                    checkPanelView.setMarkGap(maxGapWidth * progress / 100);
                    checkPanelView.setLayoutParams(checkPanelView.getLayoutParams());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int unit = imageDisplayAreaWidth / 100;//原来是/150

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (selectingID != 0) {
//                    MarkView markView = relativeLayout.findViewById(selectingID);
                    CheckPanelView checkPanelView = relativeLayout.findViewById(selectingID);
                    //设置宽度最小值为10
                    if (progress * imageDisplayAreaWidth / 100 <= 10)
                        return;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.width = progress * imageDisplayAreaWidth / 100;  //设置宽高
                    layoutParams.height = checkPanelView.getHeight();
                    layoutParams.leftMargin = checkPanelView.getLeft();
                    layoutParams.topMargin = checkPanelView.getTop();
                    checkPanelView.setLayoutParams(layoutParams);
//                    layoutParams.height = markView.getHeight();
//                    layoutParams.leftMargin = markView.getLeft();
//                    layoutParams.topMargin = markView.getTop();
//                    markView.setLayoutParams(layoutParams);
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

        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //            private int unit = imageDisplayAreaHeight / 150;
            private int unit = imageDisplayAreaHeight / 100;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (selectingID != 0) {
//                    MarkView markView = relativeLayout.findViewById(selectingID);
                    CheckPanelView checkPanelView = relativeLayout.findViewById(selectingID);
                    //设置宽度最小值为10
                    if (progress * imageDisplayAreaHeight / 100 <= 10)
                        return;
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.width = checkPanelView.getWidth();  //设置宽高
                    layoutParams.height = progress * imageDisplayAreaHeight / 100;
                    layoutParams.leftMargin = checkPanelView.getLeft();
                    layoutParams.topMargin = checkPanelView.getTop();
                    checkPanelView.setLayoutParams(layoutParams);
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
    public MarkView getAdaptedMark(MarkView markView){

//      2021-0130图片压缩，MarkView也要压缩！！！压缩Mark图片
        float rateX = markView.getX()/ imageDisplayAreaWidth;
        int adaptedX = (int)(bitmap.getWidth()*rateX);
        float rateWidth = (float) markView.getWidth()/ imageDisplayAreaWidth;
        int adaptedWidth = (int)(bitmap.getWidth()*rateWidth);

        float rateY = markView.getY()/ imageDisplayAreaHeight;
        int adaptedY = (int)(bitmap.getHeight()*rateY);
        float rateHeight = (float)markView.getHeight()/ imageDisplayAreaHeight;
        int adaptedHeight = (int)(bitmap.getHeight()*rateHeight);

        ViewGroup.LayoutParams layoutParams = markView.getLayoutParams();

//        将MarkView对应在bitmap中的位置进行缩放，便于后续操作，但是这样操作后，按下“next”键时，
//        显示的markview会缩小、错位，得到的灰度数据似乎是正常的

        markView.setAdaptedX(adaptedX);
        markView.setAdaptedY(adaptedY);
        markView.setAdaptedWidth(adaptedWidth);
        markView.setAdaptedHeight(adaptedHeight);
//        layoutParams.width = width;
//        layoutParams.height = height;
//        markView.setLayoutParams(layoutParams);

        return markView;
    }
    public void onSelected(int ID){
        CheckPanelView checkPanelView = relativeLayout.findViewById(ID);
        checkPanelView.onSelecet();
//        MarkView markView = relativeLayout.findViewById(ID);
//        markView.onSelected();

        widthSeekBar.setProgress(checkPanelView.getLayoutParams().width*100/imageDisplayAreaWidth);
        heightSeekBar.setProgress(checkPanelView.getLayoutParams().height*100/imageDisplayAreaHeight);
//        seekBarWidth.setProgress(markView.getLayoutParams().width/(imageDisplayAreaWidth/100));
//        seekBarHeight.setProgress(markView.getLayoutParams().height/(imageDisplayAreaHeight/100));

//        seekBarH.setProgress(markView.getHeight()/screenHeight*150);
//        seekBarW.setProgress(markView.getWidth()/screenWidth*150);
    }
    public void offSelected(int ID){
//        MarkView markView = (MarkView)relativeLayout.findViewById(ID);
        CheckPanelView checkPanelView = (CheckPanelView) relativeLayout.findViewById(ID);
        checkPanelView.offSelected();
//        markView.offSelected();
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

    private void addCheckPanelView(View view) {
//        View tempView = new View(this);
        CheckPanelView tempCheckPanelView = new CheckPanelView(this);
        tempCheckPanelView.setBitmap(bitmap);
        tempCheckPanelView.setId(checkPanelViewId);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = (widthSeekBar.getProgress()*imageDisplayAreaWidth)/100;
        layoutParams.height = (heightSeekBar.getProgress()*imageDisplayAreaHeight)/100;
        layoutParams.leftMargin = 10;
        layoutParams.topMargin = 100;


        tempCheckPanelView.setLayoutParams(layoutParams);
        int maxGapWidth = (tempCheckPanelView.getWidth()-tempCheckPanelView.getMarkQuantity()*10)/(tempCheckPanelView.getMarkQuantity()-1);
        tempCheckPanelView.setMarkGap(maxGapWidth*markGapSeekBar.getProgress()/100);
        tempCheckPanelView.setOnTouchListener(moveOnTouchListener);
        relativeLayout.addView(tempCheckPanelView);
//        view.setLayoutParams(layoutParams);
//        relativeLayout.addView(view);

        this.checkPanelView = tempCheckPanelView;

        setSelectingID(checkPanelView.getId());
    }
    public void deleteCheckPanelView(View view){
        if(selectingID != 0){
            CheckPanelView checkPanelView = relativeLayout.findViewById(selectingID);
            relativeLayout.removeView(checkPanelView);
//            MarkView markView = relativeLayout.findViewById(selectingID);
//            relativeLayout.removeView(markView);
//            for(int i = 0;i < markViews.size();i++){
//                if(markViews.get(i) == markView){
//                    markViews.remove(i);
//                    break;
//                }
//            }
            selectingID = 0;
        }
    }
//markView弃用，addMarkView准备删除
    public void addMarkView(View view){
        View testView = new View(this);

        MarkView markView = new MarkView(this);
        markView.setId(markId++);
        markView.setMark(new Mark());
        markView.setBitmap(bitmap);
//        TODO 2021-0316 markView的adapted* 属性放到这里来添加


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //通过获取当前seekBar的比例来设置markView的宽高
        layoutParams.width = widthSeekBar.getProgress()*(imageDisplayAreaWidth / 100);
        layoutParams.height = heightSeekBar.getProgress()*(imageDisplayAreaHeight / 100);
        layoutParams.leftMargin = 100;
        layoutParams.topMargin = 100;

//        markView.setAdaptedWidth(layoutParams.width);
//        markView.setAdaptedHeight(layoutParams.height);

        markView.setLayoutParams(layoutParams);
        markView.setOnTouchListener(moveOnTouchListener);
        relativeLayout.addView(markView);
        testView.setLayoutParams(layoutParams);

        relativeLayout.addView(testView);

        markViews.add(markView);
        setSelectingID(markView.getId());
    }
//markView弃用，deleteMarkView准备删除
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

    public void loadPicture(View view){
//      TODO 测试通过后删除,并恢复hou后续代码
        PictureService.pickPhoto(FunctionSampleActivity.this);
//        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
//        listDialog.setTitle("Please choose a way to get the picture. ");
//        //选择获取照片的方式
//        String[] getPictures = {"Photograph","Photo Album"};
//        listDialog.setItems(getPictures, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case 0:
//                        picturePath = PictureService.takePhoto(FunctionSampleActivity.this);
//                        break;
//                    case 1:
//                        PictureService.pickPhoto(FunctionSampleActivity.this);
//                        break;
//                }
//            }
//        });
//        listDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        listDialog.show();
    }

//    获取灰度
    public void analyse(){
        checkPanelView.setAdapted(imageDisplayAreaWidth,imageDisplayAreaHeight);
        CheckPanel checkPanel = new CheckPanel();
        checkPanelView.setCheckPanel(checkPanel);
        checkPanelView.cutPanelToMark();
//        CheckPanelView checkPanelView = findViewById(checkPanelViewId);
//        checkPanelView.setAdapted(imageDisplayAreaWidth,imageDisplayAreaHeight);
//        CheckPanel checkPanel = checkPanelView.cutPanelToMark();
        checkPanel.setStripeQuantity(checkPanelView.getStripeQuantity());


//        List<Mark> markList = new ArrayList<>();
//        Mark mark = null;
//        for(int i = 0;i < markViews.size();i++){
//            markViews.get(i).setAdapted(imageDisplayAreaWidth,imageDisplayAreaHeight);
//            markList.add(PictureService.analyse(markViews.get(i)));
////            MarkView markView = markViews.get(i);
////            MarkView markView = getAdaptedMark(markViews.get(i));//应该在MarkView中建立该方法
////            mark = PictureService.analyse(bitmap, markView);
//        }
//        跳转到fit界面
        Intent intent = new Intent(this,FunctionFittingActivity.class);
        intent.putExtra("length",checkPanel.getMarkList().size());
//        for (int i = 0;i < markViews.size();i++){
//            String str = "mark" + String.valueOf(i);
//            intent.putExtra(str, markList.get(i));
//        }
        intent.putExtra("checkPanel", checkPanel);
        startActivity(intent);
    }

    public void secondSample(){
        Mark mark =null;

        for(int i = 0;i < markViews.size();i++){
            markViews.get(i).setAdapted(imageDisplayAreaWidth,imageDisplayAreaHeight);
            mark = PictureService.analyse(markViews.get(i));
//            MarkView markView = getAdaptedMark(markViews.get(i));
////            mark = PictureService.analyse(bitmap, markView);
//            mark = PictureService.analyse(markView);
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
        markViews.get(0).setAdapted(imageDisplayAreaWidth,imageDisplayAreaHeight);
        Mark mark = PictureService.analyse(markViews.get(0));
//        Mark mark = PictureService.analyse(bitmap, getMark(markViews.get(0)));

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
//        根据markView的横坐标(X)对markViews列表进行排序,X小的在前
//        Collections.sort(markViews);

        if(function.equals(FUNCTION_FIRST_SAMPLE))
//            if(markViews.size() < 3){
//                Toast.makeText(this,"样本数量不足以建立标曲，请重试",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else
                analyse();
        else if(function.equals(FUNCTION_SECOND_SAMPLE)) {
            secondSample();
        }
        else if(function.equals(FUNCTION_CHECK)) {
            detect();
        }
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
    public int getImageDisplayAreaWidth() {
        return imageDisplayAreaWidth;
    }

    public int getImageDisplayAreaHeight() {
        return imageDisplayAreaHeight;
    }

    public static void setCheckMode(int mode){
        CHECK_MODE = mode;
    }
}

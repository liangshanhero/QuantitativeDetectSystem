package com.example.quantitativedetectsystem.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.quantitativedetectsystem.R;
import com.example.quantitativedetectsystem.domain.Archive;
import com.example.quantitativedetectsystem.domain.Stripe;
import com.example.quantitativedetectsystem.domain.Mark;
import com.example.quantitativedetectsystem.service.PictureService;
import com.example.quantitativedetectsystem.view.AbbreviationCurve;
import com.example.quantitativedetectsystem.view.TVS;

import java.util.ArrayList;
import java.util.List;

import static com.example.quantitativedetectsystem.view.TVS.TVS_ID;

//Bn/B0仅仅对灰度进行操作，暂未对浓度值进行同样操作
public class FunctionFittingActivity extends MainActivity {
    private List<Mark> mark1 = new ArrayList<>();
    private List<Mark> mark2 = new ArrayList<>();
    private List<TVS> tvsList1 = new ArrayList<>();
    private List<TVS> tvsList2 = new ArrayList<>();
    private List<Archive> archives1 = new ArrayList<>();
    private List<Archive> archives2 = new ArrayList<>();
    private LinearLayout linearLayout;
    private View.OnClickListener listener1,listener2;
    private int lengtht = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_fitting);
        init();
    }
    private void init(){
        Intent intent = getIntent();
        int length = intent.getIntExtra("length",0);
        linearLayout = findViewById(R.id.linear_fitting);
        for(int i = 0;i < length;i++){
            String str = "points" + String.valueOf(i);
            Mark virginPoint = new Mark(intent.getIntArrayExtra(str));
            mark1.add(virginPoint);
        }
        initListener1();
        initListener2();
        for(int i = 0; i< mark1.size(); i++){
            Mark virginPoint = mark1.get(i);
            int[] test = PictureService.getFeatures(virginPoint.getDotrowAvgGrays());
            virginPoint.setFeatureIndexOnDotrowIndex(test);
            virginPoint.setMode(TVS_ID+i);
            TVS tvs1 = createTVS(virginPoint,TVS_ID+i,"strip "+String.valueOf(i+1),listener1);
            tvsList1.add(tvs1);
            linearLayout.addView(tvs1.getLinearLayout());
        }
    }

    public TVS createTVS(Mark virginPoint, int ID, String name, View.OnClickListener listener){
        int[] features = virginPoint.getFeatureIndexOnDotrowIndex();
        AbbreviationCurve AC = new AbbreviationCurve(this,getScreenWidth()*4/7,virginPoint.getDotrowAvgGrays(),(float)1,features,0);
        AC.setId(ID);
        AC.setOnClickListener(listener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = getScreenWidth()*4/7;
        layoutParams.height = getScreenWidth()*4/7/2;
        layoutParams.weight = 4;
        AC.setLayoutParams(layoutParams);
        TVS tvs = new TVS(this,name,AC);
        return tvs;
    }

    public boolean createArchive(){
        archives1.clear();
        archives2.clear();
        if(mark2.size() > 0){
            if(!isSame(0)){
                Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                return false;
            }
            int[] indexs = mark1.get(0).getFeatureIndex();
            for(int i = 0;i < indexs.length;i++){
                Archive archive1 = new Archive(i);
                Archive archive2 = new Archive(i);
                archives1.add(archive1);
                archives2.add(archive2);
            }
            //为每个标曲输入B0
            for(int i = 0;i < indexs.length;i++){
                Mark virginPoint1 = mark1.get(0);
                Mark virginPoint2 = mark2.get(0);
                int index = indexs[i];
                float g1,g2;
                g1 = virginPoint1.getTrC(index);//对应位置的灰度/C的值
                g2 = virginPoint2.getTrC(index);
                Archive archive1 = archives1.get(i);
                Archive archive2 = archives2.get(i);
                archive1.setGray0(g1);
                archive2.setGray0(g2);
            }
            //为标曲输入用于构建的样本值
            for(int i = 1; i < mark1.size(); i++){
                if(!isSame(i)){
                    Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(!tvsList1.get(i).getaSwitch().isChecked()||!tvsList2.get(i).getaSwitch().isChecked())
                    continue;
                Mark virginPoint1 = mark1.get(i);
                Mark virginPoint2 = mark2.get(i);
                for(int j = 0;j < indexs.length;j++){
                    int index = indexs[j];
                    float g1,g2,c1,c2;
                    c1 = virginPoint1.getFeatureIndexConcentrations()[j];
                    c2 = virginPoint2.getFeatureIndexConcentrations()[j];
                    g1 = virginPoint1.getTrC(index);//对应位置的灰度/C的值
                    g2 = virginPoint2.getTrC(index);
                    Archive archive1 = archives1.get(j);
                    Archive archive2 = archives2.get(j);
                    Stripe stripe1 = new Stripe(c1,g1/archive1.getGray0());
                    Stripe stripe2 = new Stripe(c2,g2/archive2.getGray0());
                    archive1.addFeature(stripe1);
                    archive2.addFeature(stripe2);
                }
            }
        }
        else {
            int[] indexs = mark1.get(0).getFeatureIndex();
            for(int i = 0;i < indexs.length;i++){
                Archive archive1 = new Archive(i);
                Archive archive2 = new Archive(i);
                archives1.add(archive1);
                archives2.add(archive2);
            }
            //为每个标曲输入B0
            for(int i = 0;i < indexs.length;i++){
                Mark virginPoint1 = mark1.get(0);
                int index = indexs[i];
                float g1;
                g1 = virginPoint1.getTrC(index);//对应位置的灰度/C的值
                Archive archive1 = archives1.get(i);
                archive1.setGray0(g1);
            }
            //为标曲输入用于构建的样本值
            for(int i = 1; i < mark1.size(); i++){
                if(!tvsList1.get(i).getaSwitch().isChecked())
                    continue;
                Mark virginPoint1 = mark1.get(i);
                for(int j = 0;j < indexs.length;j++){
                    int index = indexs[j];
                    float g1,c1;
                    c1 = virginPoint1.getFeatureIndexConcentrations()[j];
                    g1 = virginPoint1.getTrC(index);//对应位置的灰度/C的值
                    Archive archive1 = archives1.get(j);
                    Stripe stripe1 = new Stripe(c1,g1/archive1.getGray0());
                    Log.w("Result",String.valueOf(g1/archive1.getGray0()));
                    archive1.addFeature(stripe1);
                }
            }
        }
        return true;
    }

    public boolean isSame(int index){
        if(mark1.get(index).getFeatureIndex().length == mark2.get(index).getFeatureIndex().length)
            return true;
        else
            return false;
    }

    public void initListener1(){
        listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionFittingActivity.this,FunctionInputDataActivity.class);
                intent.putExtra("VirginPoint", mark1.get(v.getId()-TVS_ID));
                intent.putExtra("length",lengtht);
                intent.putExtra("function","data");
                startActivityForResult(intent,REQUEST_CODE_DATA);
            }
        };
    }
    public void initListener2(){
        listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionFittingActivity.this,FunctionInputDataActivity.class);
                intent.putExtra("VirginPoint", mark2.get(v.getId()-TVS_ID- mark1.size()));
                intent.putExtra("length",lengtht);
                intent.putExtra("function","data");
                startActivityForResult(intent,REQUEST_CODE_DATA);
            }
        };
    }
    public void secondSample(View view){
        Intent intent = new Intent(this, FunctionSampleActivity.class);
        intent.putExtra("function", FunctionSampleActivity.FUNCTION_SECOND_SAMPLE);
        startActivityForResult(intent,REQUEST_CODE_SECOND);
    }

    public void secondGet(){
        linearLayout.removeAllViews();
        for(int i = 0; i< mark2.size(); i++){
            TVS tvs1 = tvsList1.get(i);
            Mark virginPoint = mark2.get(i);
            int[] features = PictureService.getFeatures(virginPoint.getDotrowAvgGrays());
            virginPoint.setFeatureIndexOnDotrowIndex(features);
            virginPoint.setMode(TVS_ID+i+ mark2.size());
            TVS tvs2 = createTVS(virginPoint,TVS_ID+i+ mark2.size(),"Sstrip "+String.valueOf(i+1),listener2);
            tvsList2.add(tvs2);
            linearLayout.addView(tvs1.getLinearLayout());
            linearLayout.addView(tvs2.getLinearLayout());
        }
    }

    public void InputtedData(int ID){
        for(int i = 0;i < tvsList1.size();i++){
            TVS tvs = tvsList1.get(i);
            if(tvs.getAbbreviationCurveView().getId() == ID){
                String[] str = tvs.getTextView().getText().toString().split("f");
                if(str.length<2)
                    tvs.getTextView().setText(tvs.getTextView().getText()+"(fin)");
                tvs.setHasInput(true);
                break;
            }
        }
        for(int i = 0;i < tvsList2.size();i++){
            TVS tvs = tvsList2.get(i);
            if(tvs.getAbbreviationCurveView().getId() == ID){
                String[] str = tvs.getTextView().getText().toString().split("f");
                if(str.length<2)
                    tvs.getTextView().setText(tvs.getTextView().getText()+"(fin)");
                tvs.setHasInput(true);
                break;
            }
        }
    }

//    首先计算拟合，并跳转到结果界面FunctionFormulaActivity
    public void fitting(View view){
        if(!createArchive())
            return;
        Intent intent = new Intent(this,FunctionFormulaActivity.class);
        intent.putExtra("length",archives1.size());
        intent.putExtra("function","Formula");
        for(int i = 0;i < archives1.size();i++){
            Archive archive1 = archives1.get(i);
            Archive archive2 = archives2.get(i);
            String str1 = "Archive"+"1"+String.valueOf(i);
            String str2 = "Archive"+"2"+String.valueOf(i);
            intent.putExtra(str1,archive1);
            intent.putExtra(str2,archive2);
        }
        if(mark2.size() <= 0)
            FunctionFormulaActivity.setOneTwo(FunctionFormulaActivity.ONE);
        else
            FunctionFormulaActivity.setOneTwo(FunctionFormulaActivity.TWO);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_DATA:
                    int ID = data.getIntExtra("ID",0);
                    for(Mark virginPoint: mark1){
                        if(ID == virginPoint.getMode()){
                            virginPoint.setIaC(data.getIntArrayExtra("ids"),data.getFloatArrayExtra("conc"));
                            virginPoint.setFlag(data.getIntExtra("FLAG",1));
                        }
                    }
                    for(Mark virginPoint: mark2){
                    if(ID == virginPoint.getMode()){
                        virginPoint.setIaC(data.getIntArrayExtra("ids"),data.getFloatArrayExtra("conc"));
                        virginPoint.setFlag(data.getIntExtra("FLAG",1));
                    }
                }
                    InputtedData(ID);
                    break;
                case REQUEST_CODE_SECOND:
                    int length = data.getIntExtra("length",0);
                    if(length != mark1.size()){
                        Toast.makeText(this,"两次扫描的样本数量不一致，请重试",Toast.LENGTH_SHORT).show();
                        mark2.clear();
                        tvsList2.clear();
                        break;
                    }
                    for(int i = 0;i < length;i++){
                        String str = "points" + String.valueOf(i);
                        Mark mark = new Mark(data.getIntArrayExtra(str));
                        mark2.add(mark);
                    }
                    secondGet();
                    break;
            }
    }

}

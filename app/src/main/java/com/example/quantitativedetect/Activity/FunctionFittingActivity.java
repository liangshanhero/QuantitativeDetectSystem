package com.example.quantitativedetect.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.Stripe;
import com.example.quantitativedetect.domain.Line;

import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.view.AbbreviationCurve;
import com.example.quantitativedetect.view.TVS;

import java.util.ArrayList;
import java.util.List;

import static com.example.quantitativedetect.service.PictureService.getFeatures;
import static com.example.quantitativedetect.view.TVS.TVS_ID;

//Bn/B0仅仅对灰度进行操作，暂未对浓度值进行同样操作
public class FunctionFittingActivity extends MainActivity {
    private List<Mark> firstPicMarkList = new ArrayList<>();
    private List<Mark> secondPicMarkList = new ArrayList<>();
    private List<TVS> tvsList1 = new ArrayList<>();
    private List<TVS> tvsList2 = new ArrayList<>();
    private List<Stripe> firstPicStripes = new ArrayList<>();
    private List<Stripe> secondPicStripes = new ArrayList<>();
    private LinearLayout linearLayout;
    private View.OnClickListener listener1,listener2;
    private int length = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_fitting);
        init();
    }
    private void init(){
        Intent intent = getIntent();
        int markViewQuantity = intent.getIntExtra("length",0);
        linearLayout = findViewById(R.id.linear_fitting);
        for(int i = 0;i < markViewQuantity;i++){
            String str = "points" + String.valueOf(i);

            Mark mark = (Mark) intent.getSerializableExtra(str);
            firstPicMarkList.add(mark);
        }
        initListener1();
        initListener2();
        for(int i = 0; i< firstPicMarkList.size(); i++){
            Mark mark = firstPicMarkList.get(i);
            mark = getFeatures(mark);
            mark.setMode(TVS_ID+i);
            TVS tvs1 = createTVS(mark,TVS_ID+i,"strip "+String.valueOf(i+1),listener1);
            tvsList1.add(tvs1);
            linearLayout.addView(tvs1.getLinearLayout());
        }
    }

    public TVS createTVS(Mark mark, int ID, String name, View.OnClickListener listener){

//        TODO 2021-0130 暂时使用固定的值代替
//        
//        AbbreviationCurve abbreviationCurve = new AbbreviationCurve(this,getScreenWidth()*4/7, mark.getDotrowAvgGrays(),(float)1,features,0);
//        int[] features = new int[0];
//        int[] dotrowAvgGrays = new int[0];
        AbbreviationCurve abbreviationCurve = new AbbreviationCurve(this,getScreenWidth()*4/7, mark,(float)1,0);
        

        
        abbreviationCurve.setId(ID);
        abbreviationCurve.setOnClickListener(listener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = getScreenWidth()*4/7;
        layoutParams.height = getScreenWidth()*4/7/2;
        layoutParams.weight = 4;
        abbreviationCurve.setLayoutParams(layoutParams);
        TVS tvs = new TVS(this,name,abbreviationCurve);
        return tvs;
    }

    public boolean createStripes(){
        firstPicStripes.clear();
        secondPicStripes.clear();
        if(secondPicMarkList.size() > 0){//有第二张图的情况,暂时不处理
            if(!isSame(0)){
                Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                return false;
            }
            List<Line> firstPicMarkFeatureLineList = firstPicMarkList.get(0).getFeatureLineList();//.getFeatureIndex();
            List<Line> secondPicMarkFeatureLineList = secondPicMarkList.get(0).getFeatureLineList();
            for(int i = 0;i < firstPicMarkFeatureLineList.size();i++){
                Stripe stripe1 = new Stripe(i);
                Stripe stripe2 = new Stripe(i);
                firstPicStripes.add(stripe1);
                secondPicStripes.add(stripe2);
            }
            //为每个标曲输入B0
            for(int i = 0;i < firstPicMarkFeatureLineList.size();i++){
                Mark firstPicMark = firstPicMarkList.get(0);
                Mark secondPicMark = secondPicMarkList.get(0);
                Line line = firstPicMarkFeatureLineList.get(i);
                float g1,g2;
                g1 = firstPicMarkFeatureLineList.get(i).getGray();//对应位置的灰度/C的值
                g2 = secondPicMarkFeatureLineList.get(i).getGray();
                Stripe stripe1 = firstPicStripes.get(i);
                Stripe stripe2 = secondPicStripes.get(i);
                stripe1.setGray0(g1);
                stripe2.setGray0(g2);
            }
            //为标曲输入用于构建的样本值
            for(int i = 1; i < firstPicMarkList.size(); i++){
                if(!isSame(i)){
                    Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(!tvsList1.get(i).getaSwitch().isChecked()||!tvsList2.get(i).getaSwitch().isChecked())
                    continue;
                Mark firstPicMark = firstPicMarkList.get(i);
                Mark secondPicMark = secondPicMarkList.get(i);
                for(int j = 0; j < firstPicMarkFeatureLineList.size(); j++){
                    Line firstPicMarkFeatureLine = firstPicMarkFeatureLineList.get(j);
                    Line secondPicMarkFeatureLine = secondPicMarkFeatureLineList.get(j);
                    float g1,g2,c1,c2;
                    c1 = firstPicMark.getFeatureLineList().get(j).getConcentration();
                    c2 = secondPicMark.getFeatureLineList().get(j).getConcentration();

                    g1 = firstPicMarkFeatureLine.getGray();
                    g2 = secondPicMarkFeatureLine.getGray();
                    Stripe stripe1 = firstPicStripes.get(j);
                    Stripe stripe2 = secondPicStripes.get(j);
                    Line line1 = new Line(c1, (int)(g1/ stripe1.getGray0()));
                    Line line2 = new Line(c2,(int)(g2/ stripe2.getGray0()));
                    stripe1.addLine(line1);
                    stripe2.addLine(line2);
                }
            }
        }
        else {
            List<Line> firstPicFeatureLineList = firstPicMarkList.get(0).getFeatureLineList();
            for(int i = 0;i < firstPicFeatureLineList.size(); i++){
                Stripe stripe1 = new Stripe(i);
                Stripe stripe2 = new Stripe(i);
//            gray0默认值为1
                firstPicStripes.add(stripe1);
                secondPicStripes.add(stripe2);
            }
            //为每个标曲输入B0
//            Mark mark = firstPicMarkList.get(0);
//            firstPicFeatureLineList
            for(int i = 0;i < firstPicFeatureLineList.size(); i++){
//                TODO 2021-0130 原来的是TRC，**********！！！！！！！！！！！！！！！！！！！！！
//                float g1 = firstPicFeatureLineList.get(i).getGray();//对应位置的灰度/C的值///
                float g1 = firstPicFeatureLineList.get(i).getGray()/(float)firstPicMarkList.get(0).getLineWidthPixelQuantity();
                Stripe stripe1 = firstPicStripes.get(i);
                stripe1.setGray0(g1);
//                TODO 2021-0131 可以试着将featureLine加入到对应的archive1中（暂时没想好怎么用，但是感觉有用。。。。。。）
            }
            //为标曲输入用于构建的样本值
//            TODO　2021-0209 firstPicMarkList.size()=1,i=1时无法进入循环,且size为1,后面的取值操作使用i会有越界错误
//             故将循环初始值改为i=0,修改以后后续完成情况良好,且特征line已加入firstPicStripes中
            for(int i = 0; i < firstPicMarkList.size(); i++){
                if(!tvsList1.get(i).getaSwitch().isChecked()){
                    continue;
                }

//                依次拿取每个mark,越界报错,将Mark mark = firstPicMarkList.get(i);改为:
                Mark mark = firstPicMarkList.get(i);

                for(int j = 0;j < firstPicFeatureLineList.size(); j++){

//                    mark.getFeatureLineList().get(j).getGray();
                    float g1,c1;
                    c1 = mark.getFeatureLineList().get(j).getConcentration();
//                    g1 = mark.getFeatureLineList().get(j).getGray();//对应位置的灰度/C的值
                    g1 = mark.getFeatureLineList().get(j).getGray()/(float)mark.getLineWidthPixelQuantity();
                    Stripe stripe1 = firstPicStripes.get(j);

                    Line line1 = new Line(c1,(int)(g1/ stripe1.getGray0()));
                    Log.w("Result",String.valueOf(g1/ stripe1.getGray0()));
                    stripe1.addLine(line1);
                }
            }
        }
        return true;
    }

    public boolean isSame(int index){
        if(firstPicMarkList.get(index).getFeatureLineList().size() == secondPicMarkList.get(index).getFeatureLineList().size())
            return true;
        else
            return false;
    }

    public void initListener1(){
        listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionFittingActivity.this,FunctionInputDataActivity.class);
                intent.putExtra("VirginPoint", firstPicMarkList.get(v.getId()-TVS_ID));
                intent.putExtra("length", length);
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
                intent.putExtra("VirginPoint", secondPicMarkList.get(v.getId()-TVS_ID- firstPicMarkList.size()));
                intent.putExtra("length", length);
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
        for(int i = 0; i< secondPicMarkList.size(); i++){
            TVS tvs1 = tvsList1.get(i);
            Mark mark = secondPicMarkList.get(i);

            mark.setMode(TVS_ID+i+ secondPicMarkList.size());
            TVS tvs2 = createTVS(mark,TVS_ID+i+ secondPicMarkList.size(),"Sstrip "+String.valueOf(i+1),listener2);
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
    public void onFitting(View view){
        if(!createStripes())
            return;
        Intent intent = new Intent(this,FunctionFormulaActivity.class);
        intent.putExtra("length", firstPicStripes.size());
        intent.putExtra("function","Formula");
        for(int i = 0; i < firstPicStripes.size(); i++){

            Stripe stripe1 = firstPicStripes.get(i);
            Stripe stripe2 = secondPicStripes.get(i);
            String str1 = "Archive"+"1"+String.valueOf(i);
            String str2 = "Archive"+"2"+String.valueOf(i);
//TODO            stripe中的line为null,找到如何降line即特征点放入到stripe中,否则在FunctionFormulaActivity中无法获取到数据
            intent.putExtra(str1, stripe1);
            intent.putExtra(str2, stripe2);
        }


        if(secondPicMarkList.size() <= 0)
            FunctionFormulaActivity.setOneTwo(FunctionFormulaActivity.ONE);
        else
            FunctionFormulaActivity.setOneTwo(FunctionFormulaActivity.TWO);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_DATA:
                    int ID = intent.getIntExtra("ID",0);
                    for(Mark mark: firstPicMarkList){
                        if(ID == mark.getMode()){
//                            mark.setIaC(intent.getIntArrayExtra("ids"),intent.getFloatArrayExtra("conc"));
//                            mark.setFlag(intent.getIntExtra("FLAG",1));
                        }
                    }
                    for(Mark virginPoint: secondPicMarkList){
                    if(ID == virginPoint.getMode()){
//                        virginPoint.setIaC(intent.getIntArrayExtra("ids"),intent.getFloatArrayExtra("conc"));
//                        virginPoint.setFlag(intent.getIntExtra("FLAG",1));
                    }
                }
                    InputtedData(ID);
                    break;
                case REQUEST_CODE_SECOND:
                    int length = intent.getIntExtra("length",0);
                    if(length != firstPicMarkList.size()){
                        Toast.makeText(this,"两次扫描的样本数量不一致，请重试",Toast.LENGTH_SHORT).show();
                        secondPicMarkList.clear();
                        tvsList2.clear();
                        break;
                    }
                    for(int i = 0;i < length;i++){
                        String str = "points" + String.valueOf(i);
                        Mark mark = intent.getParcelableExtra(str);
                        secondPicMarkList.add(mark);
                    }
                    secondGet();
                    break;
            }
    }

}

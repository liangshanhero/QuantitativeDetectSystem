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
import com.example.quantitativedetect.view.GrayCurve;
import com.example.quantitativedetect.view.MarkSwitch;

import java.util.ArrayList;
import java.util.List;

import static com.example.quantitativedetect.service.PictureService.getFeatures;
import static com.example.quantitativedetect.view.MarkSwitch.MARK_SWITCH_ID;

//Bn/B0仅仅对灰度进行操作，暂未对浓度值进行同样操作
public class FunctionFittingActivity extends MainActivity {
    private List<Mark> firstPicMarkList = new ArrayList<>();
    private List<Mark> secondPicMarkList = new ArrayList<>();
    private List<MarkSwitch> markSwitchList1 = new ArrayList<>();
    private List<MarkSwitch> markSwitchList2 = new ArrayList<>();
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
            String str = "mark" + String.valueOf(i);
//            把intent里对应str的数据转换成对象

            Mark mark = (Mark) intent.getSerializableExtra(str);
            firstPicMarkList.add(mark);
        }
        initListener1();
        initListener2();
        for(int i = 0; i< firstPicMarkList.size(); i++){
            Mark mark = firstPicMarkList.get(i);
            mark = getFeatures(mark);
            mark.setDetectMethodPlusID(MARK_SWITCH_ID +i);
            MarkSwitch tempMarkSwitch = createMarkGrayCurveSwitchView(mark, MARK_SWITCH_ID +i,"mark "+String.valueOf(i+1),listener1);
            markSwitchList1.add(tempMarkSwitch);
            linearLayout.addView(tempMarkSwitch.getLinearLayout());
        }
    }

    public MarkSwitch createMarkGrayCurveSwitchView(Mark mark, int ID, String name, View.OnClickListener listener){
//
//        AbbreviationCurve abbreviationCurve = new AbbreviationCurve(this,getScreenWidth()*4/7, mark.getDotrowAvgGrays(),(float)1,features,0);
//        int[] features = new int[0];
//        int[] dotrowAvgGrays = new int[0];
        GrayCurve grayCurve = new GrayCurve(this,getScreenWidth()*4/7, mark,(float)1,0);


        grayCurve.setId(ID);
        grayCurve.setOnClickListener(listener);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.width = getScreenWidth()*4/7;
        layoutParams.height = getScreenWidth()*4/7/2;
        layoutParams.weight = 4;
        grayCurve.setLayoutParams(layoutParams);
        MarkSwitch markSwitch = new MarkSwitch(this,name, grayCurve,mark);
        return markSwitch;
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
                stripe1.settLineAndeCLineGrayRatio(g1);
                stripe2.settLineAndeCLineGrayRatio(g2);
            }
            //为标曲输入用于构建的样本值
            for(int i = 1; i < firstPicMarkList.size(); i++){
                if(!isSame(i)){
                    Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(!markSwitchList1.get(i).getValidSwitch().isChecked()||!markSwitchList2.get(i).getValidSwitch().isChecked())
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
                    Line line1 = new Line(c1, (int)(g1/ stripe1.gettLineAndeCLineGrayRatio()));
                    Line line2 = new Line(c2,(int)(g2/ stripe2.gettLineAndeCLineGrayRatio()));
                    stripe1.addLine(line1);
                    stripe2.addLine(line2);
                }
            }
        }
        else {
            List<Line> firstPicFeatureLineList = firstPicMarkList.get(0).getFeatureLineList();
            for(int i = 0;i < firstPicFeatureLineList.size(); i++){
                if (!firstPicFeatureLineList.get(i).isValid()){
                    continue;
                }
                Stripe stripe1 = new Stripe(i);
                Stripe stripe2 = new Stripe(i);
//            tLineAndeCLineGrayRatio默认值为1
                float g1 = firstPicMarkList.get(0).getTrC(i);
                stripe1.settLineAndeCLineGrayRatio(g1);
                firstPicStripes.add(stripe1);
                secondPicStripes.add(stripe2);
            }
            //为每个标曲输入B0
//            Mark mark = firstPicMarkList.get(0);
//            firstPicFeatureLineList
//            for(int i = 0;i < firstPicFeatureLineList.size(); i++){
////                TODO 2021-0130 原来的是TRC，**********！！！！！！！！！！！！！！！！！！！！！
////                float g1 = firstPicFeatureLineList.get(i).getGray();//对应位置的灰度/C的值///
////                float g1 = firstPicFeatureLineList.get(i).getGray()/(float)firstPicMarkList.get(0).getLineWidthPixelQuantity();
//                float g1 = firstPicMarkList.get(0).getTrC(i);
//                Stripe stripe1 = firstPicStripes.get(i);
//                stripe1.settLineAndeCLineGrayRatio(g1);
////                TODO 2021-0131 可以试着将featureLine加入到对应的archive1中（暂时没想好怎么用，但是感觉有用。。。。。。）
//            }
            //为标曲输入用于构建的样本值
//            TODO　2021-0209 firstPicMarkList.size()=1,i=1时无法进入循环,且size为1,后面的取值操作使用i会有越界错误
//             故将循环初始值改为i=0,修改以后后续完成情况良好,且特征line已加入firstPicStripes中
            for(int i = 0; i < firstPicMarkList.size(); i++){
                if(!markSwitchList1.get(i).getValidSwitch().isChecked()){
                    continue;
                }

//                依次拿取每个mark,越界报错,将Mark mark = firstPicMarkList.get(i);改为:
                Mark mark = firstPicMarkList.get(i);

                for(int j = 0;j < firstPicFeatureLineList.size(); j++){

//                    mark.getFeatureLineList().get(j).getGray();
                    float grayRatio1,concentration1;
                    concentration1 = mark.getFeatureLineList().get(j).getConcentration();
//                    g1 = mark.getFeatureLineList().get(j).getGray();//对应位置的灰度/C的值
                    grayRatio1 = mark.getTrC(i);
//                    g1 = mark.getFeatureLineList().get(j).getGray()/(float)mark.getLineWidthPixelQuantity();
                    Stripe stripe1 = firstPicStripes.get(j);

                    Line line1 = new Line(concentration1,(int)(grayRatio1/ stripe1.gettLineAndeCLineGrayRatio()));
                    Log.w("Result",String.valueOf(grayRatio1/ stripe1.gettLineAndeCLineGrayRatio()));
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
                intent.putExtra("Mark", firstPicMarkList.get(v.getId()- MARK_SWITCH_ID));
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
                intent.putExtra("Mark", secondPicMarkList.get(v.getId()- MARK_SWITCH_ID - firstPicMarkList.size()));
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
            MarkSwitch markSwitch1 = markSwitchList1.get(i);
            Mark mark = secondPicMarkList.get(i);

            mark.setDetectMethodPlusID(MARK_SWITCH_ID +i+ secondPicMarkList.size());
            MarkSwitch markSwitch2 = createMarkGrayCurveSwitchView(mark, MARK_SWITCH_ID +i+ secondPicMarkList.size(),"Smark "+String.valueOf(i+1),listener2);
            markSwitchList2.add(markSwitch2);
            linearLayout.addView(markSwitch1.getLinearLayout());
            linearLayout.addView(markSwitch2.getLinearLayout());
        }
    }

    public void InputtedData(int ID){
        for(int i = 0; i < markSwitchList1.size(); i++){
            MarkSwitch markSwitch = markSwitchList1.get(i);
            if(markSwitch.getGrayCurve().getId() == ID){
                String[] str = markSwitch.getTextView().getText().toString().split("f");
                if(str.length<2)
                    markSwitch.getTextView().setText(markSwitch.getTextView().getText()+"(fin)");
                markSwitch.setHasInput(true);
                break;
            }
        }
        for(int i = 0; i < markSwitchList2.size(); i++){
            MarkSwitch markSwitch = markSwitchList2.get(i);
            if(markSwitch.getGrayCurve().getId() == ID){
                String[] str = markSwitch.getTextView().getText().toString().split("f");
                if(str.length<2)
                    markSwitch.getTextView().setText(markSwitch.getTextView().getText()+"(fin)");
                markSwitch.setHasInput(true);
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
                    Mark tempMark = (Mark) intent.getSerializableExtra("Mark");
//                    int ID = intent.getIntExtra("ID",0);
                    int ID = tempMark.getDetectMethodPlusID();
                    firstPicMarkList.get(ID-MARK_SWITCH_ID).setLineList(tempMark.getLineList());
                    firstPicMarkList.get(ID-MARK_SWITCH_ID).setFeatureLineList(tempMark.getFeatureLineList());
                    for(Mark mark: firstPicMarkList){
                        if(ID == mark.getDetectMethodPlusID()){
//                            TODO 2021-0319
                            mark.setConcentrations(intent.getIntArrayExtra("ids"),intent.getFloatArrayExtra("conc"));
                            mark.setFlag(intent.getIntExtra("FLAG",1));
                        }
                    }
                    for(Mark virginPoint: secondPicMarkList){
                    if(ID == virginPoint.getDetectMethodPlusID()){
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
                        markSwitchList2.clear();
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

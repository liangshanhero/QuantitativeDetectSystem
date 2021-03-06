package com.example.quantitativedetect.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.CheckPanel;
import com.example.quantitativedetect.domain.Feature;
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

    private List<Feature> firstPicFeatureList = new ArrayList<>();
    private List<Feature> secondPicFeatureList = new ArrayList<>();
    private List<MarkSwitch> markSwitchList1 = new ArrayList<>();
    private List<MarkSwitch> markSwitchList2 = new ArrayList<>();
    private List<Stripe> firstPicStripes = new ArrayList<>();
    private List<Stripe> secondPicStripes = new ArrayList<>();
    private CheckPanel checkPanel;
    private LinearLayout linearLayout;
    private View.OnClickListener listener1,listener2;
    private int length = 5;
    private int stripeQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_fitting);
        init();
    }
    private void init(){
        Intent intent = getIntent();
        linearLayout = findViewById(R.id.linear_fitting);
//        for(int i = 0;i < markViewQuantity;i++){
//            String str = "mark" + String.valueOf(i);
////            把intent里对应str的数据转换成对象
//
//            Mark mark = (Mark) intent.getSerializableExtra(str);
//            firstPicMarkList.add(mark);
//        }
        checkPanel = (CheckPanel) intent.getSerializableExtra("checkPanel");
        stripeQuantity = checkPanel.getMarkList().get(0).getStripeQuantity();
        initListener1();
        initListener2();
        checkPanel = getFeatures(checkPanel,stripeQuantity);
        for (int i = 0; i < checkPanel.getMarkList().size(); i++) {
            Mark mark = checkPanel.getMarkList().get(i);
            mark.setDetectMethodPlusID(MARK_SWITCH_ID + i);
            MarkSwitch tempMarkSwitch = createMarkGrayCurveSwitchView(mark,MARK_SWITCH_ID+i,"mark "+String.valueOf(i+1),listener1);
            markSwitchList1.add(tempMarkSwitch);
            linearLayout.addView(tempMarkSwitch.getLinearLayout());
        }
        Toast.makeText(this,"Please click the curve to input concentrations.",Toast.LENGTH_LONG).show();


//        for(int i = 0; i< firstPicMarkList.size(); i++){
//            Mark mark = firstPicMarkList.get(i);
//            mark = getFeatures(mark);
//            mark.setDetectMethodPlusID(MARK_SWITCH_ID +i);
//            MarkSwitch tempMarkSwitch = createMarkGrayCurveSwitchView(mark, MARK_SWITCH_ID +i,"mark "+String.valueOf(i+1),listener1);
//            markSwitchList1.add(tempMarkSwitch);
//            linearLayout.addView(tempMarkSwitch.getLinearLayout());
//        }
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

    public boolean createFeatures(){
        firstPicStripes.clear();
        secondPicStripes.clear();

        if(secondPicMarkList.size() > 0){//有第二张图的情况,暂时不处理
            if(!isSame(0)){
                Toast.makeText(this,"请正确输入所有样本的浓度值（数量不对称）",Toast.LENGTH_SHORT).show();
                return false;
            }
            List<Stripe> firstPicMarkStripeList = firstPicMarkList.get(0).getStripeList();//.getFeatureIndex();
            List<Stripe> secondPicMarkStripeList = secondPicMarkList.get(0).getStripeList();
            for(int i = 0;i < firstPicMarkStripeList.size();i++){
                Stripe stripe1 = new Stripe(i);
                Stripe stripe2 = new Stripe(i);
                firstPicStripes.add(stripe1);
                secondPicStripes.add(stripe2);
            }
            //为每个标曲输入B0
            for(int i = 0;i < firstPicMarkStripeList.size();i++){
                Mark firstPicMark = firstPicMarkList.get(0);
                Mark secondPicMark = secondPicMarkList.get(0);
                Line line = firstPicMarkStripeList.get(i).getMaxGrayLine();
                float g1,g2;
                g1 = firstPicMarkStripeList.get(i).getMaxGrayLine().getGray();//对应位置的灰度/C的值
                g2 = secondPicMarkStripeList.get(i).getMaxGrayLine().getGray();
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
                for(int j = 0; j < firstPicMarkStripeList.size(); j++){
                    Line firstPicMarkFeatureLine = firstPicMarkStripeList.get(j).getMaxGrayLine();
                    Line secondPicMarkFeatureLine = secondPicMarkStripeList.get(j).getMaxGrayLine();
                    float g1,g2,c1,c2;
                    c1 = firstPicMark.getStripeList().get(j).getMaxGrayLine().getConcentration();
                    c2 = secondPicMark.getStripeList().get(j).getMaxGrayLine().getConcentration();

                    g1 = firstPicMarkFeatureLine.getGray();
                    g2 = secondPicMarkFeatureLine.getGray();
                    Stripe stripe1 = firstPicStripes.get(j);
                    Stripe stripe2 = secondPicStripes.get(j);
                    Line line1 = new Line(c1, (int)(g1/ stripe1.gettLineAndeCLineGrayRatio()));
                    Line line2 = new Line(c2,(int)(g2/ stripe2.gettLineAndeCLineGrayRatio()));
//                    stripe1.addLine(line1);
//                    stripe2.addLine(line2);
                }
            }
        }
        else {
            //            重构

//            初始化featureList,避免之后抛空指针
            List<Feature> featureList = new ArrayList<>();
            for (int i = 0; i < stripeQuantity; i++) {
                featureList.add(i,new Feature());
            }

            for (int i = 0; i < checkPanel.getMarkList().size(); i++) {
                Mark mark = checkPanel.getMarkList().get(i);
                for (int j = 0; j < stripeQuantity; j++) {
                    featureList.get(j).getStripeList().add(mark.getStripeList().get(j));
                }
            }
//            设置每条线所需点的控制线
            for (int i = 0; i < featureList.size(); i++) {
                Feature feature = featureList.get(i);
                feature.setB0((float)feature.getStripeList().get(0).getMaxGrayLine().getGray() / (float)featureList.get(0).getStripeList().get(0).getMaxGrayLine().getGray());
                for (int j = 0; j < feature.getStripeList().size(); j++) {
                    feature.getStripeList().get(j).setB((float)feature.getStripeList().get(j).getMaxGrayLine().getGray() / (float)featureList.get(0).getStripeList().get(j).getMaxGrayLine().getGray());
                }
            }
            checkPanel.setFeatureList(featureList);
//            重构完,待测试
            //重构
//            List<Line> firstMarkFeatureLineList = firstPicMarkList.get(0).getFeatureLineList();
//            List<Line> validFeatureLineList = new ArrayList<>();
////            将有效的特征行取出
//            for (int i = 0; i < firstMarkFeatureLineList.size(); i++) {
//                if (firstMarkFeatureLineList.get(i).isValid()){
//                    validFeatureLineList.add(firstMarkFeatureLineList.get(i));
//                }
//            }
////            将第一条mark的CLine和featureLine加入到stripe中.
//            List<Stripe> stripeList = new ArrayList<>();
////            mark的第一条featureLine为Cline,因此i从1开始
//            for (int i = 1; i < validFeatureLineList.size(); i++) {
//                Stripe stripe = new Stripe(i);
//                stripe.getcLineList().add(validFeatureLineList.get(0));
//                stripe.getFeatureLineList().add(validFeatureLineList.get(i));
//                stripeList.add(stripe);
//            }
////            遍历剩下的markList
//            for (int i = 1; i < firstPicMarkList.size(); i++) {
//                Mark tempMark = firstPicMarkList.get(i);
//                for (int j = 1; j < tempMark.getFeatureLineList().size(); j++) {
//                    //stripeList的第0个是从i=1开始添加的,因此此处取第i-1个stripe
//                    int lineInY = tempMark.getFeatureLineList().get(i).getAdaptedY();
//                    int stripeInY = stripeList.get(i-1).getFeatureLineList().get(j-1).getAdaptedY();
////                    判断取出的featureLine是否与stripe取出的featureLine在bitmap中处于相同的高度,如果在这个范围内,则将本行加入到对应stripe中
////                    ±5的依据暂时没有,待测试.
//                    if ( lineInY < stripeInY + 5 && lineInY > stripeInY -5){
//                        stripeList.get(i-1).getcLineList().add(tempMark.getFeatureLineList().get(0));
//                        stripeList.get(i-1).getFeatureLineList().add(tempMark.getFeatureLineList().get(i));
//                    }
//                }
//            }
////            获取B0和B的值,为Y轴的B/B0做准备
//            for (int i = 0; i < stripeList.size() ; i++) {
//                Stripe tempStripe = stripeList.get(i);
//                for (int j = 0; j < tempStripe.getFeatureLineList().size(); j++) {
//                    if (j==0){
//                        tempStripe.setB0( (float) tempStripe.getFeatureLineList().get(0).getGray() / (float) tempStripe.getcLineList().get(0).getGray());
//                    }
//                    else{
//                        float tempB = (float)tempStripe.getFeatureLineList().get(j).getGray() / (float)tempStripe.getcLineList().get(j).getGray();
//                        tempStripe.getBList().add(tempB);
//                    }
//
//                    tempStripe.getLines().add(new Line(tempStripe.getFeatureLineList().get(j).getConcentration(), (int) (tempStripe.getBList().get(j)/tempStripe.getB0())));
//                    Log.w("Result",String.valueOf( tempStripe.getB0()/ tempStripe.gettLineAndeCLineGrayRatio()));
//                }
//            }
//            firstPicStripes.addAll(stripeList);
//            重构完,待测试


/*
//            List<Line> lineList = checkPanel.getMarkList().get(0).getLineList();
            List<Line> firstPicFirstFeatureLineList = firstPicMarkList.get(0).getFeatureLineList();
            for(int i = 0;i < firstPicFirstFeatureLineList.size(); i++){
                if (!firstPicFirstFeatureLineList.get(i).isValid()){
                    continue;
                }
                Stripe stripe1 = new Stripe(i);
                Stripe stripe2 = new Stripe(i);
//            tLineAndCLineGrayRatio默认值为1
                float g1 = firstPicMarkList.get(0).getTrC(i);
                stripe1.settLineAndeCLineGrayRatio(g1);
                firstPicStripes.add(stripe1);
                secondPicStripes.add(stripe2);
            }
            //为每个标曲输入B0
            Mark mark = firstPicMarkList.get(0);
//            firstPicFirstFeatureLineList
            for(int i = 0;i < firstPicFirstFeatureLineList.size(); i++){
//                TODO 2021-0130 原来的是TRC，**********！！！！！！！！！！！！！！！！！！！！！
//                float g1 = firstPicFeatureLineList.get(i).getGray();//对应位置的灰度/C的值///
//                float g1 = firstPicFeatureLineList.get(i).getGray()/(float)firstPicMarkList.get(0).getLineWidthPixelQuantity();
                float g1 = firstPicMarkList.get(0).getTrC(i);
                Stripe stripe1 = firstPicStripes.get(i);
                stripe1.settLineAndeCLineGrayRatio(g1);
//                TODO 2021-0131 可以试着将featureLine加入到对应的archive1中（暂时没想好怎么用，但是感觉有用。。。。。。）
            }
//            为标曲输入用于构建的样本值
//            TODO　2021-0209 firstPicMarkList.size()=1,i=1时无法进入循环,且size为1,后面的取值操作使用i会有越界错误
//             故将循环初始值改为i=0,修改以后后续完成情况良好,且特征line已加入firstPicStripes中
            for(int i = 0; i < firstPicMarkList.size(); i++){
                if(!markSwitchList1.get(i).getValidSwitch().isChecked()){
                    continue;
                }

//                依次拿取每个mark,越界报错,将Mark mark = firstPicMarkList.get(i);改为:
                mark = firstPicMarkList.get(i);

                for(int j = 0;j < firstPicFirstFeatureLineList.size(); j++){

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
            }*/
        }
        return true;
    }

    public boolean isSame(int index){
        if(firstPicMarkList.get(index).getStripeList().size() == secondPicMarkList.get(index).getStripeList().size())
            return true;
        else
            return false;
    }

    public void initListener1(){
        listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionFittingActivity.this,FunctionInputDataActivity.class);
                intent.putExtra("Mark", checkPanel.getMarkList().get(v.getId() - MARK_SWITCH_ID));
//                intent.putExtra("Mark", firstPicMarkList.get(v.getId()- MARK_SWITCH_ID));
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

//        TODO 测试,待删除
        float[][] tempConc = new float[][]{{(float)0,(float)0,(float) 0, (float) 0, (float) 0, (float) 0},
                {(float)0,(float)0.05,(float) 1, (float) 0.05, (float) 0.01, (float) 0.25},
                {(float)0,(float)0.1,(float) 2, (float) 0.1, (float) 0.05, (float) 0.5},
                {(float)0,(float)0.2,(float) 4, (float) 0.2, (float) 0.1, (float) 1},
                {(float)0,(float)0.3,(float) 6, (float) 0.3, (float) 0.25, (float) 2},
                {(float)0,(float)0.4,(float) 8, (float) 0.4, (float) 0.5, (float) 4}};
        for (int i = 0; i < checkPanel.getMarkQuantity(); i++) {
            Mark mark = checkPanel.getMarkList().get(i);
            for (int j = 0; j < mark.getStripeQuantity(); j++) {
                mark.getStripeList().get(j).getMaxGrayLine().setConcentration(tempConc[i][j]);
            }
        }



        if(!createFeatures())
            return;
        Intent intent = new Intent(this,FunctionFormulaActivity.class);
        intent.putExtra("checkPanel",checkPanel);
//        intent.putExtra("length",checkPanel.getStripeList().size()/checkPanel.getMarkList().size());
//        intent.putExtra("length", firstPicStripes.size());
        intent.putExtra("function","Formula");
//        for(int i = 0; i < firstPicStripes.size(); i++){
//            Stripe stripe1 = firstPicStripes.get(i);
//            Stripe stripe2 = secondPicStripes.get(i);
//            String str1 = "Archive"+"1"+String.valueOf(i);
//            String str2 = "Archive"+"2"+String.valueOf(i);
////TODO            stripe中的line为null,找到如何降line即特征点放入到stripe中,否则在FunctionFormulaActivity中无法获取到数据

//            intent.putExtra(str1, stripe1);
//            intent.putExtra(str2, stripe2);
//        }


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
//                    checkPanel.getMarkList().get(ID - MARK_SWITCH_ID).setLineList(tempMark.getLineList());
//                    checkPanel.getMarkList().get(ID - MARK_SWITCH_ID).setFeatureLineList(tempMark.getFeatureLineList());
                    checkPanel.getMarkList().set(ID - MARK_SWITCH_ID , tempMark);
//                    firstPicMarkList.get(ID-MARK_SWITCH_ID).setLineList(tempMark.getLineList());
//                    firstPicMarkList.get(ID-MARK_SWITCH_ID).setFeatureLineList(tempMark.getFeatureLineList());
                    for(Mark mark: checkPanel.getMarkList()){
//                    for(Mark mark: firstPicMarkList){
                        if(ID == mark.getDetectMethodPlusID()){
//                            TODO 2021-0319
//                            mark.setConcentrations(intent.getIntArrayExtra("ids"),intent.getFloatArrayExtra("conc"));
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

    public List<Feature> getFirstPicFeatureList() {
        return firstPicFeatureList;
    }

    public void setFirstPicFeatureList(List<Feature> firstPicFeatureList) {
        this.firstPicFeatureList = firstPicFeatureList;
    }

    public List<Feature> getSecondPicFeatureList() {
        return secondPicFeatureList;
    }

    public void setSecondPicFeatureList(List<Feature> secondPicFeatureList) {
        this.secondPicFeatureList = secondPicFeatureList;
    }
}

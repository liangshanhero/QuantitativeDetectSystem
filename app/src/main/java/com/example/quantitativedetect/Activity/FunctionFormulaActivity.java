package com.example.quantitativedetect.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.CheckPanel;
import com.example.quantitativedetect.domain.Feature;
import com.example.quantitativedetect.domain.Line;
import com.example.quantitativedetect.domain.LinearRegressionModel;
import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.domain.Stripe;

import com.example.quantitativedetect.domain.Result;
import com.example.quantitativedetect.service.FunctionService;
import com.example.quantitativedetect.view.LinearRegressionCurve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionFormulaActivity extends Activity {
    private static final String UNIT = "ppb";
    public static int ONE_TWO = 2;
    public static final int ONE = 1;
    public static final int TWO = 2;
    private CheckPanel checkPanel;
    private List<Stripe> firstPicStripes = new ArrayList<>();
    private List<Stripe> secondPicStripes = new ArrayList<>();
    private List<Result> resultList = new ArrayList<>();
    private TextView textView;
    private RelativeLayout relativeLayout;
    private String function;
    private float[] strips;
    private int stripeQuantity;
    private int now = 0;
    private LinearRegressionCurve linearRegressionCurve;
    private List<LinearRegressionModel> linearRegressionModelList = new ArrayList<>();
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_formula);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        checkPanel = (CheckPanel) intent.getSerializableExtra("checkPanel");
        stripeQuantity = checkPanel.getStripeQuantity();
//        stripeQuantityInOneMark = intent.getIntExtra("length",0);
        function = intent.getStringExtra("function");
        //  TODO firstPicArchive没有传递过来，为空，暂时自定义一个archive对象，
//        Archive tempFirstPicArchive = new Archive(1);
//        Archive tempSecondPicArchive = new Archive(2);
//        firstPicArchives.add(tempFirstPicArchive);
//        secondPicArchives.add(tempSecondPicArchive);


        if(function.equals("Formula")){

//            for(int i = 0; i < stripeQuantityInOneMark; i++){
//                String str = "Archive";
//                Stripe firstPicStripe = (Stripe) intent.getSerializableExtra(str+"1"+String.valueOf(i));
//                Stripe secondPicStripe = (Stripe) intent.getSerializableExtra(str+"2"+String.valueOf(i));
//                firstPicStripes.add(firstPicStripe);
//                secondPicStripes.add(secondPicStripe);
//            }
            fitRule();
//  TODO 规则从哪里来？？？
//  TODO 2020-0209,根据前端传来的数据构建Rule,而不是使用空的Rule(temRule)代替

//            Rule temRule = new Rule();
//            ruleList.add(temRule);
//            ruleList.get(0);
            linearRegressionCurve = new LinearRegressionCurve(this, linearRegressionModelList.get(0),MainActivity.getScreenWidth());
            linearRegressionCurve.setArchive(linearRegressionModelList.get(now));
//            linearRegressionCurve.setArchive(firstPicStripes.get(now), secondPicStripes.get(now), linearRegressionModelList.get(now));
        }
        else if(function.equals("Result")){
            strips = intent.getFloatArrayExtra("strips");
            for(int i = 0; i < stripeQuantity; i++){
                String str = "Function"+String.valueOf(i);
                LinearRegressionModel linearRegressionModel = (LinearRegressionModel) intent.getSerializableExtra(str);
                linearRegressionModelList.add(linearRegressionModel);
            }
            computing();
            linearRegressionCurve = new LinearRegressionCurve(this, linearRegressionModelList.get(0),MainActivity.getScreenWidth());
            linearRegressionCurve.setPoint((float) resultList.get(0).getConcentration(),strips[0], linearRegressionModelList.get(0));
        }
        textView = findViewById(R.id.textView_formula);
        relativeLayout = findViewById(R.id.relative_formula);
        textView.setText(equation(0));
        relativeLayout.addView(linearRegressionCurve);
    }

//    拟合规则
    public void fitRule(){
//        float[] concrations = new float[firstPicStripes.size()];
//        float[] grays = new float[firstPicStripes.size()];
        int markQuantity = checkPanel.getMarkQuantity();
//        float[] concrations = new float[checkPanel.getStripeList().size()];
//        float[] grays = new float[checkPanel.getStripeList().size()];
        float[] concrations = new float[markQuantity];
        float[] grays = new float[markQuantity];
//        for (int i = 0; i < checkPanel.getMarkList().size(); i++) {

            if(ONE_TWO == TWO){
//                TODO 此分支待重构
//                for(int j = 0; j < firstPicStripes.get(i).length(); j++){
//                    Line firstPicLine = firstPicStripes.get(i).getLine(j);
//                    Line secondPicLine = secondPicStripes.get(i).getLine(j);
//                    grays[j] = (firstPicLine.getGray()+ secondPicLine.getGray())/2;
//                    concrations[j] = (firstPicLine.getConcentration()+ secondPicLine.getConcentration())/2;
//                }
//                LinearRegressionModel linearRegressionModel = FunctionService.fit(concrations,grays);
//                linearRegressionModel.setBias((firstPicStripes.get(i).gettLineAndeCLineGrayRatio()+ secondPicStripes.get(i).gettLineAndeCLineGrayRatio())/2);
//                linearRegressionModelList.add(linearRegressionModel);
            }else{
//                测试
//                int[][] testGray = new int[stripeQuantity][checkPanel.getMarkList().size()];
//                float[][] testConc = new float[stripeQuantity][checkPanel.getMarkList().size()];
//                int index= 0;
//                for (int i = 0; i < stripeQuantity; i++) {
//                    for (int j = 0; j < checkPanel.getMarkList().size(); j++) {
//                        testConc[i][j] = checkPanel.getMarkList().get(j).getStripeList().get(i).getMaxGrayLine().getConcentration();
//                        testGray[i][j] = checkPanel.getMarkList().get(j).getStripeList().get(i).getMaxGrayLine().getGray();
//                    }
//                }
//                int[][] testGray2 = new int[stripeQuantity][checkPanel.getMarkList().size()];
//                float[][] testConc2 = new float[stripeQuantity][checkPanel.getMarkList().size()];
//                int index2= 0;
//                for (int i = 0; i < stripeQuantity; i++) {
//                    for (int j = 0; j < markQuantity; j++) {
//                        testConc2[i][j] = checkPanel.getFeatureList().get(i).getStripeList().get(j).getMaxGrayLine().getConcentration();
//                        testGray2[i][j] = checkPanel.getFeatureList().get(i).getStripeList().get(j).getMaxGrayLine().getGray();
//                    }
//                }
// TODO

//                int stripeQuantity = checkPanel.getStripeQuantity();
//                for (int i = 0; i < stripeQuantity; i++) {
//                    List<Stripe> tempStripeList = new ArrayList<>();
//                    for (int j = 0; j < markQuantity; j++) {
//                        grays[j]=checkPanel.getStripeList().get(i+ ).getGray();
//                        concrations[j]=checkPanel.getStripeList().get(j).getConcentration();
//                        tempStripeList.add(checkPanel.getStripeList().get(j));
//                    }
//                }

//                第一条feature是控制线，不需要
                for (int i = 1; i < checkPanel.getFeatureList().size(); i++) {
                    Feature feature = checkPanel.getFeatureList().get(i);
//                    for (int j = 0; j < feature.getStripeList().size(); j++) {
//                        concrations[j] = feature.getStripeList().get(j).getMaxGrayLine().getConcentration();
//                        grays[j] = feature.getStripeList().get(j).getMaxGrayLine().getGray();
//                    }
                    LinearRegressionModel linearRegressionModel = FunctionService.fluorescentMicrosphereFit(feature);
                    linearRegressionModel.setBias(feature.getB0());
//                    LinearRegressionModel linearRegressionModel = FunctionService.fit(concrations,grays);
//                    linearRegressionModel.setBias(checkPanel.getBias(i));
                    linearRegressionModel.setFeature(feature);
                    linearRegressionModelList.add(linearRegressionModel);
                }
//                for (int i = 0; i < stripeQuantity; i++) {
////                    List<Stripe> tempStripeList = new ArrayList<>();
//                    List<Stripe> tempStripeList = new ArrayList<>();
//                    for (int j = 0; j < checkPanel.getMarkList().size(); j++) {
//                        concrations[j] = checkPanel.getMarkList().get(j).getStripeList().get(i).getMaxGrayLine().getConcentration();
//                        grays[j] = checkPanel.getMarkList().get(j).getStripeList().get(i).getMaxGrayLine().getGray();
//                        tempStripeList.add(checkPanel.getMarkList().get(j).getStripeList().get(i));
//                    }
//                    LinearRegressionModel linearRegressionModel = FunctionService.fit(concrations,grays);
//                    linearRegressionModel.setBias(checkPanel.getBias(i));
//                    linearRegressionModel.setStripeList(tempStripeList);
//                    linearRegressionModelList.add(linearRegressionModel);
//                }

            }

//        }

//                2021-01-31 firstPicArchives.size()对应的是featureLineList.size()，
//                即一个archive，就是一种物质在不同的试纸（mark）上的灰度值的集合
            /*example：
         fL->featureLine
         -------------------------------------------------
         |        |    mark1   |   mark2    |   mark3    |
         -------------------------------------------------
         |archive1|     fL     |     fL     |     fL     |
         -------------------------------------------------
         |archive2|     fL     |     fL     |     fL     |
         -------------------------------------------------
         |archive2|     fL     |     fL     |     fL     |
         -------------------------------------------------
             */
        /*
        for(int i = 0; i < firstPicStripes.size(); i++){
            // 2021-02-18 concrations似乎应该定义在循环外,否则无法多次赋值,但定义在循环外后后续的fit将不正常
            float[] concrations = new float[firstPicStripes.size()];
            float[] grays = new float[firstPicStripes.size()];
//            float[] concrations = new float[firstPicArchives.get(i).length()];
//            float[] grays = new float[firstPicArchives.get(i).length()];
            if(ONE_TWO == TWO){
                for(int j = 0; j < firstPicStripes.get(i).length(); j++){
                    Line firstPicLine = firstPicStripes.get(i).getLine(j);
                    Line secondPicLine = secondPicStripes.get(i).getLine(j);
                    grays[j] = (firstPicLine.getGray()+ secondPicLine.getGray())/2;
                    concrations[j] = (firstPicLine.getConcentration()+ secondPicLine.getConcentration())/2;
                }
                LinearRegressionModel linearRegressionModel = FunctionService.fit(concrations,grays);
                linearRegressionModel.setBias((firstPicStripes.get(i).gettLineAndeCLineGrayRatio()+ secondPicStripes.get(i).gettLineAndeCLineGrayRatio())/2);
                linearRegressionModelList.add(linearRegressionModel);
            }
            else{
//                for(int j = 0; j < firstPicArchives.get(i).length(); j++){
//                    Line stripe1 = firstPicArchives.get(i).getLine(j);
//                    grays[j] = stripe1.getGray();
//                    concrations[j] = stripe1.getConcentration();
//                }
//                for(int j = 0; j < firstPicStripes.size(); j++){
//                firstPicStripes中仅存放了一条line,因此定义长度为6的concrations和grays仅在第1个可能有值传入.
                for(int j = 0; j < firstPicStripes.get(i).getLines().size(); j++){
                    //TODO 2021-02-18 firstPicStripes.get(i).getLine(j)得到的line中gray似乎都为44(页面中的B0也为44),
                    // concentration似乎都为0,即没有获取到前端输入的数据
                    Line line = firstPicStripes.get(i).getLine(j);
                    grays[j] = line.getGray();
                    concrations[j] = line.getConcentration();
                }

                firstPicStripes.get(i);
                Stripe stripe = firstPicStripes.get(i);
                int lengthTest = stripe.length();
                LinearRegressionModel linearRegressionModel = FunctionService.fit(concrations, grays);
                linearRegressionModel.setBias(firstPicStripes.get(i).gettLineAndeCLineGrayRatio());
                linearRegressionModelList.add(linearRegressionModel);
            }
        }
        */
    }

    public void computing(){
        for(int i = 0;i < strips.length;i++){
            double gray = strips[i]/ linearRegressionModelList.get(i).getBias();
            double conc = FunctionService.calculateConcentration(linearRegressionModelList.get(i),gray);
            Result result = new Result();
            result.setConcentration(conc);
            resultList.add(result);
        }
    }

    public String equation(int index){
        String str = "";
        if(function.equals("Formula")){
            char c = '+';
            if(linearRegressionModelList.get(index).getOffset() < 0)
                c = '-';
            int gray0 = linearRegressionModelList.get(index).getFeature().getStripeList().get(0).getMaxGrayLine().getGray();
//            str = "Concentration = 10^("+" * " +" ( ( ( Gray / "+ gray0 + " ) / B0 ) "+c+" " + String.format("%.2f",Math.abs(linearRegressionModelList.get(index).getOffset()))+" ) / " + String.format("%.2f", linearRegressionModelList.get(index).getSlope())+ " )\n"+"B0 = "+String.format("%.2f", linearRegressionModelList.get(index).getBias());
            str = "Concentration = "+" ( ( ( Gray / "+ gray0 + " ) / B0 ) "+c+" " + String.format("%.2f",Math.abs(linearRegressionModelList.get(index).getOffset()))+" ) / " +" ( " + String.format("%.2f", linearRegressionModelList.get(index).getSlope()) +" )\n"+"B0 = "+String.format("%.2f", linearRegressionModelList.get(index).getBias());
        }
        else if(function.equals("Result")){
            str = "Concentration = "+String.format("%.2f",resultList.get(index).getConcentration())+" "+UNIT;
        }
        return str;
    }

    public void next(View view){
        if(now >= stripeQuantity -2){
            now = stripeQuantity -2;
            Toast.makeText(this,"This is the last one.",Toast.LENGTH_SHORT).show();
            return;
        }
        now++;
        if(function.equals("Formula")){
            linearRegressionCurve.setArchive(linearRegressionModelList.get(now));
//            linearRegressionCurve.setArchive(firstPicStripes.get(now), secondPicStripes.get(now), linearRegressionModelList.get(now));
        }
        else if(function.equals("Result")){
            linearRegressionCurve.setPoint((float) resultList.get(now).getConcentration(),strips[now], linearRegressionModelList.get(now));
        }
        textView.setText(equation(now));
    }

    public void previous(View view){
        if(now <= 0){
            now = 0;
            Toast.makeText(this,"This is the first one.",Toast.LENGTH_SHORT).show();
            return;
        }
        now--;
        if(function.equals("Formula")){
            linearRegressionCurve.setArchive(linearRegressionModelList.get(now));
//            linearRegressionCurve.setArchive(firstPicStripes.get(now), secondPicStripes.get(now), linearRegressionModelList.get(now));
        }
        else if(function.equals("Result")){
            linearRegressionCurve.setPoint((float) resultList.get(now).getConcentration(),strips[now], linearRegressionModelList.get(now));
        }
        textView.setText(equation(now));
    }

    public void rename(View view){
        final EditText editText = new EditText(this);

        if(function.equals("Formula")){
            editText.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
        else if(function.equals("Result")){
            editText.setText(new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date()));
        }
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        inputDialog.setTitle("Please input the record name: ").setView(editText);
        inputDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = editText.getText().toString();
                save(name);
                Toast.makeText(FunctionFormulaActivity.this,"记录已保存",Toast.LENGTH_SHORT).show();
            }
        });
        inputDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        inputDialog.show();
    }

    public void save(String name){
        if(function.equals("Formula")){
            LinearRegressionModel linearRegressionModel = linearRegressionModelList.get(now);
            linearRegressionModel.setName(name);
            linearRegressionModel.save();
        }
        else if(function.equals("Result")){
            Result result = resultList.get(now);
            result.setName(name);
            result.save();
        }
    }
    public static void setOneTwo(int oneTwo){
        ONE_TWO = oneTwo;
    }
}

package com.example.quantitativedetect.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.LinearRegressionModel;
import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.domain.Stripe;
import com.example.quantitativedetect.view.GrayConcentrationSwitchView;
import com.example.quantitativedetect.view.GrayCurve;
import com.example.quantitativedetect.view.MarkSwitch;
import com.example.quantitativedetect.view.TextSpinnerSwitchView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FunctionInputDataActivity extends Activity {

    public static int SPINNER_ID = 1086;
    public static final int TEXT_SPINNER_SWITCH = 0;
//    public static int GRAY_CONCENTRATION_SWITCH = 1;
    private Mark mark;

    private MarkSwitch markSwitch;

    private List<GrayConcentrationSwitchView> grayConcentrationSwitchViewList = new ArrayList<>();
    private List<TextSpinnerSwitchView> textSpinnerSwitchViewList = new ArrayList<>();
//    private int[] functions = new int[]{1,2,3,4,5};
    private int[] functions = new int[]{0,0,0,0,0};
    private String function;
    private int length;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private GrayCurve grayCurve;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_data);
        init();
    }

    private void init(){
        Intent intent = getIntent();
        length = intent.getIntExtra("length",0);
        mark = (Mark) intent.getSerializableExtra("Mark");
        function = intent.getStringExtra("function");
//        markSwitch = (MarkSwitch) intent.getSerializableExtra("MarkSwitch");
        linearLayout = findViewById(R.id.linear_data);
        relativeLayout = findViewById(R.id.relative_AC);
//        TODO 2021-0130
//        abbreviationCurve = new AbbreviationCurve(this,MainActivity.getScreenWidth(), mark.getDotrowAvgGrays(),(float)1, mark.getFeatureIndexOnDotrowIndex(),1);
//        mark.getLineList()
//        int[] dotrowAvgGrays = new int[0];
//        int[] featureIndexOnDotrowIndex = new int[0];
        grayCurve = new GrayCurve(this,MainActivity.getScreenWidth(), mark, (float)1, 1);
        grayCurve.setCurveType(1);
        relativeLayout.addView(grayCurve);
        if(function.equals("data")) {
            initGrayConcentrationSwitchViews();
//          将焦点放在第一个启用的特征点的editView上
            boolean isFirstPositive=false;
            int[] tempFeatureIndex = grayCurve.getMaxGrayIndexInPoints();
            for (int i = 0; i < tempFeatureIndex.length && !isFirstPositive; i++) {
                if(tempFeatureIndex[i]>-1){
                    grayConcentrationSwitchViewList.get(i).getEditText().requestFocus();
                    isFirstPositive=true;
                }
            }
        }
        else if(function.equals("check")) {
            intiTextSpinnerSwitchView();
        }
    }

    private void intiTextSpinnerSwitchView(){
        final List<LinearRegressionModel> linearRegressionModelList = DataSupport.findAll(LinearRegressionModel.class);
        String[] list = new String[linearRegressionModelList.size()];
        for(int i = 0; i < linearRegressionModelList.size(); i++){
            list[i] = linearRegressionModelList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.list_function,list);
        for(int i = 0;i < length;i++){
//            TODO getTrc已注释,trc值暂为0,需要修改
//             T/C：T 线的颜色/荧光强度与 C 线的颜色/荧光强度之比，T线：feature line，C线：第一条feature line
            String name = "B"+String.valueOf(i)+" = T"+String.valueOf(i)+"/C = "+String.format("%.2f", mark.getMaxGrayLineTrC(i));
            Spinner spinner = new Spinner(this);
            spinner.setId(SPINNER_ID+i);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    for(int i = 0;i < length;i++){
                        if(parent.getId() == SPINNER_ID+i){
                            selectFunction(i,position);
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onChanged(0);
                }
            };
            TextSpinnerSwitchView textSpinnerSwitchView = new TextSpinnerSwitchView(this,spinner,name);
            textSpinnerSwitchView.setListener(listener);
            textSpinnerSwitchViewList.add(textSpinnerSwitchView);
            linearLayout.addView(textSpinnerSwitchView.getLinearLayout());
        }
    }

    private void initGrayConcentrationSwitchViews(){
//        TODO 2021-03-15 length 表示特征线的数量,但是只有最左侧的Mark会有所有的特征线,而右侧的Mark不一定有全部的特征线,
//         想办法不把length写死
        for(int i = 0;i < length || i<mark.getStripeList().size();i++){
//            原代码:
//            for(int i = 0;i < length;i++){
//          TODO 2020-0130，取值方法待解决，暂时使用固定值代替
//          mark.getLineWidthPixelQuantity()=5(似乎一直不变),mark.getFeatureLineList().get(i+1).getGray()=44/64(会变)
            String trGray = String.format("%.2f",(float)mark.getMaxGrayLineTrC(i));
//            String trGray = String.format("%.2f",(float) mark.getFeatureLineList().get(i).getGray()/mark.getLineWidthPixelQuantity());
//          String value = String.format("%.2f",(float) mark.getDotrowAvgGrays()[mark.getFeatureIndexOnDotrowIndex()[i+1]]/ mark.getLineWidthPixelQuantity());
//            String value = "1234123412341234.1234123412341243";
            GrayConcentrationSwitchView grayConcentrationSwitchView = new GrayConcentrationSwitchView(this,trGray);
            CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onChanged(1);
                }
            };
            final int finalI = i;
            View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    onFocus(finalI);
                }
            };

            int[] tempFeatureIndex = grayCurve.getMaxGrayIndexInPoints();
            if (!mark.getStripeList().get(i).getMaxGrayLine().isValid()){
                grayConcentrationSwitchView.getEditText().setText("");
                grayConcentrationSwitchView.getEditText().setEnabled(false);
                grayConcentrationSwitchView.getValidSwitch().setChecked(false);
                tempFeatureIndex[i] = -1 - tempFeatureIndex[i];
                grayCurve.setMaxGrayIndexInPoints(tempFeatureIndex);
            }
            if(mark.getStripeList().get(i).getMaxGrayLine().getConcentration()!=0){
                grayConcentrationSwitchView.getEditText().setText(Float.toString(mark.getStripeList().get(i).getMaxGrayLine().getConcentration()));
            }
            grayConcentrationSwitchView.setCheckedChangeListener(checkedChangeListener);
            grayConcentrationSwitchView.setFocusChangeListener(focusChangeListener);
            
            grayConcentrationSwitchViewList.add(grayConcentrationSwitchView);
            linearLayout.addView(grayConcentrationSwitchView.getLinearLayout());
        }
    }

    public void onFocus(int finalI) {

        int lineIndexInPoints = 0;
        for (int i = 0; i <= finalI; i++) {
            if (i!=finalI){
                lineIndexInPoints += mark.getStripeList().get(i).getLineList().size();
            }else{
                lineIndexInPoints += mark.getStripeList().get(i).getLineList().indexOf(mark.getStripeList().get(i).getMaxGrayLine());
            }
        }
        grayCurve.setSelectedPointIndex(lineIndexInPoints);
        grayCurve.invalidate();
    }

    public void onChanged(int switchType){
        int[] maxGrayIndexInPoints = new int[mark.getStripeList().size()];
        int maxGrayIndex = 0;
        for (int i = 0; i < mark.getStripeList().size(); i++) {
            Stripe stripe = mark.getStripeList().get(i);
            maxGrayIndexInPoints[i] = maxGrayIndex + stripe.getLineList().indexOf(stripe.getMaxGrayLine());
            maxGrayIndex += stripe.getLineList().size();
        }

        /*switchType的值有0,1
        // 0表示变化的switch的type是TEXT_SPINNER_SWITCH
        // 1表示变化的switch的type是GRAY_CONCENTRATION_SWITCH
         */
        if(switchType == TEXT_SPINNER_SWITCH){
            for(int i = 0; i < textSpinnerSwitchViewList.size(); i++){
                TextSpinnerSwitchView textSpinnerSwitchView = textSpinnerSwitchViewList.get(i);
                if(!textSpinnerSwitchView.getaSwitch().isChecked()){
                    maxGrayIndexInPoints[i+1] = maxGrayIndexInPoints[0];
                }
            }
        }
        else {
            for(int i = 0; i < grayConcentrationSwitchViewList.size(); i++){
                GrayConcentrationSwitchView grayConcentrationSwitchView = grayConcentrationSwitchViewList.get(i);
                if(!grayConcentrationSwitchView.getValidSwitch().isChecked()){
                    maxGrayIndexInPoints[i] = -1 - maxGrayIndexInPoints[i];
                    grayConcentrationSwitchView.getEditText().setText("");
                    grayConcentrationSwitchView.getEditText().setEnabled(false);
                    mark.getStripeList().get(i).getMaxGrayLine().setValid(false);
                }else{
//                    grayConcentrationSwitchView.getEditText().setId(i);
                    grayConcentrationSwitchView.getEditText().setEnabled(true);
                    mark.getStripeList().get(i).getMaxGrayLine().setValid(true);
//                    grayConcentrationSwitchView.getEditText().setNextFocusDownId(i+1);
                }
            }
        }
        grayCurve.setMaxGrayIndexInPoints(maxGrayIndexInPoints);
    }


    public void selectFunction(int index,int position){
        functions[index] = position;
    }

    public void input(){
        int n = 0;
        int cLineIndex = 0;
        for(int i = 0;i < grayConcentrationSwitchViewList.size();i++){
            if(grayConcentrationSwitchViewList.get(i).getValidSwitch().isChecked()) {
                if (n==0){
                    cLineIndex=n;
                }
                n++;
            }
        }
        int[] IDs = new int[n];
        float[] conc = new float[n];
        int index = 0;
//        TODO concTemp临时变量,这样就不用填浓度数据了,测试完成后删除
        int markNumber = mark.getDetectMethodPlusID()-10086;
        float[][] tempConc = new float[][]{{(float)0,(float)0,(float) 0, (float) 0, (float) 0, (float) 0},
                {(float)0,(float)0.05,(float) 1, (float) 0.05, (float) 0.01, (float) 0.25},
                {(float)0,(float)0.1,(float) 2, (float) 0.1, (float) 0.05, (float) 0.5},
                {(float)0,(float)0.2,(float) 4, (float) 0.2, (float) 0.1, (float) 1},
                {(float)0,(float)0.3,(float) 6, (float) 0.3, (float) 0.25, (float) 2},
                {(float)0,(float)0.4,(float) 8, (float) 0.4, (float) 0.5, (float) 4}};
        boolean isClinePassed = false;
//        第0个grayConcentrationSwitchView不应该输入浓度(Control Line)
        for(int i = 0;i < grayConcentrationSwitchViewList.size();i++){
            GrayConcentrationSwitchView grayConcentrationSwitchView = grayConcentrationSwitchViewList.get(i);
            if(grayConcentrationSwitchView.getValidSwitch().isChecked()){
                //        TODO 测试完成后取消注释
//                if(TextUtils.isEmpty(grayConcentrationSwitchView.getEditText().getText())){
//                    Toast.makeText(this,"请输入所有被选中的样本值！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String str = String.valueOf(grayConcentrationSwitchView.getEditText().getText());
//                float cc = Float.parseFloat(str);

//                if (i==cLineIndex){
//                    continue;
//                }
                IDs[index] = i;
//                conc[index++] = cc;
                conc[index] = tempConc[markNumber][index];
                index++;
            }
        }

        mark.setIsConcentrationInputted(Mark.FLAG_INPUTTED);
        Intent intent = getIntent();

//        intent.putExtra("ID", mark.getDetectMethodPlusID());
//        IDs:在指定试制区域中检测出的峰值点的编号索引数组(featureIndex[])
//      TODO 2021-0319

        index = 0;
//        第0个grayConcentrationSwitchView不应该输入浓度(Control Line),故i从1开始
        for (int i = 0; i < mark.getStripeList().size(); i++) {
            if (mark.getStripeList().get(i).getMaxGrayLine().isValid()/* && cLineIndex!=i*/){
                mark.getStripeList().get(i).getMaxGrayLine().setConcentration(conc[index++]);
            }
//            if (i==cLineIndex){
//                mark.getStripeList().get(i).getMaxGrayLine().setConcentration(1);
//            }
        }
        getIntent().putExtra("Mark",mark);
        intent.putExtra("ids",IDs);
//        intent.putExtra("conc",conc);
        intent.putExtra("FLAG",Mark.FLAG_INPUTTED);
        setResult(RESULT_OK,intent);
        this.finish();
    }

    /*
    check ()
     */
    public void check(){
        int n = 0;
        List<LinearRegressionModel> linearRegressionModelList = DataSupport.findAll(LinearRegressionModel.class);
        for(int i = 0;i < length;i++){
            if(textSpinnerSwitchViewList.get(i).getaSwitch().isChecked())
                n++;
        }

        float[] strips = new float[n];
        Intent intent = new Intent(this,FunctionFormulaActivity.class);
        int index = 0;
        for(int i = 0;i < length;i++){
            TextSpinnerSwitchView textSpinnerSwitchView = textSpinnerSwitchViewList.get(i);
            if(textSpinnerSwitchView.getaSwitch().isChecked()){
                //TODO 2021-0218 getTrc已注释,trc值暂为0,需要修改
                strips[index] = mark.getMaxGrayLineTrC(i);
                String str = "Function"+String.valueOf(index++);
                intent.putExtra(str, linearRegressionModelList.get(functions[i]));
            }
        }
        intent.putExtra("strips",strips);
        intent.putExtra("length",strips.length);
        intent.putExtra("function","Result");
        startActivity(intent);
    }

    public void concentrationsConfirm(View view){
        if(function.equals("data")){
            input();
        }
        else if(function.equals("check"))
            check();
    }

    public void back(View view){
        this.finish();
    }
}

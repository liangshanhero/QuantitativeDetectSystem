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
import com.example.quantitativedetect.view.GrayCurve;
import com.example.quantitativedetect.view.GrayConcentrationSwitchView;
import com.example.quantitativedetect.view.TextSpinnerSwitchView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FunctionInputDataActivity extends Activity {

    public static int SPINNER_ID = 1086;
    private Mark mark;
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
        mark = (Mark) intent.getSerializableExtra("VirginPoint");
        function = intent.getStringExtra("function");
        linearLayout = findViewById(R.id.linear_data);
        relativeLayout = findViewById(R.id.relative_AC);
//        TODO 2021-0130
//        abbreviationCurve = new AbbreviationCurve(this,MainActivity.getScreenWidth(), mark.getDotrowAvgGrays(),(float)1, mark.getFeatureIndexOnDotrowIndex(),1);
//        mark.getLineList()
//        int[] dotrowAvgGrays = new int[0];
//        int[] featureIndexOnDotrowIndex = new int[0];
        grayCurve = new GrayCurve(this,MainActivity.getScreenWidth(), mark, (float)1, 1);
        grayCurve.setFlag(1);
        relativeLayout.addView(grayCurve);
        if(function.equals("data"))
            initSTE();
        else if(function.equals("check"))
            intiTSS();
    }

    private void intiTSS(){
        final List<LinearRegressionModel> linearRegressionModelList = DataSupport.findAll(LinearRegressionModel.class);
        String[] list = new String[linearRegressionModelList.size()];
        for(int i = 0; i < linearRegressionModelList.size(); i++){
            list[i] = linearRegressionModelList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.list_function,list);
        for(int i = 0;i < length;i++){
//            TODO getTrc已注释,trc值暂为0,需要修改
//             T/C：T 线的颜色/荧光强度与 C 线的颜色/荧光强度之比，T线：feature line，C线：第一条feature line
            String name = "B"+String.valueOf(i)+" = T"+String.valueOf(i)+"/C = "+String.format("%.2f", mark.getTrC(i));
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

    private void initSTE(){
        for(int i = 0;i < length;i++){
//          TODO 2020-0130，取值方法待解决，暂时使用固定值代替
//          mark.getLineWidthPixelQuantity()=5(似乎一直不变),mark.getFeatureLineList().get(i+1).getGray()=44/64(会变)
            String value = String.format("%.2f",(float) mark.getFeatureLineList().get(i+1).getGray()/mark.getLineWidthPixelQuantity());
//          String value = String.format("%.2f",(float) mark.getDotrowAvgGrays()[mark.getFeatureIndexOnDotrowIndex()[i+1]]/ mark.getLineWidthPixelQuantity());
//            String value = "1234123412341234.1234123412341243";
            GrayConcentrationSwitchView grayConcentrationSwitchView = new GrayConcentrationSwitchView(this,value);
            CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onChanged(1);
                }
            };
            grayConcentrationSwitchView.setListener(listener);
            grayConcentrationSwitchViewList.add(grayConcentrationSwitchView);
            linearLayout.addView(grayConcentrationSwitchView.getLinearLayout());
        }
    }
    public void onChanged(int flag){
        //TODO ****************************************************************
        // List<Line> feature = (List<Line>) mark.getFeatureLineList().clone();

        int[] feature = new int[0];
        if(flag == 0){
            for(int i = 0; i < textSpinnerSwitchViewList.size(); i++){
                TextSpinnerSwitchView textSpinnerSwitchView = textSpinnerSwitchViewList.get(i);
                if(!textSpinnerSwitchView.getaSwitch().isChecked()){
                    feature[i+1] = feature[0];
                }
            }
        }
        else {
            for(int i = 0; i < grayConcentrationSwitchViewList.size(); i++){
                GrayConcentrationSwitchView grayConcentrationSwitchView = grayConcentrationSwitchViewList.get(i);
                if(!grayConcentrationSwitchView.getValidSwitch().isChecked()){
                    feature[i+1] = feature[0];
                }
            }
        }
        grayCurve.setFeatureIndex(feature);
    }

    public void selectFunction(int index,int position){
        functions[index] = position;
    }

    public void input(){
        int n = 0;
        for(int i = 0;i < length;i++){
            if(grayConcentrationSwitchViewList.get(i).getValidSwitch().isChecked())
                n++;
        }
        int[] IDs = new int[n];
        float[] conc = new float[n];
        int index = 0;
//        TODO concsTemp临时变量,这样就不用填浓度数据了,测试完成后删除
        int[] concsTemp ={2,4,6,8,10};
        for(int i = 0;i < length;i++){
            GrayConcentrationSwitchView grayConcentrationSwitchView = grayConcentrationSwitchViewList.get(i);
            if(grayConcentrationSwitchView.getValidSwitch().isChecked()){
                //        TODO 测试完成后取消代码注释,
//                if(TextUtils.isEmpty(ste.getEditText().getText())){
//                    Toast.makeText(this,"请输入所有被选中的样本值！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String str = String.valueOf(ste.getEditText().getText());
//                float cc = Float.parseFloat(str);
                IDs[index] = i;
//                conc[index++] = cc;
                conc[index++] = concsTemp[i];
            }
        }

        mark.setFlag(Mark.FLAG_INPUTTED);
        Intent intent = new Intent();
        intent.putExtra("ID", mark.getMode());
//        IDs:在指定试制区域中检测出的峰值点的编号索引数组(featureIndex[])
        intent.putExtra("ids",IDs);
        intent.putExtra("conc",conc);
        intent.putExtra("FLAG", Mark.FLAG_INPUTTED);
        setResult(RESULT_OK,intent);
        this.finish();
    }

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
                strips[index] = mark.getTrC(i);
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
        if(function.equals("data"))
            input();
        else if(function.equals("check"))
            check();
    }

    public void back(View view){
        this.finish();
    }
}

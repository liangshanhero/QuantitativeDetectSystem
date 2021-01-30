package com.example.quantitativedetectsystem.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quantitativedetectsystem.R;
import com.example.quantitativedetectsystem.domain.Rule;
import com.example.quantitativedetectsystem.domain.Mark;
import com.example.quantitativedetectsystem.view.AbbreviationCurve;
import com.example.quantitativedetectsystem.view.STE;
import com.example.quantitativedetectsystem.view.TSS;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FunctionInputDataActivity extends Activity {

    public static int SPINNER_ID = 1086;
    private Mark mark;
    private List<STE> steList = new ArrayList<>();
    private List<TSS> tssList = new ArrayList<>();
    private int[] functions = new int[]{0,0,0,0,0};
    private String function;
    private int length;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private AbbreviationCurve abbreviationCurve;
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
        int[] dotrowAvgGrays = new int[0];
        int[] featureIndexOnDotrowIndex = new int[0];
        abbreviationCurve = new AbbreviationCurve(this,MainActivity.getScreenWidth(), dotrowAvgGrays, (float)1, featureIndexOnDotrowIndex,1);
        abbreviationCurve.setFLAG(1);
        relativeLayout.addView(abbreviationCurve);
        if(function.equals("data"))
            initSTE();
        else if(function.equals("check"))
            intiTSS();
    }

    private void intiTSS(){
        final List<Rule> ruleList = DataSupport.findAll(Rule.class);
        String[] list = new String[ruleList.size()];
        for(int i = 0; i < ruleList.size(); i++){
            list[i] = ruleList.get(i).getName();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.list_function,list);
        for(int i = 0;i < length;i++){
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
            TSS tss = new TSS(this,spinner,name);
            tss.setListener(listener);
            tssList.add(tss);
            linearLayout.addView(tss.getLinearLayout());
        }
    }

    private void initSTE(){
        for(int i = 0;i < length;i++){
//          TODO 2020-0130，取值方法待解决，暂时使用固定值代替
//          String value = String.format("%.2f",(float) mark.getDotrowAvgGrays()[mark.getFeatureIndexOnDotrowIndex()[i+1]]/ mark.getLineWidthPixelQuantity());
            String value = "1234123412341234.1234123412341243";
            STE ste = new STE(this,value);
            CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onChanged(1);
                }
            };
            ste.setListener(listener);
            steList.add(ste);
            linearLayout.addView(ste.getLinearLayout());
        }
    }
    public void onChanged(int flag){
        //TODO ****************************************************************
        // List<Line> feature = (List<Line>) mark.getFeatureLineList().clone();
        int[] feature = new int[0];
        if(flag == 0){
            for(int i = 0;i < tssList.size();i++){
                TSS tss = tssList.get(i);
                if(!tss.getaSwitch().isChecked()){
                    feature[i+1] = feature[0];
                }
            }
        }
        else {
            for(int i = 0;i < steList.size();i++){
                STE ste = steList.get(i);
                if(!ste.getaSwitch().isChecked()){
                    feature[i+1] = feature[0];
                }
            }
        }
        abbreviationCurve.setFeatures(feature);
    }

    public void selectFunction(int index,int position){
        functions[index] = position;
    }

    public void input(){
        int n = 0;
        for(int i = 0;i < length;i++){
            if(steList.get(i).getaSwitch().isChecked())
                n++;
        }
        int[] IDs = new int[n];
        float[] conc = new float[n];
        int index = 0;
        for(int i = 0;i < length;i++){
            STE ste = steList.get(i);
            if(ste.getaSwitch().isChecked()){
                if(TextUtils.isEmpty(ste.getEditText().getText())){
                    Toast.makeText(this,"请输入所有被选中的样本值！",Toast.LENGTH_SHORT).show();
                    return;
                }
                String str = String.valueOf(ste.getEditText().getText());
                float cc = Float.parseFloat(str);
                IDs[index] = i;
                conc[index++] = cc;
            }
        }

        mark.setFlag(Mark.FLAG_INPUTTED);
        Intent intent = new Intent();
        intent.putExtra("ID", mark.getMode());
        intent.putExtra("ids",IDs);
        intent.putExtra("conc",conc);
        intent.putExtra("FLAG", Mark.FLAG_INPUTTED);
        setResult(RESULT_OK,intent);
        this.finish();
    }

    public void check(){
        int n = 0;
        List<Rule> ruleList = DataSupport.findAll(Rule.class);
        for(int i = 0;i < length;i++){
            if(tssList.get(i).getaSwitch().isChecked())
                n++;
        }

        float[] strips = new float[n];
        Intent intent = new Intent(this,FunctionFormulaActivity.class);
        int index = 0;
        for(int i = 0;i < length;i++){
            TSS tss = tssList.get(i);
            if(tss.getaSwitch().isChecked()){
                strips[index] = mark.getTrC(i);
                String str = "Function"+String.valueOf(index++);
                intent.putExtra(str, ruleList.get(functions[i]));
            }
        }
        intent.putExtra("strips",strips);
        intent.putExtra("length",strips.length);
        intent.putExtra("function","Result");
        startActivity(intent);
    }

    public void next(View view){
        if(function.equals("data"))
            input();
        else if(function.equals("check"))
            check();
    }

    public void back(View view){
        this.finish();
    }
}

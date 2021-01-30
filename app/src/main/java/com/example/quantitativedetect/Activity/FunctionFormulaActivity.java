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
import com.example.quantitativedetect.domain.Archive;
import com.example.quantitativedetect.domain.Rule;
import com.example.quantitativedetect.domain.Stripe;
import com.example.quantitativedetect.domain.Result;
import com.example.quantitativedetect.service.FunctionService;
import com.example.quantitativedetect.view.RuleCurve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FunctionFormulaActivity extends Activity {
    private static final String UNIT = "ppb";
    public static int ONE_TWO = 2;
    public static final int ONE = 1;
    public static final int TWO = 2;
    private List<Archive> archives1 = new ArrayList<>();
    private List<Archive> archives2 = new ArrayList<>();
    private List<Result> resultList = new ArrayList<>();
    private TextView textView;
    private RelativeLayout relativeLayout;
    private String function;
    private float[] strips;
    private int length,now = 0;
    private RuleCurve ruleCurve;
    private List<Rule> ruleList = new ArrayList<>();
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_formula);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        length = intent.getIntExtra("length",0);
        function = intent.getStringExtra("function");
        if(function.equals("Formula")){
            for(int i = 0;i < length;i++){
                String str = "Archive";
                Archive archive1 = (Archive) intent.getSerializableExtra(str+"1"+String.valueOf(i));
                Archive archive2 = (Archive) intent.getSerializableExtra(str+"2"+String.valueOf(i));
                archives1.add(archive1);
                archives2.add(archive2);
            }
            fitFormula();
            ruleCurve = new RuleCurve(this, ruleList.get(0),MainActivity.getScreenWidth());
            ruleCurve.setArchive(archives1.get(now),archives2.get(now), ruleList.get(now));
        }
        else if(function.equals("Result")){
            strips = intent.getFloatArrayExtra("strips");
            for(int i = 0;i < length;i++){
                String str = "Function"+String.valueOf(i);
                Rule rule = (Rule) intent.getSerializableExtra(str);
                ruleList.add(rule);
            }
            computing();
            ruleCurve = new RuleCurve(this, ruleList.get(0),MainActivity.getScreenWidth());
            ruleCurve.setPoint((float) resultList.get(0).getConcentration(),strips[0], ruleList.get(0));
        }
        textView = findViewById(R.id.textView_formula);
        relativeLayout = findViewById(R.id.relative_formula);
        textView.setText(equation(0));
        relativeLayout.addView(ruleCurve);
    }
    public void fitFormula(){
        for(int i = 0;i < archives1.size();i++){
            float[] conc = new float[archives1.get(i).length()];
            float[] grey = new float[archives1.get(i).length()];
            if(ONE_TWO == TWO){
                for(int j = 0;j < archives1.get(i).length();j++){
                    Stripe stripe1 = archives1.get(i).getFeature(j);
                    Stripe stripe2 = archives2.get(i).getFeature(j);
                    grey[j] = (stripe1.getGray()+ stripe2.getGray())/2;
                    conc[j] = (stripe1.getConcentration()+ stripe2.getConcentration())/2;
                }
                Rule rule = FunctionService.fitFunction(conc,grey);
                rule.setB0((archives1.get(i).getGray0()+archives2.get(i).getGray0())/2);
                ruleList.add(rule);
            }
            else{
                for(int j = 0;j < archives1.get(i).length();j++){
                    Stripe stripe1 = archives1.get(i).getFeature(j);
                    grey[j] = stripe1.getGray();
                    conc[j] = stripe1.getConcentration();
                }
                Rule rule = FunctionService.fitFunction(conc,grey);
                rule.setB0(archives1.get(i).getGray0());
                ruleList.add(rule);
            }
        }
    }

    public void computing(){
        for(int i = 0;i < strips.length;i++){
            double grey = strips[i]/ ruleList.get(i).getB0();
            double conc = FunctionService.calConc(ruleList.get(i),grey);
            Result result = new Result();
            result.setConcentration(conc);
            resultList.add(result);
        }
    }

    public String equation(int index){
        String str = "";
        if(function.equals("Formula")){
            char c = '+';
            if(ruleList.get(index).getOffset() < 0)
                c = '-';
            str = "Concentration = "+String.format("%.2f", ruleList.get(index).getSlope())+" * Bn/B0 "+c+" " + String.format("%.2f",Math.abs(ruleList.get(index).getOffset()))+"\n"+"B0 = "+String.format("%.2f", ruleList.get(index).getB0());
        }
        else if(function.equals("Result")){
            str = "Concentration = "+String.format("%.2f",resultList.get(index).getConcentration())+" "+UNIT;
        }
        return str;
    }

    public void next(View view){
        if(now >= length-1){
            now = length-1;
            Toast.makeText(this,"后面没有了",Toast.LENGTH_SHORT).show();
            return;
        }
        now++;
        if(function.equals("Formula")){
            ruleCurve.setArchive(archives1.get(now),archives2.get(now), ruleList.get(now));
        }
        else if(function.equals("Result")){
            ruleCurve.setPoint((float) resultList.get(now).getConcentration(),strips[now], ruleList.get(now));
        }
        textView.setText(equation(now));
    }

    public void previous(View view){
        if(now <= 0){
            now = 0;
            Toast.makeText(this,"前面没有了",Toast.LENGTH_SHORT).show();
            return;
        }
        now--;
        if(function.equals("Formula")){
            ruleCurve.setArchive(archives1.get(now),archives2.get(now), ruleList.get(now));
        }
        else if(function.equals("Result")){
            ruleCurve.setPoint((float) resultList.get(now).getConcentration(),strips[now], ruleList.get(now));
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
            Rule rule = ruleList.get(now);
            rule.setName(name);
            rule.save();
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

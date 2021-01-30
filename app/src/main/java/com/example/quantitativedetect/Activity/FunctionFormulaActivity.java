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
import com.example.quantitativedetect.domain.Line;
import com.example.quantitativedetect.domain.Rule;

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
    private List<Archive> firstPicArchives = new ArrayList<>();
    private List<Archive> secondPicArchives = new ArrayList<>();
    private List<Result> resultList = new ArrayList<>();
    private TextView textView;
    private RelativeLayout relativeLayout;
    private String function;
    private float[] strips;
    private int length;
    private int now = 0;
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
        //  TODO firstPicArchive没有传递过来，为空，暂时自定义一个archive对象，
//        Archive tempFirstPicArchive = new Archive(1);
//        Archive tempSecondPicArchive = new Archive(2);
//        firstPicArchives.add(tempFirstPicArchive);
//        secondPicArchives.add(tempSecondPicArchive);


        if(function.equals("Formula")){

            for(int i = 0;i < length;i++){
                String str = "Archive";
                Archive firstPicArchive = (Archive) intent.getSerializableExtra(str+"1"+String.valueOf(i));
                Archive secondPicArchive = (Archive) intent.getSerializableExtra(str+"2"+String.valueOf(i));


                firstPicArchives.add(firstPicArchive);
                secondPicArchives.add(secondPicArchive);
            }
            fitRule();
//  TODO 规则从哪里来？？？
            Rule temRule = new Rule();
            ruleList.add(temRule);
//            ruleList.get(0);
            ruleCurve = new RuleCurve(this, ruleList.get(0),MainActivity.getScreenWidth());


            ruleCurve.setArchive(firstPicArchives.get(now), secondPicArchives.get(now), ruleList.get(now));
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

//    拟合规则
    public void fitRule(){
        for(int i = 0; i < firstPicArchives.size(); i++){

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
            float[] concrations = new float[firstPicArchives.size()];
            float[] grays = new float[firstPicArchives.size()];
//            float[] concrations = new float[firstPicArchives.get(i).length()];
//            float[] grays = new float[firstPicArchives.get(i).length()];
            if(ONE_TWO == TWO){
                for(int j = 0; j < firstPicArchives.get(i).length(); j++){
                    Line stripe1 = firstPicArchives.get(i).getLine(j);
                    Line stripe2 = secondPicArchives.get(i).getLine(j);
                    grays[j] = (stripe1.getGray()+ stripe2.getGray())/2;
                    concrations[j] = (stripe1.getConcentration()+ stripe2.getConcentration())/2;
                }
                Rule rule = FunctionService.fit(concrations,grays);
                rule.setBias((firstPicArchives.get(i).getGray0()+ secondPicArchives.get(i).getGray0())/2);
                ruleList.add(rule);
            }
            else{


//                for(int j = 0; j < firstPicArchives.get(i).length(); j++){
//                    Line stripe1 = firstPicArchives.get(i).getLine(j);
//                    grays[j] = stripe1.getGray();
//                    concrations[j] = stripe1.getConcentration();
//                }
                for(int j = 0; j < firstPicArchives.get(i).getLines().size(); j++){
                    Line stripe1 = firstPicArchives.get(i).getLine(j);
                    grays[j] = stripe1.getGray();
                    concrations[j] = stripe1.getConcentration();
                }

                firstPicArchives.get(i);
                Archive archive = firstPicArchives.get(i);
                int lengthTest = archive.length();
                Rule rule = FunctionService.fit(concrations, grays);
                rule.setBias(firstPicArchives.get(i).getGray0());
                ruleList.add(rule);
            }
        }
    }

    public void computing(){
        for(int i = 0;i < strips.length;i++){
            double grey = strips[i]/ ruleList.get(i).getBias();
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
            str = "Concentration = "+String.format("%.2f", ruleList.get(index).getSlope())+" * Bn/B0 "+c+" " + String.format("%.2f",Math.abs(ruleList.get(index).getOffset()))+"\n"+"B0 = "+String.format("%.2f", ruleList.get(index).getBias());
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
            ruleCurve.setArchive(firstPicArchives.get(now), secondPicArchives.get(now), ruleList.get(now));
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
            ruleCurve.setArchive(firstPicArchives.get(now), secondPicArchives.get(now), ruleList.get(now));
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

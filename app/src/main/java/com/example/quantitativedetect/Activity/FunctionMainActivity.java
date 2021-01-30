package com.example.quantitativedetect.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.Rule;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class FunctionMainActivity extends MainActivity {

    private int function;
    private List<Rule> ruleList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_main);
        init();
    }

    public void init(){
        Intent intent = getIntent();
        function = intent.getIntExtra("function",MainActivity.IMMEDIATE);
        Button buttonTop = findViewById(R.id.button_detect);
        Button button = findViewById(R.id.button_standard_curve);
        if(function == MainActivity.IMMEDIATE){

        }
        else if(function == MainActivity.HISTORY){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MainActivity.getScreenWidth()/3,MainActivity.getScreenWidth()/6);
            layoutParams.gravity = Gravity.CENTER;
            buttonTop.setBackground(getDrawable(R.drawable.b_record));
            button.setBackground(getDrawable(R.drawable.b_curve));
            layoutParams.topMargin = 400;
            buttonTop.setLayoutParams(layoutParams);
            button.setLayoutParams(layoutParams);
        }
    }


    private String[] findRecord(){
        ruleList = DataSupport.findAll(Rule.class);
        String[] names = new String[ruleList.size()];
        for(int i = 0; i < ruleList.size(); i++)
            names[i] = ruleList.get(i).getName();
        return names;
    }

    public void createFunction(){
        Intent intent = new Intent(FunctionMainActivity.this, FunctionSampleActivity.class);
        intent.putExtra("function", FunctionSampleActivity.FUNCTION_FIRST_SAMPLE);
        startActivity(intent);
    }

    public void detection(){
        Intent intent = new Intent(this, FunctionSampleActivity.class);
        intent.putExtra("function", FunctionSampleActivity.FUNCTION_CHECK);
        startActivity(intent);
    }

    public void askHistoryRecord(){
        startActivity(new Intent(this,RecordViewActivity.class));
    }

    public void askHistoryFunction(){
        String[] names = findRecord();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Please select a record. ");
        dialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showHistoricalRule(which);
            }
        });
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
    public void showHistoricalRule(int index){
        Rule rule = ruleList.get(index);
        Intent intent = new Intent(this,FunctionViewActivity.class);
        intent.putExtra("Function", rule);
        startActivity(intent);
    }

    public void detect(View view){
        if(function == MainActivity.IMMEDIATE){
            detection();
        }
        else if(function == MainActivity.HISTORY){
            askHistoryRecord();
        }
    }

    public void standardCurve(View view){
        if(function == MainActivity.IMMEDIATE){
            createFunction();
        }
        else if(function == MainActivity.HISTORY){
            askHistoryFunction();
        }
    }
}

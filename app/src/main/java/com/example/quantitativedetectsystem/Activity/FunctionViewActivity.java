package com.example.quantitativedetectsystem.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quantitativedetectsystem.R;
import com.example.quantitativedetectsystem.domain.Rule;
import com.example.quantitativedetectsystem.view.RuleCurve;

import org.litepal.crud.DataSupport;

public class FunctionViewActivity extends Activity {

    private Rule rule;
    private TextView textView;
    private RelativeLayout relativeLayout;
    private RuleCurve ruleCurve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_view);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        rule = (Rule)intent.getSerializableExtra("Function");
        textView = findViewById(R.id.textView_view);
        relativeLayout = findViewById(R.id.relative_view);
        ruleCurve = new RuleCurve(this, rule,MainActivity.getScreenWidth());
        relativeLayout.addView(ruleCurve);
        char c = '+';
        if(rule.getOffset() < 0)
            c = '-';
        String str = "Concentration = "+String.format("%.2f", rule.getSlope())+" * Bn/B0 "+c+" " + String.format("%.2f",Math.abs(rule.getOffset()))+"\n"+"B0 = "+String.format("%.2f", rule.getB0());
        textView.setText(str);
    }

    public void back(View view){
        this.finish();
    }

    public void delete(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("请问是否确定删除当前项目？ ");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataSupport.deleteAll(Rule.class,"name = ?", rule.getName());
                FunctionViewActivity.this.finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
}

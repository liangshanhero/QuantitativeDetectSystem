package com.example.quantitativedetect.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.LinearRegressionModel;
import com.example.quantitativedetect.view.LinearRegressionCurve;

import org.litepal.crud.DataSupport;

public class FunctionViewActivity extends Activity {

    private LinearRegressionModel linearRegressionModel;
    private TextView textView;
    private RelativeLayout relativeLayout;
    private LinearRegressionCurve linearRegressionCurve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_view);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        linearRegressionModel = (LinearRegressionModel)intent.getSerializableExtra("Function");
        textView = findViewById(R.id.textView_view);
        relativeLayout = findViewById(R.id.relative_view);
        linearRegressionCurve = new LinearRegressionCurve(this, linearRegressionModel,MainActivity.getScreenWidth());
        relativeLayout.addView(linearRegressionCurve);
        char c = '+';
        if(linearRegressionModel.getOffset() < 0)
            c = '-';
        String str = "Concentration = "+String.format("%.2f", linearRegressionModel.getSlope())+" * Bn/B0 "+c+" " + String.format("%.2f",Math.abs(linearRegressionModel.getOffset()))+"\n"+"B0 = "+String.format("%.2f", linearRegressionModel.getBias());
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
                DataSupport.deleteAll(LinearRegressionModel.class,"name = ?", linearRegressionModel.getName());
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

package com.example.quantitativedetect.Activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quantitativedetect.R;
import com.example.quantitativedetect.domain.Result;

import org.litepal.crud.DataSupport;

import java.util.List;

public class RecordViewActivity extends Activity {
    private TableLayout tableLayout;
    private List<Result> resultList;
    public static final int RECORD_ID = 10086;
    private View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
    }

    public void init(){
        this.tableLayout = findViewById(R.id.table_record);
        resultList = DataSupport.findAll(Result.class);
        initListener();
        showRecord();
    }

    public void title(){
        TableRow tableRow = new TableRow(this);
        tableRow.setBackgroundColor(Color.WHITE);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 2;
        layoutParams.topMargin = 2;
        layoutParams.bottomMargin = 2;
        layoutParams.rightMargin = 2;
        tableRow.setLayoutParams(layoutParams);
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        TextView textView1 = new TextView(this);
        TextView textView2 = new TextView(this);
        TextView textView3 = new TextView(this);
        textView1.setTextSize(20);
        textView2.setTextSize(20);
        textView3.setTextSize(20);
        textView1.setGravity(Gravity.CENTER);
        textView2.setGravity(Gravity.CENTER);
        textView3.setGravity(Gravity.CENTER);
        textView1.setText("Record");
        textView2.setText("Concentration");
        textView3.setText(" ");
        textView1.setLayoutParams(params);
        textView2.setLayoutParams(params);
        textView3.setLayoutParams(params);
        tableRow.addView(textView1);
        tableRow.addView(textView2);
        tableRow.addView(textView3);
        tableLayout.addView(tableRow);
    }

    public void showRecord(){
        tableLayout.removeAllViews();
        title();
        for(int i = 0;i < resultList.size();i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setBackgroundColor(Color.WHITE);
            TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 2;
            layoutParams.topMargin = 2;
            layoutParams.bottomMargin = 2;
            layoutParams.rightMargin = 2;
            tableRow.setLayoutParams(layoutParams);
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            Button button = new Button(this);
            textView1.setTextSize(15);
            textView2.setTextSize(15);
            textView1.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
            textView1.setGravity(Gravity.CENTER);
            textView2.setGravity(Gravity.CENTER);
            button.setGravity(Gravity.CENTER);
            textView1.setText(resultList.get(i).getName());
            textView2.setText(String.format("%.2f",resultList.get(i).getConcentration()));
//            button.setText("删除");
            button.setBackground(getDrawable(R.drawable.b_delete));
            TableRow.LayoutParams params1 = new TableRow.LayoutParams(MainActivity.getScreenWidth()/3,MainActivity.getScreenWidth()/7);
            params1.weight = 1;
            button.setId(RECORD_ID+i);
            button.setOnClickListener(listener);
            textView1.setLayoutParams(params);
            textView2.setLayoutParams(params);
            button.setLayoutParams(params1);
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(button);
            tableLayout.addView(tableRow);
        }
    }

    public void initListener(){
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Result result = resultList.remove(v.getId()-RECORD_ID);
                result.delete();
                Toast.makeText(RecordViewActivity.this,"记录已删除",Toast.LENGTH_SHORT).show();
                showRecord();
            }
        };
    }

    public void back(View view){
        this.finish();
    }
}

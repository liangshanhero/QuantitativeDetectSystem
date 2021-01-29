package com.example.quantitativedetectsystem.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.quantitativedetectsystem.R;

public class MainActivity extends AppCompatActivity {
    public static int screenWidth,screenHeight;
    public static final int REQUEST_CODE_DATA = 10087;
    public static final int REQUEST_CODE_SECOND = 10089;
    public static final int HISTORY = 8888;
    public static final int IMMEDIATE = 7777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //获取屏幕宽高
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            this.screenWidth = dm.widthPixels;
            this.screenHeight = dm.heightPixels;
        }
    }

//    首页点击TRFMs-xLFIA,跳转到FunctionsActivity界面
    public void fluorescentMicrosphere(View view){
        FunctionSampleActivity.setCheckMode(FunctionSampleActivity.FLUORESCENT_MICROSPHERE);
        Intent intent = new Intent(this, FunctionMainActivity.class);
        intent.putExtra("function",IMMEDIATE);
        startActivity(intent);
    }

    public void colloidalGold(View view){
        FunctionSampleActivity.setCheckMode(FunctionSampleActivity.COLLOIDAL_GOLD);
        Intent intent = new Intent(this, FunctionMainActivity.class);
        intent.putExtra("function",IMMEDIATE);
        startActivity(intent);
    }

    public void record(View view){

        Intent intent = new Intent(this, FunctionMainActivity.class);
        intent.putExtra("function",HISTORY);
        startActivity(intent);
    }

    public void introduction(View view){
        startActivity(new Intent(this,IntroductionActivity.class));
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }
}

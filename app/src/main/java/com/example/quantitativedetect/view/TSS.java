package com.example.quantitativedetect.view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class TSS {
    private Switch aSwitch;
    private TextView textView;
    private Spinner spinner;
    private LinearLayout linearLayout;
    private Context context;
    private String name;

    public TSS(Context context,Spinner spinner,String name){
        this.context = context;
        this.name = name;
        this.spinner = spinner;
        init();
    }

    public void init(){
        linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        textView = new TextView(context);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.weight = 1;
        layoutParams1.setMargins(50,10,50,10);
        textView.setLayoutParams(layoutParams1);
        textView.setTextSize(10);
        textView.setText(name);

        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER;
        layoutParams3.weight = 2;
        layoutParams3.setMargins(50,10,50,10);
        spinner.setLayoutParams(layoutParams3);

        aSwitch = new Switch(context);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams2.weight = 1;
        layoutParams2.setMargins(50,10,50,10);
        aSwitch.setChecked(true);
        aSwitch.setLayoutParams(layoutParams2);

        linearLayout.addView(textView);
        linearLayout.addView(spinner);
        linearLayout.addView(aSwitch);
    }

    public Switch getaSwitch() {
        return aSwitch;
    }
    public LinearLayout getLinearLayout() {
        return this.linearLayout;
    }
    public void setListener(CompoundButton.OnCheckedChangeListener listener){
        aSwitch.setOnCheckedChangeListener(listener);
    }
}

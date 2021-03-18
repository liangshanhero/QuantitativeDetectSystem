package com.example.quantitativedetect.view;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class GrayConcentrationSwitchView {
    // 2021-0218 function="data"时调用(默认)
    // 含义:input界面的"灰度值-浓度值-开关"布局
    // PS:灰度值为相对灰度值=mark.getFeatureLineList().get(i+1).getGray()/mark.getLineWidthPixelQuantity()
    private Switch validSwitch;
    private TextView textView;
    private EditText editText;
    private LinearLayout linearLayout;
    private Context context;
    private String value;

    public GrayConcentrationSwitchView(Context context, String value){
        this.context = context;
        this.value = value;
        init();
    }
    private void init(){
        linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        textView = new TextView(context);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.gravity = Gravity.CENTER;
        layoutParams1.weight = 1;
        layoutParams1.leftMargin = 50;
        textView.setLayoutParams(layoutParams1);
        textView.setText(value);

        editText = new EditText(context);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams2.weight = 3;
        editText.clearFocus();
        editText.setLayoutParams(layoutParams2);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        validSwitch = new Switch(context);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER;
        layoutParams3.weight = 1;
        layoutParams3.rightMargin = 50;
        validSwitch.setChecked(true);
        validSwitch.setLayoutParams(layoutParams3);

        linearLayout.addView(textView);
        linearLayout.addView(editText);
        linearLayout.addView(validSwitch);
    }

    public Switch getValidSwitch() {
        return validSwitch;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        validSwitch.setOnCheckedChangeListener(listener);
    }
    public void setFocusChangeListener(View.OnFocusChangeListener listener){
        editText.setOnFocusChangeListener(listener);
    }
    public LinearLayout getLinearLayout() {
        return this.linearLayout;
    }


}

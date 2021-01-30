package com.example.quantitativedetect.view;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class STE  {
    private Switch aSwitch;
    private TextView textView;
    private EditText editText;
    private LinearLayout linearLayout;
    private Context context;
    private String value;

    public STE(Context context,String value){
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

        aSwitch = new Switch(context);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.gravity = Gravity.CENTER;
        layoutParams2.weight = 1;
        layoutParams2.rightMargin = 50;
        aSwitch.setChecked(true);
        aSwitch.setLayoutParams(layoutParams2);

        editText = new EditText(context);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER;
        layoutParams3.weight = 3;
        editText.setLayoutParams(layoutParams3);
        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        linearLayout.addView(textView);
        linearLayout.addView(editText);
        linearLayout.addView(aSwitch);
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setListener(CompoundButton.OnCheckedChangeListener listener){
        aSwitch.setOnCheckedChangeListener(listener);
    }

    public LinearLayout getLinearLayout() {
        return this.linearLayout;
    }
}

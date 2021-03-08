package com.example.quantitativedetect.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
//原来是TVS
public class TextViewSwitch extends View {
    // 2021-0218 猜测TVS:TextView Switch
    public static final int BASE_ID = 171;
    private LinearLayout linearLayout;
    private TextView textView;
    private View abbreviationCurveView;
    private Switch aSwitch;
    private String name;
    private Context context;
    private boolean hasInput = false;
    public static final int TVS_ID = 10086;

    public TextViewSwitch(Context context, String name, AbbreviationCurve abbreviationCurveView){
        super(context);
        this.context = context;
        this.name = name;
        this.abbreviationCurveView = abbreviationCurveView;
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
        layoutParams1.weight = 2;
        textView.setLayoutParams(layoutParams1);
        textView.setText(name);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.weight = 4;
//        AC.setLayoutParams(layoutParams2);

        aSwitch = new Switch(context);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER;
        layoutParams3.weight = 1;
        aSwitch.setChecked(true);
        aSwitch.setLayoutParams(layoutParams3);

        linearLayout.addView(textView);
        linearLayout.addView(abbreviationCurveView);
        linearLayout.addView(aSwitch);
    }
    public LinearLayout getLinearLayout(){return this.linearLayout;}

    public String getName() {
        return name;
    }

    public Switch getaSwitch() {
        return aSwitch;
    }

    public TextView getTextView() {
        return textView;
    }

    public boolean isHasInput() {
        return hasInput;
    }

    public View getAbbreviationCurveView() {
        return abbreviationCurveView;
    }

    public void setHasInput(boolean hasInput) {
        this.hasInput = hasInput;
    }
}

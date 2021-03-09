package com.example.quantitativedetect.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.quantitativedetect.domain.Mark;

//原来是TVS
//（1）表示是否将对应的Mark用于后面的拟合。（2）点击该视图的曲线，打开输入灰度与浓度界面
public class MarkSwitch extends View {
    // 2021-0218 猜测TVS:TextView Switch
    private Mark mark;
    public static final int BASE_ID = 171;
    private LinearLayout linearLayout;
    private TextView textView;
    private View grayCurve;
    private Switch validSwitch;
    private String name;
    private Context context;
    private boolean hasInput = false;
    public static final int TVS_ID = 10086;

    public MarkSwitch(Context context, String name, GrayCurve grayCurve,Mark mark){
        super(context);
        this.context = context;
        this.name = name;
        this.grayCurve = grayCurve;
        this.mark = mark;
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

        validSwitch = new Switch(context);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.gravity = Gravity.CENTER;
        layoutParams3.weight = 1;
        validSwitch.setChecked(true);
        validSwitch.setLayoutParams(layoutParams3);

        linearLayout.addView(textView);
        linearLayout.addView(grayCurve);
        linearLayout.addView(validSwitch);
    }
    public LinearLayout getLinearLayout(){return this.linearLayout;}

    public String getName() {
        return name;
    }

    public Switch getValidSwitch() {
        return validSwitch;
    }

    public TextView getTextView() {
        return textView;
    }

    public boolean isHasInput() {
        return hasInput;
    }

    public View getGrayCurve() {
        return grayCurve;
    }

    public void setHasInput(boolean hasInput) {
        this.hasInput = hasInput;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }
}

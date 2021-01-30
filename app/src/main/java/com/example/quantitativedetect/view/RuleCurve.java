package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.quantitativedetect.Activity.FunctionFormulaActivity;
import com.example.quantitativedetect.domain.Archive;
import com.example.quantitativedetect.domain.Rule;
import com.example.quantitativedetect.service.FunctionService;

public class RuleCurve extends BaseCoordinate {
    private Rule rule;
    private int flag = 1;
    private float conc, gray;
    private Archive archive1,archive2;
    double max,min;
    public RuleCurve(Context context, Rule rule, int width){
        super(context);
        this.rule = rule;
        super.init(width,width/2,40);
        if(rule.getSlope() > 0){
            max = FunctionService.calConc(rule,1);
            min = FunctionService.calConc(rule,0);
        }
        else {
            max = FunctionService.calConc(rule,0);
            min = FunctionService.calConc(rule,1);
        }
    }
    public void setArchive(Archive archive1, Archive archive2, Rule rule){
        this.archive1 = archive1;
        this.archive2 = archive2;
        this.rule = rule;
        if(rule.getSlope() > 0){
            max = FunctionService.calConc(rule,1);
            min = FunctionService.calConc(rule,0);
        }
        else {
            max = FunctionService.calConc(rule,0);
            min = FunctionService.calConc(rule,1);
        }
        this.flag = 2;
        invalidate();
    }

    public void formula(Canvas canvas){
        Paint paint = new Paint();
        //画点
        for(int i = 0;i < archive1.length();i++){
            paint.setStrokeWidth(10);

            float x1 = (float)(archive1.getFeature(i).getConcentration()-min)/(float)(max-min)*(wid-2*pad);

            float y1 = hei-pad-archive1.getFeature(i).getGray()*(hei-2*pad);

            paint.setColor(Color.RED);
            canvas.drawPoint(x1+pad,y1,paint);

            if(FunctionFormulaActivity.ONE_TWO == FunctionFormulaActivity.TWO){
                float x2 = (float)(archive2.getFeature(i).getConcentration()-min)/(float)(max-min)*(wid-2*pad);
                float y2 = hei-pad-archive2.getFeature(i).getGray()*(hei-2*pad);
                paint.setColor(Color.GREEN);
                canvas.drawPoint(x2+pad,y2,paint);

                paint.setStrokeWidth(1);
                paint.setColor(Color.BLACK);

                canvas.drawLine(x1+pad,y1,x2+pad,y2,paint);
            }
        }
    }

    public void setPoint(float conc, float gray, Rule rule){
        this.conc = conc;
        this.gray = gray/(float) rule.getBias();
        this.rule = rule;
        if(rule.getSlope() > 0){
            max = FunctionService.calConc(rule,1);
            min = FunctionService.calConc(rule,0);
        }
        else {
            max = FunctionService.calConc(rule,0);
            min = FunctionService.calConc(rule,1);
        }
        this.flag = 3;
        invalidate();
    }

    public void drawResult(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        float x = (float)(conc - min)/(float)(max-min)*(wid-2*pad);
        float y = hei-pad- gray *(hei-2*pad);
        canvas.drawPoint(x+pad,y,paint);

        paint.setStrokeWidth(1);
        paint.setColor(Color.GREEN);
        for(int i = pad;i < x+pad;i += 10)
            canvas.drawLine(i,y,i+5,y,paint);
        for(int i = hei-pad;i > y;i -= 10)
            canvas.drawLine(x+pad,i,x+pad,i-5,paint);
    }


    @Override
    public void invalidate(){
        super.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawLine(pad,hei-pad,wid-pad,hei-pad,paint);//X轴
        canvas.drawLine(pad,pad,pad,hei-pad,paint);//Y轴
        canvas.drawLine(pad,pad,wid-pad,pad,paint);//上X轴
        canvas.drawLine(wid-pad,pad,wid-pad,hei-pad,paint);//右Y轴
        paint.setStrokeWidth(1);
        int interval = ( hei - 2*pad )/5;
        for(int i = 1;i <= 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(pad,pad+i*interval,wid-pad,pad+i*interval,paint);
        }
        paint.setTextSize(20);
        canvas.drawText("bN/b0",pad-30,pad,paint);
        canvas.drawText(String.valueOf(0.8),pad-30,pad+interval,paint);
        canvas.drawText(String.valueOf(0.6),pad-30,pad+2*interval,paint);
        canvas.drawText(String.valueOf(0.4),pad-30,pad+3*interval,paint);
        canvas.drawText(String.valueOf(0.2),pad-30,pad+4*interval,paint);
        canvas.drawText(String.valueOf(0),pad-30,pad+5*interval,paint);

        interval = (wid - 2*pad)/5;
        for(int i = 1;i < 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(pad+i*interval,pad,pad+i*interval,hei - pad,paint);
        }

        double target = (max-min)/5;
        for(int i = 1;i < 5;i++)
            canvas.drawText(String.format("%.2f",min+target*i),i*interval+pad-10,hei-pad+20,paint);
        canvas.drawText(String.format("%.2f",min),pad-30,hei-pad+20,paint);
        canvas.drawText(String.format("%.2f",max),wid-pad,hei-pad+20,paint);

        //画方程
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(255,165,0));

        for(float i = pad;i < hei-pad-1;i++){
            double conc1 = FunctionService.calConc(rule,(hei-pad-i)/(hei-2*pad));
            double conc2 = FunctionService.calConc(rule,(hei-pad-i-1)/(hei-2*pad));
            float x1 = (float)(conc1-min)/(float) (max-min)*(wid-2*pad);
            float x2 = (float)(conc2-min)/(float) (max-min)*(wid-2*pad);
            if(x1 < 0)
                continue;
            canvas.drawLine(x1+pad,i,x2+pad,i+1,paint);
        }

        if(flag == 2)
            formula(canvas);
        else if(flag == 3)
            drawResult(canvas);
    }

}

package com.example.quantitativedetectsystem.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BaseCoordinate extends View {
    int wid = 0,hei = 0,pad = 0;
    public BaseCoordinate(Context context){
        super(context);
        setWillNotDraw(false);
    }

    public void init(int width,int height,int pad){
        this.wid = width;
        this.hei = height;
        this.pad = pad;
        invalidate();
    }
    @Override
    public void invalidate(){
        super.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        //绘制坐标系
        canvas.drawLine(pad,hei-pad,pad,pad,paint);//Y轴
        canvas.drawLine(pad,hei-pad,wid-pad,hei-pad,paint);//X轴
    }
}

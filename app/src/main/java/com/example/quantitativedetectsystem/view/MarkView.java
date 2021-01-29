package com.example.quantitativedetectsystem.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class MarkView extends View {
    RectF rectF;

    private int width,height;
    private int color = Color.GREEN;

    private Paint paint = new Paint();

    public MarkView(Context context, int width, int height){
        super(context);
        this.width = width;
        this.height = height;
        setWillNotDraw(false);
    }
    public MarkView(Context context){
        super(context);
        setWillNotDraw(false);
    }
    public void setWidth(int width){
        this.width = width;
        invalidate();
    }
    public void setHeight(int height){
        this.height = height;
        invalidate();
    }
    public void onSelected(){
        this.color = Color.RED;
        invalidate();
    }
    public void offSelected(){
        this.color = Color.GREEN;
        invalidate();
    }


    @Override
    public void invalidate(){
        super.invalidate();
    }
    @Override
    public void onDraw(Canvas canvas){
        rectF = new RectF(0,0,width,height);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(rectF,paint);
    }
}

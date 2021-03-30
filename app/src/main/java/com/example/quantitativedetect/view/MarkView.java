package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.quantitativedetect.domain.Mark;

public class MarkView extends View implements Comparable<MarkView>{
    private Mark mark;
//    private Bitmap bitmap ;// 后期添加：表示该markView在哪个bitmap。

    RectF rectF;//表示本markView
    private int adaptedWidth;
    private int adaptedHeight;
    private int adaptedX;
    private int adaptedY;
    private int color = Color.GREEN;
    private Paint paint = new Paint();


    public MarkView(Context context){
        super(context);
        setWillNotDraw(false);
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
//宽度和高度相对于屏幕，后期确认是否要改成相对与bitmap，或添加新的属性
        rectF = new RectF(0,0, getLayoutParams().width, getLayoutParams().height);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRect(rectF,paint);
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public int getAdaptedHeight() {
        return adaptedHeight;
    }

    public void setAdaptedHeight(int adaptedHeight) {
        this.adaptedHeight = adaptedHeight;
    }

    public int getAdaptedWidth() {
        return adaptedWidth;
    }

    public void setAdaptedWidth(int adaptedWidth) {
        this.adaptedWidth = adaptedWidth;
    }

    public int getAdaptedY() {
        return adaptedY;
    }

    public void setAdaptedY(int adaptedY) {
        this.adaptedY = adaptedY;
    }

    public int getAdaptedX() {
        return adaptedX;
    }

    public void setAdaptedX(int adaptedX) {
        this.adaptedX = adaptedX;
    }

    public void setAdapted(int imageDisplayAreaWidth,int imageDisplayAreaHeight,Bitmap bitmap){
        float rateX = getX()/ imageDisplayAreaWidth;
        int adaptedX = (int)(bitmap.getWidth()*rateX);
        float rateWidth = (float) getWidth()/ imageDisplayAreaWidth;
        int adaptedWidth = (int)(bitmap.getWidth()*rateWidth);

        float rateY = getY()/ imageDisplayAreaHeight;
        int adaptedY = (int)(bitmap.getHeight()*rateY);
        float rateHeight = (float) getHeight()/ imageDisplayAreaHeight;
        int adaptedHeight = (int)(bitmap.getHeight()*rateHeight);

        this.adaptedX = adaptedX;
        this.adaptedY = adaptedY;
        this.adaptedWidth = adaptedWidth;
        this.adaptedHeight = adaptedHeight;
    }
    @Override
    public int compareTo(MarkView o){
        return (int) (getX()-o.getX());
    }
}

package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import com.example.quantitativedetect.domain.Mark;

public class MarkView extends View {
    private Mark mark;
    private Bitmap bitmap ;// 后期添加：表示该markView在哪个bitmap。

    RectF rectF;//表示本markView
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
        paint.setStrokeWidth(3);
        canvas.drawRect(rectF,paint);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }
}

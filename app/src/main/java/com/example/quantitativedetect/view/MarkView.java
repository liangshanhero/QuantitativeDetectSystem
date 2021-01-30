package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

public class MarkView extends View {
    RectF rectF;


    private int color = Color.GREEN;

    private Paint paint = new Paint();

    public MarkView(Context context, int width, int height){
        this(context);

        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

        layoutParams.height = height;
        layoutParams.width = width;

        this.setLayoutParams(layoutParams);

        setWillNotDraw(false);
    }
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

        rectF = new RectF(0,0, getLayoutParams().width, getLayoutParams().height);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawRect(rectF,paint);
    }
}

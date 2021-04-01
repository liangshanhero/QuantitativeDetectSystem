package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BaseCoordinate extends View {
    int coordinateWidth = 0, coordinateHeight = 0, coordinatePad = 0;
    public BaseCoordinate(Context context){
        super(context);
        setWillNotDraw(false);
    }

    public void init(int width,int height,int pad){
        this.coordinateWidth = width;
        this.coordinateHeight = height;
        this.coordinatePad = pad;
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
        canvas.drawLine(coordinatePad, coordinateHeight - coordinatePad, coordinatePad, coordinatePad,paint);//Y轴
        canvas.drawLine(coordinatePad, coordinateHeight - coordinatePad, coordinateWidth - coordinatePad, coordinateHeight - coordinatePad,paint);//X轴
    }
}

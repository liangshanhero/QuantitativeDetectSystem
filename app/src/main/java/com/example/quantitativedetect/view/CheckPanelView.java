package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import com.example.quantitativedetect.domain.CheckPanel;

public class CheckPanelView extends View {

    private CheckPanel checkPanel;
    private Bitmap bitmap;

    RectF rectF;

    private int markGap;
    private int markQuantity;
    private int stripeQuantity;

    private int color = Color.GREEN;
    private Paint paint = new Paint();


    public CheckPanelView(Context context) {
        super(context);
        this.markGap = 50;
        this.markQuantity = 7;
        this.stripeQuantity = 6;
        setWillNotDraw(false);
    }
    public void onSelecet(){
        this.color = Color.RED;
        invalidate();
    }
    public void offSelected(){
        this.color = Color.GREEN;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        rectF = new RectF(0,0,getLayoutParams().width,getLayoutParams().height);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        canvas.drawRect(rectF,paint);
        float markWidth = ((rectF.width() - markGap*(markQuantity-1)) / markQuantity);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
//        绘制mark分区线
        for (int i = 1; i < markQuantity; i++) {
            float startX = rectF.left + i *  markWidth + (i-1) * markGap;
            float stopX = startX;
            float startY = rectF.top;
            float stopY = rectF.bottom;
//            绘制mark右边界线
            canvas.drawLine(startX,startY,stopX,stopY,paint);
//            绘制下一个mark的左边界线
            canvas.drawLine(startX+markGap,startY,stopX+markGap,stopY,paint);
        }
//        绘制stripe分区线
        float stripeHeight = rectF.height()/stripeQuantity;
        for (int i = 0; i < markQuantity; i++) {
            for (int j = 0; j < stripeQuantity-1; j++) {
                float startX = rectF.left + i*(markWidth + markGap);
                float stopX = startX + markWidth;
                float startY = rectF.top + (j+1)*stripeHeight;
                float stopY = startY;
                canvas.drawLine(startX,startY,stopX,stopY,paint);
            }
        }



    }


    public int getMarkGap() {
        return markGap;
    }

    public void setMarkGap(int markGap) {
        this.markGap = markGap;
    }

    public int getMarkQuantity() {
        return markQuantity;
    }

    public void setMarkQuantity(int markQuantity) {
        this.markQuantity = markQuantity;
    }

    public int getStripeQuantity() {
        return stripeQuantity;
    }

    public void setStripeQuantity(int stripeQuantity) {
        this.stripeQuantity = stripeQuantity;
    }

    public CheckPanel getCheckPanel() {
        return checkPanel;
    }

    public void setCheckPanel(CheckPanel checkPanel) {
        this.checkPanel = checkPanel;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

package com.example.quantitativedetectsystem.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


public class AbbreviationCurve extends BaseCoordinate {
    private int[] points;
    private int[] features;
    private float CL = 1;
    private float zoom = 1;
    private int FLAG = 0;
    public AbbreviationCurve(Context context,int width,int[] points,float zoom,int[] features,int FLAG){
        super(context);
        super.init(width,width/2,40);
        this.points = points;
        this.zoom = zoom*(width/2-2*pad);//width/3为View的高度，而width/3-2*pad才是Y轴的长度
        this.features = features;
        this.CL = points[features[0]];
        this.FLAG = FLAG;
        if(FLAG == 0){
            this.zoom = zoom*(width/3-2*pad);
            super.init(width,width/3,40);
        }
    }
    public void setFeatures(int[] features){
        this.features = features;
        invalidate();
    }
    public void setWid(int wid){
        super.init(wid,wid/3,25);
        invalidate();
    }
    public void setFLAG(int flag){
        this.FLAG = flag;
        if(FLAG==1)
        invalidate();
    }
    @Override
    public void invalidate(){
        super.invalidate();
    }

    public void bigDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawLine(pad,pad,wid-pad,pad,paint);//上X轴
        canvas.drawLine(wid-pad,pad,wid-pad,hei-pad,paint);//右Y轴
        paint.setStrokeWidth(1);
        int interval = ( hei - 2*pad )/5;
        for(int i = 1;i <= 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(pad,pad+i*interval,wid-pad,pad+i*interval,paint);
        }
        paint.setTextSize(20);
        canvas.drawText("T/C",pad-30,pad,paint);
        canvas.drawText(String.valueOf(0.8),pad-30,pad+interval,paint);
        canvas.drawText(String.valueOf(0.6),pad-30,pad+2*interval,paint);
        canvas.drawText(String.valueOf(0.4),pad-30,pad+3*interval,paint);
        canvas.drawText(String.valueOf(0.2),pad-30,pad+4*interval,paint);
        canvas.drawText(String.valueOf(0),pad-30,pad+5*interval,paint);

        paint.setStrokeWidth(3);
        for(int i = 0;i < points.length-1;i++){
            paint.setColor(Color.rgb(255,165,0));
            float sx = (float)i/points.length * (wid-2*pad) + pad;
            float sy = hei - pad - ((float)points[i])/CL*zoom;
            float ex = (float)(i+1)/points.length * (wid-2*pad) + pad;
            float ey = hei - pad - ((float)points[i+1])/CL*zoom;
            canvas.drawLine(sx,sy,ex,ey,paint);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(9);
        for(int i = 0;i < features.length;i++){
            paint.setColor(Color.RED);
            if(i == 0)
                paint.setColor(Color.GREEN);
            float x = (float)features[i]/points.length * (wid - 2*pad) + pad;
            float y = hei - pad - (float)points[features[i]]/CL * zoom;
            canvas.drawPoint(x,y,paint);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(FLAG == 0){
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            //画曲线
            for(int i = 0;i < points.length-1;i++){
                float sx = (float)i/points.length * (wid-2*pad) + pad;
                float sy = hei - pad - ((float)points[i])/CL*zoom;
                float ex = (float)(i+1)/points.length * (wid-2*pad) + pad;
                float ey = hei - pad - ((float)points[i+1])/CL*zoom;
                canvas.drawLine(sx,sy,ex,ey,paint);
            }
            paint.setColor(Color.RED);
            paint.setStrokeWidth(4);
            for(int i = 1;i < features.length;i++){
                float x = (float)features[i]/points.length * (wid - 2*pad) + pad;
                float y = hei - pad - (float)points[features[i]]/CL * zoom;
                canvas.drawPoint(x,y,paint);
            }
        }
        else if(FLAG == 1)
            bigDraw(canvas);
    }
}

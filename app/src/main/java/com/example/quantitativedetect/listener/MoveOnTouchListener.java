package com.example.quantitativedetect.listener;

import android.view.MotionEvent;
import android.view.View;

import com.example.quantitativedetect.Activity.FunctionSampleActivity;


public class MoveOnTouchListener implements View.OnTouchListener {
    private int xDelta;
    private int yDelta;
    private FunctionSampleActivity functionSampleActivity;
    public static int imageWidth,imageHeight;

    public MoveOnTouchListener(FunctionSampleActivity functionSampleActivity){
        this.functionSampleActivity = functionSampleActivity;
        imageWidth = functionSampleActivity.getImageWidth();
        imageHeight = functionSampleActivity.getImageHeight();
    }
    @Override
    public boolean onTouch(View view, MotionEvent event){
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                    functionSampleActivity.setSelectingID(view.getId());
                xDelta = x - (int)view.getX();
                yDelta = y - (int)view.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int xDistance = x - xDelta;
                int yDistance = y - yDelta;
                if(xDistance < 0)
                    xDistance = 0;
                if (yDistance < 0)
                    yDistance = 0;
                if (xDistance + view.getWidth() > imageWidth)
                    xDistance = imageWidth - view.getWidth();
                if (yDistance + view.getHeight() > imageHeight)
                    yDistance = imageHeight - view.getHeight();
                view.setX(xDistance);
                view.setY(yDistance);
                break;
        }
        functionSampleActivity.getRelativeLayout().invalidate();
        return true;
    }

    public static void setImageHeight(int Height){
        imageHeight = Height;
    }
}

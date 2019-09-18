package cn.com.fyl.learn.pathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Date:2019/9/18 0018
 * Time:上午 11:18
 * author:fengyalu
 */

public class PathView extends View {

    private Paint paint;
    private Path path;

    public void clear() {
        if (null != path) {
            // 清空所有已经画过的path至原始状态
            path.reset();
        }
        invalidate();
    }

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6);
        paint.setColor(Color.BLACK);
        //样式为描边
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    float fromX = 0;
    float fromY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                fromX = x;
                fromY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - fromX) > 0 || Math.abs(y - fromY) > 0) {
                    path.lineTo(x, y);
                    fromX = x;
                    fromY = y;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }

        //重新绘制
        invalidate();
        return true;
    }

}

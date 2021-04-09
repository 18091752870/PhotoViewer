/*
实现功能：
1. 单击拖动移动图片，可以设定距离初始位置的最大偏移量
2. 双指收缩和旋转，收缩最大比例可以设置
3. 双击初始化/最大化，在图片已经变化时双击可以将图片恢复至初始状态，在初始状态双击可以将图片放大到最大
使用方法：
直接使用ZoomView控件，将图片放入即可
示例：
<com.example.MyProject.MyView
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:layout_centerInParent="true">
        <ImageView
            android:id="@+id/iv"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/white"
            android:scaleType="centerCrop"
            android:src="@drawable/mypic"/>
</com.example.MyProject.MyView>
*/
package com.example.photoviewer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import static android.content.ContentValues.TAG;

public class MyView extends RelativeLayout {
    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float maxScale = 5;//最大伸缩比例
    private float minScale = 0.2f;//最小伸缩比例
    private float rotation; // 旋转角度

    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    private int moveType; // 0=未选择，1=拖动，2=缩放
    private int maxHorLenth = 500; //最大水平位移偏移量
    private int maxVerLenth = 800; //最大垂直位移偏移量

    //双击事件中的变量
    private MotionEvent ev1 = null;
    private int flag = 0;//双击事件判定
    private float DoubleTime = 400;//双击之间最大时间间隔
    private int DoubleDis = 10000;//双击之间最大距离间隔

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if(ev1!=null)
                {
                    if((event.getEventTime() - ev1.getEventTime()) < DoubleTime)
                    {
                        int deltaX =(int) ev1.getX() - (int)event.getX();
                        int deltaY =(int) ev1.getY()- (int)event.getY();
                        if((deltaX * deltaX + deltaY * deltaY) < DoubleDis)
                        {
                            if(scale==1)
                            {
                                if((getTranslationX() !=0)||(getTranslationY()!=0)||(getRotation()!=0))
                                {
                                    actionX = 0;
                                    actionY = 0;
                                    translationX = 0;
                                    translationY = 0;
                                    setTranslationX(0);
                                    setTranslationY(0);
                                    setRotation(0);
                                }
                                else
                                {
                                    scale = maxScale;
                                    setScaleX(scale);
                                    setScaleY(scale);
                                }
                            }
                            else if((scale > 1)||(scale < 1))
                            {
                                scale = 1;
                                setScaleX(scale);
                                setScaleY(scale);
                                setRotation(0);

                                actionX = 0;
                                actionY = 0;
                                translationX = 0;
                                translationY = 0;
                                setTranslationX(0);
                                setTranslationY(0);
                            }
                            flag = 1;
                        }
                    }
					ev1 = null;
					break;
                }

                moveType = 1;
                actionX = event.getRawX();
                actionY = event.getRawY();
                ev1 = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == 1) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;

                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                    if(translationX > maxHorLenth) {
                        actionX = actionX - (translationX-maxHorLenth);
                        translationX = maxHorLenth;
                    }
                    else if(translationX < -maxHorLenth) {
                        actionX = actionX + (-translationX-maxHorLenth);
                        translationX = -maxHorLenth;
                    }
                    if(translationY > maxVerLenth) {
                        actionY = actionY - (translationY-maxVerLenth);
                        translationY = maxVerLenth;
                    }
                    else if(translationY < -maxVerLenth) {
                        actionY = actionY + (-translationY-maxVerLenth);
                        translationY = -maxVerLenth;
                    }
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                } else if (moveType == 2) {
                    scale = scale * getSpacing(event) / spacing;
                    if(scale > maxScale)
                    {
                        scale = maxScale;
                    }
                    else if(scale < minScale)
                    {
                        scale = minScale;
                    }
                    setScaleX(scale);
                    setScaleY(scale);
                    rotation = rotation + getDegree(event) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    setRotation(rotation);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(ev1 == null && flag==0)
                {
                    ev1 = MotionEvent.obtain(event);
                }
                else if(flag == 1)
                {
                    flag = 0;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }
        return super.onTouchEvent(event);
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
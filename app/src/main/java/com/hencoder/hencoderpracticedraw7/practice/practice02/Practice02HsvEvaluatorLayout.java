package com.hencoder.hencoderpracticedraw7.practice.practice02;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.hencoder.hencoderpracticedraw7.R;

public class Practice02HsvEvaluatorLayout extends RelativeLayout {
    Practice02HsvEvaluatorView view;
    Button animateBt;

    public Practice02HsvEvaluatorLayout(Context context) {
        super(context);
    }

    public Practice02HsvEvaluatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Practice02HsvEvaluatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        view = (Practice02HsvEvaluatorView) findViewById(R.id.objectAnimatorView);
        animateBt = (Button) findViewById(R.id.animateBt);

        animateBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofInt(view, "color", 0xffff0000, 0xff00ff00);
                animator.setEvaluator(new HsvEvaluator()); // 使用自定义的 HsvEvaluator
                animator.setInterpolator(new LinearInterpolator());
                animator.setDuration(2000);
                animator.start();
            }
        });
    }

    private class HsvEvaluator implements TypeEvaluator<Integer> {

        float[] startHsv;
        float[] endHsv;
        float[] outHsv = new float[3];

        // 重写 evaluate() 方法，让颜色按照 HSV 来变化
        //HSV 为 Hue 色相[0,360)、Saturation 饱和度[0,1]和 Value 明亮度[0,1]。
        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            Log.d("", "fraction = " + fraction + " start " + startValue + " end " + endValue);
            //转换颜色。
            if (startHsv == null) {
                startHsv = new float[3];
                Color.colorToHSV(startValue, startHsv);
                endHsv = new float[3];
                Color.colorToHSV(endValue, endHsv);

//                float hueDelta = endHsv[0] - startHsv[0];
//                if (hueDelta > 180) {
//                    endHsv[0] -= 360;
//                } else if (hueDelta < -180) {
//                    endHsv[0] += 360;
//                }
            }



            outHsv[0] = startHsv[0] + (endHsv[0] - startHsv[0]) * fraction;
            outHsv[1] = startHsv[1] + (endHsv[1] - startHsv[1]) * fraction;
            outHsv[2] = startHsv[2] + (endHsv[2] - startHsv[2]) * fraction;


            //猜测作用是让颜色变化更加平滑。
            float outHue = outHsv[0];
            if (outHue > 360) {
                outHsv[0] -= 360;
            } else if (outHue < 0) {
                outHsv[0] += 360;
            }

            //32位颜色，分别用 8 位表示 ARGB，右移 8 * 3 = 24 位得到 Alpha.
            int alpha = startValue >> 24 + (int) ((endValue >> 24 - startValue >> 24) * fraction);
            return Color.HSVToColor(alpha, outHsv);
        }
    }
}

package com.example.cchen.test1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.cchen.test1.R;

/**
 * Created by cchen on 2016/6/18.
 */
public class KyChartView extends FrameLayout {
    public float MIN_VALUE = 0f;
    public float MAX_VALUE = 100f;
    public int valueTextId;

    public KyChartView(Context context) {
        super(context);
        init(context);
    }

    public KyChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KyChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.bar_chart, null);
        addView(inflate, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setTitle(String text) {
        TextView txt_bar = (TextView) findViewById(R.id.txt_bar);
        txt_bar.setText(text);
    }

    public void setValue(float value) {
        if (value > MAX_VALUE) value = MAX_VALUE;
        if (value < MIN_VALUE) value = MIN_VALUE;

        TextView txt_value = (TextView) findViewById(R.id.txt_value);
        txt_value.setText(getContext().getString(valueTextId, value));
        View view_value = findViewById(R.id.view_value);
        view_value.getLayoutParams().height =
                (int) ((getResources().getDimensionPixelSize(R.dimen.bar_container_height)
                        - getResources().getDimensionPixelSize(R.dimen.bar_container_border_width) * 2)
                        * value / (MAX_VALUE - MIN_VALUE));
    }

}

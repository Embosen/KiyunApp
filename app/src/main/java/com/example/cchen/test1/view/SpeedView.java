/**
 * Copyright 2014  XCL-Charts
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Project XCL-Charts
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version v0.1
 */

package com.example.cchen.test1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;

import com.example.cchen.test1.R;

import org.xclcharts.chart.DialChart;
import org.xclcharts.common.MathHelper;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotAttrInfo;
import org.xclcharts.renderer.plot.Pointer;
import org.xclcharts.view.GraphicalView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @ClassName DialChart例子
 * @Description 仪表盘例子
 */
public class SpeedView extends GraphicalView {

    public static final float MAX_VALUE = 80f;
    private String TAG = "DialChart03View";

    private DialChart chart = new DialChart();
    public float mPercentage = 0.0f;

    public SpeedView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }


    private void initView() {
        chartRender();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        chart.setChartRange(w, h);
    }

    public void chartRender() {
        try {

            //设置标题背景
            chart.setApplyBackgroundColor(true);
            chart.setBackgroundColor(Color.BLACK);//.rgb(28, 129, 243));
            //绘制边框
            chart.showRoundBorder();

            //设置当前百分比
            chart.getPointer().setPercentage(mPercentage);

            //设置指针长度
            chart.getPointer().setLength(0.75f);

            //增加轴
            addAxis();
            /////////////////////////////////////////////////////////////
            //增加指针
            addPointer();
            //设置附加信息
            addAttrInfo();
            /////////////////////////////////////////////////////////////

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }

    }

    public void addAxis() {

        List<Float> ringPercentage = new ArrayList<Float>();
        float rper = MathHelper.getInstance().div(1, 8); //相当于40%	//270, 4
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);
        ringPercentage.add(rper);

        List<Integer> rcolor = new ArrayList<Integer>();
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.WHITE);
        rcolor.add(Color.RED);
        chart.addStrokeRingAxis(0.95f, 0.85f, ringPercentage, rcolor);


        List<String> rlabels = new ArrayList<String>();
        rlabels.add("0");
        rlabels.add("5");
        rlabels.add("10");
        rlabels.add("15");
        rlabels.add("20");
        rlabels.add("25");
        rlabels.add("30");
        rlabels.add("35");
        rlabels.add("40");
        rlabels.add("45");
        rlabels.add("50");
        rlabels.add("55");
        rlabels.add("60");
        rlabels.add("65");
        rlabels.add("70");
        rlabels.add("75");
        rlabels.add("80");
        chart.addInnerTicksAxis(0.85f, rlabels);


        chart.getPlotAxis().get(0).getFillAxisPaint().setColor(Color.BLACK);
        chart.getPlotAxis().get(1).getFillAxisPaint().setColor(Color.rgb(28, 129, 243));
        chart.getPlotAxis().get(1).getTickLabelPaint().setColor(Color.WHITE);
        chart.getPlotAxis().get(1).getTickLabelPaint().setTextSize(getResources().getInteger(R.integer.dial_tick_label_size));
        chart.getPlotAxis().get(1).getTickMarksPaint().setColor(Color.WHITE);
        chart.getPlotAxis().get(1).hideAxisLine();
        chart.getPlotAxis().get(1).setDetailModeSteps(3);

        chart.getPointer().setPointerStyle(XEnum.PointerStyle.LINE);
        chart.getPointer().getPointerPaint().setColor(Color.rgb(217, 34, 34));
        chart.getPointer().getPointerPaint().setStrokeWidth(3);
        chart.getPointer().getPointerPaint().setStyle(Style.STROKE);
        chart.getPointer().hideBaseCircle();

    }

    //增加指针
    public void addPointer() {
        chart.addPointer();
        List<Pointer> mp = chart.getPlotPointer();
        mp.get(0).setPercentage(mPercentage);
        //设置指针长度
        mp.get(0).setLength(0.75f);
        mp.get(0).getPointerPaint().setColor(Color.WHITE);
        mp.get(0).setPointerStyle(XEnum.PointerStyle.LINE);
        mp.get(0).hideBaseCircle();

    }


    private void addAttrInfo() {
        /////////////////////////////////////////////////////////////
        PlotAttrInfo plotAttrInfo = chart.getPlotAttrInfo();
        //设置附加信息
        Paint paintTB = new Paint();
        paintTB.setColor(Color.WHITE);
        paintTB.setTextAlign(Align.CENTER);
        paintTB.setTextSize(30);
        paintTB.setAntiAlias(true);
        plotAttrInfo.addAttributeInfo(XEnum.Location.TOP, getContext().getString(R.string.speed), 0.3f, paintTB);

        Paint paintBT = new Paint();
        paintBT.setColor(Color.WHITE);
        paintBT.setTextAlign(Align.CENTER);
        paintBT.setTextSize(35);
        paintBT.setFakeBoldText(true);
        paintBT.setAntiAlias(true);
        float v = mPercentage * MAX_VALUE;
        BigDecimal bigDecimal = new BigDecimal(v).setScale(0, BigDecimal.ROUND_HALF_UP);
        plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM,
                bigDecimal + "", 0.6f, paintBT);

        Paint paintBT2 = new Paint();
        paintBT2.setColor(Color.WHITE);
        paintBT2.setTextAlign(Align.CENTER);
        paintBT2.setTextSize(30);
        paintBT2.setFakeBoldText(true);
        paintBT2.setAntiAlias(true);
        plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM, "km/h", 0.8f, paintBT2);
    }

    public void setCurrentStatus(float percentage) {
        mPercentage = percentage;
        chart.clearAll();

        //设置当前百分比
        chart.getPointer().setPercentage(mPercentage);
        addAxis();
        //增加指针
        addPointer();
        addAttrInfo();
    }


    @Override
    public void render(Canvas canvas) {
        // TODO Auto-generated method stub
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}

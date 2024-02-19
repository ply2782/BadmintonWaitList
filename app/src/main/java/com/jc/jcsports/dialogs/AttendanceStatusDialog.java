package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jc.jcsports.R;
import com.jc.jcsports.databinding.DialogAttendancestatusBinding;
import com.jc.jcsports.model.Calculate;
import com.jc.jcsports.utils.ApisCall;
import com.jc.jcsports.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

public class AttendanceStatusDialog extends Dialog {
    private final Context ctx;
    private DialogAttendancestatusBinding binding;
    private final String TAG = "AttendanceStatusDialog";
    private final Handler handler;

    public AttendanceStatusDialog(@NonNull Context context) {
        super(context);
        handler = new Handler();
        ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_attendancestatus, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);
        dataInit();
        clickListener();
        try {
            JSONObject object = new JSONObject();
            object.put("width", -1);
            object.put("height", -1);
            Util.dialogLayoutInfoUpdate(ctx, getWindow(), object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void dataInit() {
        ApisCall.calculatedAttendanceList().subscribe(new DisposableSingleObserver<>() {
            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Map<Integer, Calculate> integerCalculateMap) {
                List<Calculate> calculateList = Observable.fromIterable(integerCalculateMap.entrySet()).map(Map.Entry::getValue)
                        .sorted((calculate1, calculate2) -> Integer.compare(calculate2.dateCtn, calculate1.dateCtn))
                        .take(20)
                        .toList()
                        .blockingGet();
                handler.post(() -> initBarChart(binding.chart, calculateList));
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }
        });
    }

    private void initBarChart(BarChart barChart, List<Calculate> calculateList) {
        // Find max and min dateCtn
        int maxDateCtn = Integer.MIN_VALUE;
        for (Calculate calculate : calculateList) {
            maxDateCtn = Math.max(maxDateCtn, calculate.dateCtn);
        }


        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false);
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false);
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false);

        Description description = new Description();
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.setEnabled(false);
        barChart.setDescription(description);

        // X, Y 바의 애니메이션 효과
        barChart.animateY(1000);
        barChart.animateX(1000);


        // X 축 레이블을 이름으로 설정하기 위한 IAxisValueFormatter 구현
        ValueFormatter xAxisFormatter = new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < calculateList.size()) {
                    // 해당 인덱스의 Calculate 객체에서 이름을 가져와 반환
                    return calculateList.get((int) value).name;
                } else {
                    return ""; // 유효한 인덱스 범위가 아닌 경우 빈 문자열 반환
                }
            }
        };
        // 바텀 좌표 값
        XAxis xAxis = barChart.getXAxis();

        // x축 위치 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 그리드 선 수평 거리 설정
        xAxis.setGranularity(1f);
        // x축 텍스트 컬러 설정
        xAxis.setTextColor(Color.BLACK);
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false);
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(xAxisFormatter); // IAxisValueFormatter 설정
        xAxis.setTextSize(10f);
        // 하단 번호 짝수만 나오는 걸 갯수대로 나오게 설정
        xAxis.setLabelCount(calculateList.size());


        YAxis leftAxis = barChart.getAxisLeft();
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false);
        // 좌측 텍스트 컬러 설정
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value >= 0 && value < calculateList.size()) {
                    // 해당 인덱스의 Calculate 객체에서 이름을 가져와 반환
                    return String.valueOf(calculateList.get((int) value).dateCtn);
                } else {
                    return ""; // 유효한 인덱스 범위가 아닌 경우 빈 문자열 반환
                }
            }
        });
        leftAxis.setGranularity(1f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMaximum(maxDateCtn);
        leftAxis.setAxisMinimum(0);


        YAxis rightAxis = barChart.getAxisRight();
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false);
        // 우측 텍스트 컬러 설정
        rightAxis.setTextColor(Color.GREEN);
        rightAxis.setAxisMaximum(maxDateCtn);
        rightAxis.setAxisMinimum(0);


        // 바차트의 타이틀
        Legend legend = barChart.getLegend();
        // 범례 모양 설정 (default = 정사각형)
        legend.setForm(Legend.LegendForm.LINE);
        // 타이틀 텍스트 사이즈 설정
        legend.setTextSize(20f);
        // 타이틀 텍스트 컬러 설정
        legend.setTextColor(Color.BLACK);
        // 범례 위치 설정
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        // 범례 방향 설정
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // 차트 내부 범례 위치하게 함 (default = false)
        legend.setDrawInside(false);
        setData(barChart, calculateList);
    }

    // 차트 데이터 설정
    private void setData(BarChart barChart, List<Calculate> calculates) {
        // Zoom In / Out 가능 여부 설정
        barChart.setScaleEnabled(false);
        List<BarEntry> valueList = new ArrayList<>();
        String title = ctx.getString(R.string.attendanceStatus);
        for (int i = 0; i < calculates.size(); i++) {
            Calculate cal = calculates.get(i);
            valueList.add(new BarEntry(i, cal.dateCtn));
        }
        BarDataSet barDataSet = new BarDataSet(valueList, title);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(15f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        barDataSet.setColors(
                Color.rgb(207, 248, 246),
                Color.rgb(148, 212, 212),
                Color.rgb(136, 180, 187),
                Color.rgb(118, 174, 175),
                Color.rgb(255, 99, 132),
                Color.rgb(255, 159, 64),
                Color.rgb(255, 205, 86),
                Color.rgb(154, 62, 35),
                Color.rgb(53, 102, 255),
                Color.rgb(101, 220, 25));
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                // 소수점 이하를 제거하여 정수로 표시
                return String.valueOf((int) value);
            }
        });
        barChart.invalidate();

    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void clickListener() {
        binding.closeBtn.setOnClickListener((view -> dismiss()));
    }
}

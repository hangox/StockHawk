package com.udacity.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.History;
import com.udacity.stockhawk.databinding.ActivityStockHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class StockHistoryActivity extends MyActivity<ActivityStockHistoryBinding>  {

    private String mHistory;
    private String mSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSymbol = getIntent().getStringExtra("symbol");
        mHistory = getIntent().getStringExtra("history");
        setTitle(mSymbol);
        Timber.d("symbol:%s \n  history: \n %s" , mSymbol, mHistory);

        setUpChart();


    }

    @NonNull
    private void setUpChart() {
        LineChart chart = mDataBinding.chart1;
        chart.setPinchZoom(true);

        //转为为历史格式的数据
        final String[] timeAndHistorys  = mHistory.split("\n");
        final List<History> histories = new ArrayList<>();
        for (int i = timeAndHistorys.length - 1; i >= 0 ; i--) {
            histories.add(new History(timeAndHistorys[i]));
        }
        Collections.sort(histories, new Comparator<History>() {
            @Override
            public int compare(History o1, History o2) {
                return (int) (o1.mDate.getTime() - o2.mDate.getTime());
            }
        });

        //添加到轴中
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < histories.size(); i++) {
            yVals.add(new Entry(i,histories.get(i).mValue));
        }

        LineDataSet dataSet = new LineDataSet(yVals,mSymbol);
        int colorAccent = getResources().getColor(R.color.colorAccent);
        dataSet.setCircleColor(colorAccent);
        dataSet.setValueTextColor(colorAccent);
        dataSet.setColor(colorAccent);
        final LineData data = new LineData();
        data.addDataSet(dataSet);
        chart.setData(data);

        final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd", Locale.CHINA);
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Timber.d(histories.get((int) value)+"");
                return format.format(histories.get((int) value).mDate);
            }
        });
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value+"%";
            }
        });




    }

    private void setData(LineChart chart) {
        // create a dataset and give it a type

        ArrayList<Entry> values = new ArrayList<Entry>();

        String[] timeAndHistorys  = mHistory.split("\n");
        for (int i = 0; i < timeAndHistorys.length; i++) {
            String[] timeHistorySplit = timeAndHistorys[i].split(",");
            values.add(new Entry(i,i));
        }


        LineDataSet dataSet = new LineDataSet(values, "DataSet 1");
        dataSet.setColor(Color.BLUE);


        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);
        chart.setData(new LineData());
    }

    @Override
    protected int provideLayoutId() {
        return R.layout.activity_stock_history;
    }


}

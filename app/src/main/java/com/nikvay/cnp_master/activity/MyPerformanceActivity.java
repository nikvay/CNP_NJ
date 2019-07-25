package com.nikvay.cnp_master.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.NotificationAdapter;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.common.VolunteerClickListener;
import com.nikvay.cnp_master.model.NotificationModule;
import com.nikvay.cnp_master.utils.CalenderUtil;
import com.nikvay.cnp_master.utils.SharedUtil;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyPerformanceActivity extends AppCompatActivity implements VolleyCompleteListener
{


    private ImageView iv_back_image_activity;
    private PieChart myPerformanceFirstPieChart, myPerformanceSecondPieChart;
    private PieData pieDataFirstChart, pieDataSecondChart;
    private boolean isStartDate = false;
    ArrayList<Integer> colors = new ArrayList<>();
    SharedUtil sharedUtil;
    private String user_id;
    private TextView textAllPerformance,
            textDateStartPerformance,
            textDateEndPerformance;
    String currentDate;


    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<Entry> entriesSecond = new ArrayList<>();

    private int order_received=0, generated_qu=0, lost_qu=0, attendance=0, visit=0, collection=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_performance);

        sharedUtil = new SharedUtil(this);
        user_id = sharedUtil.getUserDetails().getUser_id();

        initialization();


        events();

        callMyPerformance();




    }
    private void initialization() {
        iv_back_image_activity = findViewById(R.id.iv_back_image_activity);
        myPerformanceFirstPieChart = findViewById(R.id.myPerformanceFirstPieChart);
        myPerformanceSecondPieChart = findViewById(R.id.myPerformanceSecondPieChart);
        textDateStartPerformance = findViewById(R.id.textDateStartPerformance);
        textDateEndPerformance = findViewById(R.id.textDateEndPerformance);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        textDateEndPerformance.setText(currentDate);
        textDateStartPerformance.setText(currentDate);

    }
    private void events() {
        iv_back_image_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        textDateStartPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                showDatePickerDialog(v);

            }
        });
        textDateEndPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = false;
                showDatePickerDialog(v);

            }
        });



    }


    private void callMyPerformance() {
        entries.clear();
        entriesSecond.clear();
        user_id = sharedUtil.getUserDetails().getUser_id();
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.MY_PERFORMANCE);
        map.put("sales_person_id", user_id);
        map.put("first_date", textDateStartPerformance.getText().toString());
        map.put("last_date", textDateEndPerformance.getText().toString());
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.MY_PERFORMANCE, true);


    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.MY_PERFORMANCE: {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");

                    /*String first_date = jsonObject.getString("first_date");
                    String last_date = jsonObject.getString("last_date");
                    textDateEndPerformance.setText(first_date);
                    textDateStartPerformance.setText(last_date);
                    */

                    order_received = Integer.parseInt(jsonObject.getString("order_recive"));
                    generated_qu = Integer.parseInt(jsonObject.getString("genrated_quot_count"));
                    lost_qu = Integer.parseInt(jsonObject.getString("order_lost_count"));
                    attendance =Integer.parseInt( jsonObject.getString("attendence_count"));
                    visit = Integer.parseInt(jsonObject.getString("visit_count"));
                    collection = Integer.parseInt(jsonObject.getString("collection_count"));

                    pieChartSet();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }

    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {

    }


    private void pieChartSet() {
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.green));


        Entry entryGQ = new Entry(order_received, 0);
        Entry entryLQ = new Entry(generated_qu, 1);
        Entry entryOR = new Entry(lost_qu, 2);


        entries.add(entryGQ);
        entries.add(entryLQ);
        entries.add(entryOR);
        //float perAttendance=(attendance/100)*30;
        Entry entryGQSecond = new Entry(0, 0);
        Entry entryLQSecond = new Entry(visit, 1);
        Entry entryORSecond = new Entry(collection, 2);

        entriesSecond.add(entryGQSecond);
        entriesSecond.add(entryLQSecond);
        entriesSecond.add(entryORSecond);


        pieDataFirstChart = new PieData(getFirstXValue(), getFirstYValue());
        myPerformanceFirstPieChart.setData(pieDataFirstChart);
        myPerformanceFirstPieChart.setHoleRadius(15);
        myPerformanceFirstPieChart.setDescription("");
        myPerformanceFirstPieChart.invalidate();
        Legend legendFirstChart = myPerformanceFirstPieChart.getLegend();
        legendFirstChart.setTextSize(12);
        legendFirstChart.setTextColor(getResources().getColor(R.color.colorPrimary));


        pieDataSecondChart = new PieData(getSecondXValue(), getSecondYValue());
        myPerformanceSecondPieChart.setData(pieDataSecondChart);
        myPerformanceSecondPieChart.setHoleRadius(15);
        myPerformanceSecondPieChart.setDescription("");
        myPerformanceSecondPieChart.invalidate();
        Legend legendSecondChart = myPerformanceSecondPieChart.getLegend();
        legendSecondChart.setTextSize(12);
        legendSecondChart.setTextColor(getResources().getColor(R.color.colorPrimary));



    }

    private PieDataSet getFirstYValue() {
        PieDataSet pieDataSet;

        pieDataSet = new PieDataSet(entries, "");

        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);

        return pieDataSet;

    }

    private ArrayList<String> getFirstXValue() {

        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Generated Quotation");
        xValues.add("Lost Quotation");
        xValues.add("Order Received");
        return xValues;
    }


    private PieDataSet getSecondYValue() {
        PieDataSet pieDataSet;

        pieDataSet = new PieDataSet(entriesSecond, "");
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setDrawValues(true);

        return pieDataSet;

    }

    private ArrayList<String> getSecondXValue() {

        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Attendance");
        xValues.add("Visit");
        xValues.add("Collection");
        return xValues;
    }


    private void showDatePickerDialog(final View v) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Time chosenDate = new Time();
                        chosenDate.set(dayOfMonth, monthOfYear, year);

                        long dateAttendance = chosenDate.toMillis(true);
                        currentDate = String.valueOf(DateFormat.format("dd-MM-yyyy", dateAttendance));

                        if(v.getId()==R.id.textDateEndPerformance)
                        {
                            textDateEndPerformance.setText(currentDate);
                            callMyPerformance();
                        }
                        if(v.getId()==R.id.textDateStartPerformance)
                        {
                            textDateStartPerformance.setText(currentDate);
                                callMyPerformance();
                        }



                    }
                }, year, month, date);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

}

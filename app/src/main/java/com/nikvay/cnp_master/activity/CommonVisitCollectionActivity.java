package com.nikvay.cnp_master.activity;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.ViewCollectionAdapter;
import com.nikvay.cnp_master.adapter.ViewVisitAdapter;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.common.VibrateOnClick;
import com.nikvay.cnp_master.model.CollectionModel;
import com.nikvay.cnp_master.model.VisitListModel;
import com.nikvay.cnp_master.utils.CalenderUtil;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.utils.UserData;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class CommonVisitCollectionActivity extends AppCompatActivity implements VolleyCompleteListener {
    private TextView textAllCollection,
            textDateStartCollection,
            textDateEndCollection;
    private RecyclerView recyclerMyCollection;
    private UserData userData;
    private CollectionModel collectionModel;
    private ArrayList<CollectionModel> collectionArrayList = new ArrayList<>();
    private boolean isStartDate = false;
    private Button btnViewVC,
            btnAddVC;
    private String mActivityType = null;
    private Toolbar toolbar;
    private ArrayList<VisitListModel> visitArraylist = new ArrayList<>();
    private VisitListModel visitListModel;
    private ViewVisitAdapter visitAdapter;
    private ViewCollectionAdapter collectionAdapter;
    public static boolean isAdded = false;
    private SwipeRefreshLayout refreshMyCustomer;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_collection);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActivityType = getIntent().getExtras().getString(StaticContent.ActivityType.ACTIVITY_TYPE);
        localBrodcastInitialize();
        initialize();
    }

    private void localBrodcastInitialize()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY);
            if (message.equals(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY)) {
                finish();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize()
    {
        VibrateOnClick.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        fab = findViewById(R.id.fab);
        userData = new UserData(getApplicationContext());
        textAllCollection = findViewById(R.id.textAllCollection);
        textDateStartCollection = findViewById(R.id.textDateStartCollection);
        textDateEndCollection = findViewById(R.id.textDateEndCollection);
        refreshMyCustomer = findViewById(R.id.refreshMyCustomer);
        btnViewVC = findViewById(R.id.btnViewVC);
        btnAddVC = findViewById(R.id.btnAddVC);
        recyclerMyCollection = findViewById(R.id.recyclerMyCollection);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerMyCollection.setLayoutManager(manager);
        if (mActivityType.equals(StaticContent.ActivityType.VIEW_COLLECTION)) {
            getSupportActionBar().setTitle("Collection");
            callViewCollectionWS(ServerConstants.serverUrl.VIEW_COLLECTION, ServerConstants.ServiceCode.VIEW_COLLECTION);
            btnAddVC.setText("Add Collection");
        } else {
            getSupportActionBar().setTitle("Visit");
            callViewCollectionWS(ServerConstants.serverUrl.VIEW_VISITS, ServerConstants.ServiceCode.VIEW_VISITS);
            btnAddVC.setText("Add Visit");
        }

        events();
    }

    private void events() {
        textDateStartCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = true;
                showDatePickerDialog();
            }
        });
        textDateEndCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartDate = false;
                showDatePickerDialog();
            }
        });

        btnViewVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                if (mActivityType.equals(StaticContent.ActivityType.VIEW_COLLECTION)) {
                    callViewWS(ServerConstants.serverUrl.VIEW_COLLECTION, ServerConstants.ServiceCode.VIEW_COLLECTION);
                } else {
                    callViewWS(ServerConstants.serverUrl.VIEW_VISITS, ServerConstants.ServiceCode.VIEW_VISITS);

                }
            }
        });

        btnAddVC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                if (mActivityType.equals(StaticContent.ActivityType.VIEW_COLLECTION)) {
                    startActivity(new Intent(CommonVisitCollectionActivity.this, AddCollectionActivity.class));
                } else {
                    startActivity(new Intent(CommonVisitCollectionActivity.this, AddVisitsActivity.class));

                }
            }
        });
        refreshMyCustomer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMyCustomer.setRefreshing(false);
                if (mActivityType.equals(StaticContent.ActivityType.VIEW_COLLECTION)) {

                    callViewCollectionWS(ServerConstants.serverUrl.VIEW_COLLECTION, ServerConstants.ServiceCode.VIEW_COLLECTION);

                } else {

                    callViewCollectionWS(ServerConstants.serverUrl.VIEW_VISITS, ServerConstants.ServiceCode.VIEW_VISITS);

                }

            }
        });
        recyclerMyCollection.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!recyclerView.canScrollVertically(1)) {
                    fab.hide();
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {


                if (dy == 0) {
                    fab.show();

                } else if (dy > 0) {

                    fab.show();
                } else if (dy < 0) {
                    fab.hide();
                }


                super.onScrolled(recyclerView, dx, dy);


            }
        });


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (isAdded) {
            isAdded = false;
            if (mActivityType.equals(StaticContent.ActivityType.VIEW_COLLECTION)) {
                callViewCollectionWS(ServerConstants.serverUrl.VIEW_COLLECTION, ServerConstants.ServiceCode.VIEW_COLLECTION);

            } else {
                callViewCollectionWS(ServerConstants.serverUrl.VIEW_VISITS, ServerConstants.ServiceCode.VIEW_VISITS);

            }

        }
    }

    private void callViewWS(String mServiceUrl, int mServiceCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, mServiceUrl);
        map.put("sales_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        map.put("first_date", textDateStartCollection.getText().toString());
        map.put("last_date", textDateEndCollection.getText().toString());
        new MyVolleyPostMethod(this, map, mServiceCode, true);
    }

    private void callViewCollectionWS(String mServiceUrl, int mServiceCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, mServiceUrl);
        map.put("sales_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        new MyVolleyPostMethod(this, map, mServiceCode, true);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.VIEW_COLLECTION: {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    String first_date = jsonObject.getString("first_date");
                    String last_date = jsonObject.getString("last_date");
                    textDateStartCollection.setText(first_date);
                    textDateEndCollection.setText(last_date);
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("collection_details");
                        if (jsonArray.length() > 0) {
                            fab.show();
                            collectionArrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                String id = jdata.getString("id");
                                String sales_person_id = jdata.getString("sales_person_id");
                                String company_name = jdata.getString("company_name");
                                String cust_name = jdata.getString("cust_name");
                                String amount = jdata.getString("amount");
                                String bill_no = jdata.getString("bill_no");
                                String date = jdata.getString("date");
                                String is_deposit_amount = jdata.getString("is_deposit_amount");
                                collectionModel = new CollectionModel();
                                collectionModel.setId(id);
                                collectionModel.setSales_person_id(sales_person_id);
                                collectionModel.setCust_name(cust_name);
                                collectionModel.setCompany_name(company_name);
                                collectionModel.setAmount(amount);
                                collectionModel.setBill_no(bill_no);
                                collectionModel.setDate(date);
                                collectionModel.setIs_deposit_amount(is_deposit_amount);
                                collectionArrayList.add(collectionModel);
                            }
                            collectionAdapter = new ViewCollectionAdapter(getApplicationContext(), collectionArrayList);
                            recyclerMyCollection.setAdapter(collectionAdapter);
                        } else {

                            Toast.makeText(getApplicationContext(), "No data Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No data Found", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case ServerConstants.ServiceCode.VIEW_VISITS: {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    String first_date = jsonObject.getString("first_date");
                    String last_date = jsonObject.getString("last_date");
                    textDateStartCollection.setText(first_date);
                    textDateEndCollection.setText(last_date);
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("branch_details");
                        JSONArray jsonArray1 = jsonObject.getJSONArray("ho_details");
                        if ((jsonArray.length() > 0) || (jsonArray1.length() > 0)) {
                            fab.show();
                            visitArraylist.clear();

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jdata = jsonArray.getJSONObject(i);
                                    String id = jdata.getString("id");
                                    String sales_person_id = jdata.getString("sales_person_id");
                                    String cust_name = jdata.getString("cust_name");
                                    String contact_person = jdata.getString("contact_person");
                                    String tel = jdata.getString("tel");
                                    String email = jdata.getString("email");
                                    String msgg = jdata.getString("msg");
                                    String date = jdata.getString("date");
                                    visitListModel = new VisitListModel();
                                    visitListModel.setId(id);
                                    visitListModel.setSales_person_id(sales_person_id);
                                    visitListModel.setCust_name(cust_name);
                                    visitListModel.setContact_person(contact_person);
                                    visitListModel.setTel(tel);
                                    visitListModel.setEmail(email.equalsIgnoreCase("") ? "N/A" : email);
                                    visitListModel.setMsg(msgg);
                                    visitListModel.setDate(date);
                                    visitArraylist.add(visitListModel);
                                }
                            }
                            if (jsonArray1.length() > 0) {
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jdata1 = jsonArray1.getJSONObject(i);
                                    String id = jdata1.getString("id");
                                    String sales_person_id = jdata1.getString("sales_person_id");
                                    String cust_name = "";
                                    String contact_person = "";
                                    String tel = "";
                                    String email = "";
                                    String msgg = jdata1.getString("message");
                                    String date = jdata1.getString("date");
                                    visitListModel = new VisitListModel();
                                    visitListModel.setId(id);
                                    visitListModel.setSales_person_id(sales_person_id);
                                    visitListModel.setCust_name(cust_name);
                                    visitListModel.setContact_person(contact_person);
                                    visitListModel.setTel(tel);
                                    visitListModel.setEmail(email);
                                    visitListModel.setMsg(msgg);
                                    visitListModel.setDate(date);
                                    visitArraylist.add(visitListModel);

                                }

                            }
                            Collections.reverse(visitArraylist);
                            visitAdapter = new ViewVisitAdapter(getApplicationContext(), visitArraylist);
                            recyclerMyCollection.setAdapter(visitAdapter);
                        } else {

                            Toast.makeText(getApplicationContext(), "No data Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ERR0R", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }

    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {

    }

    private void showDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(this, new CalenderSelectDateListener(),
                        year,
                        month,
                        date);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    class CalenderSelectDateListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            try {
                if (isStartDate) {
                    textDateStartCollection.setText(CalenderUtil.convertDate11(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                } else {
                    textDateEndCollection.setText(CalenderUtil.convertDate11(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

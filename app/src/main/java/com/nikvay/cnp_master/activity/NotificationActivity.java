package com.nikvay.cnp_master.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.ExplodedViewAdapter;
import com.nikvay.cnp_master.adapter.NotificationAdapter;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.model.ExplodedViewModel;
import com.nikvay.cnp_master.model.NotificationModule;
import com.nikvay.cnp_master.utils.SharedUtil;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity  implements VolleyCompleteListener
{

    ImageView iv_back, iv_empty_list;
    RecyclerView recycler_view_notification;
    NotificationAdapter notificationAdapter;
    ArrayList<NotificationModule> notificationModuleArrayList = new ArrayList<>();
    SharedUtil sharedUtil;
    private String user_id;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        find_All_IDs();
        events();
        callNotificationList();

    }

    private void callNotificationList()
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.NOTIFICATION_LIST);
        map.put("sales_person_id", user_id);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.NOTIFICATION_LIST, true);

    }


    private void find_All_IDs() {
        sharedUtil = new SharedUtil(this);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_notification = findViewById(R.id.recycler_view_notification);
        iv_empty_list = findViewById(R.id.iv_empty_list);
        user_id = sharedUtil.getUserDetails().getUser_id();
        fab=findViewById(R.id.fab);


        //        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_view_notification.setLayoutManager(linearLayoutManager);
        recycler_view_notification.setHasFixedSize(true);
    }

    private void events() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recycler_view_notification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if(!recyclerView.canScrollVertically(1))
                {
                    fab.hide();
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {


                if(dy==0)
                {
                    fab.show();

                }
                else if(dy>0){

                    fab.show();
                }
                else if(dy<0)
                {
                    fab.hide();
                }



                super.onScrolled(recyclerView,dx,dy);



            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.NOTIFICATION_LIST: {
                try {
                    NotificationModule notificationModule = new NotificationModule();
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    String image_path = jsonObject.getString("image_path");
                    notificationModule.setImage_path(image_path);
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("notification_list");
                        if (jsonArray.length() > 0) {
                            fab.show();
                            notificationModuleArrayList = new ArrayList<>();
                            notificationModuleArrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                String id = jdata.getString("id");
                                String title = jdata.getString("title");
                                String pdf_name = jdata.getString("pdf_name");
                                String date = jdata.getString("date");

                                notificationModule.setId(id);
                                notificationModule.setTitle(title);
                                notificationModule.setPdf_name(pdf_name);
                                notificationModule.setDate(date);
                                notificationModuleArrayList.add(notificationModule);
                            }

                            notificationAdapter = new NotificationAdapter(NotificationActivity.this, notificationModuleArrayList);
                            recycler_view_notification.setAdapter(notificationAdapter);
                            notificationAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            iv_empty_list.setVisibility(View.VISIBLE);
                        }

                    } else {
                        if (notificationModuleArrayList.size() > 0) {
                            notificationModuleArrayList.clear();
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }

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
}

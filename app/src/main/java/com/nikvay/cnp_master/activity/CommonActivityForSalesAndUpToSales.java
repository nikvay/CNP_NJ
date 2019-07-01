package com.nikvay.cnp_master.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.CustomerSalesAdapter;
import com.nikvay.cnp_master.adapter.CustomerUptosaleAdapter;
import com.nikvay.cnp_master.apicallcommon.ApiClient;
import com.nikvay.cnp_master.apicallcommon.ApiInterface;
import com.nikvay.cnp_master.model.CustomerSalesModel;
import com.nikvay.cnp_master.model.CustomerSalesModule;
import com.nikvay.cnp_master.model.CustomerUpToSaleModel;
import com.nikvay.cnp_master.model.SuccessModel;
import com.nikvay.cnp_master.model.UptoSalesMonthAndSales;
import com.nikvay.cnp_master.utils.NetworkUtils;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.utils.SuccessDialog;
import com.nikvay.cnp_master.utils.UserData;
import com.nikvay.cnp_master.volley_support.ShowLoader;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonActivityForSalesAndUpToSales extends AppCompatActivity {


    private CustomerUptosaleAdapter customerUptosaleAdapter;
    private CustomerSalesAdapter customerSalesAdapter;
    private ArrayList<CustomerUpToSaleModel> customerUpToSaleModelArrayList = new ArrayList<>();
    private ArrayList<UptoSalesMonthAndSales> uptoSalesMonthAndSalesArrayList = new ArrayList<>();
    private ArrayList<CustomerSalesModel> customerSalesModelArrayList = new ArrayList<>();
    private ArrayList<CustomerSalesModule> customerSalesModuleArrayList = new ArrayList<>();
    private TextView textCustomerName, textCompanyName, textMonth, textSales, textTitle;
    private RecyclerView recyclerView;
    private ImageView iv_back_image_activity;
    private String sale_person_id, cust_id, activity_type, TAG = getClass().getSimpleName();
    private UserData userData;
    private SuccessDialog successDialog;
    private ApiInterface apiInterface;
    ShowLoader showLoader;
    ImageView iv_list_not_found_;
    LinearLayout ll_hide_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_for_sales_and_up_to_sales);

        initialize();
        events();
    }

    private void events() {
        iv_back_image_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initialize() {
        userData = new UserData(getApplicationContext());
        showLoader = new ShowLoader(CommonActivityForSalesAndUpToSales.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        textCustomerName = findViewById(R.id.textCustomerName);
        textCompanyName = findViewById(R.id.textCompanyName);
        textMonth = findViewById(R.id.textMonth);
        textSales = findViewById(R.id.textSales);
        recyclerView = findViewById(R.id.recycler_view);
        textTitle = findViewById(R.id.textTitle);
        iv_list_not_found_ = findViewById(R.id.iv_list_not_found_);
        iv_back_image_activity = findViewById(R.id.iv_back_image_activity);
        ll_hide_layout = findViewById(R.id.ll_hide_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommonActivityForSalesAndUpToSales.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sale_person_id = userData.getUserData(StaticContent.UserData.USER_ID);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            cust_id = bundle.getString(StaticContent.IntentType.CUSTOMER_ID);
            activity_type = activity_type = bundle.getString(StaticContent.ActivityType.ACTIVITY_TYPE);

            if (activity_type.equalsIgnoreCase(StaticContent.IntentValue.VIEW_UPTO_SALES)) {
                textTitle.setText("Upto Sales");

                if (NetworkUtils.isNetworkAvailable(CommonActivityForSalesAndUpToSales.this))
                    callUptoSalesWs();
                else
                    NetworkUtils.isNetworkNotAvailable(CommonActivityForSalesAndUpToSales.this);


            } else {
                textTitle.setText("Sales");
                textSales.setText("Sales");
                textMonth.setText("Year");

                if (NetworkUtils.isNetworkAvailable(CommonActivityForSalesAndUpToSales.this))
                    callSalesWS();
                else
                    NetworkUtils.isNetworkNotAvailable(CommonActivityForSalesAndUpToSales.this);


            }

        }


    }

    private void callSalesWS() {

        showLoader.showDialog();
        Call<SuccessModel> call = apiInterface.viewUptoSales(sale_person_id, cust_id);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showLoader.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModel = response.body();

                        String message = null, code = null;
                        if (successModel != null) {
                            message = successModel.getMsg();
                            code = successModel.getError_code();
                            textCustomerName.setText(successModel.getCustomer_name());
                            textCompanyName.setText(successModel.getCompany_name());
                            customerUpToSaleModelArrayList.clear();

                            if (code.equalsIgnoreCase("1")) {

                                customerSalesModelArrayList = successModel.getCustomerSalesModelArrayList();

                                if (customerSalesModelArrayList.size() != 0) {


                                    for (int i = 0; i <= customerUpToSaleModelArrayList.size(); i++) {

                                        String year1 = String.valueOf(customerSalesModelArrayList.get(i).getYear1() == null ? "" : customerSalesModelArrayList.get(i).getYear1());
                                        String sales_count1= String.valueOf(customerSalesModelArrayList.get(i).getSale_count1() == null ? "" : customerSalesModelArrayList.get(i).getSale_count1());

                                        String year2 = String.valueOf(customerSalesModelArrayList.get(i).getYear2() == null ? "" : customerSalesModelArrayList.get(i).getYear2());
                                        String sales_count2 = String.valueOf(customerSalesModelArrayList.get(i).getSale_count2() == null ? "" : customerSalesModelArrayList.get(i).getSale_count2());

                                        String year3 = String.valueOf(customerSalesModelArrayList.get(i).getYear3() == null ? "" : customerSalesModelArrayList.get(i).getYear2());
                                        String sales_count3 = String.valueOf(customerSalesModelArrayList.get(i).getSale_count3() == null ? "" : customerSalesModelArrayList.get(i).getSale_count3());

                                        if (!year1.equalsIgnoreCase("")) {
                                            CustomerSalesModule customerSalesModule1 = new CustomerSalesModule(year1, sales_count1);
                                            customerSalesModuleArrayList.add(customerSalesModule1);
                                        }
                                        if (!year2.equalsIgnoreCase("")) {
                                            CustomerSalesModule customerSalesModule2 = new CustomerSalesModule(year2, sales_count2);
                                            customerSalesModuleArrayList.add(customerSalesModule2);
                                        }
                                        if (!year3.equalsIgnoreCase("")) {
                                            CustomerSalesModule customerSalesModule3 = new CustomerSalesModule(year3, sales_count3);
                                            customerSalesModuleArrayList.add(customerSalesModule3);

                                        }


                                    }


                                    customerSalesAdapter = new CustomerSalesAdapter(CommonActivityForSalesAndUpToSales.this, customerSalesModuleArrayList);
                                    recyclerView.setAdapter(customerSalesAdapter);
                                    customerSalesAdapter.notifyDataSetChanged();


                                    /*iv_list_not_found_.setVisibility(View.VISIBLE);
                                    ll_hide_layout.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
*/

                                } else {
                                    iv_list_not_found_.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(CommonActivityForSalesAndUpToSales.this, "List Not Found", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                showLoader.dismissDialog();
            }
        });


    }

    private void callUptoSalesWs() {


        showLoader.showDialog();
        Call<SuccessModel> call = apiInterface.viewUptoSales(sale_person_id, cust_id);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showLoader.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModel = response.body();

                        String message = null, code = null;
                        if (successModel != null) {
                            message = successModel.getMsg();
                            code = successModel.getError_code();
                            textCustomerName.setText(successModel.getCustomer_name());
                            textCompanyName.setText(successModel.getCompany_name());
                            customerUpToSaleModelArrayList.clear();

                            if (code.equalsIgnoreCase("1")) {

                                customerUpToSaleModelArrayList = successModel.getCustomerUpToSaleModelArrayList();

                                if (customerUpToSaleModelArrayList.size() != 0) {


                                    for (int i = 0; i <customerUpToSaleModelArrayList.size(); i++) {

                                        String month1 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth1() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth1());
                                        String month1_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth1_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth1_count());

                                        String month2 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth2() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth2());
                                        String month2_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth2_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth2_count());

                                        String month3 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth3() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth3());
                                        String month3_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth3_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth3_count());

                                        String month4 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth4() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth4());
                                        String month4_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth4_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth4_count());

                                        String month5 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth5() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth5());
                                        String month5_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth5_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth5_count());

                                        String month6 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth6() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth6());
                                        String month6_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth6_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth6_count());

                                        String month7 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth7() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth7());
                                        String month7_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth7_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth7_count());

                                        String month8 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth7() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth7());
                                        String month8_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth8_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth8_count());

                                        String month9 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth9() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth9());
                                        String month9_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth9_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth9_count());

                                        String month10 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth10() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth10());
                                        String month10_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth10_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth10_count());

                                        String month11 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth11() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth11());
                                        String month11_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth11_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth11_count());

                                        String month12 = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth12() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth12());
                                        String month12_count = String.valueOf(customerUpToSaleModelArrayList.get(i).getMonth12_count() == null ? "" : customerUpToSaleModelArrayList.get(i).getMonth12_count());

                                        if (!month1.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales1 = new UptoSalesMonthAndSales(month1, month1_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales1);
                                        }
                                        if (!month2.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales2 = new UptoSalesMonthAndSales(month2, month2_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales2);
                                        }
                                        if (!month3.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales3 = new UptoSalesMonthAndSales(month3, month3_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales3);

                                        }
                                        if (!month4.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales4 = new UptoSalesMonthAndSales(month4, month4_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales4);
                                        }
                                        if (!month5.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales5 = new UptoSalesMonthAndSales(month5, month5_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales5);
                                        }
                                        if (!month6.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales6 = new UptoSalesMonthAndSales(month6, month6_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales6);
                                        }
                                        if (!month7.equalsIgnoreCase("")) {

                                            UptoSalesMonthAndSales uptoSalesMonthAndSales7 = new UptoSalesMonthAndSales(month7, month7_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales7);
                                        }
                                        if (!month8.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales8 = new UptoSalesMonthAndSales(month8, month8_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales8);
                                        }
                                        if (!month9.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales9 = new UptoSalesMonthAndSales(month9, month9_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales9);
                                        }
                                        if (!month10.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales10 = new UptoSalesMonthAndSales(month10, month10_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales10);
                                        }
                                        if (!month11.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales11 = new UptoSalesMonthAndSales(month11, month11_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales11);
                                        }
                                        if (!month12.equalsIgnoreCase("")) {
                                            UptoSalesMonthAndSales uptoSalesMonthAndSales12 = new UptoSalesMonthAndSales(month12, month12_count);
                                            uptoSalesMonthAndSalesArrayList.add(uptoSalesMonthAndSales12);
                                        }

                                    }


                                        customerUptosaleAdapter = new CustomerUptosaleAdapter(CommonActivityForSalesAndUpToSales.this, uptoSalesMonthAndSalesArrayList);
                                        recyclerView.setAdapter(customerUptosaleAdapter);
                                        customerUptosaleAdapter.notifyDataSetChanged();


                                        /*iv_list_not_found_.setVisibility(View.VISIBLE);
                                        ll_hide_layout.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.GONE);*/


                                } else {
                                    iv_list_not_found_.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(CommonActivityForSalesAndUpToSales.this, "List Not Found", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                showLoader.dismissDialog();
            }
        });

    }
}

package com.nikvay.cnp_master.activity;

import android.app.Dialog;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.ExplodedViewAdapter;
import com.nikvay.cnp_master.adapter.MyCustomerAdapter;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.common.VibrateOnClick;
import com.nikvay.cnp_master.model.ExplodedViewModel;
import com.nikvay.cnp_master.model.MyCustomerModel;
import com.nikvay.cnp_master.utils.MyCustomerResponse;
import com.nikvay.cnp_master.utils.SelectCustomerInterface;
import com.nikvay.cnp_master.utils.SelectExplodedView;
import com.nikvay.cnp_master.utils.SharedUtil;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.utils.SuccessDialog;
import com.nikvay.cnp_master.utils.SuccessDialogClosed;
import com.nikvay.cnp_master.utils.UserData;
import com.nikvay.cnp_master.utils.ValidationUtil;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CNPApplicationActivity extends AppCompatActivity implements VolleyCompleteListener, SelectExplodedView, SuccessDialogClosed, SelectCustomerInterface {

    ImageView iv_back_image_activity, iv_addEmailId;
    SharedUtil sharedUtil;
    EditText edtCustomerEmailId, edtOptionalEmailId;
    public static boolean isToRefresh = false;
    Button btnSend;
    ImageView textCNPApplicationCH;
    TextView textCNPApplication;
    String user_id;
    ExplodedViewModel explodedViewModel;
    ArrayList<ExplodedViewModel> explodedViewModelsArrayKList;
    Dialog selectExplodedPdfDialog;
    RecyclerView recyclerDialogEV;
    ExplodedViewAdapter explodedViewAdapter;

    String selectedId, pdfName;
    private SuccessDialog successDialog;
    SelectExplodedView selectExplodedView;
    String emailId, optionalEmailId;


    private FloatingActionButton fab;


    //  select Customer Dialog
    private TextView tv_select_customer_application;
    private Dialog selectCustomerDialog;
    private Button btnOkDialogSC, btnCancelDialogSC;
    private EditText editSearchC;
    private RecyclerView recyclerDialogSC;
    private UserData userData;
    private ArrayList<MyCustomerModel> arrayListC = new ArrayList<>();
    private MyCustomerResponse myCustomerResponse;
    private MyCustomerAdapter adapterC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnpapplication);
        initialize();
    }

    private void initialize() {
        VibrateOnClick.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sharedUtil = new SharedUtil(this);
        userData = new UserData(getApplicationContext());
        successDialog = new SuccessDialog(this, true);
        iv_back_image_activity = findViewById(R.id.iv_back_image_activity);
        edtCustomerEmailId = findViewById(R.id.edtCustomerEmailId);
        btnSend = findViewById(R.id.btnSend);
        textCNPApplicationCH = findViewById(R.id.textCNPApplicationCH);
        iv_addEmailId = findViewById(R.id.iv_addEmailId);
        edtOptionalEmailId = findViewById(R.id.edtOptionalEmailId);


        selectExplodedPdfDialog = new Dialog(this);
        selectExplodedPdfDialog.setContentView(R.layout.dialog_explodedview_pdf);
        recyclerDialogEV = selectExplodedPdfDialog.findViewById(R.id.recyclerViewExplodedPdf);
        fab = selectExplodedPdfDialog.findViewById(R.id.fab);


        selectExplodedPdfDialog.setCancelable(true);

        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        recyclerDialogEV.setLayoutManager(layout);
        recyclerDialogEV.setHasFixedSize(true);


        textCNPApplication = findViewById(R.id.textCNPApplication);


        myCustomerResponse = new MyCustomerResponse(getApplicationContext());


        tv_select_customer_application = findViewById(R.id.tv_select_customer_application);

        selectCustomerDialog = new Dialog(this);
        selectCustomerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectCustomerDialog.setContentView(R.layout.dialog_select_customer);
        selectCustomerDialog.setCancelable(false);
        btnOkDialogSC = selectCustomerDialog.findViewById(R.id.btnOkDialogSC);
        btnCancelDialogSC = selectCustomerDialog.findViewById(R.id.btnCancelDialogSC);
        editSearchC = selectCustomerDialog.findViewById(R.id.editSearchC);
        recyclerDialogSC = selectCustomerDialog.findViewById(R.id.recyclerDialogSC);
        recyclerDialogSC = selectCustomerDialog.findViewById(R.id.recyclerDialogSC);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerDialogSC.setLayoutManager(manager);


        events();
    }

    private void events() {


        iv_back_image_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        textCNPApplicationCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                callApplicationTypeWS();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                emailId = edtCustomerEmailId.getText().toString().trim();
                optionalEmailId = edtOptionalEmailId.getText().toString().trim();

                if (emailId.equalsIgnoreCase("")) {
                    edtCustomerEmailId.setError("Please Enter Email Id");
                    edtCustomerEmailId.requestFocus();
                } else if (!ValidationUtil.emailCheck(emailId)) {
                    edtCustomerEmailId.setError("Invalid EmailId");
                    edtCustomerEmailId.requestFocus();

                } else if (selectedId == null) {
                    Toast.makeText(CNPApplicationActivity.this, "Please Select Exploded Pdf", Toast.LENGTH_SHORT).show();
                } else {


                    if (optionalEmailId.equalsIgnoreCase("")) {
                        callApplicationSendWS(selectedId, emailId);
                    } else if (!ValidationUtil.emailCheck(optionalEmailId)) {
                        edtOptionalEmailId.setError("Invalid EmailId");
                        edtOptionalEmailId.requestFocus();
                    } else {
                        String emailID = emailId + "," + optionalEmailId;
                        callApplicationSendWS(selectedId, emailID);
                    }
                }


            }
        });

        iv_addEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtOptionalEmailId.setVisibility(View.VISIBLE);
                iv_addEmailId.setVisibility(View.GONE);

            }
        });


        recyclerDialogEV.addOnScrollListener(new RecyclerView.OnScrollListener() {
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



        tv_select_customer_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                callMyCustomerWS();
            }
        });

        editSearchC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterC.getFilter().filter(editSearchC.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCancelDialogSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtCustomerEmailId.setText("");
                selectCustomerDialog.dismiss();
            }
        });
        btnOkDialogSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCustomerDialog.dismiss();
            }
        });


    }

    private void callApplicationSendWS(String selectedId, String emailId) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.SEND_CNP_APPLICATION);
        map.put("pdf_id", selectedId);
        map.put("email_id", emailId);
        map.put("user_id", user_id);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.SEND_CNP_APPLICATION, true);
    }

    private void callApplicationTypeWS() {
        user_id = sharedUtil.getUserDetails().getUser_id();
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.CNP_APPLICATION);
        map.put("user_id", user_id);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.CNP_APPLICATION, true);
    }


    private void callMyCustomerWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.MY_CUSTOMER_LIST);
        map.put("sale_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.MY_CUSTOMER_LIST, true);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case ServerConstants.ServiceCode.CNP_APPLICATION: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            fab.show();
                            explodedViewModelsArrayKList = new ArrayList<>();
                            explodedViewModelsArrayKList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                String id = jdata.getString("id");
                                String name = jdata.getString("name");
                                ExplodedViewModel explodedViewModel = new ExplodedViewModel();
                                explodedViewModel.setId(id);
                                explodedViewModel.setPdfName(name);
                                explodedViewModelsArrayKList.add(explodedViewModel);
                            }

                            explodedViewAdapter = new ExplodedViewAdapter(this, explodedViewModelsArrayKList, true);
                            selectExplodedPdfDialog.show();
                            Window window = selectExplodedPdfDialog.getWindow();
                            window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            recyclerDialogEV.setAdapter(explodedViewAdapter);

                        }

                    } else {
                        if (explodedViewModelsArrayKList.size() > 0) {
                            explodedViewModelsArrayKList.clear();
                            explodedViewAdapter.notifyDataSetChanged();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                }
                break;
            }
            case ServerConstants.ServiceCode.SEND_CNP_APPLICATION: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE)) {
                        // Toast.makeText(ExplodedViewActivity.this, "Send Successfully", Toast.LENGTH_SHORT).show();
                        successDialog.showDialog("Application  Send Successfully", true);
                        selectedId = null;
                        pdfName = null;
                        edtCustomerEmailId.setText("");
                        edtOptionalEmailId.setText("");
                        edtOptionalEmailId.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), " Not Send Try Again", Toast.LENGTH_SHORT).show();

                }
                break;
            }
            case ServerConstants.ServiceCode.MY_CUSTOMER_LIST: {
                arrayListC.clear();
                arrayListC = myCustomerResponse.getCustomerResponse(response);
                if (arrayListC != null) {
                    adapterC = new MyCustomerAdapter(getApplicationContext(), arrayListC, true, this, true);
                    selectCustomerDialog.show();
                    Window window = selectCustomerDialog.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    recyclerDialogSC.setAdapter(adapterC);
                }
                break;
            }
        }
    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {

    }

    @Override
    public void getExplodedId(String explodedViewId) {
        this.selectedId = explodedViewId;
        selectExplodedPdfDialog.dismiss();
    }

    @Override
    public void getPdfName(String pdfName) {
        this.pdfName = pdfName;
        textCNPApplication.setText(pdfName);
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        ExplodedViewActivity.isToRefresh = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    @Override
    public void getCustomerName(String mCustomerName) {

    }

    @Override
    public void getCustomerDetail(MyCustomerModel customerModel) {
        edtCustomerEmailId.setText(customerModel.getEmail_id());

    }
}

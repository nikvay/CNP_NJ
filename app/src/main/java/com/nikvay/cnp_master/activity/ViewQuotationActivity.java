package com.nikvay.cnp_master.activity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.QuotationListProductAdapter;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.model.ViewQuotationModel;
import com.nikvay.cnp_master.utils.MathCalculation;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.volley_support.AppController;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewQuotationActivity extends AppCompatActivity implements VolleyCompleteListener {
    private RecyclerView recyclerVQ;
    private String mQuotationCount;
    private ViewQuotationModel model;
    private ArrayList<ViewQuotationModel> arrayList = new ArrayList<>();
    private TextView
            textQNumberVQ,
            textStatusVQ,tv_remark,
            textTotalPrice;
    private AutoCompleteTextView textCustomerNameVQ,
            textBillingContactPersonVQ,
            textMobileNumberVQ,
            textEmailIdVQ,
            textPackingChargesVQ,
            textBranchNameVQ,
            textBillingAddressVQ;
    private String quote_num,
            discount_limit = null,
            mTotalAmount = null,
            cust_name,
            billing_contact_person,
            tel_no,
            email_id,
            packing_charges,
            branch_name,
            status,
            is_remind = "",
            new_billing_address,
            gst_type;
    private int mTotalPrice = 0;
    private QuotationListProductAdapter adapter;
    private LinearLayout linearButtonsVQ;
    private Button btnDynamic;
    private Button btnCancel;
    private MathCalculation mathCalculation;
    float mNetAmount = 0;
    float ans = 0;
    private Button btnGotIt,btn_ok,btn_cancel;
    private Button btnOKReason;
    private Button btnCancelReason;
    private int isMail = 0;
    private Dialog cancelDialog;

    private Dialog remarkDialog;
EditText etRemark;
    private EditText editCancelReason;
    private TextView textNetAmountQV,
            textPackingChargesHQV,
            textPackingChargesQV,
            textInsuranceChargesHQV,
            textInsuranceChargesQV,
            textSGSTHQV,
            textSGSTQV,
            textCGSTHQV,
            textCGSTQV,
            textIGSTHQV,
            textIGSTQV,
            textFreightChargesHQV,
            textFreightChargesQV,
            textCancelReasonVQ;
    private AppController appController;
    public static boolean isUpdated = false;
    private boolean toHideEdit = false;
    private boolean toDownloadEdit = false;
  //  private ImageView iv_image_download;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quotation);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        mQuotationCount = getIntent().getExtras().getString(StaticContent.IntentType.QUOTATION_COUNT);
        localBrodcastInitialize();
        initialize();
    }


    private void localBrodcastInitialize() {
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

    private void initialize()
    {

        remarkDialog=new Dialog(this);
        remarkDialog.setContentView(R.layout.remark_dialog);
        btn_ok=remarkDialog.findViewById(R.id.btn_ok);
        etRemark=remarkDialog.findViewById(R.id.et_remark);
        btn_cancel=remarkDialog.findViewById(R.id.btn_cancel);
        tv_remark=findViewById(R.id.remark);
        appController = (AppController) getApplicationContext();
        mathCalculation = new MathCalculation();
        cancelDialog = new Dialog(this);
        cancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDialog.setContentView(R.layout.dialog_cancle_quotation);
        cancelDialog.setCancelable(false);
        btnOKReason = cancelDialog.findViewById(R.id.btnOKReason);
        btnCancelReason =  cancelDialog.findViewById(R.id.btnCancelReason);
        editCancelReason = cancelDialog.findViewById(R.id.editCancelReason);
        textQNumberVQ =  findViewById(R.id.textQNumberVQ);
        textStatusVQ = findViewById(R.id.textStatusVQ);
        textTotalPrice =  findViewById(R.id.textTotalPrice);
        textCancelReasonVQ = findViewById(R.id.textCancelReasonVQ);
        textCustomerNameVQ = findViewById(R.id.textCustomerNameVQ);
        textBillingContactPersonVQ = findViewById(R.id.textBillingContactPersonVQ);
        textMobileNumberVQ = findViewById(R.id.textMobileNumberVQ);
        textEmailIdVQ = findViewById(R.id.textEmailIdVQ);
        textPackingChargesVQ = findViewById(R.id.textPackingChargesVQ);
        textBranchNameVQ = findViewById(R.id.textBranchNameVQ);
        textBillingAddressVQ =  findViewById(R.id.textBillingAddressVQ);
       // iv_image_download =  findViewById(R.id.iv_image_download);
        quotationInitialize();
        linearButtonsVQ = findViewById(R.id.linearButtonsVQ);
        btnDynamic = findViewById(R.id.btnDynamic);
        btnCancel = findViewById(R.id.btnCancel);
        btnGotIt =  findViewById(R.id.btnGotIt);
        recyclerVQ =  findViewById(R.id.recyclerVQ);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerVQ.setLayoutManager(manager);
        recyclerVQ.setNestedScrollingEnabled(false);
        callViewQuotationWS();
        events();
    }

    private void quotationInitialize() {
        textNetAmountQV = findViewById(R.id.textNetAmountQV);
        textPackingChargesHQV =  findViewById(R.id.textPackingChargesHQV);
        textPackingChargesQV = findViewById(R.id.textPackingChargesQV);
        textInsuranceChargesHQV =  findViewById(R.id.textInsuranceChargesHQV);
        textInsuranceChargesQV = findViewById(R.id.textInsuranceChargesQV);
        textSGSTHQV = findViewById(R.id.textSGSTHQV);
        textSGSTQV =  findViewById(R.id.textSGSTQV);
        textCGSTHQV = findViewById(R.id.textCGSTHQV);
        textCGSTQV =  findViewById(R.id.textCGSTQV);
        textIGSTHQV =  findViewById(R.id.textIGSTHQV);
        textIGSTQV = findViewById(R.id.textIGSTQV);
        textFreightChargesHQV =  findViewById(R.id.textFreightChargesHQV);
        textFreightChargesQV =  findViewById(R.id.textFreightChargesQV);
    }

    private void events() {

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
          remark();
            }
        });

        tv_remark.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                remarkDialog.show();

            }
        });
        btnCancelReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCancelReason.setText("");
                cancelDialog.dismiss();

            }
        });
        btnOKReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editCancelReason.getText().toString().isEmpty()) {
                    changeStatusWS(0);
                } else {
                    Toast.makeText(getApplicationContext(), "Enter Reason for cancle ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case "3": {
                        btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_MAIL);
                        isMail = 1;
                        changeStatusWS(3);
                        break;
                    }
                    case "2": {
                        btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_MAIL);
                        isMail = 1;
                        changeStatusWS(3);
                        break;
                    }
                    case "5": {
                        textStatusVQ.setText(StaticContent.QuotationStatusCode.SEND_FOR_APPROVAL);
                        changeStatusWS(1);
                        break;
                    }
                    case "1": {
                        textStatusVQ.setText(StaticContent.QuotationStatusCode.SEND_FOR_APPROVAL);
                        changeStatusWS(1);
                        break;
                    }
                }
            }
        });
        btnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatusWS(4);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog.show();
                Window window = cancelDialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });


      /*  iv_image_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callDownloadPdfList();
            }
        });
*/
    }

    private void remark()
    {
        String remark=etRemark.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("quote_num",quote_num);
        map.put(ServerConstants.URL, ServerConstants.serverUrl.quote_summary);
        map.put("quote_summary", remark);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.REMARK, true);

    }

    private void callDownloadPdfList() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.QUOTATION_PDF_LIST);
        map.put("quote_num", quote_num);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.QUOTATION_PDF_LIST, true);
    }

    //TODO
    private void changeStatusWS(int statusCode)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.CHANGE_STATUS);
        map.put("status", String.valueOf(statusCode));
        map.put("quote_num", quote_num);
        if (statusCode == 0) {
            map.put("cancel_reason", editCancelReason.getText().toString());
        }
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.CHANGE_STATUS, true);
    }

    private void callViewQuotationWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.VIEW_QUOTATION);
        map.put("quote_num", mQuotationCount);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.VIEW_QUOTATION, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isUpdated) {
            callViewQuotationWS();
            isUpdated = false;
            mTotalPrice = 0;
            mNetAmount = 0;
            ans = 0;
            isMail = 0;
            QuotationListActivity.toRefresh = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                isUpdated = false;
                finish();
                return true;
            }
            case R.id.actionEdit: {
                if (arrayList.size() != 0) {
                    appController.setDatObject(arrayList);
                    Intent intent = new Intent(ViewQuotationActivity.this, EditQuotationActivity.class);
                    intent.putExtra(StaticContent.IntentKey.QUOTATION_NAME, quote_num);
                    intent.putExtra(StaticContent.IntentKey.DISCOUNT_LIMIT, discount_limit);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No product fround in this quotation", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.downloadQuotation: {
                if (arrayList.size() != 0) {
                    callDownloadPdfList();
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isUpdated = false;
        finish();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.VIEW_QUOTATION: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    discount_limit = jsonObject.getString("discount_limit");
                    is_remind = jsonObject.getString("is_remind");
                    status = jsonObject.getString("status");
                    if (status.equals("0")) {
                        String cancel_reason = jsonObject.getString("cancel_reason");
                        textCancelReasonVQ.setVisibility(View.VISIBLE);
                        textCancelReasonVQ.setText(cancel_reason);
                    } else {
                        textCancelReasonVQ.setVisibility(View.GONE);
                    }
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("quotetion_list");
                        if (jsonArray.length() > 0) {
                            arrayList.clear();
                            mTotalPrice = 0;
                            mNetAmount = 0;
                            ans = 0;
                            isMail = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                quote_num = jdata.getString("quote_num");
                                String product_discounted_price = jdata.getString("product_discounted_price");
                                cust_name = jdata.getString("cust_name");
                                billing_contact_person = jdata.getString("billing_contact_person");
                                tel_no = jdata.getString("tel_no");
                                email_id = jdata.getString("email_id");
                                packing_charges = jdata.getString("packing_charges");
                                branch_name = jdata.getString("branch_name");
                                String discount_value = jdata.getString("discount_value");
                                String product_id = jdata.getString("product_id");
                                String name = jdata.getString("name");
                                String price = jdata.getString("price").replace(",", "");
                                String gross_wt = jdata.getString("gross_wt");
                                String net_wt = jdata.getString("net_wt");
                                String dimension = jdata.getString("dimension");
                                String packing_charges = jdata.getString("packing_charges");
                                String insurance_charges = jdata.getString("insurance_charges");
                                String freight_terms = jdata.getString("freight_terms");
                                gst_type = jdata.getString("gst_type");
                                String SGST = jdata.getString("SGST");
                                String CGST = jdata.getString("CGST");
                                String IGST = jdata.getString("IGST");
                                String product_qty = jdata.getString("product_qty");
                                new_billing_address = jdata.getString("new_billing_address");
                                if (new_billing_address.equals("null")) {
                                    new_billing_address = "N/A";
                                }
                                int mPrice = Integer.valueOf(price);
                                int mQuantity = Integer.valueOf(product_qty);
                                int totalPrice = mPrice * mQuantity;
                                mTotalPrice = Integer.valueOf(price.replace(",", "")) + mTotalPrice;
                                String mQbasedPrice = String.valueOf(totalPrice);
                                model = new ViewQuotationModel();
                                model.setProduct_discounted_price(product_discounted_price);
                                model.setPacking_charges(packing_charges);
                                model.setInsurance_charges(insurance_charges);
                                model.setFreight_terms(freight_terms);
                                model.setSGST(SGST);
                                //  model.setGst_type(gst_type);
                                model.setNew_billing_address(new_billing_address);
                                model.setCGST(CGST);
                                model.setIGST(gst_type.equals(StaticContent.GstType.WITHIN_STATE) ? "0" : IGST);

                                model.setDiscount_value(discount_value);
                                model.setProduct_id(product_id);
                                model.setName(name);
                                model.setmQbasedPrice(mQbasedPrice);
                                model.setProduct_qty(product_qty);
                                model.setPrice(price);
                                model.setGross_wt(gross_wt);
                                model.setNet_wt(net_wt);
                                model.setDimension(dimension);
                                arrayList.add(model);

                                mNetAmount = mNetAmount + mathCalculation.calculatePer(discount_value, mQbasedPrice);
                            }

                            float mNetTemp = mNetAmount;
                            float  mPackaging = Float.valueOf(mathCalculation.getAddedPercentage(String.valueOf(mNetAmount), packing_charges));
                            ans = mNetAmount + Math.round(mPackaging);

                            float mInsurance = Float.valueOf(mathCalculation.getAddedPercentage(String.valueOf(mNetAmount), model.getInsurance_charges()));
                            ans = ans +Math.round(mInsurance);
                            ans = ans +Integer.valueOf(model.getFreight_terms());

                            float mSGST =Math.round(!model.getSGST().equals("0") ? Float.valueOf(mathCalculation.getAddedPercentage(String.valueOf(ans), model.getSGST())) : 0);
                            float mCGST =Math.round(!model.getSGST().equals("0") ? Float.valueOf(mathCalculation.getAddedPercentage(String.valueOf(ans), model.getCGST())) : 0);
                            float mIGST =Math.round( !model.getSGST().equals("0") ? Float.valueOf(mathCalculation.getAddedPercentage(String.valueOf(mNetAmount), model.getIGST())) : 0);


                            ans = ans + mSGST+mCGST+mIGST;





                            textNetAmountQV.setText("Net Amount: " + String.format("%.0f", mNetTemp));
                            textPackingChargesHQV.setText(model.getPacking_charges() + "% " + getResources().getString(R.string.packaging_charges));
                            textPackingChargesQV.setText(String.valueOf(Math.round(mPackaging)));

                            textInsuranceChargesHQV.setText(model.getInsurance_charges() + "% " + getResources().getString(R.string.insurance_charges));
                            textInsuranceChargesQV.setText(String.valueOf(Math.round(mInsurance)));


                            if (gst_type.equals(StaticContent.GstType.WITHIN_STATE)) {
                                textIGSTHQV.setText("0" + "% " + getResources().getString(R.string.igst_charges));
                                textIGSTQV.setText("0");
                                ans=ans-mIGST;
                                textSGSTHQV.setText(model.getSGST() + "% " + getResources().getString(R.string.sgst_charges));
                                textSGSTQV.setText(String.format("%.0f", mSGST));
                                textCGSTHQV.setText(model.getCGST() + "% " + getResources().getString(R.string.cgst_charges));
                                textCGSTQV.setText(String.format("%.0f", mCGST));


                            } else {
                                textIGSTHQV.setText(model.getIGST() + "% " + getResources().getString(R.string.igst_charges));
                                textIGSTQV.setText(String.format("%.0f", mIGST));
                                textSGSTHQV.setText("0" + "% " + getResources().getString(R.string.sgst_charges));
                                textSGSTQV.setText("0");
                                ans=ans-mSGST;
                                textCGSTHQV.setText("0" + "% " + getResources().getString(R.string.cgst_charges));
                                textCGSTQV.setText("0");
                                ans=ans-mCGST;
                            }
                           /* textSGSTHQV.setText(model.getSGST() + "% " + getResources().getString(R.string.sgst_charges));
                            textSGSTQV.setText(String.format("%.0f", mSGST));
                            textCGSTHQV.setText(model.getCGST() + "% " + getResources().getString(R.string.packaging_charges));
                            textCGSTQV.setText(String.format("%.0f", mCGST));
                            textIGSTHQV.setText(model.getIGST() + "% " + getResources().getString(R.string.igst_charges));
                            textIGSTQV.setText(String.format("%.0f", mIGST));*/
                            textFreightChargesHQV.setText(model.getFreight_terms() + " " + getResources().getString(R.string.freight_charges));
                            textFreightChargesQV.setText(String.valueOf(model.getFreight_terms()));
                            textTotalPrice.setText("Total Amount: " + String.format("%.0f", ans));
                            textQNumberVQ.setText(quote_num);
                            getStatusMeaning(status);
                            textCustomerNameVQ.setText(cust_name);
                            textBillingContactPersonVQ.setText(billing_contact_person);
                            textMobileNumberVQ.setText(tel_no);
                            textEmailIdVQ.setText(email_id);
                            textPackingChargesVQ.setText(packing_charges);
                            textBranchNameVQ.setText(branch_name);
                            textBillingAddressVQ.setText(new_billing_address);
                            if (status.equals("0") || status.equals("4")||status.equals("6")) {
                                linearButtonsVQ.setVisibility(View.GONE);
                            } else {
                                linearButtonsVQ.setVisibility(View.VISIBLE);
                            }
                            setStatusButtonText(status);
                            adapter = new QuotationListProductAdapter(getApplicationContext(), arrayList);
                            recyclerVQ.setAdapter(adapter);

                        } else {
                            if (arrayList.size() > 0) {
                                arrayList.clear();
                                adapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getApplicationContext(), "Quotation without products", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case ServerConstants.ServiceCode.CHANGE_STATUS: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        QuotationListActivity.toRefresh = true;
                        if (cancelDialog.isShowing()) {
                            cancelDialog.dismiss();
                        }
                        if (isMail == 1) {
                            callSendMailWS();
                        } else {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            callViewQuotationWS();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case ServerConstants.ServiceCode.SEND_MAIL: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        Toast.makeText(getApplicationContext(), "Mail Sent", Toast.LENGTH_SHORT).show();
                        callViewQuotationWS();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            case ServerConstants.ServiceCode.QUOTATION_PDF_LIST: {
                try {JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {

                        String folder_path=jsonObject.getString("folder_path");
                        String pdf_name=jsonObject.getString("pdf_name");
                        String url=ServerConstants.serverUrl.BASE_URL+folder_path+pdf_name;

                        /*if (pdf_name.equalsIgnoreCase("null"))
                        {
                            iv_image_download.setVisibility(View.GONE);

                        }
                        else {
                            iv_image_download.setVisibility(View.VISIBLE);
                            downloadQuotationPdfList(url);
                        }*/
                        downloadQuotationPdfList(url);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

        }
    }


    private void downloadQuotationPdfList(String url)
    {

        downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri= Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long reference=downloadManager.enqueue(request);
    }



    private void callSendMailWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.SEND_MAIL);
        map.put("quote_num", quote_num);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.SEND_MAIL, true);
    }

    private void setStatusButtonText(String status) {
        switch (status) {
            case "1": {
                btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_FOR_APPROVAL);
                btnGotIt.setVisibility(View.GONE);
                break;
            }
            case "2": {
                if (!is_remind.equals("") && is_remind.equals("0")) {
                    btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_MAIL);
                    btnGotIt.setVisibility(View.GONE);
                } else {
                    btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_REMINDER);
                    btnGotIt.setVisibility(View.GONE);
                }
                break;
            }
            case "3": {
                if (!is_remind.equals("") && is_remind.equals("0")) {
                    btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_MAIL);
                } else {
                    btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_REMINDER);
                }
                break;
            }
            case "5": {
                btnDynamic.setText(StaticContent.QuotationStatusCode.SEND_FOR_APPROVAL);
                btnGotIt.setVisibility(View.GONE);
                break;
            }
        }

    }

    private void getStatusMeaning(String status) {
        switch (status) {
            case "0": {
                toHideEdit = true;
                toDownloadEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.ZERO);
                textStatusVQ.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                break;
            }
            case "1": {
                toDownloadEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.ONE);
                textStatusVQ.setTextColor(getResources().getColor(R.color.yellow));
                break;
            }
            case "2": {
                toDownloadEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.TWO);
                textStatusVQ.setTextColor(getResources().getColor(R.color.yellow));
                break;
            }
            case "3": {
                textStatusVQ.setText(StaticContent.QuotationStatusCode.THREE);
                textStatusVQ.setTextColor(getResources().getColor(R.color.yellow));
                break;
            }
            case "4": {
                toDownloadEdit = true;
                toHideEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.FOUR);
                textStatusVQ.setTextColor(getResources().getColor(R.color.green));
                break;
            }
            case "5": {
                toDownloadEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.FIVE);
                textStatusVQ.setTextColor(getResources().getColor(R.color.yellow));
                break;
            }
            case "6": {
                toDownloadEdit = true;
                toHideEdit = true;
                invalidateOptionsMenu();
                textStatusVQ.setText(StaticContent.QuotationStatusCode.SIX);
                textStatusVQ.setTextColor(getResources().getColor(R.color.green));
                break;

            }
        }
    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_product, menu);
        if (toHideEdit) {

            menu.getItem(0).setVisible(false);

        }
        if(toDownloadEdit)
        {
            menu.getItem(1).setVisible(false);
        }

        return true;
    }


}

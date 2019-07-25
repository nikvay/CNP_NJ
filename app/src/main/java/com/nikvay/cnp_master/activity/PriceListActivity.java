package com.nikvay.cnp_master.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.ProductPriceRecyclerAdapter;
import com.nikvay.cnp_master.adapter.ProductTypeAdapter;
import com.nikvay.cnp_master.common.ProductTypeNotifier;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.fragments.PriceListFragment;
import com.nikvay.cnp_master.model.Product;
import com.nikvay.cnp_master.model.ProductTypeModel;
import com.nikvay.cnp_master.utils.Logs;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PriceListActivity extends AppCompatActivity implements VolleyCompleteListener, ProductTypeNotifier {

    //Weight Declaration
    private Spinner spinnerPC;
    private RecyclerView recyclerPC;


    private List<String> productType = new ArrayList<>();
    private ArrayAdapter<String> productCatAdapter;
    private String mProductType = null;
    private ProductTypeModel productTypeModel;
    private ProductTypeAdapter productTypeAdapter;
    private ArrayList<ProductTypeModel> arrayListtype = new ArrayList<>();
    private static final String TAG = PriceListFragment.class.getSimpleName();
    private TextView txt_no_data_found;
    private RecyclerView recyclerView_product_price;
    private ArrayList<Product> productArrayList;
    private ProductPriceRecyclerAdapter productPriceRecyclerAdapter;
    private ArrayList<Product> products;
    private Dialog selectProductPrice;
    private Product product;
    private  ImageView iv_back_image,iv_back_image_activity,iv_arrow;
    private EditText editSearchP;

    FloatingActionButton floatingActionButton,dialog_fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);

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

    private void initialize() {
        productType.clear();
        productType.add("Local");
        productType.add("Export");
        spinnerPC = findViewById(R.id.spinnerPC);
        productCatAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, productType);
        productCatAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPC.setAdapter(productCatAdapter);


        products = new ArrayList<>();
        selectProductPrice = new Dialog(this);
        selectProductPrice.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectProductPrice.setContentView(R.layout.dialog_pricelist);
        selectProductPrice.setCancelable(false);



        final LinearLayoutManager managerPT = new LinearLayoutManager(getApplicationContext());
        LinearLayoutManager managerPC = new LinearLayoutManager(getApplicationContext());


        txt_no_data_found = selectProductPrice.findViewById(R.id.txt_no_data_found);
        recyclerView_product_price = selectProductPrice.findViewById(R.id.recyclerView_product_price);
        iv_back_image = selectProductPrice.findViewById(R.id.iv_back_image);
        editSearchP=selectProductPrice.findViewById(R.id.editSearchP);
        recyclerView_product_price.setLayoutManager(managerPC);
        recyclerView_product_price.setHasFixedSize(true);
        iv_back_image_activity=findViewById(R.id.iv_back_image_activity);


       dialog_fab=selectProductPrice.findViewById(R.id.dialog_fab);

       floatingActionButton=findViewById(R.id.fab);
        recyclerPC = findViewById(R.id.recyclerPC);
        recyclerPC.setLayoutManager(managerPT);
        recyclerPC.setHasFixedSize(true);





        editSearchP.setText("");
        events();
    }

    private void events() {

        spinnerPC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                callProductTypeWS();
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        iv_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                productTypeAdapter.notifyDataSetChanged();
                callProductTypeWS();
                selectProductPrice.dismiss();
            }
        });




        recyclerPC.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {



                if(!recyclerView.canScrollVertically(1))
                {
                    floatingActionButton.hide();
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    floatingActionButton.show();
                }





                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {


                if(dy==0)
                {
                    floatingActionButton.show();

                }
                else if(dy>0){

                    floatingActionButton.show();
                }
                else if(dy<0)
                {
                    floatingActionButton.hide();
                }



                super.onScrolled(recyclerView,dx,dy);
            }
        });


        recyclerView_product_price.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if(!recyclerView.canScrollVertically(1))
                {
                    dialog_fab.hide();
                }
                else if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    dialog_fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {


                if(dy==0)
                {
                    dialog_fab.show();

                }
                else if(dy>0){

                    dialog_fab.show();
                }
                else if(dy<0)
                {
                    dialog_fab.hide();
                }



                super.onScrolled(recyclerView,dx,dy);



            }
        });

        //End Spinner


        editSearchP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                productPriceRecyclerAdapter.getFilter().filter(editSearchP.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_back_image_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        callProductTypeWS();
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.POST_PRODUCT_TYPE: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            floatingActionButton.show();
                            arrayListtype.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                String id = jdata.getString("id");
                                String type_name = jdata.getString("type_name");
                                productTypeModel = new ProductTypeModel();
                                productTypeModel.setId(id);
                                productTypeModel.setType_name(type_name);
                                arrayListtype.add(productTypeModel);
                            }
                            productTypeAdapter = new ProductTypeAdapter(arrayListtype, this);
                            recyclerPC.setAdapter(productTypeAdapter);


                        } else {

                            if (arrayListtype.size() > 0) {
                                floatingActionButton.hide();
                                arrayListtype.clear();
                                productTypeAdapter.notifyDataSetChanged();

                            }
                            floatingActionButton.hide();
                            Toast.makeText(getApplicationContext(), "Coming soon...", Toast.LENGTH_SHORT).show();

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();

                }
                break;
            }
            case ServerConstants.ServiceCode.GET_PRODUCT: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        JSONArray jsonArray = jsonObject.getJSONArray("product_list");
                        if (jsonArray.length() > 0) {
                            dialog_fab.show();
                            productArrayList = new ArrayList<>();
                            productArrayList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jdata = jsonArray.getJSONObject(i);
                                String product_id = jdata.getString("product_id");
                                String name = jdata.getString("name");
                                String price = jdata.getString("price");
                                product = new Product();
                                product.setName(name);
                                product.setPrice(price);
                                product.setProduct_id(product_id);

                                productArrayList.add(product);
                            }
                            productPriceRecyclerAdapter = new ProductPriceRecyclerAdapter(getApplicationContext(), productArrayList);
                            selectProductPrice.show();

                            Window window = selectProductPrice.getWindow();
                            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            recyclerView_product_price.setAdapter(productPriceRecyclerAdapter);

                            adapterOnClick();

                        } else {
                            Toast.makeText(getApplicationContext(), "No product found", Toast.LENGTH_SHORT).show();
                           callProductTypeWS();

                        }
                    } else {

                        Toast.makeText(getApplicationContext(), "No product found", Toast.LENGTH_SHORT).show();
                        callProductTypeWS();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    selectProductPrice.dismiss();
                    Toast.makeText(getApplicationContext(), "No product found", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void adapterOnClick() {
        productPriceRecyclerAdapter.setOnItemClickListener(new ProductPriceRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(Product productModule, int position) {
                String name = productModule.getName();
                String price = productModule.getPrice();

                Intent intent = new Intent(PriceListActivity.this, PriceDetailsActivity.class);
                intent.putExtra("NAME", name);
                intent.putExtra("PRICE", price);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.PRICE_LIST:
                Logs.showLogE(TAG, response);
                break;
        }

    }

    private void callProductTypeWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.POST_PRODUCT_TYPE);
        map.put("local_or_export", productType.get(spinnerPC.getSelectedItemPosition() < 0 ? 0 : spinnerPC.getSelectedItemPosition()));
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.POST_PRODUCT_TYPE, true);
    }

    @Override
    public void selectedProductType(String mID) {
        mProductType = mID;
        callProductListWS(mID);
    }

    private void callProductListWS(String mID) {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.GET_PRODUCT);
        map.put("product_type", mID);
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.GET_PRODUCT, true);

    }

    @Override
    public void onBackPressed() {
        selectProductPrice.dismiss();
        super.onBackPressed();
    }
}


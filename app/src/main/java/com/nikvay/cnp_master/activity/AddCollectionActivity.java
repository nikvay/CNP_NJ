package com.nikvay.cnp_master.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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


import com.google.gson.Gson;
import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.MyCustomerAdapter;
import com.nikvay.cnp_master.apicallcommon.ApiClient;
import com.nikvay.cnp_master.apicallcommon.ApiInterface;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.common.VibrateOnClick;
import com.nikvay.cnp_master.model.MyCustomerModel;
import com.nikvay.cnp_master.model.SuccessModel;
import com.nikvay.cnp_master.utils.MyCustomerResponse;
import com.nikvay.cnp_master.utils.NetworkUtils;
import com.nikvay.cnp_master.utils.SelectCustomerInterface;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.utils.SuccessDialog;
import com.nikvay.cnp_master.utils.SuccessDialogClosed;
import com.nikvay.cnp_master.utils.UserData;
import com.nikvay.cnp_master.volley_support.MyVolleyPostMethod;
import com.nikvay.cnp_master.volley_support.ShowLoader;
import com.nikvay.cnp_master.volley_support.VolleyCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class AddCollectionActivity extends AppCompatActivity implements VolleyCompleteListener, SelectCustomerInterface, SuccessDialogClosed {
    private AutoCompleteTextView textCustomerNameCollection,
            textAmountCollection,
            textBillCollection,
            textCompanyNameCollection,
            textBillDate,
            textPhoto,
            textRemark;
    private Button btnSubmitCollection;
    private UserData userData;
    private TextView textSelectCustomerVisitC;
    private ImageView textSelectCustomerCHC;
    private MyCustomerResponse myCustomerResponse;
    private ArrayList<MyCustomerModel> arrayListC = new ArrayList<>();
    private MyCustomerAdapter adapterC;
    private Dialog selectCustomerDialog;
    private Button btnOkDialogSC, btnCancelDialogSC;
    private RecyclerView recyclerDialogSC;
    private String mCustomerNameC = null;
    private EditText editSearchC;
    private SuccessDialog successDialog;
    private FloatingActionButton floatingActionButton;
    private String date, TAG = getClass().getSimpleName();
    private boolean isSelect = false;
    private ApiInterface apiInterface;
    ShowLoader showLoader;


    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    private static final String IMAGE_DIRECTORY = "/CNPINDIA";
    Bitmap bitmap;
    Uri imageUrl;
    String photo;
    String mCurrentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        localBrodcastInitialize();
        initialize();
    }

    private void initialize() {
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        showLoader = new ShowLoader(AddCollectionActivity.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        VibrateOnClick.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        userData = new UserData(getApplicationContext());
        successDialog = new SuccessDialog(this, true);
        myCustomerResponse = new MyCustomerResponse(getApplicationContext());
        selectCustomerDialog = new Dialog(this);
        selectCustomerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectCustomerDialog.setContentView(R.layout.dialog_select_customer);
        selectCustomerDialog.setCancelable(false);
        btnOkDialogSC = selectCustomerDialog.findViewById(R.id.btnOkDialogSC);
        btnCancelDialogSC = selectCustomerDialog.findViewById(R.id.btnCancelDialogSC);
        editSearchC = selectCustomerDialog.findViewById(R.id.editSearchC);
        recyclerDialogSC = selectCustomerDialog.findViewById(R.id.recyclerDialogSC);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerDialogSC.setLayoutManager(manager);
        textCustomerNameCollection = findViewById(R.id.textCustomerNameCollection);
        floatingActionButton = findViewById(R.id.fabUploadImage);
        textCustomerNameCollection.setEnabled(false);
        textAmountCollection = findViewById(R.id.textAmountCollection);
        textBillCollection = findViewById(R.id.textBillCollection);
        btnSubmitCollection = findViewById(R.id.btnSubmitCollection);
        textSelectCustomerVisitC = findViewById(R.id.textSelectCustomerVisitC);
        textSelectCustomerCHC = findViewById(R.id.textSelectCustomerCHC);
        textCompanyNameCollection = findViewById(R.id.textCompanyNameCollection);

        textBillDate = findViewById(R.id.textBillDate);
        textPhoto = findViewById(R.id.textPhoto);
        textRemark = findViewById(R.id.textRemark);


        textPhoto.setEnabled(false);
        textPhoto.setTextColor(getResources().getColor(android.R.color.darker_gray));
        textBillDate.setText(date);
        textBillDate.setEnabled(false);
        textBillDate.setTextColor(getResources().getColor(android.R.color.darker_gray));
        events();
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

    private void events() {
        btnSubmitCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                if (isValid()) {
                    //callCollectionWS();
                    if (NetworkUtils.isNetworkAvailable(AddCollectionActivity.this))
                        callCollectionAdd();
                    else
                        NetworkUtils.isNetworkNotAvailable(AddCollectionActivity.this);


                }
            }
        });
        btnCancelDialogSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearchC.setText("");
                mCustomerNameC = null;
                textCustomerNameCollection.setText(null);
                selectCustomerDialog.dismiss();
            }
        });
        btnOkDialogSC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearchC.setText("");
                if (mCustomerNameC != null) {
                    textCustomerNameCollection.setText(mCustomerNameC);
                }
                selectCustomerDialog.dismiss();
            }
        });
        textSelectCustomerVisitC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMyCustomerWS();
            }
        });
        textSelectCustomerCHC.setOnClickListener(new View.OnClickListener() {
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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    private void callMyCustomerWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.MY_CUSTOMER_LIST);
        map.put("sale_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.MY_CUSTOMER_LIST, true);
    }

    private boolean isEmpty(AutoCompleteTextView mTextView) {
        if (mTextView.getText().toString().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean isValid() {
        if (!isEmpty(textCustomerNameCollection)) {
            textCustomerNameCollection.setError("Enter Customer Name");
            textCustomerNameCollection.requestFocus();
            return false;
        } else {
            textCustomerNameCollection.setError(null);
            textCustomerNameCollection.clearFocus();
        }
        if (!isEmpty(textAmountCollection)) {
            textAmountCollection.setError("Enter Amount");
            textAmountCollection.requestFocus();
            return false;
        } else {
            textAmountCollection.setError(null);
            textAmountCollection.requestFocus();
        }
        if (!isEmpty(textBillCollection)) {
            textBillCollection.setError("Enter Bill number");
            textBillCollection.requestFocus();
            return false;
        } else {
            textBillCollection.setError(null);
            textBillCollection.requestFocus();
        }

        if (!isEmpty(textBillDate)) {
            textBillDate.setError("Enter Bill Date");
            textBillDate.requestFocus();
            return false;
        } else {
            textBillDate.setError(null);
            textBillDate.requestFocus();
        }


        return true;
    }

    private void callCollectionWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.COLLECTION);
        map.put("sales_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        map.put("cust_name", textCustomerNameCollection.getText().toString());
        map.put("company_name", textCompanyNameCollection.getText().toString());
        map.put("amount", textAmountCollection.getText().toString());
        map.put("bill_no", textBillCollection.getText().toString());
        map.put("date", textBillDate.getText().toString());
        map.put("remark", textRemark.getText().toString());
        if (!(photo == null)) {
            map.put("photo", photo);
        }
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.COLLECTION, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.COLLECTION: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        CommonVisitCollectionActivity.isAdded = true;
                        // Toast.makeText(getApplicationContext(), "Collection added successfully", Toast.LENGTH_SHORT).show();
                        successDialog.showDialog("Collection added successfully", true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case ServerConstants.ServiceCode.MY_CUSTOMER_LIST: {
                if (arrayListC != null) {
                    arrayListC.clear();
                }
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
    public void getCustomerName(String mCustomerName) {
        mCustomerNameC = mCustomerName;
    }

    @Override
    public void getCustomerDetail(MyCustomerModel customerModel) {
        textCompanyNameCollection.setText(customerModel.getCompany_name());
    }

    @Override
    public void dialogClosed(boolean mClosed) {
        finish();
    }


    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddCollectionActivity.this);
        pictureDialog.setTitle("Select Action");
        pictureDialog.setIcon(R.drawable.ic_vector_camera);
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera", "Cancel"};
        pictureDialog.setCancelable(false);

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        takeFromGallery();
                        break;
                    case 1:
                        takeFromCamera();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    private void takeFromCamera() {


         /*  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           //Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                photoFile = getPictureFile();
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCurrentPhotoPath = photoFile.getAbsolutePath();
                   Uri fileUri = FileProvider.getUriForFile(this,
                            "com.nikvay.cnp_master.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, MY_CAMERA_REQUEST_CODE);
                }
            }*/

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
    }


    /*private File getPictureFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(pictureFile, ".jpg", storageDir);
            // mCurrentPhotoPath = image.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
      *//*  mCurrentPhotoPath = image.getAbsolutePath();
        fileUri=Uri.parse(mCurrentPhotoPath);*//*
        return image;
    }*/

    private void takeFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MY_GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if(resultCode==RESULT_OK) {

                if (requestCode == MY_CAMERA_REQUEST_CODE ) {

                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                   // image_view_doctor_report.setImageBitmap(thumbnail);
                    saveImage(thumbnail);
                    //                    update pick start
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    byte[] imgByte = byteArrayOutputStream.toByteArray();
                    photo= Base64.encodeToString(imgByte,Base64.DEFAULT);
                    textPhoto.setText("File Uploaded");

                } else if(requestCode==MY_GALLERY_REQUEST_CODE) {
                    imageUrl = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(AddCollectionActivity.this.getContentResolver(), imageUrl);
                    // ==== User Defined Method ======
                    convertToBase64(bitmap); //converting image to base64 string
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertToBase64(final Bitmap bitmap) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bAOS);
        byte[] imageBytes = bAOS.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        textPhoto.setText("File Uploaded");
        isSelect = true;

    }

    private void convertToBase64Camera(final Bitmap bitmap) {
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bAOS);
        byte[] imageBytes = bAOS.toByteArray();
        photo = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        textPhoto.setText("File Uploaded");
        isSelect = true;

    }
    // ===========*** End Image Upload Data ***=============


    private void callCollectionAdd() {

        String sales_person_id = userData.getUserData(StaticContent.UserData.USER_ID);
        String cust_name = textCustomerNameCollection.getText().toString();
        String company_name = textCustomerNameCollection.getText().toString();
        String amount = textAmountCollection.getText().toString();
        String bill_no = textBillCollection.getText().toString();
        String date = textBillDate.getText().toString();
        String remark = textRemark.getText().toString();


        showLoader.showDialog();
        Call<SuccessModel> call = apiInterface.addCollection(sales_person_id, cust_name, company_name, amount, bill_no, date, remark, photo);
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

                            if (code.equalsIgnoreCase("1")) {
                                CommonVisitCollectionActivity.isAdded = true;
                                successDialog.showDialog("Collection added successfully", true);

                            } else {
                                Toast.makeText(AddCollectionActivity.this, "Response Wrong", Toast.LENGTH_SHORT).show();
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

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(AddCollectionActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}

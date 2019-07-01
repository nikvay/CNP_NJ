package com.nikvay.cnp_master.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.MyCustomerAdapter;
import com.nikvay.cnp_master.apicallcommon.ApiClient;
import com.nikvay.cnp_master.apicallcommon.ApiInterface;
import com.nikvay.cnp_master.common.ServerConstants;
import com.nikvay.cnp_master.model.SuccessModel;
import com.nikvay.cnp_master.utils.NetworkUtils;
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
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.LENGTH_LONG;

public class AddDepositActivity extends AppCompatActivity implements VolleyCompleteListener, SuccessDialogClosed {

    private String collection_id, sales_person_id, description;
    private UserData userData;
    private ImageView iv_back_image_activity;
    private FloatingActionButton fabUploadImage;
    private AutoCompleteTextView textDescription, textPhoto;
    private Button btnSubmitDeposit;
    private SuccessDialog successDialog;
    private ApiInterface apiInterface;
    ShowLoader showLoader;


    // =========== Upload image ================
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int MY_GALLERY_REQUEST_CODE = 2;
    Bitmap bitmap;
    Uri fileUri;
    Uri imageUrl;
    String photo,TAG = getClass().getSimpleName();
    private boolean isSelect = false;
    private static final String IMAGE_DIRECTORY = "/CNPINDIA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deposit);

        initialize();
        events();
    }

    private void initialize() {
        userData = new UserData(getApplicationContext());
        showLoader = new ShowLoader(AddDepositActivity.this);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        iv_back_image_activity = findViewById(R.id.iv_back_image_activity);
        fabUploadImage = findViewById(R.id.fabUploadImage);
        textDescription = findViewById(R.id.textDescription);
        textPhoto = findViewById(R.id.textPhoto);
        btnSubmitDeposit = findViewById(R.id.btnSubmitDeposit);
        textPhoto.setEnabled(false);
        textPhoto.setTextColor(getResources().getColor(android.R.color.darker_gray));

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            collection_id = bundle.getString(StaticContent.IntentType.COLLECTION_NUMBER);

        }

        sales_person_id = userData.getUserData(StaticContent.UserData.USER_ID);
       // Toast.makeText(this, collection_id + " " + sales_person_id, Toast.LENGTH_SHORT).show();

       successDialog = new SuccessDialog(this, true);
    }

    private void events() {

        fabUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btnSubmitDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = textDescription.getText().toString().trim();
                if (description.equalsIgnoreCase("")) {
                    textDescription.setError("Enter Description");
                    textDescription.requestFocus();
                } else {

                    if (NetworkUtils.isNetworkAvailable(AddDepositActivity.this))
                        callCollectionDeposit();
                    else
                        NetworkUtils.isNetworkNotAvailable(AddDepositActivity.this);

                    //callSubmitDepositWS();

                }

            }
        });
        iv_back_image_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void callSubmitDepositWS() {
        HashMap<String, String> map = new HashMap<>();
        map.put(ServerConstants.URL, ServerConstants.serverUrl.COLLECTION_DEPOSIT);
        map.put("sales_person_id", sales_person_id);
        map.put("collection_id", collection_id);
        map.put("description", description);
        if (!(photo == null)) {
            map.put("photo", photo);
        }
        new MyVolleyPostMethod(this, map, ServerConstants.ServiceCode.COLLECTION_DEPOSIT, true);
    }


    // ===================================*** Image Upload Data ***=================================
    private void showPictureDialog() {
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddDepositActivity.this);
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
        // Check if this device has a camera
        /*if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    // Open default camera
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);             // start the image capture Intent
            startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);        //100
        } else {
            // no camera on this device
            Toast.makeText(AddDepositActivity.this, "Camera not supported", LENGTH_LONG).show();
        }*/

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,MY_CAMERA_REQUEST_CODE);
    }

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

                if (requestCode == MY_CAMERA_REQUEST_CODE) {

                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    // image_view_doctor_report.setImageBitmap(thumbnail);
                    saveImage(thumbnail);
                    //                    update pick start
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] imgByte = byteArrayOutputStream.toByteArray();
                    photo = Base64.encodeToString(imgByte, Base64.DEFAULT);
                    textPhoto.setText("File Uploaded");

                } else if (requestCode == MY_GALLERY_REQUEST_CODE) {
                    imageUrl = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(AddDepositActivity.this.getContentResolver(), imageUrl);
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
        textPhoto.setText("File Upload");
        isSelect = true;

    }

    // ===========*** End Image Upload Data ***=============


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case ServerConstants.ServiceCode.COLLECTION_DEPOSIT: {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String error_code = jsonObject.getString("error_code");
                    String msg = jsonObject.getString("msg");
                    if (error_code.equals(StaticContent.ServerResponseValidator.ERROR_CODE) && msg.equals(StaticContent.ServerResponseValidator.MSG)) {
                        // Toast.makeText(getApplicationContext(), "Collection Deposit added successfully", Toast.LENGTH_SHORT).show();
                        successDialog.showDialog("Collection Deposit added successfully", true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

        }

    }

    @Override
    public void onTaskFailed(String response, int serviceCode) {

    }

    @Override
    public void dialogClosed(boolean mClosed) {
        finish();
    }



    private void callCollectionDeposit() {

        String sales_person_id=userData.getUserData(StaticContent.UserData.USER_ID);


        showLoader.showDialog();
        Call<SuccessModel> call = apiInterface.addCollectionDeposit(sales_person_id,description,collection_id,photo);
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
                                successDialog.showDialog( "Collection Deposit added successfully",true);

                            } else {
                                Toast.makeText(AddDepositActivity.this, "Deposit Not Added", Toast.LENGTH_SHORT).show();
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
            MediaScannerConnection.scanFile(AddDepositActivity.this,
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

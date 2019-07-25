package com.nikvay.cnp_master.volley_support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nikvay.cnp_master.utils.InternetErrorDialog;
import com.nikvay.cnp_master.utils.LoginAlertDialog;
import com.nikvay.cnp_master.utils.StaticContent;
import com.nikvay.cnp_master.utils.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyVolleyPostMethod
{
    Context context;
    private int serviceCode;
    private Map<String, String> map;
    private VolleyCompleteListener mVolleylistener;
    ShowLoader showLoader;
    InternetErrorDialog internetErrorDialog;
    private LoginAlertDialog loginAlertDialog;
    private UserData userData;

    public MyVolleyPostMethod(Context context, HashMap<String, String> map, int serviceCode, boolean isDialog)
    {
        this.map = map;
        this.serviceCode = serviceCode;
        this.context = context;
        this.userData = new UserData(context);
        this.loginAlertDialog = new LoginAlertDialog(context);
        internetErrorDialog=new InternetErrorDialog(context);
        if (isDialog) {
            showLoader = new ShowLoader(context);
        }
        if (isNetworkAvailable(context)) {
            mVolleylistener = (VolleyCompleteListener) context;
            myBackgroundGetClass(context, serviceCode, map, isDialog);
        } else {
            internetErrorDialog.showDialog("No Internet Connection");
        }
    }

    private void myBackgroundGetClass(final Context context, int serviceCode, final Map<String, String> map, final boolean isDialog) {

        final int code = serviceCode;
        String url = map.get("url");
        map.remove("url");
        if (!userData.getUserData(StaticContent.UserData.USER_ID).equals("")) {
            map.put("sales_person_id", userData.getUserData(StaticContent.UserData.USER_ID));
        }
        if (isDialog) {
            showLoader.showDialog();
        }
        mVolleylistener = (VolleyCompleteListener) context;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (isDialog) {
                            showLoader.dismissDialog();
                        }
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String error_code = jsonObject.getString("error_code");
                                if (error_code.equals(StaticContent.LoginAlertCode.RE_LOGIN_CODE)) {
                                    String msg = jsonObject.getString("msg");
                                    loginAlertDialog.showLoginAlertDialog(msg);
                                } else {
                                    mVolleylistener.onTaskCompleted(response, code);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mVolleylistener.onTaskFailed(error.toString(), code);
                        if (isDialog) {
                            showLoader.dismissDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };

        int socketTimeout = 10000;//10 seconds - change to what you want
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //RequestQueue requestQueue = Volley.newRequestQueue(context);
        //requestQueue.add(stringRequest);
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

}

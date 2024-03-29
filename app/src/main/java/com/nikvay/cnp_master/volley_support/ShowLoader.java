package com.nikvay.cnp_master.volley_support;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.nikvay.cnp_master.R;


public class ShowLoader {

    private Dialog dialog;
    private Context context;

    public ShowLoader(Context context) {
        this.context = context;
    }

    public ShowLoader showDialog() {

        if (dialog != null) {
            dialog = null;
        }

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource (android.R.color.transparent);
        dialog.show();
        return null;
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

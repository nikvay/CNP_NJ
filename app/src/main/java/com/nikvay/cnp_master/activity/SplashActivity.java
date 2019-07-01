package com.nikvay.cnp_master.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.common.RuntimePermissionsActivity;
import com.nikvay.cnp_master.utils.SharedUtil;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Tamboli on 16-Sep-17.
 */

public class SplashActivity extends RuntimePermissionsActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 20;
    SharedUtil sharedUtil;
    TextView text_cnp_message;
    ImageView iv_logo;
    int duration = 3000;
    Handler handler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);

        text_cnp_message = findViewById(R.id.text_cnp_message);
        setScaleAnimation(text_cnp_message);

        iv_logo = findViewById(R.id.iv_logo);
        setScaleAnimationImage(iv_logo);

      /*  final RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        anim.setDuration(850); //Put desired duration per anim cycle here, in milliseconds

        iv_logo.startAnimation(anim);//Start animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_logo.setAnimation(null);  //Later on, use view.setAnimation(null) to stop it.
            }
        }, 1000);*/

        sharedUtil = new SharedUtil(SplashActivity.this);
        if (isDeviceBuildVersionMarshmallow()) {
            getPermisson();
        } else {
            handler(duration);
        }
    }

    private boolean isDeviceBuildVersionMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void getPermisson() {
        SplashActivity.super.requestAppPermissions(new
                        String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                }, R.string
                        .runtime_permissions_txt
                , REQUEST_PERMISSIONS);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isDeviceBuildVersionMarshmallow()) {
            getPermisson();
        } else {
            handler(duration);
        }
    }

    private void handler(final int duration) {
        handler.postDelayed(mUpdateTimeTask, duration);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {

                if (sharedUtil.getUserDetails().getUser_id() == null) {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                } else {
                    Intent in = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onPermissionsGranted(final int requestCode) {
        if (requestCode == REQUEST_PERMISSIONS) {
            handler(duration);
        }
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f, Animation.REVERSE, 0.5f, Animation.REVERSE, 0.5f);
        anim.setDuration(300);
        view.animate();
        view.startAnimation(anim);
    }

    private void setScaleAnimationImage(View view) {
        ScaleAnimation anim = new ScaleAnimation(1.0f, 0.1f, 1.0f, 0.1f, Animation.REVERSE, 0.5f, Animation.REVERSE, 0.5f);
        anim.setDuration(300);
        view.animate();
        view.startAnimation(anim);
    }


}
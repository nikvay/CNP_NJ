package com.nikvay.cnp_master.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.adapter.DrawerRecyclerAdapter;
import com.nikvay.cnp_master.common.CommonVars;
import com.nikvay.cnp_master.common.RecyclerItemClickListener;
import com.nikvay.cnp_master.common.VibrateOnClick;
import com.nikvay.cnp_master.fragments.ChangePasswordFragment;
import com.nikvay.cnp_master.fragments.HomeScreenFragment;
import com.nikvay.cnp_master.fragments.MyCustomerFragment;
import com.nikvay.cnp_master.fragments.ProfileFragment;
import com.nikvay.cnp_master.fragments.StockListFragment;
import com.nikvay.cnp_master.model.Drawer;
import com.nikvay.cnp_master.utils.CommonIntent;
import com.nikvay.cnp_master.utils.FilesUtil;
import com.nikvay.cnp_master.utils.LogoutPopup;
import com.nikvay.cnp_master.utils.Logout_Application;
import com.nikvay.cnp_master.utils.SharedUtil;
import com.nikvay.cnp_master.utils.ShowToast;
import com.nikvay.cnp_master.utils.StaticContent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HomeScreenFragment.iHomeScreenFragmentItemClick, ProfileFragment.iProfileFragmentItemClick, ChangePasswordFragment.iChangePasswordFragmentItemClick
{

    private static final String TAG = MainActivity.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    LinearLayout layout_developed_by;
    LinearLayout layout_profile;
    //  LinearLayout linearLayout_home_bottom_nav,linearLayout_price_bottom_nav,linearLayout_quotation_bottom_nav;
    public static Toolbar toolbar;
    TextView txt_name, txt_email_id;
    ImageView img_profile;
    RecyclerView rv_Drawer;
    public static RecyclerView.Adapter adapter;
    DrawerLayout drawer;
    ArrayList<Drawer> drawers;
    SharedUtil sharedUtil;
    public static String image_base = null;
    private Fragment mmFragment;

    private Dialog dialog_welcome_msg;
    private Button btn_ok_welcome_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        localBrodcastInitialize();

        sharedUtil = new SharedUtil(MainActivity.this);
        drawers = new ArrayList<Drawer>();
        drawers.add(new Drawer(R.drawable.ic_vector_home, StaticContent.DrawerItems.HOME));
        //drawers.add(new Drawer(R.drawable.search, "Search"));
        drawers.add(new Drawer(R.drawable.ic_vector_notification, StaticContent.DrawerItems.NOTIFICATION));
        drawers.add(new Drawer(R.drawable.ic_vector_profile, StaticContent.DrawerItems.MY_ACCOUNT));
        drawers.add(new Drawer(R.drawable.ic_vector_search, StaticContent.DrawerItems.CHECK_PRICE));
        drawers.add(new Drawer(R.drawable.ic_vector_stock, StaticContent.DrawerItems.CHECK_STOCK));
        drawers.add(new Drawer(R.drawable.quotation_icon, StaticContent.DrawerItems.QUOTATION));
        drawers.add(new Drawer(R.drawable.my_customer_icon, StaticContent.DrawerItems.MY_CUSTOMER));
        drawers.add(new Drawer(R.drawable.ic_vector_mark_attendance, StaticContent.DrawerItems.MARK_ATTENDANCE));
        drawers.add(new Drawer(R.drawable.ic_vector_explodedview, StaticContent.DrawerItems.EXPLODED_VIEW));
        drawers.add(new Drawer(R.drawable.ic_leave_application, StaticContent.DrawerItems.LEAVE_APPLICATION));
        drawers.add(new Drawer(R.drawable.ic_holiday_list, StaticContent.DrawerItems.HOLIDAY_LIST));
        drawers.add(new Drawer(R.drawable.ic_my_performance, StaticContent.DrawerItems.MY_PERFORMANCE));
        //  drawers.add(new Drawer(R.drawable.key, StaticContent.DrawerItems.CHANGE_PASSWORD));
        drawers.add(new Drawer(R.drawable.ic_vector_logout, StaticContent.DrawerItems.LOGOUT));


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        VibrateOnClick.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        layout_profile = (LinearLayout) navigationView.findViewById(R.id.layout_profile);
        layout_developed_by = (LinearLayout) navigationView.findViewById(R.id.layout_developed_by);
        txt_name = (TextView) navigationView.findViewById(R.id.txt_name);
        txt_email_id = (TextView) navigationView.findViewById(R.id.txt_email_id);
        img_profile = (ImageView) navigationView.findViewById(R.id.img_profile);
        rv_Drawer = (RecyclerView) navigationView.findViewById(R.id.rv_Drawer);
        /*linearLayout_home_bottom_nav=findViewById(R.id.linearLayout_home_bottom_nav);
        linearLayout_price_bottom_nav=findViewById(R.id.linearLayout_price_bottom_nav);
        linearLayout_quotation_bottom_nav=findViewById(R.id.linearLayout_quotation_bottom_nav);*/
         LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rv_Drawer.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_Drawer.setHasFixedSize(true);
        rv_Drawer.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));

        adapter = new DrawerRecyclerAdapter(MainActivity.this, drawers);
        rv_Drawer.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(StaticContent.DrawerItems.MY_ACCOUNT, new ProfileFragment());
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        layout_developed_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonIntent.openUrl(MainActivity.this, "http://nikvay.com/");
            }
        });

        loadHomeFragment();

        rv_Drawer.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        displayActivity(drawers.get(position).getCategory_name());
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    }
                })
        );
       /* linearLayout_home_bottom_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                *//*if(!(mmFragment instanceof HomeScreenFragment)) {
                    loadHomeFragment();
                }*//*
                loadHomeFragment();

            }
        });
        linearLayout_quotation_bottom_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RequestQuotationActivity.class);
                startActivity(intent);
            }
        });
        linearLayout_price_bottom_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PriceListActivity.class);
                startActivity(intent);
            }
        });*/

        /*Show Welcome Message Dialog*/
        Bundle bundle = getIntent().getExtras();
        String isLoginSuccessful = "";
        assert bundle != null;
        if (bundle != null) {
            isLoginSuccessful = bundle.getString("isLoginSuccessful");

        } else {
            isLoginSuccessful = "";
        }

        dialog_welcome_msg = new Dialog(this);
        dialog_welcome_msg.setContentView(R.layout.dialog_wecome_msg);
        btn_ok_welcome_msg = dialog_welcome_msg.findViewById(R.id.btn_ok_welcome_msg);


        assert isLoginSuccessful != null;
        if (isLoginSuccessful.equals("yes")) {
            dialog_welcome_msg.show();
        }

        btn_ok_welcome_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_welcome_msg.dismiss();
            }
        });


    }

    private void localBrodcastInitialize()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY);
            if (message.equals(StaticContent.LocalBrodcastReceiverCode.CLOSE_ACTIVITY)) {
                Intent intentt = new Intent(MainActivity.this, LoginActivity.class);
                intentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentt);
            }
        }
    };


    private void showUserDetails() {


        if (sharedUtil.getUserDetails().getFirst_name() != null && sharedUtil.getUserDetails().getLast_name() != null) {
            txt_name.setText(sharedUtil.getUserDetails().getFirst_name() + " " + sharedUtil.getUserDetails().getLast_name());
        } else {
            txt_name.setText("Guest User");
        }

        txt_email_id.setText(sharedUtil.getUserDetails().getEmail());


    }


    private void displayActivity(String name) {

        switch (name) {
            case StaticContent.DrawerItems.HOME:
                VibrateOnClick.vibrate();
                loadHomeFragment();
                break;

            case StaticContent.DrawerItems.NOTIFICATION:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                break;

            case StaticContent.DrawerItems.MY_ACCOUNT:
                VibrateOnClick.vibrate();
                loadFragment(StaticContent.DrawerItems.MY_ACCOUNT, new ProfileFragment());
                break;

           /* case StaticContent.DrawerItems.CHECK_PRICE:
                loadPriceListFragment();
                break;*/


            case StaticContent.DrawerItems.CHECK_PRICE:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, PriceListActivity.class));
                break;

            case StaticContent.DrawerItems.CHECK_STOCK:
                VibrateOnClick.vibrate();
                loadStockListFragment();
                break;
            case StaticContent.DrawerItems.MARK_ATTENDANCE:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, AttendanceActivity.class));
                break;

            case StaticContent.DrawerItems.EXPLODED_VIEW:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, ExplodedViewActivity.class));
                break;

            case StaticContent.DrawerItems.LEAVE_APPLICATION:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, LeaveApplicationActivity.class));
                break;
            case StaticContent.DrawerItems.HOLIDAY_LIST:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, HolidayListActivity.class));
                break;

            case StaticContent.DrawerItems.MY_PERFORMANCE:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, MyPerformanceActivity.class));
                break;
         /*   case StaticContent.DrawerItems.CHANGE_PASSWORD:
                loadFragment(StaticContent.DrawerItems.CHANGE_PASSWORD, new ChangePasswordFragment(this));
                break;*/

            case StaticContent.DrawerItems.MY_CUSTOMER:
                VibrateOnClick.vibrate();
                loadFragment(StaticContent.DrawerItems.MY_CUSTOMER, new MyCustomerFragment(this));
                break;
            case StaticContent.DrawerItems.QUOTATION:
                VibrateOnClick.vibrate();
                startActivity(new Intent(MainActivity.this, RequestQuotationActivity.class));
                break;
            case StaticContent.DrawerItems.LOGOUT:
                VibrateOnClick.vibrate();
//                LogoutPopup.Logout(MainActivity.this);
                Logout_Application logout_application=new Logout_Application(MainActivity.this);
                logout_application.showDialog();
                break;

            default:
                // loadHomeFragment();
                break;
        }
    }

    // load HomeScreenFragment
    public void loadHomeFragment() {

        mmFragment = new HomeScreenFragment(this);
        if (mmFragment != null) {
            CommonVars.status = "home";
            toolbar.setTitle("CNP Business App");
            mmFragment.setHasOptionsMenu(true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content_frame, mmFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            showUserDetails();
        }
    }

    public void loadFragment(String mTitle, Fragment mFragment) {
        if (mFragment != null) {
            CommonVars.status = mTitle;
            toolbar.setTitle(mTitle);
            mFragment.setHasOptionsMenu(true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            showUserDetails();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                assert selectedImage != null;
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String img_Decodable_Str = cursor.getString(columnIndex);
                image_base = FilesUtil.getStringImage(BitmapFactory.decodeFile(img_Decodable_Str));
                if (ProfileFragment.img_profile1 != null) {
                    ProfileFragment.layout1.setVisibility(View.VISIBLE);
                    ProfileFragment.layout2.setVisibility(View.GONE);
                    ProfileFragment.img_profile1.setImageBitmap(BitmapFactory.decodeFile(img_Decodable_Str));
                }
            } catch (Exception e) {
                Log.e(TAG, "Something went embrassing");
            }
        } else {
            Log.e(TAG, "Hey pick your image first");
        }
    }

    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (CommonVars.status.equals("home")) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    System.exit(0);
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                ShowToast.showToast("Press again to exit", MainActivity.this);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                loadHomeFragment();
            }
        }
    }

    @Override
    public void loadStockListFragment() {
        loadFragment(StaticContent.DrawerItems.CHECK_STOCK, new StockListFragment());
    }


   /* public void loadPriceListFragment() {
        loadFragment(StaticContent.DrawerItems.CHECK_PRICE, new PriceListActivity());
    }*/

}

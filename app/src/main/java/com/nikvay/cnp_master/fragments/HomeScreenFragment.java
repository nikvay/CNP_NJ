package com.nikvay.cnp_master.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nikvay.cnp_master.R;
import com.nikvay.cnp_master.activity.CNPApplicationActivity;
import com.nikvay.cnp_master.activity.CNPProfileActivity;
import com.nikvay.cnp_master.activity.CommonVisitCollectionActivity;
import com.nikvay.cnp_master.activity.ExplodedViewActivity;
import com.nikvay.cnp_master.activity.LocationActivity;
import com.nikvay.cnp_master.activity.MainActivity;
import com.nikvay.cnp_master.activity.OrderProcessActivity;
import com.nikvay.cnp_master.activity.OutstandingActivity;
import com.nikvay.cnp_master.activity.PriceListActivity;
import com.nikvay.cnp_master.activity.QuotationActivity;
import com.nikvay.cnp_master.activity.QuotationListActivity;
import com.nikvay.cnp_master.activity.RequestQuotationActivity;
import com.nikvay.cnp_master.common.CommonVars;
import com.nikvay.cnp_master.common.VibrateOnClick;
import com.nikvay.cnp_master.utils.MyBounceInterpolator;
import com.nikvay.cnp_master.utils.SharedUtil;
import com.nikvay.cnp_master.utils.StaticContent;

import static android.content.Context.VIBRATOR_SERVICE;


/**
 * Created by Param3 on 6/15/2016.
 */
@SuppressLint("ValidFragment")
public class HomeScreenFragment extends Fragment {
    private LinearLayout linearCheckPrice,
            linearCheckStock,
            linearMyCustomer,
            linearGenQuotation,
            linearQuoList,
            linearVisit,
            linearCollection,
            linearOutstanding,
            linearOrderProcess,
            linearExplodedView,
            linearCNPApplication,
            linearCNPProfile;


    private static final String TAG = HomeScreenFragment.class.getSimpleName();
    SharedUtil sharedUtil;
    private Dialog dialog_more, dialog_daily_visit;
    private Context mContext;
    private TextView textVisitDailyReport,
            textCollectionDailyReport,
            textQuoteDialogMore,
            textLocationDailyReport;


    public HomeScreenFragment(Context mContext)
    {
        this.mContext = mContext;
    }

    public interface iHomeScreenFragmentItemClick {
        public void loadStockListFragment();

        //public void loadPriceListFragment();
    }

    public iHomeScreenFragmentItemClick iHomeScreenFragmentItemClick;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            iHomeScreenFragmentItemClick = (iHomeScreenFragmentItemClick) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        initView(view);
        events();
        return view;
    }

    private void initView(View view) {
        VibrateOnClick.vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        dialog_more = new Dialog(mContext);
        dialog_more.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_daily_visit = new Dialog(mContext);
        dialog_daily_visit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_more.setContentView(R.layout.dialog_more);
        dialog_more.setTitle("More");
        dialog_daily_visit.setContentView(R.layout.dialog_daily_report);
        dialog_daily_visit.setTitle("Daily Visit");
        sharedUtil = new SharedUtil(getActivity());
        linearCheckPrice = (LinearLayout) view.findViewById(R.id.linearCheckPrice);
        linearCheckStock = (LinearLayout) view.findViewById(R.id.linearCheckStock);
        linearMyCustomer = (LinearLayout) view.findViewById(R.id.linearMyCustomer);
        linearGenQuotation = (LinearLayout) view.findViewById(R.id.linearGenQuotation);
        linearQuoList = (LinearLayout) view.findViewById(R.id.linearQuoList);
        linearVisit = (LinearLayout) view.findViewById(R.id.linearVisit);
        linearCollection = (LinearLayout) view.findViewById(R.id.linearCollection);
        linearOutstanding = (LinearLayout) view.findViewById(R.id.linearOutstanding);
        linearOrderProcess = (LinearLayout) view.findViewById(R.id.linearOrderProcess);
        linearExplodedView=view.findViewById(R.id.linearExplodedList);
        linearCNPApplication=view.findViewById(R.id.linearCNPApplication);
        linearCNPProfile=view.findViewById(R.id.linearCNPProfile);


        textVisitDailyReport = (TextView) dialog_daily_visit.findViewById(R.id.textVisitDailyReport);
        textCollectionDailyReport = (TextView) dialog_daily_visit.findViewById(R.id.textCollectionDailyReport);
        textLocationDailyReport = (TextView) dialog_daily_visit.findViewById(R.id.textLocationDailyReport);
        textQuoteDialogMore = (TextView) dialog_more.findViewById(R.id.textQuoteDialogMore);
       /* btnMore = (Button) view.findViewById(R.id.btnMore);
        callBounceAnimation(btn_check_price);
        callBounceAnimation(btn_check_stock);
        callBounceAnimation(btnMore);
        callBounceAnimation(btnDailyReport);*/
    }

    private void events() {
        textLocationDailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, LocationActivity.class));
                dialog_daily_visit.dismiss();
            }
        });
        textQuoteDialogMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, QuotationListActivity.class));
                dialog_more.dismiss();
            }
        });
        linearVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                Intent intent = new Intent(mContext, CommonVisitCollectionActivity.class);
                intent.putExtra(StaticContent.ActivityType.ACTIVITY_TYPE, StaticContent.ActivityType.VIEW_VISITS);
                startActivity(intent);
                dialog_daily_visit.dismiss();
            }
        });
        linearCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                Intent intent = new Intent(mContext, CommonVisitCollectionActivity.class);
                intent.putExtra(StaticContent.ActivityType.ACTIVITY_TYPE, StaticContent.ActivityType.VIEW_COLLECTION);
                startActivity(intent);
                dialog_daily_visit.dismiss();
            }
        });

        //load stock
        linearCheckStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                iHomeScreenFragmentItemClick.loadStockListFragment();
            }
        });





        linearCheckPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                //iHomeScreenFragmentItemClick.loadPriceListFragment();
                startActivity(new Intent(mContext, PriceListActivity.class));
            }
        });




        linearGenQuotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, RequestQuotationActivity.class));

            }
        });

        linearQuoList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
//                startActivity(new Intent(mContext, QuotationListActivity.class));
                startActivity(new Intent(mContext, QuotationActivity.class));
            }
        });
        linearOutstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, OutstandingActivity.class));

            }
        });

        linearOrderProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                //  Toast.makeText(mContext, "Coming soon...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, OrderProcessActivity.class));

            }
        });

        linearMyCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrateOnClick.vibrate();
                loadFragment(StaticContent.DrawerItems.MY_CUSTOMER, new MyCustomerFragment(mContext));
            }
        });

        linearExplodedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, ExplodedViewActivity.class));
            }
        });
        linearCNPApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, CNPApplicationActivity.class));
            }
        });
        linearCNPProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VibrateOnClick.vibrate();
                startActivity(new Intent(mContext, CNPProfileActivity.class));
            }
        });



       /* btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_more.show();
                Window window = dialog_more.getWindow();
                window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });
        btnDailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_daily_visit.show();
                Window window = dialog_daily_visit.getWindow();
                window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });*/
    }

    private void callBounceAnimation(View view) {
        Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }

    public void loadFragment(String mTitle, Fragment mFragment) {
        if (mFragment != null) {
            CommonVars.status = mTitle;
            MainActivity.toolbar.setTitle(mTitle);
            mFragment.setHasOptionsMenu(true);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
    }
}
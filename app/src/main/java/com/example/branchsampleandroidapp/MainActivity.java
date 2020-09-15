package com.example.branchsampleandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchContentSchema;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.CurrencyType;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ProductCategory;
import io.branch.referral.util.ShareSheetStyle;

public class MainActivity extends AppCompatActivity {

    public TextView tv;
    public TextView insP;
    public TextView latP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.lv);
        insP = findViewById(R.id.installP);
        latP = findViewById(R.id.latestP);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Branch.getInstance(MainActivity.this).disableTracking(true);
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                    tv.setText(referringParams.toString());
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                    tv.setText(error.toString());
                }
            }
        }).withData(this.getIntent().getData()).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)
            if (error == null) {
                Log.d("Response: ", linkProperties.toString());
            }
        }
    };

    public void createLink(View view) {
        final LinkProperties lp = new LinkProperties()
                .setChannel("channel")
                .setFeature("feature")
                .setCampaign("Product")
                .addTag("contentUid")
                .addControlParameter("$marketing_title", "sampleSDK")
                .addControlParameter("$desktop_url", "sd")
                .addControlParameter("$web_only", "true")
                .addControlParameter("$android_deeplink_path", "randomadnjsnkd")
                .addControlParameter("time", Long.toString(Calendar.getInstance().getTimeInMillis()));

        BranchUniversalObject buo = new BranchUniversalObject()
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

        buo.listOnGoogleSearch(this);

      buo.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                    tv.setText(url);
                } else {
                    Log.d("TAG", "error response  " + error);
                }
            }
        });

       /* new BranchEvent(BRANCH_STANDARD_EVENT.ADD_TO_CART)
                .setAffiliation("test_affiliation")
                .setCustomerEventAlias("my_custom_alias")
                .setCoupon("Coupon Code")
                .setCurrency(CurrencyType.USD)
                .setDescription("Customer added item to cart")
                .setShipping(0.0)
                .setTax(9.75)
                .setRevenue(1.5)
                .setSearchQuery("Test Search query")
                .addCustomDataProperty("Custom_Event_Property_Key1", "Custom_Event_Property_val1")
                .addCustomDataProperty("Custom_Event_Property_Key2", "Custom_Event_Property_val2")
                .addContentItems(buo)
                .logEvent(this);*/


        // first
        JSONObject installParams = Branch.getInstance().getFirstReferringParams();
        insP.setText("First params : " + installParams);

        // latest
        JSONObject sessionParams = Branch.getInstance().getLatestReferringParams();
        latP.setText("latest params : " + sessionParams);
    }
}
package com.example.branchsampleandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.BRANCH_STANDARD_EVENT;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AppCompatActivity {

    public TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.lv);
        Branch.getInstance(this).disableTracking(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", referringParams.toString());
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
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
                .addControlParameter("$marketing_title","sampleSDK")
                .addControlParameter("$desktop_url", "sd")
                .addControlParameter("time", Long.toString(Calendar.getInstance().getTimeInMillis()));
        BranchUniversalObject buo = new BranchUniversalObject()
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);

        buo.generateShortUrl(this, lp, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("BRANCH SDK", "got my Branch link to share: " + url);
                    tv.setText(url);
                }
                else {
                    Log.d("TAG", "error response  " + error );
                }

            }
        });

        new BranchEvent(BRANCH_STANDARD_EVENT.PURCHASE)
                .setAffiliation("affiliation_value")
                .setCoupon("coupon_value")
                .setTax(12.3)
                .setRevenue(1.5)
                .setDescription("Event_description")
                .setSearchQuery("related_search_query")
                .addCustomDataProperty("Custom_Event_Property_Key", "Custom_Event_Property_Val")
                .logEvent(this);
    }

}
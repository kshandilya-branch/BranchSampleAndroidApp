package com.example.branchsampleandroidapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class DeepLinkActivity implements Branch.BranchReferralInitListener {

    @Override
    public void onInitFinished(@Nullable JSONObject referringParams, @Nullable BranchError error) {

    }
}
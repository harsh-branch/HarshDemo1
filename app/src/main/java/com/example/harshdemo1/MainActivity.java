package com.example.harshdemo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.createShortURLButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBranchLink();
            }
        });

        Button getFirstReferringParamsButton = findViewById(R.id.getFirstReferringParamsButton);
        getFirstReferringParamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, Branch.getInstance().getFirstReferringParams().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
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
            TextView textView = findViewById(R.id.textView);
            textView.setText(linkProperties.toString());
        }
    };

    private void createBranchLink(){

        BranchUniversalObject buo = new BranchUniversalObject()
                .setCanonicalIdentifier("content/12345")
                .setTitle("My Content Title")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://lorempixel.com/400/400")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(new ContentMetadata().addCustomMetadata("key1", "value1"));

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("$web_only", "true")
                .addControlParameter("$desktop_url", "http://example.com/home")
                .addControlParameter("custom", "data")
                .addControlParameter("custom_random", Long.toString(Calendar.getInstance().getTimeInMillis()));


        buo.generateShortUrl(this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if (error == null) {
                    Log.i("MyApp", "got my Branch link to share: " + url);
                    Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
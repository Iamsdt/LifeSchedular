package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

/*******************************************************************************
 * Copyright (c) 2017.
 * Project Name:Life Scheduler
 * Created By Shudipto Trafder
 * The Android Open Source Project
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

public class DeveloperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //cast
        ImageView fb = (ImageView) findViewById(R.id.devFacebook);
        ImageView twitter = (ImageView) findViewById(R.id.devTwitter);
        ImageView google = (ImageView) findViewById(R.id.dev_google);
        ImageView github = (ImageView) findViewById(R.id.devGitHub);

        CardView cardView = (CardView) findViewById(R.id.devMySite);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLink("https://www.facebook.com/Iamsdt");
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLink("https://www.twitter.com/Iamsdt");
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLink("https://plus.google.com/u/0/+ShudiptoTrafder");
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLink("https://github.com/Iamsdt");
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLink("http://shudiptotrafder.blogspot.com/");
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Set link with intent and and create chooser for user
     *
     * @param s link to set
     **/
    private void setLink(String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(s));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Select Browser"));
        }
    }

}

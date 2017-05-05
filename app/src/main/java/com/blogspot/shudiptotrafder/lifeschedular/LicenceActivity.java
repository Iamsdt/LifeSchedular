package com.blogspot.shudiptotrafder.lifeschedular;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

public class LicenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    //text onClick from xml
    public void licenceLink(View view) {
        setLink("https://creativecommons.org/licenses/by-nc-sa/4.0/");
    }

    public void licenceGitHub(View view) {
        setLink("https://github.com/Iamsdt/LifeSchedular");
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

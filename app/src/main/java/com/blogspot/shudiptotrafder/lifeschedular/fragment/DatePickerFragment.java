package com.blogspot.shudiptotrafder.lifeschedular.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.blogspot.shudiptotrafder.lifeschedular.BuildConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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


public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private TextView textView;
    private String dateStr;

    /**
     * This methods show log error message with throwable
     *
     * @param message String show on log
     */
    private static void sle(String message) {

        final String TAG = "DatePickerFragment";

        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();

        int startMonth;

        if (dateStr != null) {
            sle("Date string not null");
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            try {
                Date date = format.parse(dateStr);
                c.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        int startDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, startYear, startMonth, startDay);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month = month + 1;

        String dateStr = dayOfMonth + "/" + month + "/" + year;
        try {
            if (textView != null) {
                textView.setText(dateStr);
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("TimeDate", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Date", dateStr);
            editor.apply();

        } catch (Exception e) {
            e.printStackTrace();
            sle(e.getMessage());
        }
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

}

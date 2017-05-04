package com.blogspot.shudiptotrafder.lifeschedular.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

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

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 5/1/2017 at 5:28 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    private TextView textView;
    private String timeStr;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();

        if (timeStr != null) {

            SimpleDateFormat format = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

            try {
                Date time = format.parse(timeStr);
                c.setTime(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int hour;

        if (timeStr != null && timeStr.contains(" PM")) {
            hour = c.get(Calendar.HOUR_OF_DAY) + 12;
        } else {
            hour = c.get(Calendar.HOUR_OF_DAY);
        }

        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        //Get the AM or PM for current time
        String aMpM = "AM";
        if (hourOfDay > 11) {
            aMpM = "PM";
        }

        //Make the 24 hour time format to 12 hour time format
        int currentHour;
        if (hourOfDay > 11) {
            currentHour = hourOfDay - 12;
        } else {
            currentHour = hourOfDay;
        }

        String time = String.valueOf(currentHour)
                + ":" + String.valueOf(minute) + " " + aMpM;

        if (textView != null) {
            textView.setText(time);
        }

        Log.e("TIME is set", time);
        SharedPreferences preferences = getActivity().getSharedPreferences("TimeDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Time", time);
        editor.apply();
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }
}

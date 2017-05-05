package com.blogspot.shudiptotrafder.lifeschedular.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blogspot.shudiptotrafder.lifeschedular.R;

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

public class Utility {

    /**
     * Get checkbox setting
     *
     * @param context for access SharedPreferences
     */

    public static boolean getNightModeEnabled(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getBoolean(context.getString(R.string.switchKey), false);
    }

    /**Get text size  setting
     * @param context for access SharedPreferences
     * */
    public static int getTextSize(Context context) {

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);

        String txtSize = preferences.getString(context.getString(R.string.textSizeKey),
                context.getString(R.string.sTextModerateValue));

        return Integer.parseInt(txtSize);

    }

}

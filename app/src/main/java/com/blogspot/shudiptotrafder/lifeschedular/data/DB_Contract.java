package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.net.Uri;
import android.provider.BaseColumns;

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
 * com.blogspot.shudiptotrafder.lifeschedular.data
 * Created by Shudipto Trafder on 4/16/2017 at 1:13 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class DB_Contract {

    // The authority, which is how your code knows which Content Provider to access
    static final String AUTHORITY = "com.blogspot.shudiptotrafder.lifeschedular";
    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    static final String PATH_TASKS = "tasks";
    // The base content URI = "content://"  <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class Entry implements BaseColumns{

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();
        //column name
        public static final String COLUMN_TASK_NAME= "task_name";
        public static final String COLUMN_TASK_SOLUTION= "task_solution";
        public static final String COLUMN_TASK_TYPE= "task_type";
        public static final String COLUMN_TASK_STATUS= "task_status";
        public static final String COLUMN_TASK_DUE= "task_due";
        public static final String COLUMN_TASK_DATE= "task_date";
        public static final String COLUMN_TASK_TIME= "task_time";
        //all table name
        //task type table name
        static final String TASK_TABLE_NAME = "tasks";

        /**
         * Builds a URI that adds the weather date to the end of the forecast content URI path.
         * This is used to query details about a single weather entry by date. This is what we
         * use for the detail view query. We assume a normalized date is passed to this method.
         *
         * @param id Normalized date in milliseconds
         * @return Uri to query details about a single weather entry
         */
        public static Uri buildUriWithID(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(id))
                    .build();
        }
    }

}

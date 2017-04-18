package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular.data
 * Created by Shudipto Trafder on 4/16/2017 at 1:13 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class DB_Contract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.blogspot.shudiptotrafder.lifeschedular";

    // The base content URI = "content://"  <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_TASKS = "tasks";

    public static class Entry implements BaseColumns{

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        //all table name
        //task type table name
        public static final String TASK_TABLE_NAME = "tasks";
        public static final String COLLUMN_TASK_NAME= "task_name";
        public static final String COLLUMN_TASK_SOLUTION= "task_solution";
        public static final String COLLUMN_TASK_TYPE= "task_type";
        public static final String COLLUMN_TASK_STATUS= "task_status";
        public static final String COLLUMN_TASK_DATE= "task_date";
        public static final String COLLUMN_TASK_TIME= "task_time";


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

package com.blogspot.shudiptotrafder.lifeschedular.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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


public class DataProvider extends ContentProvider {

    //use to get all data from this path
    private static final int TASKS = 100;
    //use to get single data from a single row
    private static final int TASK_WITH_ID = 101;
    //match with which uri
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mHelper;

    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          *All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        matcher.addURI(DB_Contract.AUTHORITY,DB_Contract.PATH_TASKS,TASKS);
        matcher.addURI(DB_Contract.AUTHORITY,DB_Contract.PATH_TASKS + "/#",TASK_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {

        //create db instance
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        //get db
        final SQLiteDatabase database = mHelper.getWritableDatabase();

        //type of uri match
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){

            //all data in a table uri
            case TASKS:
                returnCursor = database.query(
                        //table
                        DB_Contract.Entry.TASK_TABLE_NAME,
                        //selected column
                        projection,
                        //section
                        selection,
                        //selection arg
                        selectionArgs,
                        //group by
                        null,
                        //having
                        null,
                        sortOrder);
                break;

            // Add a case to query for a single row of data by ID
            // Use selections and selectionArgs to filter for that ID
            case TASK_WITH_ID:
                // Get the id from the URI
                String id = uri.getPathSegments().get(1);
                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id = ?";
                String[] mSelectionArg = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                returnCursor = database.query(DB_Contract.Entry.TASK_TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArg,
                        null,
                        null,
                        sortOrder);

                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }


        //set notification for data changed
        assert getContext() != null;
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return returnCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase database = mHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        //uri
        Uri returnUri;

        switch (match){

            case TASKS:
                long inserted = database.insert(DB_Contract.Entry.TASK_TABLE_NAME,null,values);

                if (inserted > 0){
                    //success
                    returnUri = ContentUris.withAppendedId(DB_Contract.Entry.CONTENT_URI,inserted);
                } else {
                    throw new RuntimeException("FAILED TO INSERTED DATA: "+uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        //notify data has changed

        if (getContext() != null){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        //uri
        int taskDelete;

        switch (match) {

            case TASK_WITH_ID:
                // Get the id from the URI
                String id = uri.getPathSegments().get(1);
                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String whereClause = "_id = ?";
                String[] whereArgs = new String[]{id};

                taskDelete = db.delete(DB_Contract.Entry.TASK_TABLE_NAME,
                        whereClause,
                        whereArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (taskDelete != 0) {
            // A task was deleted, set notification
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return taskDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mHelper.getWritableDatabase();

        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {

            case TASK_WITH_ID:

                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections

                String whereClause = "_id = ? ";
                String[] whereArgs = new String[]{id};

                tasksUpdated = db.update(DB_Contract.Entry.TASK_TABLE_NAME, values,
                        whereClause, whereArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }


    /* getType() handles requests for the MIME type of data
    We are working with two types of data:
    1) a directory and 2) a single row of data.
    This method will not be used in our app, but gives a way to standardize the data formats
    that your provider accesses, and this can be useful for data organization.
    For now, this method will not be used but will be provided for completeness.
    */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {

            case TASKS:
                // directory
                return "vnd.android.cursor.dir" + "/" + DB_Contract.AUTHORITY + "/" + DB_Contract.PATH_TASKS;

            case TASK_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + DB_Contract.AUTHORITY + "/" + DB_Contract.PATH_TASKS;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }
}

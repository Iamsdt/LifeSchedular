package com.blogspot.shudiptotrafder.lifeschedular.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.shudiptotrafder.lifeschedular.R;
import com.blogspot.shudiptotrafder.lifeschedular.data.DB_Contract;

/**
 * LifeSchedular
 * com.blogspot.shudiptotrafder.lifeschedular
 * Created by Shudipto Trafder on 4/16/2017 at 7:24 PM.
 * Don't modify without permission of Shudipto Trafder
 */

public class CustomCursorAdapter extends
        RecyclerView.Adapter<CustomCursorAdapter.MyTaskViewHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;

    public CustomCursorAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list,parent,false);

        return new MyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyTaskViewHolder holder, int position) {

        //move cursor to position
        /*Must use cursor move to position
        * if we use cursor move to first the cursor return
        * same data*/
        mCursor.moveToPosition(position);
        //get task name and show this text in text view
        final String task = mCursor.getString(mCursor.getColumnIndex(DB_Contract.Entry.COLUMN_TASK_NAME));
        String solution = mCursor.getString(mCursor.getColumnIndex(DB_Contract.Entry.COLUMN_TASK_SOLUTION));

        String date = mCursor.getString(mCursor.getColumnIndex(DB_Contract.Entry.COLUMN_TASK_DATE));
        String time = mCursor.getString(mCursor.getColumnIndex(DB_Contract.Entry.COLUMN_TASK_TIME));


        //get id and set it into
        int id = mCursor.getInt(mCursor.getColumnIndex(DB_Contract.Entry._ID));
        holder.itemView.setTag(id);

        //set all text view
        holder.task.setText(task);
        Log.e("Task Name:",task);

        /* if those text is available the we show those view
        * or those view is not shown
        * we set separate if condition so that
        * it works for particular view */

        if (solution != null && solution.length() > 0){
            holder.solutionTv.setVisibility(View.VISIBLE);
            holder.solutionTv.setText(solution);
        }

        if (date != null && date.length() > 0){
            holder.dateTv.setVisibility(View.VISIBLE);
            holder.dateTv.setText(date);
        }


        if (time != null && time.length() > 0){
            holder.textClock.setVisibility(View.VISIBLE);
            holder.textClock.setText(time);
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Task Name: "+task, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }

        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    //interface
    //replaced by another option of recycle view click listener
//    public interface ClickListener {
//        void onClickListener(String taskName);
//    }

    // Inner class for creating ViewHolders

    /*replaced by another option of recycle view click listener
    *this type is not applicable here
    *because getAdapterPosition() is not same as task _id
    *we are deleting some data or updating some data
    *so all data is not showing

    *class MyTaskViewHolder extends RecyclerView.ViewHolder
    *        implements View.OnClickListener {
    */

    class MyTaskViewHolder extends RecyclerView.ViewHolder {
        // Class variables for the task description and priority TextViews
        private TextView task,solutionTv,dateTv;
        private CardView mainCard;
        private TextView textClock;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        MyTaskViewHolder(View itemView) {
            super(itemView);

            //assign view
            //all text view
            task = (TextView) itemView.findViewById(R.id.main_list_tv);
            solutionTv = (TextView) itemView.findViewById(R.id.main_solution_tv);
            dateTv = (TextView) itemView.findViewById(R.id.main_date_tv);
            textClock = (TextView) itemView.findViewById(R.id.main_time_clock);
            mainCard = (CardView) itemView.findViewById(R.id.mainCardView);

            //replaced by another option of recycle view click listener
            //mainCard.setOnClickListener(this);

            solutionTv.setVisibility(View.GONE);
            dateTv.setVisibility(View.GONE);
            textClock.setVisibility(View.GONE);

        }

//        @Override
//        public void onClick(View v) {
//            mCursor.moveToPosition(getAdapterPosition());
//            String taskName = mCursor.getString(mCursor.getColumnIndex(DB_Contract.Entry.COLUMN_TASK_NAME));
//            clickListener.onClickListener(taskName);
//        }
    }

}

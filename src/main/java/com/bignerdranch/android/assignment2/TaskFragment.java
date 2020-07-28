package com.bignerdranch.android.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import java.util.Date;
import java.util.UUID;

public class TaskFragment extends Fragment {

    private static final String ARG_TASK_ID = "task_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String TIME_FORMAT = "hh:mm aa";

    private Task mTask;
    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mDateButton;
    private Button mTimeButton;
    private RadioGroup mStatusGroup;

    //Create new Task fragment
    public static TaskFragment newInstance(UUID taskId){

        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID,taskId);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        mTask = TaskMaster.get(getActivity()).getTask(taskId);
    }

    @Override
    public void onPause() {
        super.onPause();

        TaskMaster.get(getActivity())
                .updateTask(mTask);
    }

    //Create View
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task,container,false);

        //Title related
        mTitleField = (EditText) v.findViewById(R.id.task_title);
        mTitleField.setText(mTask.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Details related
        mDetailsField = (EditText) v.findViewById(R.id.task_details);
        mDetailsField.setText(mTask.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Date related
        mDateButton = (Button) v.findViewById(R.id.task_date);
        updateDate();
        //Change date via dialog
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mTask.getDate());
                dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //Time related
        mTimeButton = (Button) v.findViewById(R.id.task_time);
        updateTime();
        //Change time via dialog
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mTask.getDate());
                dialog.setTargetFragment(TaskFragment.this,REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        //Task status related
        mStatusGroup = (RadioGroup) v.findViewById(R.id.status_group);

        //Clear any existing checks before initialization. Check radio button according to task status
        mStatusGroup.clearCheck();

        ((RadioButton)mStatusGroup.getChildAt(mTask.getTaskCode())).setChecked(true);

        mStatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.status_pending){
                    mTask.setTaskCode(0);
                }else if(checkedId == R.id.status_cancel){
                    mTask.setTaskCode(1);
                }else if(checkedId == R.id.status_complete){
                    mTask.setTaskCode(2);
                }else{
                    mTask.setTaskCode(0);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE || requestCode == REQUEST_TIME){

            final Date date;

            if(requestCode == REQUEST_DATE){
                date = (Date) data
                        .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            }else{
                date = (Date) data
                        .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }

            mTask.setDate(date);
            updateDate();
            updateTime();
        }
    }

    //Function to update date
    private void updateDate() {
        String formattedDate = DateFormat.format(DATE_FORMAT,mTask.getDate()).toString();
        mDateButton.setText(formattedDate);
    }

    //Function to update time
    private void updateTime(){
        String formattedTime = DateFormat.format(TIME_FORMAT,mTask.getDate()).toString();
        mTimeButton.setText(formattedTime);
    }
}

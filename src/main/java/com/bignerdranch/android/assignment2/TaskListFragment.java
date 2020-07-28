package com.bignerdranch.android.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

public class TaskListFragment extends Fragment {

    private final static String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private final static String DATE_FORMAT = "dd MMMM yyyy";
    private static final String TIME_FORMAT = "hh:mm aa";

    private RecyclerView mTaskRecyclerView;
    private View mEmptyView;
    private TaskAdapter mAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list,container,false);

        //Create empty view if no tasks present. Hide when task present
        mEmptyView = view
                .findViewById(R.id.empty_view);
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        mTaskRecyclerView = (RecyclerView) view
                .findViewById(R.id.task_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        //Remove tasks by swiping right
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP| ItemTouchHelper.DOWN, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction == ItemTouchHelper.RIGHT){
                    mAdapter.taskSwipeDelete(viewHolder.getAdapterPosition());
                    updateUI();
                }
            }
        });

        mItemTouchHelper.attachToRecyclerView(mTaskRecyclerView);

        updateUI();

        return view;
    }

    private class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;
        private TextView mTaskStatus;
        private ImageView mStatusImage;
        private Task mTask;

        public TaskHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_tasks,parent,false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.task_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.task_date);
            mTimeTextView = (TextView) itemView.findViewById(R.id.task_time);
            mTaskStatus = (TextView) itemView.findViewById(R.id.task_status);
            mStatusImage = (ImageView) itemView.findViewById(R.id.status_image);
        }

        public void bind(Task task){
            //Set Task attributes
            mTask = task;
            String formattedDate = DateFormat.format(DATE_FORMAT,mTask.getDate()).toString();
            String formattedTime = DateFormat.format(TIME_FORMAT,mTask.getDate()).toString();
            mTitleTextView.setText(mTask.getTitle());
            mDateTextView.setText(formattedDate);
            mTimeTextView.setText(formattedTime);
            mTaskStatus.setText(setStatus(mTask.getTaskCode()));

            //Set icon to display depending on the task code
            switch (mTask.getTaskCode()){
                case(0):
                    mStatusImage.setImageResource(R.drawable.ic_status_pending_light);
                    break;
                case(1):
                    mStatusImage.setImageResource(R.drawable.ic_status_cancel_light);
                    break;
                case(2):
                    mStatusImage.setImageResource(R.drawable.ic_status_complete_light);
                    break;
                default:
                    mStatusImage.setImageResource(R.drawable.ic_status_pending_light);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = TaskPagerActivity.newIntent(getActivity(), mTask.getId());
            startActivity(intent);
        }
    }

    private String setStatus(int status){
        String taskStatus;
        //Set task status text
        switch (status){
            case(0):
                taskStatus = getResources().getString(R.string.todo_status_pending);
                break;
            case(1):
                taskStatus = getResources().getString(R.string.todo_status_cancel);
                break;
            case(2):
                taskStatus = getResources().getString(R.string.todo_status_complete);
                break;
            default:
                taskStatus = "Default";
        }

        return taskStatus;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_task:
                addTask();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Add new task
    private void addTask() {
        Task task = new Task();
        //Initialize Date value if null
        if(task.getDate() == null){
            task.setDate(new Date());
        }
        TaskMaster.get(getActivity()).addTask(task);
        Intent intent = TaskPagerActivity
                .newIntent(getActivity(),task.getId());
        startActivity(intent);
    }

    /*Update subtitle related parameters, including number of tasks, and subtitle visibility*/
    private void updateSubtitle(){
        TaskMaster taskMaster = TaskMaster.get(getActivity());
        int taskCount = taskMaster.getTaskList().size();
        String subtitle = getString(R.string.subtitle_format,taskCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /*Update UI related processes*/
    private void updateUI(){
        TaskMaster taskMaster = TaskMaster.get(getActivity());
        List<Task> tasks = taskMaster.getTaskList();

        if(mAdapter == null){
            mAdapter = new TaskAdapter(tasks);
            mTaskRecyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }

        //Set visibility of Empty View. Disabled if there is at least one task
        if(mAdapter.getItemCount() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }

        updateSubtitle();

    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder>{
        private List<Task> mTasks;

        public TaskAdapter(List<Task> tasks){
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        //Get number of tasks
        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        //Delete task. Will be applied when swiping
        public void taskSwipeDelete(int position){
            TaskMaster taskMaster = TaskMaster.get(getActivity());
            Task task = mTasks.get(position);
            taskMaster.removeTask(task);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(position, taskMaster.getTaskList().size());
            Toast.makeText(getContext(), R.string.remove_task, Toast.LENGTH_SHORT).show();
        }

        public void setTasks(List<Task> tasks){
            mTasks = tasks;
        }
    }
}

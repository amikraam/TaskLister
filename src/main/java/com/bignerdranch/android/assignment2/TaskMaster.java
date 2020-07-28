package com.bignerdranch.android.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.assignment2.database.TaskBaseHelper;
import com.bignerdranch.android.assignment2.database.TaskCursorWrapper;
import com.bignerdranch.android.assignment2.database.TaskDbSchema.TaskTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Class handles task related actions
public class TaskMaster {
    private static TaskMaster sTaskMaster;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    //create new TaskMaster instance
    public static TaskMaster get(Context context){
        if(sTaskMaster == null){
            sTaskMaster = new TaskMaster(context);
        }
        return sTaskMaster;
    }

    private TaskMaster(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext)
                .getWritableDatabase();
    }

    //Add a task and write to database
    public void addTask(Task t){
        ContentValues values = getContentValues(t);

        mDatabase.insert(TaskTable.NAME,null,values);

    }

    //Remove task from database
    public void removeTask(Task t){
        String uuidString = t.getId().toString();

        mDatabase.delete(TaskTable.NAME,
                TaskTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    //Get all available task as ArrayList
    public List<Task> getTaskList(){
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = queryTasks(null,null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return tasks;
    }

    //Get a specific task from the database
    public Task getTask(UUID id){
        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getTask();
        } finally {
            cursor.close();
        }
    }

    //Update values if needed
    public void updateTask(Task task){
        String uuidString = task.getId().toString();
        ContentValues values = getContentValues(task);

        mDatabase.update(TaskTable.NAME, values,
                TaskTable.Cols.UUID+" = ?",
                new String[]{uuidString});
    }

    //Reading from the database
    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new TaskCursorWrapper(cursor);
    }

    //Insert values into database
    private static ContentValues getContentValues(Task task){
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DETAILS, task.getDetails());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.STATUS, task.codeToString(task.getTaskCode()));

        return values;
    }
}

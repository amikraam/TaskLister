package com.bignerdranch.android.assignment2.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.assignment2.Task;
import com.bignerdranch.android.assignment2.database.TaskDbSchema.TaskTable;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor){
        super(cursor);
    }

    //Retrieve data from database for viewing
    public Task getTask(){
        String uuidString = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        String details = getString(getColumnIndex(TaskTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        String statusString = getString(getColumnIndex(TaskTable.Cols.STATUS));

        Task task = new Task(UUID.fromString(uuidString));
        task.setTitle(title);
        task.setDetails(details);
        task.setDate(new Date(date));
        task.setTaskCode(task.StringToCode(statusString));

        return task;
    }
}

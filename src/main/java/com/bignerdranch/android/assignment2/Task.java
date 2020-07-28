package com.bignerdranch.android.assignment2;

import java.util.Date;
import java.util.UUID;

/*Main components of a Task
* UUID for identification
* Title for task title
* Details to store details
* Date for task date
* Task Code for current status of task. Can only be Pending(0), Cancelled(1) or Completed(2)*/
public class Task {

    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private int mTaskCode;

    public Task(){
        mId = UUID.randomUUID();
    }

    public Task(UUID id){
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public int getTaskCode() {
        return mTaskCode;
    }

    public void setTaskCode(int taskCode) {
        mTaskCode = taskCode;
    }

    /*
    * Converts task code into String for database purposes
    * 0 = Pending
    * 1 = Cancelled
    * 2 = Completed
    * */
    public String codeToString(int taskCode){
        String codeString;
        switch (taskCode){
            default:
                return null;
            case 0:
                codeString = "Pending";
                break;
            case 1:
                codeString = "Cancelled";
                break;
            case 2:
                codeString = "Completed";
                break;
        }
        return codeString;
    }

    /*Convert String obtained from database into Task Code*/
    public int StringToCode(String codeString){
        if(codeString.equals("Cancelled")) return 1;
        if(codeString.equals("Completed")) return 2;

        return 0;
    }
}

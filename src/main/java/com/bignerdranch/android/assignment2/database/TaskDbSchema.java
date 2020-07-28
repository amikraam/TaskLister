package com.bignerdranch.android.assignment2.database;

public class TaskDbSchema {

    //Storage of database schema
    public static final class TaskTable{
        public static final String NAME = "tasks";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DETAILS = "details";
            public static final String DATE = "date";
            public static final String STATUS = "status";
        }
    }
}

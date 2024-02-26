package com.example.deadreckoningapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper {

    private Context context;
    public static final String DATABASE_NAME = "";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "building_maps";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "building_name";
    public static final String COLUMN_MAP = "building_map";
    public static final String COLUMN_LOCATION = "building_location";

    public DatabaseHelper(Context context) {
        this.context = context;
    }
}

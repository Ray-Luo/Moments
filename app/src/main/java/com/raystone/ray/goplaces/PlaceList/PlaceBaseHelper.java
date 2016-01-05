package com.raystone.ray.goplaces.PlaceList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ray on 12/3/2015. Perform the database creation
 */
public class PlaceBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "placeBase.db";

    public PlaceBaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + PlaceDbSchema.PlaceTable.TABLE_NAME + "(" +
                " _id integer primary key autoincrement, " +
                PlaceDbSchema.PlaceTable.Cols.UUID + ", " +
                PlaceDbSchema.PlaceTable.Cols.USER_NAME + ", " +
                PlaceDbSchema.PlaceTable.Cols.DESCRIPTION + ", " +
                PlaceDbSchema.PlaceTable.Cols.PICSDIR + ", " +
                PlaceDbSchema.PlaceTable.Cols.TIME + ", " +
                PlaceDbSchema.PlaceTable.Cols.LATITUDE + "," +
                PlaceDbSchema.PlaceTable.Cols.LONGITUDE + "," +
                PlaceDbSchema.PlaceTable.Cols.ADDRESS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {}

}

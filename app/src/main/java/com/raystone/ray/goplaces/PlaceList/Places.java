package com.raystone.ray.goplaces.PlaceList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raystone.ray.goplaces.Helper.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ray on 12/3/2015.
 */
public class Places {

    private static Places sPlaces;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static Places get(Context context)
    {
        if(sPlaces == null)
            sPlaces = new Places(context);
        return sPlaces;
    }

    private Places(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new PlaceBaseHelper(mContext).getWritableDatabase();
    }

    public void addPlace(Place place)
    {
        ContentValues values = getContentValues(place);
        mDatabase.insert(PlaceDbSchema.PlaceTable.TABLE_NAME, null, values);
    }

    public List<Place> getPlaces()
    {
        List<Place> places = new ArrayList<>();
        PlaceCursorWrapper cursor = queryPlaces(null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            places.add(cursor.getPlace());
            cursor.moveToNext();
        }
        return places;
    }

    public Place getPlace(UUID id)
    {
        PlaceCursorWrapper cursor = queryPlaces(PlaceDbSchema.PlaceTable.Cols.UUID +
        " = ? ", new String[]{id.toString()});
        try
        {
            if(cursor.getCount() == 0)
            {return null;}
            cursor.moveToFirst();
            return cursor.getPlace();
        }finally {
            cursor.close();
        }
    }

    public void updatePlace(Place place)
    {
        String uuid = place.getID().toString();
        ContentValues values = getContentValues(place);
        mDatabase.update(PlaceDbSchema.PlaceTable.TABLE_NAME,values, PlaceDbSchema.PlaceTable.Cols.UUID + " =  ?", new String[] {uuid});
    }

    private static ContentValues getContentValues(Place place)
    {
        ContentValues values = new ContentValues();
        values.put(PlaceDbSchema.PlaceTable.Cols.UUID, place.getID().toString());
        values.put(PlaceDbSchema.PlaceTable.Cols.USER_NAME,place.getUserName().toString());
        values.put(PlaceDbSchema.PlaceTable.Cols.ADDRESS,place.getAddress().toString());
        if(place.getPicDirs() != null)
            {values.put(PlaceDbSchema.PlaceTable.Cols.PICSDIR,place.getPicDirs().toString());}
        values.put(PlaceDbSchema.PlaceTable.Cols.DESCRIPTION,place.getDescription().toString());
        values.put(PlaceDbSchema.PlaceTable.Cols.TIME,place.getPlaceTime().toString());
        values.put(PlaceDbSchema.PlaceTable.Cols.LATITUDE,place.getPlaceLatitude());
        values.put(PlaceDbSchema.PlaceTable.Cols.LONGITUDE,place.getPlaceLongitude());
        return values;
    }

    private PlaceCursorWrapper queryPlaces(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(PlaceDbSchema.PlaceTable.TABLE_NAME,null,
                whereClause,whereArgs,null,null,null);
        return new PlaceCursorWrapper(cursor);
    }

}

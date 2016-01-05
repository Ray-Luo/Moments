package com.raystone.ray.goplaces.PlaceList;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.raystone.ray.goplaces.Helper.Place;

import java.util.UUID;

/**
 * Created by Ray on 12/3/2015.
 */
public class PlaceCursorWrapper extends CursorWrapper {

    public PlaceCursorWrapper(Cursor cursor)
    {super(cursor);}

    public Place getPlace()
    {
        String uuid = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.UUID));
        //String user_name = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.USER_NAME));
        String description = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.DESCRIPTION));
        String address = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.ADDRESS));
        String pics_dir = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.PICSDIR));
        String time = getString(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.TIME));
        Double latitude = getDouble(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.LATITUDE));
        Double longitude = getDouble(getColumnIndex(PlaceDbSchema.PlaceTable.Cols.LONGITUDE));

        Place place = new Place(UUID.fromString(uuid));
        place.setAddress(address);
        place.setPlaceTime(time);
        place.setDescription(description);
        place.setPicDirs(pics_dir);
        //place.setUserName(user_name);
        place.setPlaceLatitude(latitude);
        place.setPlaceLongitude(longitude);

        return place;
    }
}

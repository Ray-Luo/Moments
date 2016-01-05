package com.raystone.ray.goplaces.PlaceList;

/**
 * Created by Ray on 12/3/2015.
 */
public class PlaceDbSchema {

    public static final class PlaceTable
    {
        public static final String TABLE_NAME = "places";

        public static final class Cols
        {
            public static final String UUID = "uuid";
            public static final String USER_NAME = "username";
            public static final String DESCRIPTION = "description";
            public static final String PICSDIR = "picsdir";
            public static final String ADDRESS = "address";
            public static final String TIME = "time";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
        }
    }
}

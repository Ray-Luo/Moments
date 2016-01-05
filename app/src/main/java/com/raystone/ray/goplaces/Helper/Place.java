package com.raystone.ray.goplaces.Helper;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.UUID;

/**
 * Created by Ray on 11/21/2015.
 */
public class Place {

    private UUID mID;
    private String mTitle;
    private String mDescription;
    private String mPicDirs;
    public static String mUserName = "";
    private String mPlaceTime;
    private String mAddress;
    public static final String SPLITOR = "~";
    private double mPlaceLatitude;
    private double mPlaceLongitude;
    public static Uri mUserProfileUri;
    public static Bitmap mUserProfilePic;


    public Place()
    {
        this(UUID.randomUUID());
    }

    public Place(UUID uuid)
    {
        mID = uuid;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getPlaceTime() {
        return mPlaceTime;
    }

    public void setPlaceTime(String mPlaceTime) {
        this.mPlaceTime = mPlaceTime;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setPicDirs(String dirs)
    {
        this.mPicDirs = dirs;
    }

    public String getPicDirs() {
        return mPicDirs;
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }



    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }


    public double getPlaceLatitude() {
        return mPlaceLatitude;
    }

    public void setPlaceLatitude(double mPlaceLatitude) {
        this.mPlaceLatitude = mPlaceLatitude;
    }


    public double getPlaceLongitude() {
        return mPlaceLongitude;
    }

    public void setPlaceLongitude(double mPlaceLongitude) {
        this.mPlaceLongitude = mPlaceLongitude;
    }
}

package com.raystone.ray.goplaces_v1;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ray on 11/21/2015.
 */
public class Place {

    private UUID mID;
    private String mTitle;
    private Date mDate;
    private String mDescription;

    public Place()
    {
        this(UUID.randomUUID());
    }

    public Place(UUID uuid)
    {
        mID = uuid;
        mDate = new Date();
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

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }
}

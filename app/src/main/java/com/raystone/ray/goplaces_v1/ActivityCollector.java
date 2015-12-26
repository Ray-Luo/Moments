package com.raystone.ray.goplaces_v1;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 12/11/2015.
 * This class is intended to end all the activities and their attached fragments. Every new created activity will be added into
 * the "activities" list.
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity)
    {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity)
    {
        activities.remove(activity);
    }

    public static void finishAll()
    {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}

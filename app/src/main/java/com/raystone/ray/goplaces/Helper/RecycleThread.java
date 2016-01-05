package com.raystone.ray.goplaces.Helper;

/**
 * Created by Ray on 12/27/2015.
 */
public abstract class RecycleThread extends Thread {
    public volatile boolean exit = true;
    public void run(){};
}

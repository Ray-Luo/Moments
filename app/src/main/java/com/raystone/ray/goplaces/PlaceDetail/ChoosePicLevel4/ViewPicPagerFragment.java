package com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel4;

import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.raystone.ray.goplaces.Helper.FileUtils;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 11/25/2015.
 */
public class ViewPicPagerFragment extends android.app.Fragment {

    public static ViewPicPagerFragment newInstance()
    {return new ViewPicPagerFragment();}

    private View mView;
    private ArrayList<View> mListViews = null;                  //  A list of view which will be put into the adapter
    private ViewPager mPager;
    private ViewPicPagerAdapter mViewPicPagerAdapter;          //  The adapter for the ViewPager
    private int mCurrentPosition;                              //  The current position of the ViewPager

    // contemporary place for storing "MyBitMap"
    private List<Bitmap> bmp = new ArrayList<Bitmap>();
    private List<String> dir = new ArrayList<String>();
    private List<String> del = new ArrayList<String>();
    private int max;
    //  ViewPager's background
    private RelativeLayout photo_relativeLayout;





    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mView = null;
        mListViews = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        //  If viewing picture in "list" mode, it is not allowed to delete pictures
        if(MoveAmongFragments.fromDetailToViewPics)
        {
            mView = inflater.inflate(R.layout.level4,container,false);
            photo_relativeLayout = (RelativeLayout) mView.findViewById(R.id.photo_relativeLayout);
            photo_relativeLayout.setBackgroundColor(0x70000000);
        }else
        {
            mView = inflater.inflate(R.layout.level4_list_detail, container, false);
            MyBitMap.bmp = getPics(MoveAmongFragments.editPlace);
        }


        //  retrieve images from "MyBitMap" and put them in a temporary place
        for (int i = 0; i < MyBitMap.bmp.size(); i++) {
            bmp.add(MyBitMap.bmp.get(i));
        }
        for (int i = 0; i < MyBitMap.dir.size(); i++) {
            dir.add(MyBitMap.dir.get(i));
        }
        max = MyBitMap.max;

        //  define the "delete" button and "confirm" button
        if(MoveAmongFragments.fromDetailToViewPics) {
            Button photo_bt_del = (Button) mView.findViewById(R.id.photo_bt_del);
            Button photo_bt_enter = (Button) mView.findViewById(R.id.photo_bt_enter);

            /*
            when clicking on "delete" button. All the views in the ViewPager will be removed and the view list has changed. Update the view list, and put the new view list in the adapter and force the adapter to fresh its current view. The purpose of having a temporary place storing "MyBitMap" is that clicking the "delete" button and then pressing the "back" won't change the contents of "MyBitMap" and the adapter. It will only change after clicking the "confirm" button or keep clicking the "delete" button.
             */
            photo_bt_del.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (mListViews.size() == 1) {
                        MyBitMap.bmp.clear();
                        MyBitMap.dir.clear();
                        MyBitMap.max = 0;
                        dir.clear();
                        if(MoveAmongFragments.editPlaceMode)
                        {FileUtils.deleteDir(MoveAmongFragments.editPlace.getID().toString());}
                        returnToDetail();
                        //   What to do
                    } else {
                        String newStr = dir.get(mCurrentPosition).substring(dir.get(mCurrentPosition).lastIndexOf("/") + 1, dir.get(mCurrentPosition).lastIndexOf("."));
                        bmp.remove(mCurrentPosition);
                        dir.remove(mCurrentPosition);
                        del.add(newStr);
                        max--;
                        mPager.removeAllViews();
                        mListViews.remove(mCurrentPosition);
                        mViewPicPagerAdapter.setListViews(mListViews);
                        mViewPicPagerAdapter.notifyDataSetChanged();
                    }
                }
            });

            photo_bt_enter.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    MyBitMap.bmp = bmp;
                    MyBitMap.dir = dir;
                    MyBitMap.max = max;
                    if (MoveAmongFragments.editPlaceMode) {
                        for (int i = 0; i < del.size(); i++) {
                            FileUtils.delFile(MoveAmongFragments.editPlace.getID().toString(),
                                    del.get(i) + ".JPEG");
                        }
                    }
                    returnToDetail();
                    //getActivity().finish();
                }
            });
        }

        mPager = (ViewPager) mView.findViewById(R.id.viewpager);
        mPager.addOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < bmp.size(); i++) {
            //  initializing the list of view
            initListViews(bmp.get(i));
        }

        mViewPicPagerAdapter = new ViewPicPagerAdapter(mListViews);
        mPager.setAdapter(mViewPicPagerAdapter);
        //Intent intent = getActivity().getIntent();
        //  set the current view to be the picture one clicked
        int id = getArguments().getInt("ID");
                //intent.getIntExtra("ID", 0);
        mPager.setCurrentItem(id);

        return mView;
    }

    private void returnToDetail()
    {
        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("VIEWPICS");
        trans.remove(fragment);
        trans.commit();
        fm.popBackStack();
        if(MoveAmongFragments.fromDetailToViewPics)
            MoveAmongFragments.currentFragment = "PLACEDETAIL";
        else
            MoveAmongFragments.currentFragment = "PLACELISTDETAIL";
    }

    private void initListViews(Bitmap bm) {
        if (mListViews == null)
            mListViews = new ArrayList<View>();
        ImageView img = new ImageView(getActivity());
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //  put ImageView in the list of view
        mListViews.add(img);
    }


    public static List<Bitmap> getPics(Place place)
    {
        List<Bitmap> list = new ArrayList<>();
        if(place.getPicDirs() != null)
        {
            String[] picDir = place.getPicDirs().split(Place.SPLITOR);
            for(int i = 0; i < picDir.length; i++)
            {
                try
                {
                    list.add(MyBitMap.zipImage(picDir[i]));
                }catch (IOException e)
                {e.printStackTrace();}
            }
        }
        return list;
    }


    //  use this listener to retrieve the ViewPager's current position as so to modify the image list "bmp"
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            mCurrentPosition = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };



}

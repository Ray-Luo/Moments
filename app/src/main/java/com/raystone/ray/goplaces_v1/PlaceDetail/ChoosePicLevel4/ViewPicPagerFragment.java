package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.raystone.ray.goplaces_v1.FileUtils;
import com.raystone.ray.goplaces_v1.MoveAmongFragments;
import com.raystone.ray.goplaces_v1.MyBitMap;
import com.raystone.ray.goplaces_v1.Place;
import com.raystone.ray.goplaces_v1.PlaceList.PlaceListActivity;
import com.raystone.ray.goplaces_v1.PlaceList.PlaceListFragment;
import com.raystone.ray.goplaces_v1.PlaceList.Places;
import com.raystone.ray.goplaces_v1.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ray on 11/25/2015.
 */
public class ViewPicPagerFragment extends Fragment {

    public static ViewPicPagerFragment newInstance()
    {return new ViewPicPagerFragment();}

    private ArrayList<View> listViews = null;
    private ViewPager pager;
    private ViewPicPagerAdapter adapter;
    private int count;

    public List<Bitmap> bmp = new ArrayList<Bitmap>();
    public List<String> dir = new ArrayList<String>();
    public static List<String> del = new ArrayList<String>();
    public int max;
    public boolean fromListDetail = false;
    RelativeLayout photo_relativeLayout;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View view;
        try{
        fromListDetail = getActivity().getIntent().getBooleanExtra("fromDetail",false);}
        catch (java.lang.NullPointerException e)
        {e.printStackTrace();}
        if(!fromListDetail)
        {
            view = inflater.inflate(R.layout.level4,container,false);
            photo_relativeLayout = (RelativeLayout) view.findViewById(R.id.photo_relativeLayout);
            photo_relativeLayout.setBackgroundColor(0x70000000);
        }else
        {
            view = inflater.inflate(R.layout.level4_list_detail, container, false);
            //String a = getActivity().getIntent().getStringExtra("UUIDFromListDetail");
            MyBitMap.bmp = getPics(MoveAmongFragments.viewPicPlace);
        }



        for (int i = 0; i < MyBitMap.bmp.size(); i++) {
            bmp.add(MyBitMap.bmp.get(i));
        }
        for (int i = 0; i < MyBitMap.dir.size(); i++) {
            dir.add(MyBitMap.dir.get(i));
        }
        max = MyBitMap.max;

        if(!fromListDetail) {
            Button photo_bt_del = (Button) view.findViewById(R.id.photo_bt_del);
            Button photo_bt_enter = (Button) view.findViewById(R.id.photo_bt_enter);

            photo_bt_del.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (listViews.size() == 1) {
                        MyBitMap.bmp.clear();
                        MyBitMap.dir.clear();
                        MyBitMap.max = 0;
                        dir.clear();
                        FileUtils.deleteDir();
                        getActivity().finish();
                    } else {
                        String newStr = dir.get(count).substring(dir.get(count).lastIndexOf("/") + 1, dir.get(count).lastIndexOf("."));
                        bmp.remove(count);
                        dir.remove(count);
                        del.add(newStr);
                        max--;
                        pager.removeAllViews();
                        listViews.remove(count);
                        adapter.setListViews(listViews);
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            photo_bt_enter.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    MyBitMap.bmp = bmp;
                    MyBitMap.dir = dir;
                    MyBitMap.max = max;
                    for (int i = 0; i < del.size(); i++) {
                            FileUtils.delFile(del.get(i) + ".JPEG");
                    }
                    getActivity().finish();
                }
            });
        }

        pager = (ViewPager) view.findViewById(R.id.viewpager);
        pager.addOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < bmp.size(); i++) {
            initListViews(bmp.get(i));//
        }

        adapter = new ViewPicPagerAdapter(listViews);// 构造adapter
        pager.setAdapter(adapter);// 设置适配器
        Intent intent = getActivity().getIntent();
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);

        return view;
    }

    private void initListViews(Bitmap bm) {
        if (listViews == null)
            listViews = new ArrayList<View>();
        ImageView img = new ImageView(getActivity());// 构造textView对象
        img.setBackgroundColor(0xff000000);
        img.setImageBitmap(bm);
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        listViews.add(img);// 添加view
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


    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {// 页面选择响应函数
            count = arg0;
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。

        }

        public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

        }
    };



}

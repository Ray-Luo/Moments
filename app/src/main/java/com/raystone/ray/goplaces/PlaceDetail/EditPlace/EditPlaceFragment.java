package com.raystone.ray.goplaces.PlaceDetail.EditPlace;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.raystone.ray.goplaces.Helper.FileUtils;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.Helper.MyCurrentLocationService;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.Helper.RecycleThread;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel1.ImageBucketLevel1Fragment;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel4.ViewPicPagerFragment;
import com.raystone.ray.goplaces.PlaceList.PlaceListFragment;
import com.raystone.ray.goplaces.PlaceList.Places;
import com.raystone.ray.goplaces.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Ray on 11/23/2015.
 */
public class EditPlaceFragment extends android.app.Fragment{

    private GridView mPicGridView;
    private MyPicGridAdapter myPicGridAdapter;
    private TextView writeSomething;
    private EditText mDescrip;
    private Place mPlace;
    private ImageView mPlaceLocation;
    private ShareDialog shareDialog;
    private FloatingActionButton shareToFacebook;
    private Intent locationService;
    private View mView;
    private LocationReceiver mLocationReceiver;
    private boolean isLocationReceiverRegistered = false;
    private Double mLatitude;
    private Double mLongitude;
    private String mAddress = "testGeocoding";
    private RecycleThread mRecycleLoadPicThread;
    private RecycleThread mRecycleGeocodingThread;


    public static EditPlaceFragment newInstance()
    {
        return new EditPlaceFragment();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loadPic();
        mView = inflater.inflate(R.layout.level3_whole,container,false);
        mPicGridView = (GridView) mView.findViewById(R.id.noScrollgridview);
        mPicGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        myPicGridAdapter = new MyPicGridAdapter(getActivity());

        mPicGridView.setAdapter(myPicGridAdapter);
        mPicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == MyBitMap.bmp.size()) {
                    new MyPopupWindow(getActivity(), mPicGridView);
                } else {
                    viewPics(arg2);
                    MoveAmongFragments.fromDetailToViewPics = true;
                }
            }
        });


        mPlaceLocation = (ImageView)mView.findViewById(R.id.place_location);
        mPlaceLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocationReceiverRegistered = true;
                locationService = new Intent(getActivity(), MyCurrentLocationService.class);
                getActivity().startService(locationService);
                IntentFilter filter = new IntentFilter("com.raystone.ray.goplaces_v1" + "" +
                        ".LOCATION_SERVICE");
                mLocationReceiver = new LocationReceiver();
                getActivity().registerReceiver(mLocationReceiver, filter);
            }
        });


        shareToFacebook = (FloatingActionButton)mView.findViewById(R.id.fab);
        shareToFacebook.setRippleColor(Color.BLUE);
        shareToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy");
                    String date=sdf.format(new java.util.Date());


                    List<String> list = new ArrayList<String>();
                    if(mPlace == null){
                        mPlace = new Place();}
                    mPlace.setUserName(Place.mUserName);
                    mPlace.setAddress(mAddress);
                    mPlace.setPlaceTime(date);
                    mPlace.setDescription(mDescrip.getText().toString());
                    if(mLatitude != null && mLongitude != null)
                    {
                        mPlace.setPlaceLatitude(mLatitude);
                        mPlace.setPlaceLongitude(mLongitude);
                    }

                    for (int i = 0; i < MyBitMap.dir.size(); i++) {
                        String Str = MyBitMap.dir.get(i).substring(MyBitMap.dir.get(i)
                                .lastIndexOf("/") + 1, MyBitMap.dir.get(i).lastIndexOf("."));
                        list.add(FileUtils.SDPATH + mPlace.getID().toString() + "/" + Str + "" +
                                ".JPEG");
                        FileUtils.saveBitmap(MyBitMap.bmp.get(i), mPlace.getID().toString(), Str);
                    }
                    mPlace.setPicDirs(listToString(list));

                    List<Bitmap> images;
                    images = getPics(mPlace);
                    List<SharePhoto> photos = new ArrayList<>();
                    for (Bitmap bitmap : images) {
                        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
                        photos.add(photo);
                    }
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhotos(photos)
                            .build();
                    shareDialog = new ShareDialog(getActivity());
                    shareDialog.show(content);
                }
            }
        });


        mDescrip = (EditText)mView.findViewById(R.id.descrip);
        if(MoveAmongFragments.editPlaceMode) //MoveAmongFragments.listDetailToPlaceDetail
        {
            mDescrip.setText(MoveAmongFragments.editPlace.getDescription().toString());
        }
        writeSomething = (TextView) mView.findViewById(R.id.activity_selectimg_send);
        writeSomething.setText("Save");
        writeSomething.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy");
                String date=sdf.format(new java.util.Date());

                MoveAmongFragments.editPlace.setUserName(Place.mUserName);
                MoveAmongFragments.editPlace.setAddress(mAddress);
                MoveAmongFragments.editPlace.setPlaceTime(date);
                if(mLatitude != null && mLongitude != null)
                {
                    MoveAmongFragments.editPlace.setPlaceLatitude(mLatitude);
                    MoveAmongFragments.editPlace.setPlaceLongitude(mLongitude);
                }

                List<String> list = new ArrayList<String>();
                MoveAmongFragments.editPlace.setDescription(mDescrip.getText().toString());
                for (int i = 0; i < MyBitMap.dir.size(); i++) {
                    String Str = MyBitMap.dir.get(i).substring(
                            MyBitMap.dir.get(i).lastIndexOf("/") + 1,
                            MyBitMap.dir.get(i).lastIndexOf("."));
                    list.add(FileUtils.SDPATH+  MoveAmongFragments.editPlace.getID().toString() + "/"+Str+".JPEG");
                    FileUtils.saveBitmap(MyBitMap.bmp.get(i) , MoveAmongFragments.editPlace.getID().toString() , Str);
                }
                MoveAmongFragments.editPlace.setPicDirs(listToString(list));
                Places.get(getActivity()).updatePlace(MoveAmongFragments.editPlace);
                returnToList();
                MoveAmongFragments.editPlaceMode = false;
            }
        });

        return mView;
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

    public String listToString(List<String> str)
    {
        String string = "";
        for(String a : str)
        {string = string + a + Place.SPLITOR;}
        return string;
    }



    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(isLocationReceiverRegistered)
        {
            getActivity().unregisterReceiver(mLocationReceiver);
            mLocationReceiver = null;
            locationService = null;
            isLocationReceiverRegistered = false;
        }
        if(mRecycleLoadPicThread != null)
            mRecycleLoadPicThread.exit = false;
        if(mRecycleGeocodingThread != null)
            mRecycleGeocodingThread.exit = false;
        mPicGridView = null;
        mPlaceLocation = null;
        mDescrip = null;
        mView = null;
    }


    @SuppressLint("HandleLeak")
    public class MyPicGridAdapter extends BaseAdapter
    {
        private Context mContext;
        private int currentPosition = -1;

        public MyPicGridAdapter(Context context)
        {
            mContext = context;
        }

        public int getCount()
        {
            return MyBitMap.bmp.size() + 1;
        }

        @Override
        public Objects getItem(int position)
        {return null;}

        @Override
        public long getItemId(int position)
        {return 0;}

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.single_item_grid,parent,false);
                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.item_grid_image);
                convertView.setTag(holder);
            }
            else
            {holder = (ViewHolder)convertView.getTag();}

            if(position == MyBitMap.bmp.size())
            {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic));
                if(position == 6)
                {holder.image.setVisibility(View.GONE);}
            }
            else
            {holder.image.setImageBitmap(MyBitMap.bmp.get(position));}
            return convertView;
        }
    }

    public class ViewHolder
    {public ImageView image;}

    public void loadPic()
    {
        if(MoveAmongFragments.STATE.equals("LISTFROMPLACE"))
        {
            mPlace = MoveAmongFragments.editPlace;
            MyBitMap.dir = new ArrayList<>();
            MyBitMap.bmp = PlaceListFragment.getPics(mPlace);
            if(!mPlace.getPicDirs().equals("")){
            String[] picDir = mPlace.getPicDirs().split(Place.SPLITOR);
            for(int i = 0; i < picDir.length; i++)
            {
                MyBitMap.dir.add(picDir[i]);
            }}
            MyBitMap.max = MyBitMap.dir.size();
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
            MoveAmongFragments.STATE = "OTHERSFROMPLACE";
        }else
        {
            mRecycleLoadPicThread = new RecycleThread() {
                @Override
                public void run() {
                    super.run();
                    while (mRecycleLoadPicThread.exit) {
                        if (MyBitMap.max == MyBitMap.dir.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            String path;
                            try {
                                path = MyBitMap.dir.get(MyBitMap.max);
                                Bitmap bitmap = MyBitMap.zipImage(path);
                                MyBitMap.bmp.add(bitmap);
                                MyBitMap.max = MyBitMap.max + 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            };
            mRecycleLoadPicThread.start();
        }

        }



    android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    myPicGridAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public class MyPopupWindow extends PopupWindow
    {
        public MyPopupWindow(Context context, View contentView)
        {
            View view = View.inflate(context,R.layout.my_popupwindow,null);
            view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
            LinearLayout popup = (LinearLayout)view.findViewById(R.id.popup);
            popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.push_bottom_in));
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
            update();

            Button photoButton = (Button)view.findViewById(R.id.popupwindow_photo);
            Button cameraButton = (Button)view.findViewById(R.id.popupwindow_camera);
            Button cancelButton = (Button)view.findViewById(R.id.popupwindow_cancel);
            photoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    addPicByGallery();
                }
            });

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    photo();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private void addPicByGallery()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("LEVEL1");
        if(fragment == null) {
            fragment = ImageBucketLevel1Fragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"LEVEL1");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "LEVEL1";
    }

    private void viewPics(int position)
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("VIEWPICS");
        if(fragment == null) {
            fragment = ViewPicPagerFragment.newInstance();
        }
        Bundle bundle = new Bundle();
        bundle.putInt("ID", position);
        fragment.setArguments(bundle);
        trans.replace(R.id.login_fragment_container, fragment,"VIEWPICS");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "VIEWPICS";
    }

    private void returnToList()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("PLACELISTDETAIL");
        if(fragment == null) {
            fragment = PlaceListFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"PLACELISTDETAIL");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "PLACELISTDETAIL";
    }


    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/GoPlaces/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        path = file.getPath();
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }


    public class LocationReceiver extends BroadcastReceiver
    {
        String addressResult = null;
        @Override
        public void onReceive(Context context,Intent intent)
        {
            mLatitude = intent.getDoubleExtra("Latitude", 0);
            mLongitude = intent.getDoubleExtra("Longitude", 0);
            Toast.makeText(getActivity(), "Location found  " + mLatitude, Toast.LENGTH_SHORT).show();
            abortBroadcast();
            getActivity().stopService(locationService);
            final Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            mRecycleGeocodingThread = new RecycleThread() {
                @Override
                public void run() {
                    super.run();
                    while(mRecycleGeocodingThread.exit)
                    {
                        try {
                            List<Address> list = geocoder.getFromLocation(mLatitude, mLongitude, 1);
                            if(list != null && list.size() > 0)
                            {
                                Address address = list.get(0);
                                addressResult = address.getAddressLine(0) + ", " + address.getLocality();
                                mAddress = addressResult;
                            }
                        }catch (IOException e)
                        {e.printStackTrace();}
                    }
                }
            };
            mRecycleGeocodingThread.start();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (MyBitMap.dir.size() < 6 && resultCode == -1) {
                    MyBitMap.dir.add(path);
                }
                mRecycleLoadPicThread = new RecycleThread() {
                    @Override
                    public void run() {
                        super.run();
                        while (mRecycleLoadPicThread.exit) {
                            if (MyBitMap.max == MyBitMap.dir.size()) {
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                                break;
                            } else {
                                String path;
                                try {
                                    path = MyBitMap.dir.get(MyBitMap.max);
                                    Bitmap bitmap = MyBitMap.zipImage(path);
                                    MyBitMap.bmp.add(bitmap);
                                    MyBitMap.max = MyBitMap.max + 1;
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                };mRecycleLoadPicThread.start();
                break;
        }
    }
}

package com.raystone.ray.goplaces_v1.PlaceDetail.EditPlace;

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
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.raystone.ray.goplaces_v1.FileUtils;
import com.raystone.ray.goplaces_v1.MoveAmongFragments;
import com.raystone.ray.goplaces_v1.MyBitMap;
import com.raystone.ray.goplaces_v1.MyCurrentLocationService;
import com.raystone.ray.goplaces_v1.Place;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel1.ImageBucketLevel1Activity;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4.ViewPicActivity;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4.ViewPicPagerFragment;
import com.raystone.ray.goplaces_v1.PlaceList.PlaceListActivity;
import com.raystone.ray.goplaces_v1.PlaceList.PlaceListFragment;
import com.raystone.ray.goplaces_v1.PlaceList.Places;
import com.raystone.ray.goplaces_v1.R;

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
public class EditPlaceFragment extends Fragment{

    private GridView mPicGridView;
    private MyPicGridAdapter myPicGridAdapter;
    private TextView writeSomething;
    private EditText mDescrip;
    private Place mPlace;
    private ImageView mPlaceLocation;
    private ShareDialog shareDialog;
    private FloatingActionButton shareToFacebook;
    private Intent locationService;
    private LocationReceiver mLocationReceiver;
    private boolean isLocationReceiverRegistered = false;
    private Double mLatitude;
    private Double mLongitude;
    private String mAddress = "testGeocoding";


    public static EditPlaceFragment newInstance()
    {
        return new EditPlaceFragment();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.level3_whole,container,false);
        mPicGridView = (GridView) view.findViewById(R.id.noScrollgridview);
        mPicGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        myPicGridAdapter = new MyPicGridAdapter(getActivity());

        mPicGridView.setAdapter(myPicGridAdapter);
        mPicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == MyBitMap.bmp.size()) {
                    new MyPopupWindow(getActivity(), mPicGridView);
                } else {
                    Intent intent = new Intent(getActivity(), ViewPicActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });


        mPlaceLocation = (ImageView)view.findViewById(R.id.place_location);
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


        shareToFacebook = (FloatingActionButton)view.findViewById(R.id.fab);
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


        mDescrip = (EditText)view.findViewById(R.id.descrip);
        if(MoveAmongFragments.listDetailToPlaceDetail)
        {
            mDescrip.setText(MoveAmongFragments.listToDetailPlace.getDescription().toString());
        }
        writeSomething = (TextView) view.findViewById(R.id.activity_selectimg_send);
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
                MoveAmongFragments.listDetailToPlaceDetail = false;
                MoveAmongFragments.pickToDetail = false;


                Intent intent = new Intent(getActivity(), PlaceListActivity.class);
                startActivity(intent);
                onDetach();
            }
        });

        return view;
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
    public void onAttach(Context context)
    {
        super.onAttach(getContext());
        loadPic();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        if(isLocationReceiverRegistered)
        {
            getActivity().unregisterReceiver(mLocationReceiver);
        }
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
                if(position == 8)
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
            MyBitMap.bmp = PlaceListFragment.getPics(Places.get(getActivity()).getPlace(mPlace.getID()));
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
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
            }).start();
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
                    Intent intent = new Intent(getActivity(), ImageBucketLevel1Activity.class);
                    startActivity(intent);
                    dismiss();
                    onDetach();
                }
            });

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo();
                    dismiss();
                    onDetach();
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

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
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
            final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<Address> list = geocoder.getFromLocation(39, -96, 1);
                        if(list != null && list.size() > 0)
                        {
                            Address address = list.get(0);
                            addressResult = address.getAddressLine(0) + ", " + address.getLocality();
                            mAddress = addressResult;
                        }
                    }catch (IOException e)
                    {e.printStackTrace();}
                }
            }).start();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (MyBitMap.dir.size() < 8 && resultCode == -1) {
                    MyBitMap.dir.add(path);
                }
                break;
        }
    }
}

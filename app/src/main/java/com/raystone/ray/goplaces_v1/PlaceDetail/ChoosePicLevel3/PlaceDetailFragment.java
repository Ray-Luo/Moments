package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel3;

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
import com.raystone.ray.goplaces_v1.PlaceList.PlaceListActivity;
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
public class PlaceDetailFragment extends Fragment{

    private GridView mPicGridView;                                         //  GridView showing pictures
    private MyPicGridAdapter myPicGridAdapter;                             //  GridView's adapter
    private TextView mAddButton;                                           //   Button for adding a new place to the database
    private EditText mDescrip;                                             //   This is where one can input description for the new place
    private ShareDialog mShareDialog;                                      //  use facebook's ShareDialog to share moments on facebook
    private FloatingActionButton shareToFacebook;                          //  press this button,one can share moments on facebook
    public static Place newPlace;                                           //  temporary place for storing some contents so that one does not lose contents which they have input already when jumping back to this fragment
    private ImageView mPlaceLocation;                                      //  press this will location the current locaiton
    private Intent locationService;                                        //  Intent used to start the location service
    private LocationReceiver mLocationReceiver;                            //  the location receiver
    private boolean isLocationReceiverRegistered = false;                //  tell if the location receiver has registered or not
    private Double mLatitude;                                               //  temporarily store the latitude of the current location
    private Double mLongitude;                                              //  temporarily store the longitude of the current location
    private String mAddress = "testGeocoding";                             //  The address of the place(moment), and it will be changed to the name from reverse geocoding


    public static PlaceDetailFragment newInstance()
    {
        return new PlaceDetailFragment();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.level3_whole,container,false);

        // set things up for the gridview and its adapter
        mPicGridView = (GridView) view.findViewById(R.id.noScrollgridview);
        mPicGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        myPicGridAdapter = new MyPicGridAdapter(getActivity());
        mPicGridView.setAdapter(myPicGridAdapter);
        mPicGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == MyBitMap.bmp.size()) {
                    //  when press the "add more picture" button a popup window will pop up
                    new MyPopupWindow(getActivity(), mPicGridView);
                } else {
                    //  when press the picture itself, it will launch the ViewPicActivity and its fragment to view the picture one pressed
                    Intent intent = new Intent(getActivity(), ViewPicActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        //  This is used to locate the current location
        mPlaceLocation = (ImageView)view.findViewById(R.id.place_location);
        mPlaceLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocationReceiverRegistered = true;
                locationService = new Intent(getActivity(),MyCurrentLocationService.class);
                getActivity().startService(locationService);
                IntentFilter filter = new IntentFilter("com.raystone.ray.goplaces_v1.LOCATION_SERVICE");
                mLocationReceiver = new LocationReceiver();
                getActivity().registerReceiver(mLocationReceiver, filter);
            }
        });


        //  The share-to-facebook button
        shareToFacebook = (FloatingActionButton)view.findViewById(R.id.fab);
        shareToFacebook.setRippleColor(Color.BLUE);
        shareToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShareDialog.canShow(SharePhotoContent.class)) {

                    //  after press the share button, it will jump to the facebook-share interface. So in here to save some content so that when coming back, one won't lose the content and have to input again
                    SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy");
                    String date=sdf.format(new java.util.Date());
                    List<String> list = new ArrayList<String>();
                    newPlace.setUserName(Place.mUserName);            //  save user name
                    newPlace.setPlaceTime(date);                      //  save the date
                    newPlace.setDescription(mDescrip.getText().toString());       //  save description content
                    if(mLatitude != null && mLongitude != null)                 //  if location has been located using the location service, save it
                    {
                        newPlace.setPlaceLatitude(mLatitude);
                        newPlace.setPlaceLongitude(mLongitude);
                    }
                    newPlace.setAddress(mAddress);                                //  save the reverse geocoded address

                    for (int i = 0; i < MyBitMap.dir.size(); i++) {               //  store the url of the pictures one has select and display them when coming back
                        String Str = MyBitMap.dir.get(i).substring(MyBitMap.dir.get(i)
                                .lastIndexOf("/") + 1, MyBitMap.dir.get(i).lastIndexOf("."));
                        list.add(FileUtils.SDPATH + newPlace.getID().toString() + "/" + Str + "" +
                                ".JPEG");
                        FileUtils.saveBitmap(MyBitMap.bmp.get(i), newPlace.getID().toString(), Str);
                    }
                    newPlace.setPicDirs(listToString(list));


                    //  retrieve the images from the saved content for sharing them on facebook
                    List<Bitmap> images;
                    images = getPics(newPlace);      //  this function returns a list of bitmaps from a Place object
                    List<SharePhoto> photos = new ArrayList<>();
                    for (Bitmap bitmap : images) {
                        SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
                        photos.add(photo);
                    }
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhotos(photos)
                            .build();
                    mShareDialog = new ShareDialog(getActivity());
                    mShareDialog.show(content);
                }
            }
        });


        //   where one types description about the place(moment). if there are some description, display them when coming back
        mDescrip = (EditText)view.findViewById(R.id.descrip);
        if(newPlace.getDescription() != null){
            mDescrip.setText(newPlace.getDescription());}

        //  press this will add the place(moment) and its info to the database from which to retrieve info when viewing places in list mode
        mAddButton = (TextView) view.findViewById(R.id.activity_selectimg_send);
        mAddButton.setText("Add");
        mAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  save the place's info and store them in the database
                SimpleDateFormat sdf=new SimpleDateFormat("MM-dd-yyyy");
                String date=sdf.format(new java.util.Date());
                List<String> list = new ArrayList<String>();
                newPlace.setUserName(Place.mUserName);
                        newPlace.setPlaceTime(date);
                        newPlace.setDescription(mDescrip.getText().toString());
                        if(mLatitude != null && mLongitude != null)
                        {
                            newPlace.setPlaceLatitude(mLatitude);
                            newPlace.setPlaceLongitude(mLongitude);
                        }
                        newPlace.setAddress(mAddress);


                        for (int i = 0; i < MyBitMap.dir.size(); i++) {
                            String Str = MyBitMap.dir.get(i).substring(MyBitMap.dir.get(i).lastIndexOf("/") + 1, MyBitMap.dir.get(i).lastIndexOf("."));

                            list.add(FileUtils.SDPATH + newPlace.getID().toString() + "/" + Str +
                                    ".JPEG");
                            FileUtils.saveBitmap(MyBitMap.bmp.get(i), newPlace.getID().toString(), Str);
                        }
                        newPlace.setPicDirs(listToString(list));
                        Places.get(getActivity()).addPlace(newPlace);    //  insert a new entry in the database


                Intent intent = new Intent(getActivity(), PlaceListActivity.class);       //  after add the new place in the database, view it in the list mode
                startActivity(intent);
                onDetach();      //  when the adding-new-place process finishes, set the temporary newPlace to null and unregister the location receiver
            }
        });
        return view;
    }

    //  The two following functions first resolve the url of the stored picture to get the pictures, and to display them when jumping back to this fragment
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
        if(MoveAmongFragments.isAddPlaceFinishedOrCancelled)
            newPlace = new Place();   //  initialize the newPlace
        loadPic();    //  perform
    }

    //  This saves the description and location info into the temporary newPlace if they exist
    public void saveTempPlace()
    {
        if(!mDescrip.getText().toString().equals(""))
            newPlace.setDescription(mDescrip.getText().toString());
        if(mLatitude != null && mLongitude != null)
        {
            newPlace.setPlaceLatitude(mLatitude);
            newPlace.setPlaceLongitude(mLongitude);
        }
        MoveAmongFragments.isAddPlaceFinishedOrCancelled = false;
    }


    //  when the adding-new-place process finishes, set the temporary newPlace to null and unregister the location receiver
    @Override
    public void onDetach()
    {
        super.onDetach();
        if(isLocationReceiverRegistered) {
            getActivity().unregisterReceiver(mLocationReceiver);}
        MoveAmongFragments.isAddPlaceFinishedOrCancelled = true;
        newPlace = null;
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
            ViewHolder holder;  //  define a viewhold which consists of a Imageview
            if(convertView == null)
            {   //  if the old view is null, populate and store it in the viewhold
                convertView = LayoutInflater.from(mContext).inflate(R.layout.single_item_grid,parent,false);
                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.item_grid_image);
                convertView.setTag(holder);
            }
            else  //    if it is not null,reuse the imageview from hold
            {holder = (ViewHolder)convertView.getTag();}

            if(position == MyBitMap.bmp.size())
            {   //  The last picture of the gridview is the "add more picture button", if exceeds 8, make it GONE,meaning cannot add more pictures
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_addpic));
                if(position == 8)
                {
                    holder.image.setVisibility(View.GONE);
                }
            }
            else
            {
                holder.image.setImageBitmap(MyBitMap.bmp.get(position));
            }
            return convertView;
        }
    }

    public class ViewHolder
    {public ImageView image;}


    //  This function is used to display pictures when jumping back to the fragment. it will get the url of the image one by one, and store the images in a list.
    //  The image retrieval process happens in another thread, and it sends out a message to tell the adapter to change its content when an image is added in the list. Cannot directly change UI in the new thread.
    public void loadPic()
    {
        if(MoveAmongFragments.MAPTOPLACE)
        {
            MyBitMap.bmp = new ArrayList<>();
            MyBitMap.dir = new ArrayList<>();
            MyBitMap.max = 0;
            MoveAmongFragments.MAPTOPLACE = false;
        }
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
                            ////不同之处
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
                    saveTempPlace();
                }
            });

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo();
                    dismiss();
                    saveTempPlace();
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

            //reverse geocoding
            final Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            new Thread(new Runnable() {
                @Override
                public void run() {
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

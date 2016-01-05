package com.raystone.ray.goplaces.PlaceList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel4.ViewPicPagerFragment;
import com.raystone.ray.goplaces.PlaceDetail.EditPlace.EditPlaceFragment;
import com.raystone.ray.goplaces.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ray on 12/2/2015.
 */
public class PlaceListFragment extends android.app.Fragment {

    private View mView;
    private RecyclerView mPlaceRecycleView;
    private PlaceAdapter mPlaceAdapter;
    private Place mPlaceGridView;
    private int numberOfPics;
    private Places allPlaces;
    private List<Place> places;



    public static PlaceListFragment newInstance()
    {
        return new PlaceListFragment();
    }



    private void load()
    {
        MoveAmongFragments.editPlace = null;
        MyBitMap.bmp = new ArrayList<>();
        MyBitMap.dir = new ArrayList<>();
        MyBitMap.max = 0;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        allPlaces = null;
        places = null;
        mPlaceAdapter = null;
        mView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.place_item_list,container,false);
        load();
        mPlaceRecycleView = (RecyclerView) mView.findViewById(R.id.place_recycle_view);
        mPlaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return mView;
    }


    public void updateUI()
    {
        //  Get all the Places and put them in the RecycleView's adapter
        allPlaces = Places.get(getActivity());
        places = allPlaces.getPlaces();

        if (mPlaceAdapter == null)
        {
            mPlaceAdapter = new PlaceAdapter(places);
            mPlaceRecycleView.setAdapter(mPlaceAdapter);
        }else
        {
            mPlaceAdapter.setPlaces(places);
            mPlaceAdapter.notifyDataSetChanged();
        }
    }

    /*
    define a holder for storing every single item in the RecycleView. In a holder, there are a GridView for showing pictures, a TextView for showing description, several TextViews for showing the user name, the date when the place was added, the address of the place, and an ImageView for showing user's profile picture. Also, a Place object representing the current place and the GridView's adapter.
     */
    private class PlaceHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private GridView mPlaceItemPics;
        private PlaceDetailAdapter myPicGridAdapter;
        private TextView mDescription;
        private TextView mUserName;
        private TextView mPlaceLocation;
        private TextView mPlaceTime;
        private ImageView mProfilePic;
        private Place mPlace;

        public PlaceHolder (View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);

            mPlaceItemPics = (GridView)itemView.findViewById(R.id.place_item_pics);
            mPlaceLocation = (TextView)itemView.findViewById(R.id.place_location);
            mPlaceTime = (TextView) itemView.findViewById(R.id.place_time);
            mDescription = (TextView)itemView.findViewById(R.id.place_description);
            mUserName = (TextView)itemView.findViewById(R.id.user_name);
            mProfilePic = (ImageView)itemView.findViewById(R.id.profile_pic);

        }

        //  Bind some attributes
        public void bindPlace(Place place)
        {
            mPlace = place;
            mPlaceLocation.setText(mPlace.getAddress());
            mPlaceTime.setText(mPlace.getPlaceTime());
            mDescription.setText(mPlace.getDescription());
            mUserName.setText(mPlace.getUserName());
            mProfilePic.setImageBitmap(Place.mUserProfilePic);
            mPlaceItemPics.setSelector(new ColorDrawable(Color.TRANSPARENT));
            myPicGridAdapter = new PlaceDetailAdapter(getActivity());

            /*
             This is intend to dynamically adjust the height of the GridView for each item. When there are more than 4 pictures in the GridView, increase the height.
              */
            final float scale = getActivity().getApplicationContext().getResources().getDisplayMetrics().density;
            numberOfPics = getPics(mPlace).size();
            mPlaceItemPics.setAdapter(myPicGridAdapter);
            if(numberOfPics == 0)
            {
                mPlaceItemPics.setVisibility(View.GONE);
            }else{
                mPlaceItemPics.setVisibility(View.VISIBLE);
                if(numberOfPics > 4)
                {
                    mPlaceItemPics.getLayoutParams().height = (int) (142 * scale + 0.5f);
                }
                //  When clicking on the images, one can view larger picture, but it is not allowed to delete them.
                mPlaceItemPics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        MoveAmongFragments.fromDetailToViewPics = false;
                        //  MoveAmongFragments.viewPicPlace will be used by ViewPicPagerFragment
                        MoveAmongFragments.editPlace = mPlace;
                        viewPics(arg2);
                    }
                });
            }
        }

        @Override
        public void onClick(View view)
        {
            MoveAmongFragments.editPlace = mPlace;
            MoveAmongFragments.STATE = "LISTFROMPLACE";
            MoveAmongFragments.editPlaceMode = true;
            listToEdit();
        }

    }

    private void listToEdit()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("EDITPLACE");
        if(fragment == null) {
            fragment = EditPlaceFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"EDITPLACE");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "EDITPLACE";
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

    private class PlaceAdapter extends RecyclerView.Adapter<PlaceHolder>
    {
        private List<Place> mPlaces;

        public PlaceAdapter(List<Place> places)
        {
            mPlaces = places;
        }

        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.place_list_item,parent,false);
            return new PlaceHolder(view);
        }

        @Override
        public void onBindViewHolder(PlaceHolder holder, int position)
        {
            Place place = mPlaces.get(position);
            mPlaceGridView = place;
            holder.bindPlace(place);
        }

        @Override
        public int getItemCount()
        {return mPlaces.size();}

        public void setPlaces(List<Place> places)
        {mPlaces = places;}
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
                    list.add(MyBitMap.zipSmallImage(picDir[i]));
                }catch (IOException e)
                {e.printStackTrace();}
            }
        }
        return list;
    }



    @SuppressLint("HandleLeak")
    public class PlaceDetailAdapter extends BaseAdapter {

        private Context mContext;
        private int currentPosition = -1;
        private List<Bitmap> bitmapList = getPics(mPlaceGridView);


        public PlaceDetailAdapter(Context context)
        {
            mContext = context;
        }

        public int getCount()
        {
            return bitmapList.size() + 1;
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

            if(position == bitmapList.size())
            {
                holder.image.setImageBitmap(null);
                if(position == 8)
                {holder.image.setVisibility(View.GONE);}
            }
            else
            {holder.image.setImageBitmap(bitmapList.get(position));}
            return convertView;
        }

        public class ViewHolder
        {public ImageView image;}

    }

}

package com.raystone.ray.goplaces_v1.PlaceList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.raystone.ray.goplaces_v1.MoveAmongFragments;
import com.raystone.ray.goplaces_v1.MyBitMap;
import com.raystone.ray.goplaces_v1.MyMapActivity;
import com.raystone.ray.goplaces_v1.Place;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4.ViewPicActivity;
import com.raystone.ray.goplaces_v1.PlaceDetail.EditPlace.EditPlaceActivity;
import com.raystone.ray.goplaces_v1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ray on 12/2/2015.
 */
public class PlaceListFragment extends Fragment {

    private RecyclerView mPlaceRecycleView;
    private PlaceAdapter mPlaceAdapter;
    private Place mPlaceGridView;
    private int numberOfPics;



    public static PlaceListFragment newInstance()
    {
        return new PlaceListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.place_item_list,container,false);
        mPlaceRecycleView = (RecyclerView) v.findViewById(R.id.place_recycle_view);
        mPlaceRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent i = new Intent(getActivity(), MyMapActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        return v;
    }


    public void updateUI()
    {
        Places allPlaces = Places.get(getActivity());
        List<Place> places = allPlaces.getPlaces();

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

        public void bindPlace(Place place)
        {
            mPlace = place;


            mPlaceLocation.setText(mPlace.getAddress());
            mPlaceTime.setText(mPlace.getPlaceTime());
            mDescription.setText(mPlace.getDescription());
            mUserName.setText(mPlace.getUserName());

            if(Place.mUserProfileUri != null)
            {
                mProfilePic.setImageBitmap(Place.mUserProfilePic);
            }

            mPlaceItemPics.setSelector(new ColorDrawable(Color.TRANSPARENT));
            myPicGridAdapter = new PlaceDetailAdapter(getActivity());

            final float scale = getContext().getResources().getDisplayMetrics().density;
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
                mPlaceItemPics.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent(getActivity(), ViewPicActivity.class);
                        intent.putExtra("ID", arg2);
                        intent.putExtra("fromDetail",true);
                        intent.putExtra("UUIDFromListDetail",mPlace.getID().toString());
                        MoveAmongFragments.viewPicPlace = mPlace;
                        startActivity(intent);
                        onDetach();
                    }
                });
            }
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(getActivity(), EditPlaceActivity.class);
            MoveAmongFragments.listDetailToPlaceDetail = true;
            MoveAmongFragments.listToDetailPlace = mPlace;
            MoveAmongFragments.editPlace = mPlace;
            intent.putExtra("UUIDFromListDetail", mPlace.getID().toString());
            MoveAmongFragments.STATE = "LISTFROMPLACE";
            startActivity(intent);
        }

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

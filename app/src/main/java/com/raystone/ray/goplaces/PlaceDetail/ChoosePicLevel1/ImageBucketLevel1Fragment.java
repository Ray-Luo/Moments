package com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.raystone.ray.goplaces.Helper.AlbumHelper;
import com.raystone.ray.goplaces.Helper.ImageBucket;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel2.ImageBucketLevel2Fragment;
import com.raystone.ray.goplaces.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 11/24/2015.
 */
public class ImageBucketLevel1Fragment extends android.app.Fragment {

    public static ImageBucketLevel1Fragment newInstance()
    {
        return new ImageBucketLevel1Fragment();
    }

    private View mView;
    public static Bitmap mBitmap;   //  the default image of each folder.
    private List<ImageBucket> mDataList;    //  All the picture folders
    private GridView mGridView;             //  Image GridView showing all the thumbnails of every picture folder
    private ImageBucketLevel1Adapter mImageBucketLevel1Adapter;       //  Adapter for the above GridView
    private AlbumHelper mAlbumHelper;      //   the album help intended to get every picture holder and pictures in them
    private TextView mQuitPicking;    //  press this will exit picking pictures
    private static final String EXTRA_IMAGE_LIST = "imagelist";




    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        AlbumHelper.instance = null;
        mAlbumHelper = null;
        mBitmap = null;
        mDataList = null;
        mGridView = null;
        mImageBucketLevel1Adapter = null;
        mView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        initData();
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.image_bucket_level1,container,false);

        mQuitPicking = (TextView)mView.findViewById(R.id.quit_picking_1);
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mImageBucketLevel1Adapter = new ImageBucketLevel1Adapter(getActivity(), mDataList);
        mGridView.setAdapter(mImageBucketLevel1Adapter);

        mQuitPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMap();
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getActivity(), ImageBucketLevel2Activity.class);
                //  The intent stores a list of info of every picture in the specific folder which has just been clicked. The info includes all the pictures' thumbnail path, source path, an index, and a boolean showing whether this picture has been selected or not.
                level1ToLevel2(position);
                //intent.putExtra(ImageBucketLevel1Fragment.EXTRA_IMAGE_LIST, (Serializable) mDataList.get(position).imageList);

            }

        });

        return mView;
    }


    private void returnToMap()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("MAPFRAGMENT");
        trans.replace(R.id.login_fragment_container, fragment, "MAPFRAGMENT");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "MAPFRAGMENT";
    }

    public void level1ToLevel2(int position)
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("LEVEL2");
        if(fragment == null) {
            fragment = ImageBucketLevel2Fragment.newInstance();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImageBucketLevel1Fragment.EXTRA_IMAGE_LIST, (Serializable)
                mDataList.get(position).imageList);
        fragment.setArguments(bundle);
        trans.replace(R.id.login_fragment_container, fragment,"LEVEL2");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "LEVEL2";
    }

    private void initData() {
        //  get all the picture folders and pictures in them, store them in a list of ImageBucket. Each ImageBucket can be regraded as a image folder in which stores info of every image' info
        mAlbumHelper = AlbumHelper.getHelper();
        mAlbumHelper.init(getActivity().getApplicationContext());
        //  get the ImageBucketList which has a list of ImageBucket(Image holder). On the first level, an ImageBucket stores the number of total pictures in that folder, the name of the folder, and a list of all the pictures' info in that folder. On the second level, the list of folder stores info about every single picture in that folder.
        mDataList = mAlbumHelper.getImagesBucketList(false);
        //  The default image of every picture folder.
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic);
    }





}

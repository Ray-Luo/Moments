package com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.raystone.ray.goplaces.Helper.ImageItem;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ray on 11/24/2015.
 */
public class ImageBucketLevel2Fragment extends android.app.Fragment {


    public static ImageBucketLevel2Fragment newInstance()
    {return new ImageBucketLevel2Fragment();}

    private View mView;
    public static final String EXTRA_IMAGE_LIST = "imagelist";             //  an identifier to receive contents from the ImageBucketLevel1Fragment
    public List<ImageItem> mDataList;                                         //  This will be the list of all the pictures' info received from the ImageBucketLevel1Fragment
    public GridView mGridView;                                                //  GridView for showing images in a picture folder
    public ImageBucketLevel2Adapter mImageBucketLevel2Adapter;              //  The adapter of the GridView
    public TextView mQuitPicking2;                                           //  press this will quit picking images
    public Button mFinishPickingButton;                                     //  press this button will finish picking images

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), "You can only choose 6 pictures at most", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mDataList = null;
        mImageBucketLevel2Adapter = null;
        mHandler = null;
        mView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.image_bucket_level2, container, false);


        //  get the list of all the pictures info from the ImageBucketLevel1Fragment
        mDataList =(List<ImageItem>)getArguments().getSerializable(EXTRA_IMAGE_LIST);
                //(List<ImageItem>) getActivity().getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);

        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mImageBucketLevel2Adapter = new ImageBucketLevel2Adapter(getActivity(), mDataList, mHandler);
        mGridView.setAdapter(mImageBucketLevel2Adapter);

        //  This is intended to dynamically show how many pictures have been selected
        mImageBucketLevel2Adapter.setTextCallback(new ImageBucketLevel2Adapter.TextCallback() {
            public void onListen(int count) {
                mFinishPickingButton.setText("Finish" + "(" + count + ")");
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mImageBucketLevel2Adapter.notifyDataSetChanged();
            }

        });

        mQuitPicking2 = (TextView)mView.findViewById(R.id.quit_picking_2);
        mQuitPicking2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMap();
            }
        });

        /*
        When clicking on the items of the GridView, the source path of the image will be added in the mImageBucketLevel2Adapter.map. When clicking the "finish" button, the element in path list will be added in the  "MyBitMap.dir". So when coming back to pick another set of images, the length of the "MyBitMap.dir" will be used to judge whether the total number of selected pictures exceeds 8.
         */
        mFinishPickingButton = (Button)mView.findViewById(R.id.bt);
        mFinishPickingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                Collection<String> c = mImageBucketLevel2Adapter.map.values();
                Iterator<String> it = c.iterator();
                for (; it.hasNext(); ) {
                    list.add(it.next());
                }

                if (MyBitMap.act_bool) {
                    if (!MoveAmongFragments.editPlaceMode) {
                        level2ToDetail();
                    } else {
                        level2ToEditDetail();
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (MyBitMap.dir.size() < 6) {
                        MyBitMap.dir.add(list.get(i));
                    }
                }
            }

        });
        return mView;
    }

    private void popLevel2()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment level2 = fm.findFragmentByTag("LEVEL2");
        if(level2 != null){
            trans.remove(level2);
            trans.commit();
            fm.popBackStack();
        }
    }

    private void returnToMap()
    {
        popLevel2();
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("MAPFRAGMENT");
        trans.replace(R.id.login_fragment_container, fragment, "MAPFRAGMENT");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "MAPFRAGMENT";
    }

    private void level2ToDetail()
    {
        popLevel2();
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("PLACEDETAIL");
        trans.replace(R.id.login_fragment_container, fragment, "PLACEDETAIL");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "PLACEDETAIL";
    }

    private void level2ToEditDetail()
    {
        popLevel2();
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("EDITPLACE");
        trans.replace(R.id.login_fragment_container, fragment, "EDITPLACE");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "EDITPLACE";
    }










}

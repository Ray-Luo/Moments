package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.raystone.ray.goplaces_v1.AlbumHelper;
import com.raystone.ray.goplaces_v1.ImageBucket;
import com.raystone.ray.goplaces_v1.MyMapActivity;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel2.ImageBucketLevel2Activity;
import com.raystone.ray.goplaces_v1.R;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ray on 11/24/2015.
 */
public class ImageBucketLevel1Fragment extends Fragment {

    public static ImageBucketLevel1Fragment newInstance()
    {
        return new ImageBucketLevel1Fragment();
    }

    public static Bitmap mBitmap;
    List<ImageBucket> mDataList;
    GridView gridView;
    ImageBucketLevel1Adapter adapter;
    AlbumHelper helper;
    public TextView quitPicking;
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        helper = AlbumHelper.getHelper();
        helper.init(getContext());
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.image_bucket_level1,container,false);

        quitPicking = (TextView)view.findViewById(R.id.quit_picking_1);
        gridView = (GridView) view.findViewById(R.id.gridview);
        adapter = new ImageBucketLevel1Adapter(getActivity(), mDataList);
        gridView.setAdapter(adapter);

        quitPicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyMapActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ImageBucketLevel2Activity.class);
                intent.putExtra(ImageBucketLevel1Fragment.EXTRA_IMAGE_LIST, (Serializable)
                        mDataList.get(position).imageList);
                startActivity(intent);
            }

        });

        return view;
    }

    private void initData() {
        mDataList = helper.getImagesBucketList(false);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic);
    }





}

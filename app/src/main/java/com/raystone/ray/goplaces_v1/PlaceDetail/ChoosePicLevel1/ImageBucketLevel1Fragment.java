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

import com.raystone.ray.goplaces_v1.AlbumHelper;
import com.raystone.ray.goplaces_v1.ImageBucket;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel2.ImageBucketLevel2Activity;

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

    public static Bitmap bitmap;
    List<ImageBucket> dataList;
    GridView gridView;
    ImageBucketLevel1Adapter adapter;
    AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.image_bucket_level1,container,false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        adapter = new ImageBucketLevel1Adapter(getActivity(), dataList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long
                    id) {
                /**
                 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
                 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
                 */
                // if(dataList.get(position).isSelected()){
                // dataList.get(position).setSelected(false);
                // }else{
                // dataList.get(position).setSelected(true);
                // }
                /**
                 * 通知适配器，绑定的数据发生了改变，应当刷新视图
                 */
                // adapter.notifyDataSetChanged();
                Intent intent = new Intent(getActivity(), ImageBucketLevel2Activity.class);
                intent.putExtra(ImageBucketLevel1Fragment.EXTRA_IMAGE_LIST, (Serializable) dataList.get(position).imageList);
                startActivity(intent);
            }

        });


        helper = AlbumHelper.getHelper();
        helper.init(getContext());

        initData();
        return view;
    }

    private void initData() {
        dataList = helper.getImagesBucketList(false);
        bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic);
    }





}

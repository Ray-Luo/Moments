package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel2;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.GridView;

import com.raystone.ray.goplaces_v1.AlbumHelper;
import com.raystone.ray.goplaces_v1.ImageItem;

import java.util.List;

/**
 * Created by Ray on 11/24/2015.
 */
public class ImageBucketLevel2Fragment extends Fragment {


    public static ImageBucketLevel2Fragment newInstance()
    {return new ImageBucketLevel2Fragment();}

    public static final String EXTRA_IMAGE_LIST = "imagelist";

    // ArrayList<Entity> dataList;//鐢ㄦ潵瑁呰浇鏁版嵁婧愮殑鍒楄〃
    List<ImageItem> dataList;
    GridView gridView;
    ImageGridAdapter adapter;// 鑷畾涔夌殑閫傞厤鍣�
    AlbumHelper helper;
    Button bt;
}

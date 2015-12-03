package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.raystone.ray.goplaces_v1.AlbumHelper;
import com.raystone.ray.goplaces_v1.ImageItem;
import com.raystone.ray.goplaces_v1.MyBitMap;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel3.PlaceActivity;
import com.raystone.ray.goplaces_v1.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    ImageBucketLevel2Adapter adapter;
    AlbumHelper helper;
    Button finishPickingButton;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getActivity(), "最多选择8张图片", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.level2_grid, container, false);
        helper = AlbumHelper.getHelper();
        helper.init(getActivity().getApplicationContext());

        dataList = (List<ImageItem>) getActivity().getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new ImageBucketLevel2Adapter(getActivity(), dataList, mHandler);
        gridView.setAdapter(adapter);
        adapter.setTextCallback(new ImageBucketLevel2Adapter.TextCallback() {
            public void onListen(int count) {
                finishPickingButton.setText("完成" + "(" + count + ")");
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
            }

        });

        finishPickingButton = (Button)view.findViewById(R.id.bt);
        finishPickingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                Collection<String> c = adapter.map.values();
                Iterator<String> it = c.iterator();
                for (; it.hasNext(); ) {
                    list.add(it.next());
                }

                if (MyBitMap.act_bool) {
                    Intent intent = new Intent(getActivity(), PlaceActivity.class);
                    startActivity(intent);
                }
                for (int i = 0; i < list.size(); i++) {
                    if (MyBitMap.dir.size() < 8) {
                        MyBitMap.dir.add(list.get(i));
                    }
                }
                onDetach();
            }

        });
        return view;
    }








}

package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.raystone.ray.goplaces_v1.FileUtils;
import com.raystone.ray.goplaces_v1.MyBitMap;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel1.ImageBucketLevel1Activity;
import com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4.ViewPicActivity;
import com.raystone.ray.goplaces_v1.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Ray on 11/23/2015.
 */
public class PlaceDetailFragment extends Fragment{

    private GridView mPicGridView;
    private MyPicGridAdapter myPicGridAdapter;
    private TextView writeSomething;


    public static PlaceDetailFragment newInstance()
    {
        return new PlaceDetailFragment();
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

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == MyBitMap.bmp.size()) {
                    new MyPopupWindow(getActivity(), mPicGridView);
                } else {
                    Intent intent = new Intent(getActivity(),
                            ViewPicActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                    onDetach();
                }
            }
        });
        writeSomething = (TextView) view.findViewById(R.id.activity_selectimg_send);
        writeSomething.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < MyBitMap.dir.size(); i++) {
                    String Str = MyBitMap.dir.get(i).substring(
                            MyBitMap.dir.get(i).lastIndexOf("/") + 1,
                            MyBitMap.dir.get(i).lastIndexOf("."));
                    list.add(FileUtils.SDPATH+Str+".JPEG");
                }
                // 高清的压缩图片全部就在  list 路径里面了
                // 高清的压缩过的 bmp 对象  都在 Bimp.bmp里面
                // 完成上传服务器后 .........
                FileUtils.deleteDir();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(getContext());
        loadPic();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if(MyBitMap.max == MyBitMap.dir.size())
                    {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        break;
                    }
                    else
                    {
                        try
                        {
                            String path = MyBitMap.dir.get(MyBitMap.max);
                            ////不同之处
                            Bitmap bitmap = MyBitMap.zipImage(path);
                            MyBitMap.bmp.add(bitmap);
                            String newString = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                            FileUtils.saveBitmap(bitmap,"" + newString);
                            MyBitMap.max = MyBitMap.max + 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }catch (IOException e)
                        {e.printStackTrace();}
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
            //setBackgroundDrawable(new BitmapDrawable());
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
                    getActivity().finish();
                    dismiss();
                }
            });

            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    photo();
                    dismiss();
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

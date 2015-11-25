package com.raystone.ray.goplaces_v1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.io.IOException;
import java.util.Objects;

/**
 * Created by Ray on 11/23/2015.
 */
public class PlaceDetailFragment extends Fragment{

    private GridView mPicGridView;
    private MyPicGridAdapter myPicGridAdapter;



    public static PlaceDetailFragment newInstance()
    {
        return new PlaceDetailFragment();
    }

    @Override
    public void onAttach(Context context)
    {
        loadPic();
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

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
        public PopupWindow(Context context, View contentView)
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

            Button photoButton = (Button)view.findViewById(R.id.popupwindow_Photo);
            Button cameraButton = (Button)view.findViewById(R.id.popupwindow_camera);
            Button cancelButton = (Button)view.findViewById(R.id.popupwindow_cancel);
            photoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),)
                }
            });
        }
    }
}

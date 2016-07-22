package com.example.waffle.waffledemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.waffle.waffledemo.R;
import com.example.waffle.waffledemo.activity.RecipeListDetailActivity;
import com.example.waffle.waffledemo.bean.LocalRecipeBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by Waffle on 2016/6/28.
 */

public class RecipeAdapter extends BaseAdapter {

    private List<LocalRecipeBean> recipeBeans;
    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoaderConfiguration loaderConfig;
    private DisplayImageOptions imageOption;
    private ImageLoader imageLoader;

    public RecipeAdapter(Context context, List<LocalRecipeBean> list){
        recipeBeans = list;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        loaderConfig = ImageLoaderConfiguration.createDefault(mContext);
        ImageLoader.getInstance().init(loaderConfig);
        imageOption = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.tab_recipes_img_grey)
                .cacheInMemory(true)
                .cacheOnDisk(true).build();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return recipeBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.recipe_view_list_item,null);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.recipe_view_list_item_img);
            viewHolder.name = (TextView) convertView.findViewById(R.id.recipe_view_list_item_name);
            viewHolder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
            viewHolder.updateTime = (TextView) convertView.findViewById(R.id.recipe_view_list_item_time);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final LocalRecipeBean bean = recipeBeans.get(position);
        viewHolder.name.setText(bean.getName());
        imageLoader.displayImage(bean.getCoverPicUrl(),viewHolder.img,imageOption);
        viewHolder.updateTime.setText(bean.getUpdateTime());
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecipeListDetailActivity.class);
                intent.putExtra("name",bean.getName());
                intent.putExtra("picUrl",bean.getCoverPicUrl());
                intent.putExtra("detail",bean.getDetail());
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder{
        public RelativeLayout itemLayout;
        public ImageView img;
        public TextView name;
        public TextView updateTime;
    }
}

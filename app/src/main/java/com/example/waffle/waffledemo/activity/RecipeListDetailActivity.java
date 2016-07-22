package com.example.waffle.waffledemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.waffle.waffledemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Waffle on 2016/6/29.
 */

public class RecipeListDetailActivity extends Activity implements View.OnClickListener {

    private TextView mTitle;
    private ImageView mImg;
    private TextView mText;
    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private ImageLoader loader;
    private String picUrl;
    private LinearLayout mFavoriteBtn;
    private ImageButton mFavoriteImg;
    private TextView mFavoriteTxt;
    private LinearLayout mDislikeBtn;
    private ImageButton mDislikeImg;
    private TextView mDislikeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_layout);
        ShareSDK.initSDK(this);
        initView();
        initEvent();
        initContent();

    }

    //初始化菜谱内容
    private void initContent() {
        Intent intent = getIntent();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        picUrl = intent.getStringExtra("picUrl");
        loader = ImageLoader.getInstance();
        loader.displayImage(picUrl,mImg);
        mTitle.setText(intent.getStringExtra("name"));
        mText.setText(intent.getStringExtra("detail"));
    }

    //初始化各种事件
    private void initEvent() {
        mFavoriteBtn.setOnClickListener(this);
        mDislikeBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    //初始化主要的视图
    private void initView(){
        mFavoriteBtn = (LinearLayout) findViewById(R.id.recipe_detail_favorite_button);
        mFavoriteImg = (ImageButton) findViewById(R.id.recipe_detail_favorite_img);
        mFavoriteTxt = (TextView) findViewById(R.id.recipe_detail_favorite_txt);
        mDislikeBtn = (LinearLayout) findViewById(R.id.recipe_detail_dislike_button);
        mDislikeImg = (ImageButton) findViewById(R.id.recipe_detail_dislike_img);
        mDislikeTxt = (TextView) findViewById(R.id.recipe_detail_dislike_txt);
        mTitle = (TextView) findViewById(R.id.recipe_detail_title);
        mImg = (ImageView) findViewById(R.id.recipe_detail_img);
        mText = (TextView) findViewById(R.id.recipe_detail_text);
        mBackBtn = (ImageButton) findViewById(R.id.recipe_detail_back);
        mShareBtn = (ImageButton) findViewById(R.id.recipe_detail_share );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.recipe_detail_favorite_button:
                mFavoriteImg.setBackgroundResource(R.drawable.recipe_favorite_red);
                mFavoriteTxt.setTextColor(Color.RED);
                mDislikeImg.setBackgroundResource(R.drawable.recipe_dislike_grey);
                mDislikeTxt.setTextColor(Color.GRAY);
                break;
            case R.id.recipe_detail_dislike_button:
                mFavoriteImg.setBackgroundResource(R.drawable.recipe_favorite_grey);
                mFavoriteTxt.setTextColor(Color.GRAY);
                mDislikeImg.setBackgroundResource(R.drawable.recipe_dislike_black);
                mDislikeTxt.setTextColor(Color.BLACK);
                break;
            case R.id.recipe_detail_back:
                finish();
                break;
            case R.id.recipe_detail_share:
                OnekeyShare oks = new OnekeyShare();
                oks.setTitle("分享测试标题");
                oks.setText("分享测试文本");
                oks.setImageUrl(picUrl);
                oks.setUrl("http://www.baidu.com");
                oks.setSite("Waffle的测试App");
                oks.show(RecipeListDetailActivity.this);
                break;
        }
    }
}

package com.example.waffle.waffledemo.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.waffle.waffledemo.R;
import com.example.waffle.waffledemo.Utils.BitmapUtil;
import com.example.waffle.waffledemo.Utils.Constants;
import com.example.waffle.waffledemo.Utils.RecipesUtil;
import com.example.waffle.waffledemo.Utils.FoodMapPoiUtil;
import com.example.waffle.waffledemo.adapter.RecipeAdapter;
import com.example.waffle.waffledemo.bean.LocalRecipeBean;
import com.example.waffle.waffledemo.bean.RecipeBean;
import com.example.waffle.waffledemo.bean.UserBean;
import com.example.waffle.waffledemo.listener.FoodMapLocationListener;
import com.example.waffle.waffledemo.view.MainViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends Activity implements View.OnClickListener, PoiSearch.OnPoiSearchListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener {

    //食谱菜单组件
    private ListView recipeList;
    private RecipeAdapter mRecipeAdapter;
    private SwipeRefreshLayout refreshLayout;
    private List<LocalRecipeBean> recipeData = new ArrayList<>();
    private List<LocalRecipeBean> recipeBeans = new ArrayList<>();
    private View mListFooter,mListHeader;
    private LinearLayout mRecipeViewHeader;

    //美食地图组件
    private MapView mMapView;
    private Bundle savedInstanceState;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private TextView mMapTitle;
    private AMap aMap;
    private Marker mMyLocationMarker;
    private LinearLayout mPoiDetail;
    private TextView mPoiName,mPoiAddress;
    private PoiResult poiResult;
    private FoodMapLocationListener mMapLocationListener;
    private AMapLocation mMapLocation;
    private PoiSearch.Query query;
    private List<PoiItem> poiItems;
    private PoiItem mCurrentPoi;
    private Marker mlastMarker;
    private FoodMapPoiUtil poiOverlay;

    //我的界面相关组件
    private LinearLayout mMineSignupBtn;
    private LinearLayout mMineLoginBtn;
    private LinearLayout mMineLogoutBtn;
    private ImageButton mMineAccountPic;
    private TextView mMineUsername;

    //主界面相关组件
    private PagerAdapter pagerAdapter;
    private MainViewPager mViewPager;
    private List<View> mViews = new ArrayList<>();
    private LinearLayout mRecipesTab;
    private LinearLayout mMapTab;
    private LinearLayout mMineTab;
    private ImageButton mRecipesTabImg;
    private ImageButton mMapTabImg;
    private ImageButton mMineTabImg;
    private TextView mRecipesTabTxt;
    private TextView mMapTabTxt;
    private TextView mMineTabTxt;

    //其他相关
    private Boolean isExit = false;
    private ImageLoaderConfiguration imgLoaderConfig;
    private DisplayImageOptions imgOption;
    private ImageLoader imageLoader;

    //定位成功发送并处理message
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FoodMapLocationListener.FOOD_MAP_LOCATION_COMPLETE:
                    mMapLocation = (AMapLocation) msg.obj;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);

        Bmob.initialize(this, "b8e76681dacfc56db35b84c2af3fa886");

        imgLoaderConfig = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(imgLoaderConfig);
        imageLoader = ImageLoader.getInstance();
        imgOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        initView();
        initViewPager();
        initRecipesTab();
        initMapTab();
        initMineTab();
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stopLocation();
        locationClient = null;
        locationOption = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    //初始化主要的View
    private void initView(){
        mViewPager = (MainViewPager) findViewById(R.id.view_pager);
        mRecipesTab = (LinearLayout) findViewById(R.id.id_tab_recipes);
        mMapTab = (LinearLayout) findViewById(R.id.id_tab_map);
        mMineTab = (LinearLayout) findViewById(R.id.id_tab_mine);
        mRecipesTabImg = (ImageButton) findViewById(R.id.id_tab_recipes_img);
        mRecipesTabTxt = (TextView) findViewById(R.id.id_tab_recipes_txt);
        mMapTabImg = (ImageButton) findViewById(R.id.id_tab_map_img);
        mMapTabTxt = (TextView) findViewById(R.id.id_tab_map_txt);
        mMineTabImg = (ImageButton) findViewById(R.id.id_tab_mine_img);
        mMineTabTxt = (TextView) findViewById(R.id.id_tab_mine_txt);
    }

    //初始化主ViewPager
    private void initViewPager() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View mRecipesView = inflater.inflate(R.layout.recipe_view_layout,null);
        View mMapView = inflater.inflate(R.layout.map_view_layout,null);
        View mMineView = inflater.inflate(R.layout.mine_view_layout,null);
        mViews.add(mRecipesView);
        mViews.add(mMapView);
        mViews.add(mMineView);
        pagerAdapter = new PagerAdapter() {

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = mViews.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        mViewPager.setAdapter(pagerAdapter);
    }

    //初始化各种事件
    private void initEvent() {
        mRecipesTab.setOnClickListener(this);
        mMapTab.setOnClickListener(this);
        mMineTab.setOnClickListener(this);
        mViewPager.setScrollble(false);
    }

    //重置底部Tab按钮图片
    private void resetTab(){
        mRecipesTabImg.setBackgroundResource(R.drawable.tab_recipes_img_grey);
        mMapTabImg.setBackgroundResource(R.drawable.tab_map_img_grey);
        mMineTabImg.setBackgroundResource(R.drawable.tab_mine_img_grey);
        mRecipesTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMapTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMineTabTxt.setTextColor(getResources().getColor(R.color.grey));
    }

    //设置底部Tab为食谱选中
    private void setmRecipesTab(){
        mRecipesTabImg.setBackgroundResource(R.drawable.tab_recipes_img_green);
        mMapTabImg.setBackgroundResource(R.drawable.tab_map_img_grey);
        mMineTabImg.setBackgroundResource(R.drawable.tab_mine_img_grey);
        mRecipesTabTxt.setTextColor(getResources().getColor(R.color.green));
        mMapTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMineTabTxt.setTextColor(getResources().getColor(R.color.grey));
    }

    //设置底部Tab为地图选中
    private void setmMapTab(){
        mRecipesTabImg.setBackgroundResource(R.drawable.tab_recipes_img_grey);
        mMapTabImg.setBackgroundResource(R.drawable.tab_map_img_green);
        mMineTabImg.setBackgroundResource(R.drawable.tab_mine_img_grey);
        mRecipesTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMapTabTxt.setTextColor(getResources().getColor(R.color.green));
        mMineTabTxt.setTextColor(getResources().getColor(R.color.grey));
    }

    //设置底部Tab为我的选中
    private void setmMineTab(){
        mRecipesTabImg.setBackgroundResource(R.drawable.tab_recipes_img_grey);
        mMapTabImg.setBackgroundResource(R.drawable.tab_map_img_grey);
        mMineTabImg.setBackgroundResource(R.drawable.tab_mine_img_green);
        mRecipesTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMapTabTxt.setTextColor(getResources().getColor(R.color.grey));
        mMineTabTxt.setTextColor(getResources().getColor(R.color.green));
    }

    //初始化食谱Tab的内容
    private void initRecipesTab(){
        mRecipeViewHeader = (LinearLayout) mViews.get(0).findViewById(R.id.id_recipe_view_header);
        recipeList = (ListView) mViews.get(0).findViewById(R.id.recipe_list_view);
        mListFooter = getLayoutInflater().inflate(R.layout.recipe_view_list_footer,null);
        mListHeader = getLayoutInflater().inflate(R.layout.recipe_view_list_header,null);
        recipeList.addFooterView(mListFooter);
        recipeList.addHeaderView(mListHeader);
        mRecipeAdapter = new RecipeAdapter(this,recipeBeans);
        recipeList.setAdapter(mRecipeAdapter);
        getRecipesData();
        recipeList.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastVisiblePosition;
            boolean scrollFlag = false;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        if(view.getLastVisiblePosition() == view.getCount() -1
                                && recipeData.size() > recipeBeans.size()){
                            scrollFlag = false;
                            loadRecipesData(recipeBeans.size());
                        }
                        break;
                    default:
                        scrollFlag = true;
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(scrollFlag){
                    if(lastVisiblePosition > firstVisibleItem){
                        ObjectAnimator.ofFloat(mRecipeViewHeader,"translationY",mRecipeViewHeader.getTranslationY(),0).start();
                    } else if(lastVisiblePosition < firstVisibleItem){
                        ObjectAnimator.ofFloat(mRecipeViewHeader,"translationY",mRecipeViewHeader.getTranslationY(),-mRecipeViewHeader.getHeight()).start();
                    }
                }
                lastVisiblePosition = firstVisibleItem;
            }
        });
        refreshLayout = (SwipeRefreshLayout) mViews.get(0).findViewById(R.id.swipe_refresh_layout);
        refreshLayout.setRefreshing(false);
        refreshLayout.setColorSchemeResources(R.color.green);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                getRecipesData();
            }
        });
    }

    //获得食谱的数据
    private void getRecipesData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED){
            Toast.makeText(this,"网络不可用",Toast.LENGTH_LONG).show();
            if(refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(false);
            }
            return;
        }
        BmobQuery<RecipeBean> query = new BmobQuery<>();
        query.findObjects(new FindListener<RecipeBean>() {
            @Override
            public void done(List<RecipeBean> list, BmobException e) {
                if(list.size() > recipeData.size()){
                    recipeData.clear();
                    for(RecipeBean bean : list){
                        recipeData.add(RecipesUtil.toLocalBean(bean));
                    }
                    if(recipeBeans.size() == 0){
                        for(int i = 0 ; i < 3 ; i++){
                            recipeBeans.add(recipeData.get(i));
                        }
                        mRecipeAdapter.notifyDataSetChanged();
                    }
                }
                if(refreshLayout.isRefreshing()){
                    refreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "数据已更新",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //分批食谱加载数据
    private void loadRecipesData(final int currentCount) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = currentCount; i < currentCount + 3 && i <= recipeData.size() - 1; i++) {
                    recipeBeans.add(recipeData.get(i));
                }
                if(recipeBeans.size() == recipeData.size()){
                    recipeList.removeFooterView(mListFooter);
                }
                mRecipeAdapter.notifyDataSetChanged();
            }
        },500);
    }

    //初始化地图Tab的内容
    private void initMapTab() {
        mMapView = (MapView) mViews.get(1).findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        if(aMap == null) aMap = mMapView.getMap();
        mMapTitle = (TextView) mViews.get(1).findViewById(R.id.map_view_title);
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//        locationOption.setInterval(5000);
        locationOption.setOnceLocation(true);
        locationClient.setLocationOption(locationOption);
        mMapLocationListener = new FoodMapLocationListener(handler);
        locationClient.setLocationListener(mMapLocationListener);
        locationClient.startLocation();

        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);

        mPoiName = (TextView) mViews.get(1).findViewById(R.id.poi_name);
        mPoiDetail = (LinearLayout) mViews.get(1).findViewById(R.id.poi_detail);
        mPoiDetail.setOnClickListener(this);
    }

    //地图搜索POI
    private void doPoiResearch() {
        Toast.makeText(this,"您正在" + mMapLocation.getCity(),Toast.LENGTH_LONG).show();
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMapLocation.getLatitude(),mMapLocation.getLongitude()),18));
        mMyLocationMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.my_location_marker))).position(new LatLng(mMapLocation.getLatitude(),mMapLocation.getLongitude())));

        query = new PoiSearch.Query("美食","餐饮服务",mMapLocation.getCity());
        query.setPageSize(10);
        query.setPageNum(0);
        PoiSearch poiSearch = new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mMapLocation.getLatitude(),mMapLocation.getLongitude()), 5000, true));
        poiSearch.searchPOIAsyn();
    }

    //点击Marker后弹出顶部Header
    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            ObjectAnimator.ofFloat(mPoiDetail,"translationY",mPoiDetail.getTranslationY(),0).start();
        } else {
            ObjectAnimator.ofFloat(mPoiDetail,"translationY",mPoiDetail.getTranslationY(),-mPoiDetail.getHeight()).start();
        }
    }

    //初始化Mine界面的VIEW
    private void initMineTab() {
        mMineAccountPic = (ImageButton) mViews.get(2).findViewById(R.id.mine_account_cover_pic);
        mMineAccountPic.setOnClickListener(this);
        mMineUsername = (TextView) mViews.get(2).findViewById(R.id.mine_account_cover_username);
        mMineSignupBtn = (LinearLayout) mViews.get(2).findViewById(R.id.mine_account_signup_button);
        mMineSignupBtn.setOnClickListener(this);
        mMineLoginBtn = (LinearLayout) mViews.get(2).findViewById(R.id.mine_account_login_button);
        mMineLoginBtn.setOnClickListener(this);
        mMineLogoutBtn = (LinearLayout) mViews.get(2).findViewById(R.id.mine_account_logout_button);
        mMineLogoutBtn.setOnClickListener(this);
    }

    //按钮点击触发事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_tab_recipes:
                mViewPager.setCurrentItem(0);
                resetTab();
                setmRecipesTab();
                mMapView.onPause();
                break;
            case R.id.id_tab_map:
                mViewPager.setCurrentItem(1);
                mMapView.onResume();
                if(mMapLocation != null){
                    doPoiResearch();
                }else{
                    Toast.makeText(MainActivity.this,"定位中....",Toast.LENGTH_LONG).show();
                }
                resetTab();
                setmMapTab();
                break;
            case R.id.id_tab_mine:
                mViewPager.setCurrentItem(2);
                mMapView.onPause();
                resetTab();
                setmMineTab();
                break;
            case R.id.poi_detail:
                Intent intent = new Intent(MainActivity.this,FoodMapPoiDetailActivity.class);
                intent.putExtra("poiItem",mCurrentPoi);
                startActivity(intent);
                break;
            case R.id.mine_account_signup_button:
                Intent i = new Intent(MainActivity.this,MineSignupActivity.class);
                startActivity(i);
                break;
            case R.id.mine_account_login_button:
                Intent i2 = new Intent(MainActivity.this,MineLoginActivity.class);
                startActivityForResult(i2, Constants.ACCOUNT_LOGIN_REQUEST);
                break;
            case R.id.mine_account_logout_button:
                BmobUser.logOut();
                mMineUsername.setText("未登录");
                mMineAccountPic.setBackgroundResource(R.drawable.account_cover_pic);
                Toast.makeText(MainActivity.this,"账号已退出",Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_account_cover_pic:
                Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent2, Constants.PICTURE_CHOOSE_REQUEST);
                break;
        }
    }

    //双击退出
    @Override
    public void onBackPressed() {
        Timer tExit;
        if(!isExit){
            isExit = true;
            Toast.makeText(this,"再按一次退出Waffle", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        }else{
            finish();
        }
    }

    //POI搜索回调函数
    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(query)) {
                    poiResult = result;
                    poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
                        whetherToShowDetailInfo(false);
//                        if (mlastMarker != null) {
//                            resetlastmarker();
//                        }
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
//                        aMap.clear();
                        poiOverlay = new FoodMapPoiUtil(aMap, poiItems,this);
                        poiOverlay.addToMap();
//                        poiOverlay.zoomToSpan();

//                        aMap.addMarker(new MarkerOptions()
//                                .anchor(0.5f, 0.5f)
//                                .icon(BitmapDescriptorFactory
//                                        .fromBitmap(BitmapFactory.decodeResource(
//                                                getResources(), R.drawable.map_poi_marker)))
//                                .position(new LatLng(mMapLocation.getLatitude(), mMapLocation.getLongitude())));
//
//                        aMap.addCircle(new CircleOptions()
//                                .center(new LatLng(mMapLocation.getLatitude(), mMapLocation.getLongitude())).radius(5000)
//                                .strokeColor(Color.BLUE)
//                                .fillColor(Color.argb(50, 1, 1, 1))
//                                .strokeWidth(2));
                    }
                }
            }
        }
    }

//    private void resetlastmarker() {
//        int index = poiOverlay.getPoiIndex(mlastMarker);
//        if (index < 10) {
//            mlastMarker.setIcon(BitmapDescriptorFactory
//                    .fromBitmap(BitmapFactory.decodeResource(
//                            getResources(),
//                            markers[index])));
//        }else {
//            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
//                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
//        }
//        mlastMarker = null;
//    }



    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
//        if (mlastMarker != null) {
//            resetlastmarker();
//        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getObject() != null){
            whetherToShowDetailInfo(true);
            mCurrentPoi = (PoiItem) marker.getObject();
            mPoiName.setText(mCurrentPoi.getTitle());
        }else{
            whetherToShowDetailInfo(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.ACCOUNT_LOGIN_REQUEST:
                if(resultCode == Constants.ACCOUNT_LOGIN_COMPLETE){
                    mMineUsername.setText(BmobUser.getObjectByKey("username").toString());
                    String imgUrl = BmobUser.getCurrentUser(UserBean.class).getPortrait().getFileUrl();
                    imageLoader.loadImage(imgUrl, imgOption, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Bitmap roundBitmap = BitmapUtil.toRoundBitmap(loadedImage);
                            mMineAccountPic.setBackground(new BitmapDrawable(roundBitmap));
                        }
                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                }
            case Constants.PICTURE_CHOOSE_REQUEST:
                if(resultCode == RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, Constants.PICTURE_CROP_REQUEST);
                }
                break;
            case Constants.PICTURE_CROP_REQUEST:
                if(data!=null){
                    Bundle extras = data.getExtras();
                    Bitmap portrait = extras.getParcelable("data");
                    if (portrait != null) {
                            /**
                             * 上传服务器代码
                             */
                        mMineAccountPic.setBackground(new BitmapDrawable(BitmapUtil.toRoundBitmap(portrait)));
                    }
                }
                break;
        }

    }
}

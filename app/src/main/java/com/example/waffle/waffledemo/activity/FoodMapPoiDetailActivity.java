package com.example.waffle.waffledemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.example.waffle.waffledemo.R;
import com.example.waffle.waffledemo.Utils.ShareUtil;

import org.w3c.dom.Text;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class FoodMapPoiDetailActivity extends Activity implements View.OnClickListener {

    private TextView mPoiName;
    private TextView mPoiAddress;
    private LinearLayout mPoiCall;
    private ImageButton mBackBtn;
    private ImageButton mShareBtn;
    private String mPoiTel;
    private TextView mPoiDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_poi_detai_layout);
        ShareSDK.initSDK(FoodMapPoiDetailActivity.this);
        initView();
        initEvent();
        PoiItem poiItem = getIntent().getParcelableExtra("poiItem");
        mPoiName.setText(poiItem.getTitle());
        mPoiAddress.setText("地址：" + poiItem.getSnippet()+"\n"
                + "类型：" + poiItem.getTypeDes());
        mPoiTel = poiItem.getTel();
        mPoiDistance.setText("距您：" + poiItem.getDistance()+"米");
        if(mPoiTel ==null && "".equals(mPoiTel)){
            mPoiCall.setClickable(false);
        }


    }

    private void initEvent() {
        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mPoiCall.setOnClickListener(this);
    }

    private void initView() {
        mPoiDistance = (TextView) findViewById(R.id.poi_detail_distance);
        mPoiName = (TextView) findViewById(R.id.poi_detail_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_detail_address);
        mPoiCall = (LinearLayout) findViewById(R.id.poi_detail_call);
        mBackBtn = (ImageButton) findViewById(R.id.map_poi_detail_back);
        mShareBtn = (ImageButton) findViewById(R.id.map_poi_detail_share);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.poi_detail_call:
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + mPoiTel);
                    intent.setData(data);
                    startActivity(intent);
                break;
            case R.id.map_poi_detail_back:
                finish();
                break;
            case R.id.map_poi_detail_share:
                Toast.makeText(this,"Share!!",Toast.LENGTH_LONG).show();
                new OnekeyShare().show(FoodMapPoiDetailActivity.this);
                break;
        }
    }
}

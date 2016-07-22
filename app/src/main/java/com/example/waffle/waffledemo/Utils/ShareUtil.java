package com.example.waffle.waffledemo.Utils;

import android.content.Context;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Waffle on 2016/7/21.
 */

public class ShareUtil {

    private Context mContext;
    private String shareTitle;
    private String shareText;
    private String shareImageUrl;
    private String shareUrl;
    private String shareComment;
    private String shareSiteUrl;

    public ShareUtil(Context context){
        mContext = context;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public void setShareComment(String shareComment) {
        this.shareComment = shareComment;
    }

    public void setShareSiteUrl(String shareSiteUrl) {
        this.shareSiteUrl = shareSiteUrl;
    }



    public void share(){
        ShareSDK.initSDK(mContext);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setTitle(shareTitle == null ? "http://www.baidu.com" : shareTitle);
        oks.setText(shareText == null ? "分享测试" : shareText);
        oks.setImageUrl(shareImageUrl);
        oks.setUrl(shareUrl == null ? "http://www.baidu.com" : shareUrl);
        oks.setComment(shareComment == null ? "分享测试" : shareComment);
        oks.setSite("Waffle的Demo");
        oks.setSiteUrl(shareSiteUrl == null ? "http://www.baidu.com" : shareSiteUrl);
        oks.show(mContext);
    }
}

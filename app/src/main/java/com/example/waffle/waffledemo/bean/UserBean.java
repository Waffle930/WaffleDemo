package com.example.waffle.waffledemo.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Waffle on 2016/7/22.
 */

public class UserBean extends BmobUser {

    private BmobFile portrait;

    public BmobFile getPortrait() {
        return portrait;
    }

    public void setPortrait(BmobFile portrait) {
        this.portrait = portrait;
    }

    @Override
    public String toString() {
        return getUsername();
    }
}

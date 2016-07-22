package com.example.waffle.waffledemo.bean;

import java.io.File;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Waffle on 2016/6/28.
 */

public class RecipeBean extends BmobObject {
    private BmobFile coverPic;
    private String name;
    private String detail;

    public String getDetail() {
        return detail;
    }
    public BmobFile getCoverPic() {
        return coverPic;
    }
    public String getName() {
        return name;
    }
}

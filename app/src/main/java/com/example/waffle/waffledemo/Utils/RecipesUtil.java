package com.example.waffle.waffledemo.Utils;

import com.example.waffle.waffledemo.bean.LocalRecipeBean;
import com.example.waffle.waffledemo.bean.RecipeBean;

/**
 * Created by Waffle on 2016/7/20.
 */

public class RecipesUtil {
    public static LocalRecipeBean toLocalBean(RecipeBean netBean){
        LocalRecipeBean localBean = new LocalRecipeBean();
        localBean.setId(netBean.getObjectId());
        localBean.setName(netBean.getName());
        localBean.setCoverPicUrl(netBean.getCoverPic().getFileUrl());
        localBean.setDetail(netBean.getDetail());
        localBean.setUpdateTime(netBean.getCreatedAt().substring(5,10));
        return localBean;
    }
}

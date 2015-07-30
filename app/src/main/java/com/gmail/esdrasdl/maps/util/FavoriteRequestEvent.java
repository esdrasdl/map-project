package com.gmail.esdrasdl.maps.util;

import com.gmail.esdrasdl.maps.data.FavoriteEntity;

import java.util.List;

/**
 * Created by esdras on 29/07/15.
 */
public class FavoriteRequestEvent {
    private List<FavoriteEntity> mFavoriteList;

    public FavoriteRequestEvent(List<FavoriteEntity> favoriteEntities) {
        mFavoriteList = favoriteEntities;
    }

    public List<FavoriteEntity> getFavoriteList() {
        return mFavoriteList;
    }

    public void setFavoriteList(List<FavoriteEntity> favoriteList) {
        mFavoriteList = favoriteList;
    }
}

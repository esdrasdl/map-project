package com.gmail.esdrasdl.maps.data.net;

import com.gmail.esdrasdl.maps.data.FavoriteEntity;

import java.util.List;

/**
 * Created by esdras on 29/07/15.
 */

public class FavoritesEntity {
    List<FavoriteEntity> favorites;

    public List<FavoriteEntity> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<FavoriteEntity> favorites) {
        this.favorites = favorites;
    }
}

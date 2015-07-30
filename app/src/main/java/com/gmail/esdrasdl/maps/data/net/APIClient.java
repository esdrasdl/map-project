package com.gmail.esdrasdl.maps.data.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.gmail.esdrasdl.maps.BuildConfig;
import com.gmail.esdrasdl.maps.R;
import com.gmail.esdrasdl.maps.data.FavoriteEntity;
import com.gmail.esdrasdl.maps.util.FavoriteRequestEvent;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by esdras on 29/07/15.
 */
public class APIClient {


    private Context mContext;
    API mAPI;

    public APIClient(Context context) {
        mContext = context;
        RestAdapter mRestAdapter = new RestAdapter.Builder().
                setEndpoint(API.API_BASE_URL).
                setClient(new OkClient(createOkHttpClient())).
                build();
        if (BuildConfig.DEBUG) {
            mRestAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        } else {
            mRestAdapter.setLogLevel(RestAdapter.LogLevel.NONE);
        }
        mAPI = mRestAdapter.create(API.class);
    }

    private OkHttpClient createOkHttpClient() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);

        return okHttpClient;
    }

    public void getFavoriteList(String param) {
        if (isInternetConnectionAvailable()) {
            mAPI.getFavoriteList(param, new Callback<FavoritesEntity>() {
                @Override
                public void success(FavoritesEntity favoriteEntities, Response response) {
                    for (FavoriteEntity entity : favoriteEntities.getFavorites()) {
                        Timber.d(entity.toString());
                    }
                    EventBus.getDefault().post(new FavoriteRequestEvent(favoriteEntities.getFavorites()));
                }

                @Override
                public void failure(RetrofitError error) {
                    Timber.d(error.toString());
                }
            });
        } else {
            try {
                Toast.makeText(mContext, mContext.getText(R.string.no_connection_message), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isInternetConnectionAvailable() {
        boolean isConnected;

        ConnectivityManager connectivityManager =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

        return isConnected;
    }
}

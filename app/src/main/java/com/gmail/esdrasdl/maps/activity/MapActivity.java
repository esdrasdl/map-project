package com.gmail.esdrasdl.maps.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gmail.esdrasdl.maps.R;
import com.gmail.esdrasdl.maps.adapter.FavoriteArrayAdapter;
import com.gmail.esdrasdl.maps.controller.MapController;
import com.gmail.esdrasdl.maps.data.FavoriteEntity;
import com.gmail.esdrasdl.maps.util.FavoriteRequestEvent;
import com.gmail.esdrasdl.maps.util.GPStatusEvent;
import com.gmail.esdrasdl.maps.util.MapEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = MapActivity.class.getSimpleName();
    private GoogleMap mMap;
    private MapController mMapController;
    private TextView mAddressTextView;
    private SearchView mSearchView;
    private boolean mGpsEnable = true;
    private LocationManager mLocationManager;
    private TextView mTitleAddress;
    private String mStreet;
    private String mCity;
    private LinearLayout mFavoriteLayout;
    private ListView mFavoriteListView;
    private FavoriteArrayAdapter mAdapter;
    private TextView mCloseFavoriteLayout;
    private Toast mToast;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private FloatingActionButton mFavoriteFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupActionBar();
        setupViews();
        setupListeners();
        setupControllers();
        setupFavorites();
        recoveryLastState(savedInstanceState);

        mMap.setOnCameraChangeListener(mMapController.setCameraChangeListener());
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        setupAdapter(new ArrayList<FavoriteEntity>(0));
        setupDrawerMenu();

        EventBus.getDefault().register(this);
    }

    private void setupListeners() {
        mCloseFavoriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFavoriteLayout.setVisibility(View.GONE);
            }
        });

        mFavoriteFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavorite();
            }
        });

        mFavoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavoriteEntity item = mAdapter.getItem(position);
                mMapController.setIsMoveCameraAllow(false);
                mMapController.moveCamera(new LatLng(item.getLatitude(), item.getLongitude()));
                mCloseFavoriteLayout.performClick();
            }
        });
    }

    private void setupFavorites() {
        mMapController.getFavoriteList();

    }

    private void recoveryLastState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMapController.setIsMoveCameraAllow(false);
            double lat = savedInstanceState.getDouble(MapController.LATITUDE);
            double lon = savedInstanceState.getDouble(MapController.LONGITUDE);
            Timber.d("recoveryLastState onSaveInstanceState " + lat + "," + lon);
            mCity = savedInstanceState.getString("CITY");
            mStreet = savedInstanceState.getString("STREET");

            if (!TextUtils.isEmpty(mStreet)) {
                mAddressTextView.setText(mStreet);
            }

            if (!TextUtils.isEmpty(mCity)) {
                setAddressTitle(mCity);
            }

            mMapController.moveCamera(lat, lon);
        }
    }

    private void setupControllers() {
        mMapController = new MapController(this.getApplicationContext(), mMap);
    }


    private void moveCameraIfEnabled(Location location) {
        if (mMapController.isMoveCameraAllow()) {
            mMapController.moveCamera(location);
            mMapController.requestAddressLocation(location);
        }
    }

    private void setupLocaleByNetwork() {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {
            moveCameraIfEnabled(location);
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, mMapController.getLocationListener());
    }

    private void setupLocationByGPS() {
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            mGpsEnable = true;
            moveCameraIfEnabled(location);
        } else {
            mGpsEnable = false;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, mMapController.getLocationListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mMap.setOnCameraChangeListener(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setupLocationByGPS();
                setupLocaleByNetwork();
            }
        }).run();

    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanListeners();
    }


    private void cleanListeners() {
        mLocationManager.removeUpdates(mMapController.getLocationListener());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapController.getLastPosition() != null) {
            Timber.d("onSaveInstanceState " + mMapController.getLastPosition().toString());
            outState.putDouble(MapController.LATITUDE, mMapController.getLastPosition().latitude);
            outState.putDouble(MapController.LONGITUDE, mMapController.getLastPosition().longitude);
            outState.putString("CITY", mCity);
            outState.putString("STREET", mStreet);
        }
    }

    private void setupViews() {
        setUpMapIfNeeded();
        mAddressTextView = (TextView) findViewById(R.id.current_address_textview);
        mTitleAddress = (TextView) findViewById(R.id.address_title);
        mFavoriteLayout = (LinearLayout) findViewById(R.id.favorite_layout);
        mFavoriteListView = (ListView) findViewById(R.id.favorite_listview);
        mCloseFavoriteLayout = (TextView) findViewById(R.id.favorite_close);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mFavoriteFAB = (FloatingActionButton) findViewById(R.id.fav_fab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        setupSearchView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_center:
                mMapController.getCurrentPosition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToFavorite() {
        showInputDialog();

    }

    private void showFavoriteList() {
        mFavoriteLayout.setVisibility(View.VISIBLE);
    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mMapController.getLocationThroughAddress(query);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void setAddressTitle(String city) {
        mTitleAddress.setText(getString(R.string.current_address_city).replace("#", city));
    }

    private void setAddressTextView(String[] address) {
        try {
            if (address[0].indexOf(",") > 0) {
                mStreet = address[0].substring(0, address[0].indexOf(","));
            } else {
                mStreet = address[0];
            }
            mCity = address[1];
            mAddressTextView.setText(mStreet);
            setAddressTitle(mCity);
        } catch (Exception e) {
            mAddressTextView.setText(getString(R.string.address_not_found));
            setAddressTitle("");

        }
    }

    public void onEventMainThread(MapEvent event) {
        setAddressTextView(event.getAddress());
        mMapController.moveCamera(event.getLatLng());

    }

    public void onEventMainThread(FavoriteRequestEvent event) {
        if (event != null && event.getFavoriteList() != null) {
            if (mAdapter == null) {
                setupAdapter(event.getFavoriteList());
            } else {
                mAdapter.setList(event.getFavoriteList());
            }
        }
    }

    private void addToList(FavoriteEntity entity) {
        if (mAdapter != null) {
            mAdapter.add(entity);
        }
    }

    private void setupAdapter(List<FavoriteEntity> entities) {
        mAdapter = new FavoriteArrayAdapter(this, entities);
        mFavoriteListView.setAdapter(mAdapter);
    }

    public void onEventMainThread(GPStatusEvent event) {
        if (event.getStatus() && !mGpsEnable) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            setupLocationByGPS();
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }


    private void showInputDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.favorite_name_labelt)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.save)
                .input(R.string.fav_hint, R.string.empty, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String name;

                        if (!TextUtils.isEmpty(input.toString())) {
                            name = input.toString();
                        } else {
                            name = getAddress();
                        }
                        showToast(name + " added to your list");
                        addToList(mMapController.buildFavoriteLocation(name));
                    }
                }).show();
    }

    private String getAddress() {
        return mAddressTextView.getText().toString();
    }

    @Override
    public void onBackPressed() {
        if (mFavoriteLayout.getVisibility() == View.VISIBLE) {
            mFavoriteLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void setupDrawerMenu() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                mDrawerLayout.closeDrawers();

                if (menuItem.getGroupId() == R.id.menu_group) {
                    navigationView.getMenu().setGroupCheckable(R.id.menu_group, false, true);
                    navigationView.getMenu().setGroupCheckable(R.id.menu_group_2, true, true);
                } else {

                    navigationView.getMenu().setGroupCheckable(R.id.menu_group, true, true);
                    navigationView.getMenu().setGroupCheckable(R.id.menu_group_2, false, true);
                }
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.menu_your_places:
                        showFavoriteList();
                        return true;
                    case R.id.menu_disconnect:
                        finish();
                        return true;
                    default:

                        return true;
                }
            }

        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }
}

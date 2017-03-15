package com.example.asus_.baidumap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationClient locationClient;
    private MapView mapView;
    private BaiduMap baidumap;
    private boolean isFirstLocate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        mapView = (MapView) findViewById(R.id.tv);
        baidumap = mapView.getMap();
        baidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baidumap.setMyLocationEnabled(true);
        List<String> permissonList = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissonList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissonList.isEmpty()) {
            String[] perssions = permissonList.toArray(new String[permissonList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, perssions, 1);
        } else {
             requestLoaction();
        }
    }

   private void requestLoaction() {
        LocationClientOption locationClientOption = new LocationClientOption();
        locationClientOption.setScanSpan(5000);
        locationClientOption.setIsNeedAddress(true);
        locationClient.setLocOption(locationClientOption);
        locationClient.start();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        baidumap.setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                     requestLoaction();
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuffer str = new StringBuffer();
            str.append(bdLocation.getCity() + " " + bdLocation.getDistrict() + " " + bdLocation.getStreet());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                str.append("gps");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                str.append("wangluo");
            }
            navigateto(bdLocation);

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    private void navigateto(BDLocation bdLocation) {
        if (isFirstLocate) {
            Toast.makeText(MainActivity.this,bdLocation.getLatitude()+"-----"+bdLocation.getLongitude() , Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());

            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baidumap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baidumap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
        locationBuilder.latitude(bdLocation.getLatitude());
        locationBuilder.longitude(bdLocation.getLongitude());
        MyLocationData locationData=locationBuilder.build();
        baidumap.setMyLocationData(locationData);
    }
}

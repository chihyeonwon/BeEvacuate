package com.example.beevacuate;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private NaverMap naver_m;
    private FusedLocationSource mLocationSource;
    private UiSettings mUiSettings;
    private String LatLng;

    private double my_lati;
    private double my_long;
    private double Lat;
    private double Lng;

    PathOverlay path;

    private PathOverlay pathOverlay;
    private List<LatLng> pathPoints;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Intent intent;
        intent = getIntent();
        LatLng = intent.getStringExtra("LatLng");



        pathPoints = new ArrayList<>();





        // 최적의 위치를 반환함, 사용자의 현재 위치를 구할때 위치 정보
        mLocationSource = new FusedLocationSource(this, 101);



        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment) ((FragmentManager) fm).findFragmentById(R.id.fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(null);
            fm.beginTransaction().add(R.id.fragment, mapFragment).commit();
        }

        // onMapReady()메소드를 실행시킨다.
        mapFragment.getMapAsync(this);






    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated()) { // 권한 거부됨
                naver_m.setLocationTrackingMode(LocationTrackingMode.None);
            } else {
                naver_m.setLocationTrackingMode(LocationTrackingMode.NoFollow);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    public void close(View view) {
        finish();
    }

    // 경로 재탐색
    public void research(View view) {
        Toast myToast = Toast.makeText(this.getApplicationContext(),"경로를 재탐색 합니다", Toast.LENGTH_SHORT);
        myToast.show();
        if (path != null) {
            path.setMap(null);
        }
        if (pathOverlay != null) {
            pathOverlay.setMap(null);
            pathPoints = new ArrayList<>();
        }
        new NaverNaviApi().execute();
    }




    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        // NaverMap 객체는 개발자가 마음대로 생성할수 없다, onMapReady()메소드 매개변수 에서만 생성됨
        naver_m = naverMap;
        ArrayList<InfoWindow> arrayInfo = new ArrayList<>();



        mUiSettings = naver_m.getUiSettings();

        // 현재위치 버튼 활성화
        mUiSettings.setLocationButtonEnabled(true);

        // 회전 제스처(베어링 각도조절) 비활성화
        mUiSettings.setRotateGesturesEnabled(false);

        // .setLocationSource()메소드 등록 해야 현재 위치 버튼 눌렀을때 추적 가능
        naver_m.setLocationSource(mLocationSource);

        // .setLocationTrackingMode() 위치 추적하는 명령문(ex 임의의 버튼을 만들어서 사용할때)
        naver_m.setLocationTrackingMode(LocationTrackingMode.Follow);

        String[] abc = LatLng.split(",");

        Lat = Double.parseDouble(abc[0]);
        Lng = Double.parseDouble(abc[1]);

        CameraUpdate cameraUpdateDestination = CameraUpdate.scrollTo(new LatLng(Lat, Lng));
        cameraUpdateDestination.animate(CameraAnimation.Fly, 700);
        naver_m.moveCamera(cameraUpdateDestination);

        // 목적지 마커
        Marker marker = new Marker();
        marker.setIcon(OverlayImage.fromResource(R.drawable.shelter_icon));
        marker.setPosition(new LatLng(Lat, Lng));
        marker.setMap(naver_m);





        // 정보창 관련
        InfoWindow naviInfo = new InfoWindow();

        naviInfo.setAdapter(new InfoWindow.DefaultTextAdapter(this) {
            @NonNull
            @Override
            public CharSequence getText(@NonNull InfoWindow infoWindow) {
                return "이 위치로\n길찾기 시작";

                // todo : 정보창 마커위에 띄우고 정보창 클릭시 길찾기 시작 하도록 할것
            }
        });

        naviInfo.open(marker);



        naviInfo.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                if (path != null) {
                    path.setMap(null);
                }
                new NaverNaviApi().execute();

                CameraUpdate cameraUpdateMy = CameraUpdate.scrollAndZoomTo(new LatLng(my_lati, my_long), 14);
                cameraUpdateMy.animate(CameraAnimation.Fly, 700);
                naver_m.moveCamera(cameraUpdateMy);

                naviInfo.close();

                return true;
            }
        });

        naver_m.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {

                my_lati = location.getLatitude();
                my_long = location.getLongitude();
            }
        });


        naverMap.addOnLocationChangeListener((location) -> {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            pathPoints.add(currentLatLng);
            drawPath();
        });


    }

    // 이동 경로 그리기
    private void drawPath() {
        if (pathPoints.size() < 2) {
            // 최소한 2개의 좌표가 필요하므로, 2개 미만인 경우 경로를 그리지 않고 종료합니다.
            return;
        }

        if (pathOverlay != null) {
            pathOverlay.setMap(null);
        }

        pathOverlay = new PathOverlay();
        pathOverlay.setCoords(pathPoints);
        pathOverlay.setColor(Color.GRAY);
        pathOverlay.setOutlineWidth(15);
        pathOverlay.setMap(naver_m);
    }

    class NaverNaviApi extends AsyncTask<Void, Integer, String> {
        String opt = "traavoidtoll:traavoidcaronly";

        StringBuffer response;

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String appDirectionUrl = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start="+my_long+","+my_lati+"&goal="+Lng+","+Lat+"&option="+opt;
                URL url = new URL(appDirectionUrl);
                HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();

                urlConn.setRequestMethod("GET");
                urlConn.setRequestProperty("X-NCP-APIGW-API-KEY-ID","j04unglub7");
                urlConn.setRequestProperty("X-NCP-APIGW-API-KEY","UrswuJWRSXagOzU0S4cQJGHLW21H5x2u6wo8WnxF");
                urlConn.connect();

                int responseCode = urlConn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.e("HTTPS 응답코드",responseCode+"");
                Log.e("HTTPS body", response.toString()+"");


            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject json_route = jsonObject.getJSONObject("route");
                JSONArray jsonArray_trafast = json_route.getJSONArray("traavoidtoll");
                JSONObject JSONObject_sub = jsonArray_trafast.getJSONObject(0);
                JSONArray jsonArray_path = JSONObject_sub.getJSONArray("path");

                List<LatLng> latLngs = new ArrayList<>();
                for (int i=0 ; i<jsonArray_path.length() ; i++) {
                    String[] coor = jsonArray_path.get(i).toString().substring(1,jsonArray_path.get(i).toString().length()-1).split(",");
                    Log.e("coor0", coor[0]+""); // 경도, double로 변환해서 오버레이 적용시키면됨
                    Log.e("coor1", coor[1]+""); // 위도

                    latLngs.add(new LatLng(Double.parseDouble(coor[1]), Double.parseDouble(coor[0])));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        path = new PathOverlay();
                        path.setCoords(latLngs);

                        path.setColor(Color.RED);
                        path.setPassedColor(Color.GRAY);
                        path.setOutlineWidth(10);
//                        Log.e("latLngs 데이터",latLngs.get(0).latitude+", "+latLngs.get(0).longitude+"\n"+latLngs.get(1).latitude+", "+latLngs.get(1).longitude);
                        path.setMap(naver_m);
                    }
                });


                Log.e("traoptimal 길이",jsonArray_trafast.length()+"");
                Log.e("path 길이",jsonArray_path.length()+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
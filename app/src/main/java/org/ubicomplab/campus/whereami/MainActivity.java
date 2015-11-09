package org.ubicomplab.campus.whereami;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private static final String BASE_URL = "http://r2d2.hcii.cs.cmu.edu:9001";
    private static final String URL_CURRENTLOCATION = BASE_URL + "/campus/location/wifi";

    private static final String[] FILTER_WIFI = {"CMU","CMU-SECURE"};
    private static final int DEFAULT_RSSI_LEVEL = -100;

    //Views
    private Button mButtonCurrentLocation;
    private EditText mEditTextApiId;
    private TextView mTextViewLocation;
    private ProgressDialog mProgressDialog;

    //Params
    private WifiManager mWifiManager;
    private WiFiLocation mWiFiIndoorLocation = null;
    private boolean isWifiScan = false;
    private String mCandidateBSSID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initParams();
        initViews();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Unregister BroadcastReceiver
        if (isWifiScan){
            unregisterReceiver(mWiFiSearchResultBroadcastReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void initViews(){
        mButtonCurrentLocation = (Button) findViewById(R.id.button_currentlocation);
        mButtonCurrentLocation.setOnClickListener(btnCurrentLocationClickListener);

        mEditTextApiId = (EditText) findViewById(R.id.edittext_apiid);
        //If the sample API key is not working, please contact Jung Wook Park (jungwoop at andrew.cmu.edu)
        mEditTextApiId.setText("819ee775d92c0ef73120c600aad7ba88");
        mTextViewLocation = (TextView) findViewById(R.id.textview_location);

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage(getString(R.string.server_retrieve));
        mProgressDialog.setCancelable(true);

    }

    private void initParams(){
        //Get WiFi Manager Instance
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        //Check WiFi Availability
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }

    }

    View.OnClickListener btnCurrentLocationClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //Disable the Button
            mButtonCurrentLocation.setEnabled(false);
            startWiFiScan();
        }
    };

    private void startWiFiScan(){
        //Show Progress Dialog
        mProgressDialog.show();

        //Register Broadcast Receiver
        registerReceiver(mWiFiSearchResultBroadcastReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        //Start WiFi Scanning
        isWifiScan = mWifiManager.startScan();

    }

    private String findStrongAccessPoint(List<ScanResult> accessPointList){
        int previousRSSI = DEFAULT_RSSI_LEVEL;
        String strongAPBSSID = null;

        //Find the nearest CMU or CMU-SECURE AP in the result
        for (ScanResult result : accessPointList){
            if (result.SSID != null){
                String ssid = result.SSID.toUpperCase().trim();
                if (ssid.equals(FILTER_WIFI[0]) || ssid.equals(FILTER_WIFI[1]) ){
                    if (result.level > previousRSSI){
                        previousRSSI = result.level;
                        strongAPBSSID = result.BSSID;
                    }
                }
            }
        }
        return strongAPBSSID;
    }

    private BroadcastReceiver mWiFiSearchResultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Stop WiFi Scanning
            isWifiScan = false;
            unregisterReceiver(mWiFiSearchResultBroadcastReceiver);

            //Enable the Button
            mButtonCurrentLocation.setEnabled(true);
            mTextViewLocation.setText("");

            mCandidateBSSID = findStrongAccessPoint(mWifiManager.getScanResults());

            if (mCandidateBSSID != null){
                mWiFiIndoorLocation = null;
                new WiFiInfoUpdater(mCandidateBSSID).execute();
            }else{
                mTextViewLocation.setText(R.string.search_nocmuap_error);
                Toast.makeText(MainActivity.this, R.string.search_nocmuap_error, Toast.LENGTH_SHORT).show();
            }

        }
    };

    public class WiFiInfoUpdater extends AsyncTask<String, String, Long> {

        private RequestWiFiLocation mRequestParam;
        private CommonResult mCommonResult;
        private String nearestBSSID = "";

        public WiFiInfoUpdater(String nearestBSSID) {
            super();
            this.nearestBSSID = nearestBSSID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            String apiID = mEditTextApiId.getText().toString().trim();
            String deviceInfo = Build.MANUFACTURER + "/" + Build.MODEL;
            mRequestParam = new RequestWiFiLocation(apiID,deviceInfo,nearestBSSID);
        }

        @Override
        protected Long doInBackground(String... params) {


            try {
                String strUrl = URL_CURRENTLOCATION;
                java.net.URL address = new java.net.URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) address.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setReadTimeout(1000);
                conn.setConnectTimeout(3000);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                ObjectMapper mapper = new ObjectMapper();

                OutputStream os = conn.getOutputStream();
                byte[] requestParam = mapper.writeValueAsString(mRequestParam).toString().getBytes("UTF-8");
                Log.e("", "RequestParam: " + new String(requestParam));
                os.write(requestParam);
                os.close();
                InputStream src = conn.getInputStream();

                if (src != null) {
                    mCommonResult = mapper.readValue(src, CommonResult.class);
                } else {
                    mCommonResult = null;
                }

            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);
            mProgressDialog.cancel();

            if (mCommonResult != null) {
                if (mCommonResult.getResultObject() != null){
                    mWiFiIndoorLocation = mCommonResult.getResultObject();
                    mTextViewLocation.setText(mWiFiIndoorLocation.toString());
                }else{
                    Toast.makeText(MainActivity.this, mCommonResult.getResultMessage(), Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivity.this, R.string.server_retrieve_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mProgressDialog.isShowing())
                mProgressDialog.cancel();
        }
    }

}

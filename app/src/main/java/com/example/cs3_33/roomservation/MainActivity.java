package com.example.cs3_33.roomservation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    String  ip = "http://210.123.254.135:8080/Roomservation/";
    private BeaconManager beaconManager;
    private Region region;
    Beacon nearestBeacon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        beaconManager = new BeaconManager(this);


        final Context myApp = this;

        webView.setWebChromeClient(new WebChromeClient() { //webview에서 알림창이 뜨도록 해주는 메소드
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
            {
                new AlertDialog.Builder(myApp)
                        .setTitle("AlertDialog")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new AlertDialog.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        result.confirm();
                                    }
                                })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            };
        });

        webView.loadUrl(ip+"index.jsp");


        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);

                return true;
            }
        });

        findViewById(R.id.btn_home).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        webView.loadUrl(ip+"index.jsp");
                    }
                }
        );
        findViewById(R.id.btn_reserveration).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        webView.loadUrl(ip+"Reservation/main.jsp");
                    }
                }
        );
        findViewById(R.id.btn_check).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        webView.loadUrl(ip+"Check/checkForm.jsp");
                        webView.addJavascriptInterface(new Object() {
                            ConnectActivity CA = new ConnectActivity();

                            @JavascriptInterface
                            public void makeToast(String id, String check){
                                if(nearestBeacon.getRssi()> -70){
                                    String result = CA.SendByHttp(id, check);
                                }else{
                                    String result = CA.SendByHttp(id, "disconnect");
                                }
                            }
                        }, "app");
                    }
                }
        );
        findViewById(R.id.btn_setting).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        webView.loadUrl(ip+"Setting/main.jsp");
                    }
                }
        );



        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    nearestBeacon = list.get(0);
                    Log.d("Airport", "Nearest places: " + nearestBeacon.getRssi());
                }
            }
        });

        region = new Region("ranged region",
                UUID.fromString("20CAE8A0-A9CF-11E3-A5E2-0800200C9A66"), null, null);
        // 본인이 연결할 Beacon의 ID와 Major / Minor Code를 알아야 한다.



    }

    public void onConfigurationChanged(Configuration newConfig) {
     super.onConfigurationChanged(newConfig); //화면 전환시 처음페이지로 이동하지 않기 위해
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onResume() {
        super.onResume();
        // 블루투스 권한 승낙 및 블루투스 활성화
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    protected void onPause() {
        //beaconManager.stopRanging(region);
        super.onPause();
    }

}



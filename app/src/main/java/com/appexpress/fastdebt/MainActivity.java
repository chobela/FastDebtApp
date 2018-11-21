package com.appexpress.fastdebt;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String url;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isOnline(MainActivity.this);


        if(isOnline(MainActivity.this)){

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String uid = sp.getString("uid", "anonymous");

            url = "http://fastdebt.pe.hu/form/index.php?uid=" + uid;

            webView = findViewById(R.id.webView);
            startWebView(url);

        } else {

            Intent i = new Intent(MainActivity.this, Offline.class);
            MainActivity.this.startActivity(i);
        }

        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {

            logoutUser();
        }


    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    private void startWebView(String url) {
        //create new webview client to show progress dialogue when opening a url or
        //click on link
        webView.setWebViewClient(
                new WebViewClient() {
                    ProgressDialog progressDialog;


                    //If you will not use this method, url links are open in new browser
                    //not in webview
                    public boolean
                    shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }



                    public boolean timeout;{
                        timeout = true;
                    }

                    //show loader on url load
                    public void onLoadResource
                    (WebView view, String url) {
                        if (progressDialog == null) {
                            // in standard case YourActivity.this
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setMessage("Loading...");
                            progressDialog.show();
                        }
                    }

                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(timeout) {
                                    Intent i = new Intent(MainActivity.this, Offline.class);
                                    MainActivity.this.startActivity(i);
                                }
                            }
                        }).start();



                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();


                    }

                    public void onPageFinished(WebView view, String url) {

                        try {

                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            timeout = false;

                        } catch (Exception exception) {
                            exception.printStackTrace();

                        }
                    }
                });

        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);

        // Other webview options

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
       // webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
       // webView.setScrollbarFadingEnabled(false);
        webView.clearCache(true);
        webView.loadUrl(url);

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

   if (id == R.id.signout) {
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }


    private void logoutUser() {
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}

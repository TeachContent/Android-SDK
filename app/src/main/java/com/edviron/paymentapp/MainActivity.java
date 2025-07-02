package com.edviron.paymentapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String collectId = "68655af084927b2f7e35ecdb"; // Make sure this matches the URL's collect_request_id
    String mode = "sandbox"; // or "production"

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        WebView webView = new WebView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        webView.setLayoutParams(params);
        setContentView(webView);

        if (!mode.equals("production") && !mode.equals("sandbox")) {
            Toast.makeText(this, "Invalid Payment Mode", Toast.LENGTH_LONG).show();
            return;
        }

        String baseUrl = mode.equals("production") ?
                "https://pg.edviron.com" :
                "https://dev.pg.edviron.com";

        String paymentUrl = baseUrl + "/collect-sdk-payments?collect_id=" + collectId;

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if ((url.contains("payment-success") || url.contains("payment-failure")) && url.contains(collectId)) {
                    if (url.contains("payment-success")) {
                        onSuccess();
                    } else {
                        onFailure();
                    }
                }
            }
        });

        webView.loadUrl(paymentUrl);
    }

    private void onSuccess() {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show();
        // Optional: finish(); or startActivity(new Intent(...));
    }

    private void onFailure() {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
    }
}

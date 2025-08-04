package com.edviron.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String collectId = "6890da641cfdf371ee20af93"; // Your collect request ID
    String mode = "production"; // or "sandbox"

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
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("upi://") || url.startsWith("paytmmp://") ||
                        url.startsWith("tez://") || url.startsWith("phonepe://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        Intent chooser = Intent.createChooser(intent, "Pay with UPI");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(chooser);
                        } else {
                            Toast.makeText(MainActivity.this, "No UPI app found", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to open UPI app", Toast.LENGTH_LONG).show();
                    }
                    return true; // Don't load UPI intent in WebView
                }

                return false; // Let WebView load normal URLs
            }

            @Override
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
        Toast.makeText(this, "✅ Payment Successful", Toast.LENGTH_LONG).show();
        // Optional: You can finish or redirect here
        // finish();
    }

    private void onFailure() {
        Toast.makeText(this, "❌ Payment Failed", Toast.LENGTH_LONG).show();
    }
}

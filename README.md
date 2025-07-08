# Edviron Payment App Integration

This document describes how to integrate the Edviron Payment SDK in your Android application using a `WebView`.

## Integration Steps

### 1. Add the MainActivity Code

Add the following code to your `MainActivity.java` (or another Activity where you want to integrate the payment flow):

```java
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

    String collectId = "YOUR_COLLECT_ID"; // Set your collect_request_id here
    String mode = "development"; // Use "development" for testing or "production" for live

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

        if (!mode.equals("production") && !mode.equals("developement")) {
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
        // Optionally finish the activity or navigate to another screen
    }

    private void onFailure() {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
    }
}
```

### 2. Configure Your Parameters

- **collectId**: Replace `"YOUR_COLLECT_ID"` with your actual `collect_request_id` from Edviron.
- **mode**: Use `"development"` for testing or `"production"` for live payments.

### 3. URLs

- **Development**: `https://dev.pg.edviron.com/collect-sdk-payments?collect_id=YOUR_COLLECT_ID`
- **Production**: `https://pg.edviron.com/collect-sdk-payments?collect_id=YOUR_COLLECT_ID`

### 4. Handling Payment Result

- The SDK will check the URL for `payment-success` or `payment-failure` when the transaction completes.
- You can customize the `onSuccess()` and `onFailure()` methods to handle the result as needed (e.g., navigate to another activity, show a dialog, etc).

### 5. Required Permissions

Make sure you add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 6. Notes

- Ensure that your app's minSdkVersion and targetSdkVersion are compatible with WebView.
- The payment page is loaded in a secure WebView with JavaScript and DOM storage enabled.

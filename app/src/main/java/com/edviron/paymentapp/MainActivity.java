package com.edviron.paymentapp;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String collectId = "6865552948afccc2082e7180"; // Replace with actual ID
    String mode = "production"; // or "production"

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        if (!mode.equals("production") && !mode.equals("sandbox")) {
            Toast.makeText(this, "Invalid Payment Mode", Toast.LENGTH_LONG).show();
            return;
        }

        String baseUrl = mode.equals("production") ?
                "https://pg.edviron.com" :
                "https://dev.pg.edviron.com";

        String paymentUrl = baseUrl + "/collect-sdk-payments?collect_id=" + collectId;

        webView.getSettings().setJavaScriptEnabled(true);

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
        
        // You can also finish activity or trigger intent
    }

    private void onFailure() {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_LONG).show();
    }
}

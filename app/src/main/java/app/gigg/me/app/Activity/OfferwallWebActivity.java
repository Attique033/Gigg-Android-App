package app.gigg.me.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import app.gigg.me.app.R;

public class OfferwallWebActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView mWebView;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerwall_web);

        swipeRefreshLayout = findViewById(R.id.swipresfresh_layout);
        mWebView = findViewById(R.id.webview);

        link = getIntent().getStringExtra("link");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(link);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", url);
                try {
                    if (url.startsWith("tel:") || url.startsWith("whatsapp:") || url.startsWith("intent://")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    } else if (url.startsWith("https://twitter.com")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    } else if (url.startsWith("tg:msg_url")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    } else if (url.startsWith("https://www.facebook.com")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    } else if (url.startsWith("https://youtube.com/") || url.startsWith("https://m.youtube.com/")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    } else if (url.startsWith("market://") || url.startsWith("intent://play.app") || url.startsWith("https://play.google.com")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        mWebView.goBack();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
    }


}
package app.gigg.me.app.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.gigg.me.app.R;


public class UpworkFragment extends Fragment {
    private View view;
    private WebView webView ;
    private ProgressBar progressBar;
    private static String URL = "https://remote.co/remote-jobs/";

    @Override
    public void onResume() {
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);

        webView = view.findViewById(R.id.webView2);
        progressBar = view.findViewById(R.id.progressBar2);
        webView.loadUrl(URL);
        progressBar.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        super.onResume();
    }
    public UpworkFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upwork_job , container , false );

        return view;
    }
}

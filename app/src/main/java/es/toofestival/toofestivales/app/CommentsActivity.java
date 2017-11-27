package es.toofestival.toofestivales.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import es.toofestival.toofestivales.R;

public class CommentsActivity extends AppCompatActivity {

    private WebView webViewComments;
    private ProgressBar progressBar;
    boolean isLoading;
    private String postId;
    private String postUrl;
    private String postTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postId = getIntent().getStringExtra("id");
        postUrl = getIntent().getStringExtra("url");
        postTitle = getIntent().getStringExtra("title");

        webViewComments = (WebView) findViewById(R.id.disqus);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(postTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // finish the activity in case of empty id
        if (TextUtils.isEmpty(postId)) {
            Toast.makeText(getApplicationContext(), "The post id shouldn't be empty", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadComments();
    }

    private void loadComments() {
        WebSettings webSettings2 = webViewComments.getSettings();
        webSettings2.setJavaScriptEnabled(true);
        webSettings2.setBuiltInZoomControls(true);
        webViewComments.requestFocusFromTouch();
        webViewComments.setWebViewClient(new WebViewClient());
        webViewComments.setWebChromeClient(new WebChromeClient());
        webViewComments.loadUrl("http://toofestival.es/showcomments.php?disqus_id=" + postId + "&disqus_url=" + postUrl + "&disqus_title=" + postTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

package es.toofestival.toofestivales.app;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class SearchableActivity extends ListActivity {

    private Toolbar toolbar;
    private ListView listSearch;
    private ProgressDialog progressDialog;
    private ArrayList eventItems;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        //toolbar = (Toolbar) findViewById(R.id.appbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listSearch = getListView();
        Log.d("SEARCH", "HERE");
        handleIntent(getIntent());
    }

    public void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object o = SearchableActivity.this.getListAdapter().getItem(position);
        String pen = o.toString();
        int postID = 343;
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
        intent.putExtra("id", "" + postID);
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {
        Toast.makeText(getApplicationContext(), queryStr, Toast.LENGTH_LONG).show();
        progressDialog = new ProgressDialog(SearchableActivity.this);
        progressDialog.setMessage(getString(R.string.loading_posts));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        String url = Config.POSTS_URL + "?search=" + queryStr;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                List listPosts = (List) gson.fromJson(s, List.class);
                eventItems = new ArrayList();

                if(listPosts!=null) {
                    for (int i = 0; i < listPosts.size(); ++i) {
                        // Get attributes from JSON
                        Map<String, Object> mapPost = (Map<String, Object>) listPosts.get(i);
                        Post p = JSONParser.parsePost(mapPost);
                        // Add Post to List
                        eventItems.add(p.getTitle());
                    }
                }

                listSearch.setAdapter(new ArrayAdapter<ArrayList<Post>>(SearchableActivity.this,
                        android.R.layout.simple_list_item_1, eventItems));


                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SearchableActivity.this, R.string.error_load_posts, Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(SearchableActivity.this);
        rQueue.add(request);
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
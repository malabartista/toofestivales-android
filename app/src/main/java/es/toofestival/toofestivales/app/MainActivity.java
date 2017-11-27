package es.toofestival.toofestivales.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.adapter.EndlessRecyclerViewScrollListener;
import es.toofestival.toofestivales.adapter.Recycler_View_Adapter;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.model.User;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class MainActivity extends AppCompatActivity implements PostFragment.PostListener {

    public static final String POST_FRAGMENT_TAG = "PostFragment";
    public static final String SEARCH_FRAGMENT_TAG = "SearchFragment";
    private FragmentManager fm = null;
    private PostFragment pf;
    private SearchFragment sf;

    private Toolbar appbar;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private EndlessRecyclerViewScrollListener scrollListener;
    private View progress;
    private NavigationView navigationView;

    public static String profileImage = "";
    public static String profileName = "Invitado";
    String searchFilter = "";
    String scopeFilter = "";
    String categoryFilter = "";
    String countryFilter = "";
    List<Object> listPosts;
    List<Object> listAllPosts;
    Gson gson;
    ProgressDialog progressDialog;
    Map<String, Object> mapPost;
    int postID;

    ImageView viewGridList;
    ImageView searchList;

    private List<Post> eventItems = new ArrayList<>();
    RecyclerView recyclerView;
    Recycler_View_Adapter recyclerViewAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RequestQueue rQueue;
    boolean loadingMore = false;

    private Profile profile;
    private User user = new User();
    private AccessTokenTracker accessTokenTracker;
    private CallbackManager callbackManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // App Toolbar
        appbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);
        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        ab.setTitle(""); // hide title
        ab.setHomeAsUpIndicator(R.drawable.ic_nav_menu); // set a custom icon for the default home button
        ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        //ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        // Primary Toolbar
        toolbar = (Toolbar) findViewById(R.id.primarybar);
        //toolbar.inflateMenu(R.menu.menu_primary);

        navigationView = (NavigationView) findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                // Handle navigation view item clicks here.
                switch (menuItem.getItemId()) {
                    case R.id.menu_login: {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.menu_invite: {
                        String appLinkUrl, previewImageUrl;
                        appLinkUrl = "https://fb.me/1564023626954832";
                        previewImageUrl = "http://toofestival.es/media/toofestivales.jpg";

                        if (AppInviteDialog.canShow()) {
                            AppInviteContent content = new AppInviteContent.Builder()
                                    .setApplinkUrl(appLinkUrl)
                                    .setPreviewImageUrl(previewImageUrl)
                                    .build();
                            AppInviteDialog.show(MainActivity.this, content);
                        }
                        break;
                    }
                    case R.id.menu_calendar: {
                        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.menu_map: {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                        break;
                    }
                    /*
                    case R.id.menu_ar: {
                        Intent intent = new Intent(getApplicationContext(), ARPortraitActivity.class);
                        startActivity(intent);
                        break;
                    }*/
                    case R.id.menu_mail: {
                        sendEmail();
                        break;
                    }
                }
                //close navigation drawer
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

        });

        // View Grid list
        /*
        viewGridList = (ImageView) toolbar.findViewById(R.id.btn_view);
        viewGridList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.isProductViewAsList = !Config.isProductViewAsList;
                supportInvalidateOptionsMenu();
                //recyclerView.setLayoutManager(isProductViewAsList ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));
                if (Config.isProductViewAsList) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    viewGridList.setImageResource(R.drawable.ic_view_grid);
                } else {
                    //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    viewGridList.setImageResource(R.drawable.ic_view_list);
                }
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }
        });

        // Search Action
        searchList = (ImageView) toolbar.findViewById(R.id.btn_search);
        searchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSearchRequested();
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        */
        // Swipe Refresh Layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPosts();
            }
        });

        // Set Views
        progress = findViewById(R.id.progress);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //categoriesView = (ExpandableListView) findViewById(R.id.listCategories);
        //countriesView = (ExpandableListView) findViewById(R.id.listCountries);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Login Facebook
        /*
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken != null) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            currentAccessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String userId = object.getString("id");
                                        String imageUrl = new URL("https://graph.facebook.com/" + userId + "/picture?type=large").toString();
                                        MainActivity.profileImage = imageUrl;
                                        MainActivity.profileName = object.getString("name");;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                } else {
                    //write your code here what to do when user clicks on facebook logout
                    MainActivity.profileName = "Invitado";
                    MainActivity.profileImage = "";
                }
            }
        };
        */
        // Search Fragment
        fm = getSupportFragmentManager();
        sf = new SearchFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(android.R.id.content, sf, SEARCH_FRAGMENT_TAG);
        ft.hide(sf);
        ft.commit();

        // Profile
        profile = Profile.getCurrentProfile();
        setProfile(profile);

        // List Posts
        loadPosts();
    }
    public void setProfile(Profile profile){
        NavigationView navigationView = (NavigationView) findViewById(R.id.navview);
        View v = navigationView.getHeaderView(0);
        Menu menuNav = navigationView.getMenu();
        TextView userName = (TextView) v.findViewById(R.id.profile_name);
        ImageView userImage = (ImageView) v.findViewById(R.id.profile_image);
        MenuItem loginItem = menuNav.findItem(R.id.menu_login);
        if(profile!=null) {
            try {
                String userId = profile.getId();
                String imageUrl = new URL("https://graph.facebook.com/" + userId + "/picture?type=large").toString();
                profileImage = imageUrl;
                profileName = profile.getName();
                userName.setText(profileName);
                Glide.with(AppController.getInstance())
                        .load(profileImage)
                        .centerCrop()
                        .into(userImage);
                loginItem.setTitle("Logout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            profileImage = "";
            profileName = "Invitado";
            userName.setText(profileName);
            userImage.setImageResource(R.drawable.ic_user);
            loginItem.setTitle("Login");
        }
    }

    public void setScopeFilter(String selected) {
        scopeFilter = selected;
    }
    public void setSearchFilter(String selected) {
        searchFilter = selected;
    }
    public void setCountryFilter(String selected) {
        countryFilter = selected;
    }
    public void setCategoryFilter(String selected) {
        categoryFilter = selected;
    }

    public String setFilters(String url){
        if (!scopeFilter.isEmpty()) {
            url += "?scope=" + scopeFilter;
        } else {
            url += "?scope=future";
        }
        if (!categoryFilter.isEmpty()) {
            url += "&category=" + categoryFilter;
        }
        if (!countryFilter.isEmpty()) {
            url += "&country=" + countryFilter;
        }
        if (!searchFilter.isEmpty()) {
            url += "&search=" + searchFilter;
        }
        return url;
    }
    /*
     *  Count Total Posts From WP REST API
     */
    private void countTotalPosts() {
        String url = Config.COUNT_POSTS_URL;
        url = setFilters(url);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                int serverListSize = Integer.valueOf(s.replace("\"",""));
                recyclerViewAdapter.setServerListSize(serverListSize);
                //Toast.makeText(MainActivity.this, s.replace("\"", ""), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }
    /*
     *  Load Posts From WP REST API
     */
    public void loadPosts() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.loading_posts));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        int offset = 1;

        String url = Config.POSTS_URL;
        url = setFilters(url);
        if (Config.POSTS_ORDERBY == "popularity") {
            url += "&orderby=popularity";
        }
        StringRequest request = new StringRequest(Request.Method.GET, url + "&page=" + offset + "&per_page=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                listPosts = (List) gson.fromJson(s, List.class);
                listAllPosts = listPosts;
                eventItems = new ArrayList<>();

                if(listPosts!=null) {
                    for (int i = 0; i < listPosts.size(); ++i) {
                        // Get attributes from JSON
                        mapPost = (Map<String, Object>) listPosts.get(i);
                        Post p = JSONParser.parsePost(mapPost);
                        // Add Post to List
                        eventItems.add(p);
                    }

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                    itemAnimator.setAddDuration(1000);
                    itemAnimator.setRemoveDuration(1000);
                    recyclerView.setItemAnimator(itemAnimator);
                    recyclerViewAdapter = new Recycler_View_Adapter(MainActivity.this, eventItems,MainActivity.this){
                        @Override
                        public View getDataRow(int position, View convertView, ViewGroup parent){
                            return convertView;
                        };
                    };
                    countTotalPosts();
                    recyclerView.setAdapter(recyclerViewAdapter);
                    scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            loadMorePosts(page + 1, totalItemsCount);
                        }
                    };
                    recyclerView.addOnScrollListener(scrollListener);
                    progressDialog.dismiss();
                    // Parar la animación del indicador
                    mSwipeRefreshLayout.setRefreshing(false);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerViewAdapter = new Recycler_View_Adapter(MainActivity.this, eventItems,MainActivity.this){
                        @Override
                        public View getDataRow(int position, View convertView, ViewGroup parent){
                            return convertView;
                        };
                    };
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    Toast.makeText(MainActivity.this, "No festivals", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    progress = findViewById(R.id.progress);
                    if(progress!=null)
                        progress.setVisibility(View.GONE);
                }

                FragmentTransaction ft = fm.beginTransaction();
                ft.hide(sf);
                ft.commit();
                /*
                progressDialog.dismiss();
                // Parar la animación del indicador
                mSwipeRefreshLayout.setRefreshing(false);
                drawerLayout.closeDrawer(Gravity.LEFT);
                */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, R.string.error_load_posts, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    // Append more data into the adapter
    public void loadMorePosts(int offset, final int position) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        // Set PostActivity List
        //postsView = (ListView)findViewById(R.id.postList);

        recyclerViewAdapter.getView(position, recyclerView, (ViewGroup) findViewById(android.R.id.content));

        String url = Config.POSTS_URL;
        url = setFilters(url);
        if (Config.POSTS_ORDERBY == "popularity") {
            url += "&orderby=popularity";
        }
        StringRequest request = new StringRequest(Request.Method.GET, url + "&page=" + offset + "&per_page=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                listPosts = (List) gson.fromJson(s, List.class);
                if (listPosts != null) {
                    for (int i = 0; i < listPosts.size(); ++i) {
                        listAllPosts.add(listPosts.get(i));
                        // Get attributes from JSON
                        mapPost = (Map<String, Object>) listPosts.get(i);
                        Post p = JSONParser.parsePost(mapPost);
                        // Add Post to List
                        eventItems.add(p);
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    //recyclerViewAdapter.notifyItemRemoved(position);
                    //recyclerViewAdapter.notifyItemRangeChanged(position, listPosts.size());
                }else{
                    Toast.makeText(MainActivity.this, R.string.no_more_posts, Toast.LENGTH_LONG).show();
                    progress = findViewById(R.id.progress);
                    progress.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, R.string.error_load_more_posts, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    protected void sendEmail() {
        String[] TO = {"toofestival.es@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TooFestival.es App - ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hola TooFestival.es:");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            //Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public void onPostSelected(Post post, boolean isSearch) {
        // Find the fragment in order to set it up later
        pf = (PostFragment) getSupportFragmentManager().findFragmentByTag(POST_FRAGMENT_TAG);

        // Set necessary arguments
        Bundle args = new Bundle();
        args.putInt("id", post.getId());
        args.putString("title", post.getTitle());
        args.putString("date", post.getDate());
        StringBuilder sb = new StringBuilder();
        String sc = "";
        ArrayList categ = post.getCategories();
        for (int i = 0; i< categ.size();i++)
        {   sc = (String) categ.get(i);
            sb.append(sc);
            if(i+1<categ.size())
                sb.append(", ");
        }
        args.putStri /*
    public void onPostSelected(Post post, boolean isSearch) {
        // Find the fragment in order to set it up later
        pf = (PostFragment) getSupportFragmentManager().findFragmentByTag(POST_FRAGMENT_TAG);

        // Set necessary arguments
        Bundle args = new Bundle();
        args.putInt("id", post.getId());
        args.putString("title", post.getTitle());
        args.putString("date", post.getDate());
        StringBuilder sb = new StringBuilder();
        String sc = "";
        ArrayList categ = post.getCategories();
        for (int i = 0; i< categ.size();i++)
        {   sc = (String) categ.get(i);
            sb.append(sc);
            if(i+1<categ.size())
                sb.append(", ");
        }
        args.putString("categories", sb.toString());
        args.putString("location_town", post.getLocation_town());
        args.putString("location_lat", post.getLocation_lat());
        args.putString("location_long", post.getLocation_long());
        String urlOfficial = "<a href='" + post.getUrlOficial() + "'>Web Oficial</a>";
        args.putString("urlOfficial", urlOfficial);
        //args.putString("thumbnailUrl", post.getThumbnailUrl());
        args.putString("featuredImage", post.getFeaturedImageUrl());
        args.putString("poster", post.getPoster());
        args.putString("videoId", post.getVideo());

        // Configure PostFragment to display the right post
        pf.setUIArguments(args);

        // Show the fragment
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.show(pf);
        ft.addToBackStack(null);
        ft.commit();
    }
    */

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TooFestival.es") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://toofestival.es"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onCommentSelected(int id) {

    }

    @Override
    public void onHomePressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //toolbar menu items CallBack listener
        /*
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                switch(arg0.getItemId()) {
                    case R.id.action_search:

                        break;
                    case R.id.action_view:
                        Config.isProductViewAsList = !Config.isProductViewAsList;
                        supportInvalidateOptionsMenu();
                        //recyclerView.setLayoutManager(isProductViewAsList ? new LinearLayoutManager(this) : new GridLayoutManager(this, 2));
                        if(Config.isProductViewAsList){
                            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            arg0.setIcon(R.drawable.ic_view_grid);
                        }else{
                            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                            arg0.setIcon(R.drawable.ic_view_list);
                        }
                        recyclerView.setAdapter(recyclerViewAdapter);
                        recyclerViewAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            /*
            case R.id.login:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                */
            case R.id.search_button:
                sf = (SearchFragment) getSupportFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                // Show the fragment
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                ft.show(sf);
                ft.addToBackStack(null);
                ft.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void positionAction(View view) {
        int position = (int) view.getTag();
        //Toast.makeText(view.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();
        mapPost = (Map<String, Object>) listAllPosts.get(position);
        postID = Integer.parseInt((String) mapPost.get("event_id"));
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
        intent.putExtra("id", "" + postID);
        startActivity(intent);
    }

    // Radio buttons
    public void onToggle(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.btn_date:
                if (checked)
                    Config.POSTS_ORDERBY = "date";
                break;
            case R.id.btn_popularity:
                if (checked)
                    Config.POSTS_ORDERBY = "popularity";
                break;
        }
        loadPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((progressDialog != null) && progressDialog.isShowing())
            progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        profile = Profile.getCurrentProfile();
        setProfile(profile);
    }

}

package es.toofestival.toofestivales.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;
import es.toofestival.toofestivales.util.JsonHelper;

public class PostActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private Toolbar toolbar;
    TextView title;
    TextView date;
    TextView categories;
    TextView location;
    TextView website;
    Button btn_tickets;
    ImageView poster;
    ImageView clickToMove;
    ImageView transparent;
    private ImageView playVideoButton;
    private FloatingActionButton fab;
    NestedScrollView scroll;
    ProgressDialog progressDialog;
    boolean moveMap = false;
    GoogleMap mapGlobal;
    Double location_lat;
    Double location_long;
    String titleInfoWindow = "";
    String url = "";
    String urlOfficial = "";
    String urlTickets = "";
    Map<String, Object> mapPost;
    private static final String TAG = "PostActivity";
    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    private ShareActionProvider shareActionProvider;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        final String id = getIntent().getExtras().getString("id");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        categories = (TextView) findViewById(R.id.categories);
        location = (TextView) findViewById(R.id.location);
        website = (TextView) findViewById(R.id.website);
        btn_tickets = (Button) findViewById(R.id.btn_tickets);
        btn_tickets.setVisibility(View.INVISIBLE);
        poster = (ImageView) findViewById(R.id.poster);
        clickToMove = (ImageView) findViewById(R.id.click_to_move);
        transparent = (ImageView)findViewById(R.id.imagetrans);
        scroll = (NestedScrollView) findViewById(R.id.scroll);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        playVideoButton = (ImageView) findViewById(R.id.youtube_thumbnail);
        playVideoButton.setOnClickListener(PostActivity.this);

        progressDialog = new ProgressDialog(PostActivity.this);
        progressDialog.setMessage(getString(R.string.loading_post));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        String urlEvent = Config.POSTS_URL + id;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlEvent, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                mapPost = JsonHelper.toMap((JSONObject) jsonArray.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // parse JSON Event
                        final Post p = JSONParser.parsePost(mapPost);

                        // Setting Image And Youtube Video
                        ImageView featuredImageView = (ImageView) findViewById(R.id.featuredImage);
                        Glide.with(getApplicationContext())
                                .load(p.getFeaturedImageUrl())
                                .centerCrop()
                                .into(featuredImageView);
                        playVideoButton.setTag(p.getVideo());

                        // Set Title
                        title.setText(p.getTitle());
                        getSupportActionBar().setTitle(p.getTitle());
                        titleInfoWindow = p.getTitle();
                        //collapsingToolbarLayout.setTitle(p.getTitle());

                        // date
                        date.setText(p.getDate());

                        // location
                        location.setText(p.getLocation_town()+ ", " + p.getLocation_country());

                        // categories
                        StringBuilder sb = new StringBuilder();
                        String sc = "";
                        ArrayList categ = p.getCategories();
                        for (int i = 0; i< categ.size();i++)
                        {   sc = (String) categ.get(i);
                            sb.append(sc);
                            if(i+1<categ.size())
                                sb.append(", ");
                        }
                        categories.setText(sb.toString());

                        // weblink
                        url = p.getUrl();
                        urlOfficial = p.getUrlOficial();
                        String str_links = "<a href='" + urlOfficial + "'>Web Oficial</a>";
                        website.setLinksClickable(true);
                        website.setMovementMethod(LinkMovementMethod.getInstance());
                        website.setText( Html.fromHtml( str_links ) );

                        // tickets
                        urlTickets = p.getUrlTickets();
                        if(urlTickets!=null && !urlTickets.isEmpty()){
                            btn_tickets.setVisibility(View.VISIBLE);
                        }

                        // poster
                        //poster.setImageResource(R.drawable.festival);
                        Glide.with(PostActivity.this)
                                .load(p.getPoster())
                                .fitCenter()
                                .into(poster);

                        // posters
                        if(p.getPosters()!=null) {
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("postersList", p.getPosters());
                            GalleryFragment gallery = new GalleryFragment();
                            gallery.setArguments(bundle);

                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.fragment_gallery, gallery, "postersList")
                                    .commit();
                        }

                        // Map Location
                        location_lat = Double.parseDouble(p.getLocation_lat());
                        location_long = Double.parseDouble(p.getLocation_long());
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        if(mapFragment!=null)
                            mapFragment.getMapAsync(PostActivity.this);

                        // Share the article URL
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, titleInfoWindow + "\n" + url);
                        shareActionProvider.setShareIntent(i);

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // launching facebook comments activity
                                Intent intent = new Intent(PostActivity.this, CommentsActivity.class);

                                // passing the article url
                                intent.putExtra("id", String.valueOf(p.getPostId()));
                                intent.putExtra("url", p.getUrl());
                                intent.putExtra("title", p.getTitle());
                                startActivity(intent);
                            }
                        });
                        // progress dialog out
                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(PostActivity.this, R.string.error_load_post, Toast.LENGTH_LONG).show();
            }
        });


        RequestQueue rQueue = Volley.newRequestQueue(PostActivity.this);
        rQueue.add(request);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu_post, menu);
        // Get share menu item
        MenuItem item = menu.findItem(R.id.action_share);
        // Initialise ShareActionProvider
        // Use MenuItemCompat.getActionProvider(item) since we are using AppCompat support library
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*
            case R.id.action_comments:
                mListener.onCommentSelected(id);
                return true;
                */
            case R.id.action_share:
                return true;
            /*
            case R.id.action_send_to_wear:
                //sendToWear();
                return true;
            */
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Post Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://toofestival.es"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface PostListener {
        void onCommentSelected(int id);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == playVideoButton) {
            intent = YouTubeStandalonePlayer.createVideoIntent(
                    this, Config.YOUTUBE_API_KEY, (String) v.getTag());
        }

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }

    public void goToTickets (View view) {
        goToUrl (urlTickets);
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng locationMapMarker = new LatLng(location_lat, location_long);
        float MAP_ZOOM_MAX = map.getMaxZoomLevel();
        float MAP_ZOOM_MIN = map.getMinZoomLevel();
        map.addMarker(new MarkerOptions().position(locationMapMarker).title(String.valueOf(titleInfoWindow)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationMapMarker,12.0f));
        map.getUiSettings().setAllGesturesEnabled(moveMap);
        mapGlobal = map;
    }

    public void setMapUiSettings(View v){
        if(moveMap==false) {
            clickToMove.setImageResource(R.drawable.ic_click_to_move);
            mapGlobal.getUiSettings().setAllGesturesEnabled(true);
            moveMap = true;
            transparent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            scroll.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            scroll.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            scroll.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
            });
        }else{
            clickToMove.setImageResource(R.drawable.ic_click_to_move_red);
            mapGlobal.getUiSettings().setAllGesturesEnabled(false);
            moveMap = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public void onDestroy() {
        super.onDestroy();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

}
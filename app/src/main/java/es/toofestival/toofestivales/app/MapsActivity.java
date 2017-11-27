package es.toofestival.toofestivales.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.MyItem;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<MyItem>, ClusterManager.OnClusterInfoWindowClickListener<MyItem>, ClusterManager.OnClusterItemClickListener<MyItem>, ClusterManager.OnClusterItemInfoWindowClickListener<MyItem> {

    private Toolbar toolbar;
    private GoogleMap mMap;
    List<Object> listPosts;
    Gson gson;
    Map<String, Object> mapPost;
    int postID;

    RequestQueue rQueue;
    private ClusterManager<MyItem> mClusterManager;
    private View progress;
    private View imageTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        /*
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // hide title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        */
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mClusterManager = new ClusterManager<MyItem>(MapsActivity.this, mMap);
        mClusterManager.setRenderer(new MyClusterRenderer());
        mClusterManager.setOnClusterClickListener(MapsActivity.this);
        mClusterManager.setOnClusterInfoWindowClickListener(MapsActivity.this);
        mClusterManager.setOnClusterItemClickListener(MapsActivity.this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(MapsActivity.this);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);

        loadMapPosts();

        // Add a marker in Sydney and move the camera
        LatLng madrid = new LatLng(40.416913, -3.703577);
        //mMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(madrid));
    }

    /*
     *  Load Posts From WP REST API
     */
    public void loadMapPosts() {

        int offset = 1;
        String url = Config.POSTS_URL;
        url += "?scope=future";
        StringRequest request = new StringRequest(Request.Method.GET, url + "&page=" + offset + "&per_page=200", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                List<MyItem> items = new ArrayList<MyItem>();
                listPosts = (List) gson.fromJson(s, List.class);

                if(listPosts!=null) {
                    for (int i = 0; i < listPosts.size(); ++i) {
                        // Get attributes from JSON
                        mapPost = (Map<String, Object>) listPosts.get(i);
                        Post p = JSONParser.parsePost(mapPost);

                        String title = null;
                        String snippet = null;
                        double lat = Double.parseDouble(p.getLocation_lat());
                        double lng = Double.parseDouble(p.getLocation_long());
                        if (!p.getTitle().isEmpty()) {
                            title = p.getTitle();
                        }
                        if (!p.getFeaturedImageUrl().isEmpty()) {
                            snippet = p.getFeaturedImageUrl();
                        }
                        items.add(new MyItem(lat, lng, title, snippet, p.getId()));
                    }
                    mClusterManager.addItems(items);
                    mClusterManager.cluster();
                    imageTrans = findViewById(R.id.imagetrans);
                    if(imageTrans!=null)
                        imageTrans.setVisibility(View.GONE);
                    progress = findViewById(R.id.progress);
                    if(progress!=null)
                        progress.setVisibility(View.GONE);
                } else {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MapsActivity.this, R.string.error_load_posts, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(MapsActivity.this);
        rQueue.add(request);
    }

    public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

        public MyClusterRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item,
                                                   MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions.title(String.valueOf(item.getTitle()));
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem,
                                             Marker marker) {
            super.onClusterItemRendered(clusterItem, marker);
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MyItem> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().getTitle();
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyItem> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyItem item) {
        // Does nothing, but you could go into the user's profile page, for example.
        postID = item.getId();
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
        intent.putExtra("id", "" + postID);
        startActivity(intent);
    }

}

package es.toofestival.toofestivales.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;


public class PostFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    private Toolbar toolbar;
    private static final String TAG = "PostActivity";
    TextView title;
    TextView date;
    TextView categories;
    TextView location;
    TextView website;
    ImageView poster;
    ImageView featuredImage;
    MapView mapView;
    GoogleMap map;
    ImageView clickToMove;
    ImageView transparent;
    ScrollView scroll;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String titleInfoWindow = "";
    boolean moveMap = false;
    Double location_lat;
    Double location_long;
    private PostListener mListener;
    String url = "";
    String urlOfficial = "";
    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;
    String videoID = "";
    private ImageView playVideoButton;
    GoogleMap mapGlobal;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_post, container, false);


        //nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);


        // The following two layouts are needed to expand the collapsed Toolbar
        //appBarLayout = (AppBarLayout) findViewById(R.id.appbarLayout);
        //coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        playVideoButton = (ImageView) rootView.findViewById(R.id.youtube_thumbnail);
        playVideoButton.setOnClickListener(this);

        clickToMove = (ImageView) rootView.findViewById(R.id.click_to_move);

        //final String id = getIntent().getExtras().getString("id");
        title = (TextView) rootView.findViewById(R.id.title);
        date = (TextView) rootView.findViewById(R.id.date);
        categories = (TextView) rootView.findViewById(R.id.categories);
        location = (TextView) rootView.findViewById(R.id.location);
        website = (TextView) rootView.findViewById(R.id.website);
        poster = (ImageView) rootView.findViewById(R.id.poster);
        featuredImage = (ImageView) rootView.findViewById(R.id.featuredImage);
        MapsInitializer.initialize(getActivity());
        mapView = (MapView) rootView.findViewById(R.id.map);
        scroll = (ScrollView) rootView.findViewById(R.id.scroll);
        transparent = (ImageView) rootView.findViewById(R.id.imagetrans);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();
        return rootView;
    }

    /**
     * Since we can't call setArguments() on an existing fragment, we make our own!
     *
     * @param args Bundle containing information about the new post
     */
    public void setUIArguments(final Bundle args) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // Clear the content first
                //id = args.getInt("id");
                title.setText(args.getString("title"));
                date.setText(args.getString("date"));
                location.setText(args.getString("location_town"));
                categories.setText(args.getString("categories"));
                website.setLinksClickable(true);
                website.setMovementMethod(LinkMovementMethod.getInstance());
                website.setText( Html.fromHtml( args.getString("urlOfficial") ) );

                //featuredImage.setImageUrl(args.getString("featuredImage"),imageLoader);
                playVideoButton.setTag(args.getString("videoId"));
                Glide.with(getActivity())
                        .load(args.getString("featuredImage"))
                        .centerCrop()
                        .into(featuredImage);

                Glide.with(getActivity())
                        .load(args.getString("poster"))
                        .centerCrop()
                        .into(poster);
                location_lat = Double.parseDouble(args.getString("location_lat"));
                location_long = Double.parseDouble(args.getString("location_long"));

                // Map Location
                /*
                SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                if(mapFragment!=null)
                    mapFragment.getMapAsync((OnMapReadyCallback) getActivity());
                map = mapView.getMap();
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMyLocationEnabled(true);

                // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
                MapsInitializer.initialize(getActivity());

                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location_lat, location_long), 10);
                map.animateCamera(cameraUpdate);
                */
                // Gets to GoogleMap from the MapView and does initialization stuff
                //mapView.getMapAsync((OnMapReadyCallback) getActivity());

                // Reset Actionbar
                ((MainActivity) getActivity()).setSupportActionBar(toolbar);
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(args.getString("title"));
                ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.menu_post, menu);
        // Get share menu item
        MenuItem item = menu.findItem(R.id.action_share);
        // Initialise ShareActionProvider
        // Use MenuItemCompat.getActionProvider(item) since we are using AppCompat support library
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        // Share the article URL
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, titleInfoWindow + "\n" + url);
        shareActionProvider.setShareIntent(i);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_comments:
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
                mListener.onHomePressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PostListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement PostListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onHomePressed();
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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == playVideoButton) {
            intent = YouTubeStandalonePlayer.createVideoIntent(
                    getActivity(), Config.YOUTUBE_API_KEY, (String) v.getTag());
        }

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(getActivity(), REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getActivity().getPackageManager().queryIntentActivities(intent, 0);
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
        if (mapView != null) {
            mapView.onPause();
        }
        /*
        try {
            Class.forName("android.webkit.WebView")
                    .getMethod("onPause", (Class[]) null)
                    .invoke(webView, (Object[]) null);

        } catch (ClassNotFoundException cnfe) {

        } catch (NoSuchMethodException nsme) {

        } catch (InvocationTargetException ite) {

        } catch (IllegalAccessException iae) {

        }
        */
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
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    // Interface used to communicate with MainActivity
    public interface PostListListener {
        void onPostSelected(Post post, boolean isSearch);
    }

}
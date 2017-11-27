package es.toofestival.toofestivales.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.adapter.Recycler_View_Adapter;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private Toolbar toolbar;
    MaterialCalendarView calendarView;
    List<Object> listPosts;
    List<Object> listAllPosts;
    Map<String, Object> mapPost;

    private List<Post> eventItems = new ArrayList<>();
    RecyclerView recyclerView;
    Recycler_View_Adapter recyclerViewAdapter;
    RequestQueue rQueue;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    TextView textView;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // hide title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        textView = (TextView) findViewById(R.id.textView);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView); // get the reference of CalendarView
        // perform setOnDateChangeListener event on CalendarView
        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);
        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .commit();
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

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        textView.setText(getSelectedDatesString());
        loadPostsByDate();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        //getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    public void positionAction(View view) {
        int position = (int) view.getTag();
        //Toast.makeText(view.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();
        mapPost = (Map<String, Object>) listAllPosts.get(position);
        int postID = Integer.parseInt((String) mapPost.get("event_id"));
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
        intent.putExtra("id", "" + postID);
        startActivity(intent);
    }

    /*
     *  Load Posts From WP REST API
     */
    public void loadPostsByDate() {
        int offset = 1;
        String url = Config.POSTS_URL;
        url += "?date=" + getSelectedDatesString().replace("/","-");
        StringRequest request = new StringRequest(Request.Method.GET, url + "&page=" + offset + "&per_page=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
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

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CalendarActivity.this);
                    linearLayoutManager.setAutoMeasureEnabled(true);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
                    itemAnimator.setAddDuration(1000);
                    itemAnimator.setRemoveDuration(1000);
                    recyclerView.setItemAnimator(itemAnimator);
                    recyclerView.setNestedScrollingEnabled(false);
                    recyclerView.setHasFixedSize(false);
                    recyclerViewAdapter = new Recycler_View_Adapter(CalendarActivity.this, eventItems,CalendarActivity.this){
                        @Override
                        public View getDataRow(int position, View convertView, ViewGroup parent){
                            return convertView;
                        };
                    };
                    recyclerView.setAdapter(recyclerViewAdapter);
                    progress = findViewById(R.id.progress);
                    if(progress!=null)
                        progress.setVisibility(View.GONE);
                } else {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CalendarActivity.this);
                    recyclerViewAdapter = new Recycler_View_Adapter(CalendarActivity.this, eventItems,CalendarActivity.this){
                        @Override
                        public View getDataRow(int position, View convertView, ViewGroup parent){
                            return convertView;
                        };
                    };
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    progress = findViewById(R.id.progress);
                    if(progress!=null)
                        progress.setVisibility(View.GONE);
                    Toast.makeText(CalendarActivity.this, R.string.no_posts, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(CalendarActivity.this, R.string.error_load_posts, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(CalendarActivity.this);
        rQueue.add(request);
    }
}
package es.toofestival.toofestivales.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.adapter.SimpleDividerItemDecoration;
import es.toofestival.toofestivales.model.Post;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    // List of all dictionary words
    private List<Post> dictionaryWords;
    private List<Post> filteredList;
    private List<Post> listViewItems = new ArrayList<Post>();
    private ProgressDialog progressDialog;
    // RecycleView adapter object
    private SimpleItemRecyclerViewAdapter mAdapter;
    // Search edit box
    private EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getListItemData();
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

    // create a custom RecycleViewAdapter class
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> implements Filterable {
        private List<Post> mValues;
        private CustomFilter mFilter;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        public SimpleItemRecyclerViewAdapter(List<Post> items) {
            mValues = items;
            mFilter = new CustomFilter(SimpleItemRecyclerViewAdapter.this);
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mLocationView.setText(String.valueOf(mValues.get(position).getLocation_town()));
            holder.mTitleView.setText(mValues.get(position).getTitle());
            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
            holder.mImageView.setImageUrl(mValues.get(position).getThumbnailUrl(),imageLoader);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mLocationView;
            public final TextView mTitleView;
            public final NetworkImageView mImageView;
            public Post mItem;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (NetworkImageView) view.findViewById(R.id.thumbnail);
                mTitleView = (TextView) view.findViewById(R.id.title);
                mLocationView = (TextView) view.findViewById(R.id.location);

            }
            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }

        public class CustomFilter extends Filter {
            private SimpleItemRecyclerViewAdapter mAdapter;
            private CustomFilter(SimpleItemRecyclerViewAdapter mAdapter) {
                super();
                this.mAdapter = mAdapter;
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                final FilterResults results = new FilterResults();
                if (constraint.length() == 0) {
                    filteredList.addAll(listViewItems);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();
                    for (final Post mWords : listViewItems) {
                        if (mWords.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(mWords);
                        }
                    }
                }
                System.out.println("Count Number " + filteredList.size());
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    System.out.println("Results FOUND");
                    notifyDataSetChanged();
                } else {
                    System.out.println("Count Number 2 " + ((List<Post>) results.values).size());
                    this.mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
                return false;
            }
        };
        private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        };

        private ItemClickSupport(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        public static ItemClickSupport addTo(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ItemClickSupport(view);
            }
            return support;
        }

        public static ItemClickSupport removeFrom(RecyclerView view) {
            ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        public interface OnItemClickListener {

            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        public interface OnItemLongClickListener {

            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }
    }

    private List<Post> getListItemData(){
        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setMessage(getString(R.string.loading_posts));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Config.POSTS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                List listPosts = (List) gson.fromJson(s, List.class);
                for (int i = 0; i < listPosts.size(); ++i) {
                    // Get attributes from JSON
                    Map<String, Object> mapPost = (Map<String, Object>) listPosts.get(i);
                    Post p = JSONParser.parsePost(mapPost);
                    // Add Post to List
                    listViewItems.add(p);
                }
                progressDialog.dismiss();
                // Do It After get Events Data
                filteredList = new ArrayList<Post>();
                filteredList.addAll(listViewItems);
                searchBox = (EditText)findViewById(R.id.search_box);
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.item_list);
                recyclerView.addItemDecoration(new SimpleDividerItemDecoration(SearchActivity.this));
                assert recyclerView != null;
                mAdapter = new SimpleItemRecyclerViewAdapter(filteredList);
                recyclerView.setAdapter(mAdapter);
                ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        Post mapPost = (Post) filteredList.get(position);
                        int postID =  mapPost.getId();
                        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                        intent.putExtra("id", "" + postID);
                        startActivity(intent);
                    }
                });
                // search suggestions using the edittext widget
                searchBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mAdapter.getFilter().filter(s.toString());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(SearchActivity.this, R.string.error_load_posts, Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(SearchActivity.this);
        rQueue.add(request);
        return listViewItems;
    }
}

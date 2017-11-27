package es.toofestival.toofestivales.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import es.toofestival.toofestivales.R;
import es.toofestival.toofestivales.adapter.CategoriesAdapter;
import es.toofestival.toofestivales.adapter.CountriesAdapter;
import es.toofestival.toofestivales.model.Category;
import es.toofestival.toofestivales.model.Country;
import es.toofestival.toofestivales.model.Scope;
import es.toofestival.toofestivales.util.Config;
import es.toofestival.toofestivales.util.JSONParser;

public class SearchFragment extends Fragment {
    private Spinner spinner1, spinner2, spinner3;
    private Button btnSubmit, btnReset;
    private EditText searchText;
    String searchFilter = "";
    ArrayList<Country> listCountries;
    String countryFilter = "";
    ArrayList<Category> listCategories;
    String categoryFilter = "";
    Gson gson;
    RequestQueue rQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_layout, container, false);
        searchText = (EditText)rootView.findViewById(R.id.search);
        searchText.setFocusableInTouchMode(true);
        searchText.requestFocus();
        spinner1 = (Spinner)rootView.findViewById(R.id.spinner1);
        spinner2 = (Spinner)rootView.findViewById(R.id.spinner2);
        spinner3 = (Spinner)rootView.findViewById(R.id.spinner3);
        btnSubmit = (Button)rootView.findViewById(R.id.btnSubmit);
        btnReset = (Button)rootView.findViewById(R.id.btnReset);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFilter = searchText.getText().toString();
                ((MainActivity)getActivity()).setSearchFilter(searchFilter);
                ((MainActivity)getActivity()).loadPosts();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText.setText("");
                ((MainActivity)getActivity()).setSearchFilter("");
                spinner1.setSelection(0);
                ((MainActivity)getActivity()).setCountryFilter("");
                spinner2.setSelection(0);
                ((MainActivity)getActivity()).setCategoryFilter("");
                spinner3.setSelection(0);
                ((MainActivity)getActivity()).setScopeFilter("");
            }
        });
        List<Scope> listScope = new ArrayList<Scope>();
        Scope scope = new Scope();
        scope.setName(getString(R.string.all_events));
        scope.setValue("future");
        listScope.add(scope);
        scope = new Scope();
        scope.setName(getString(R.string.today));
        scope.setValue("today");
        listScope.add(scope);
        scope = new Scope();
        scope.setName(getString(R.string.tomorrow));
        scope.setValue("tomorrow");
        listScope.add(scope);
        scope = new Scope();
        scope.setName(getString(R.string.week));
        scope.setValue("week");
        listScope.add(scope);
        scope = new Scope();
        scope.setName(getString(R.string.month));
        scope.setValue("month");
        listScope.add(scope);
        ArrayAdapter<Scope> spinnerAdapter = new ArrayAdapter<Scope>(((MainActivity)getActivity()),android.R.layout.simple_spinner_dropdown_item,listScope){
            @Override
            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                // TODO Auto-generated method stub
                Scope scope = getItem(position);
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                text.setText(scope.getName());
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                Scope scope = getItem(position);
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                text.setText(scope.getName());
                return view;
            }
        };

        spinner3.setAdapter(spinnerAdapter);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Scope scope = (Scope)spinner3.getSelectedItem();
                String scopeFilter = scope.getValue();
                ((MainActivity)getActivity()).setScopeFilter(scopeFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        getCountries();
        getCategories();
        // Inflate the layout for this fragment
        return rootView;
    }

    /*
     *  Get Countries From Rest Countries API
     */
    public void getCountries() {
        StringRequest request = new StringRequest(Request.Method.GET, Config.COUNTRIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                JsonArray countries = (JsonArray) gson.fromJson(s, JsonArray.class);
                listCountries = JSONParser.parseCountries(countries);
                Country c = new Country();
                c.setCode("");
                c.setName(getString(R.string.country_prompt));
                c.setFlag("https://art-decor.org/mediawiki/images/a/a4/Flag_eu.svg");
                listCountries.add(0,c);
                CountriesAdapter adapter = new CountriesAdapter(((MainActivity)getActivity()), R.layout.listitems_layout, R.id.title, listCountries);
                spinner1.setAdapter(adapter);

                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Country c=(Country) spinner1.getSelectedItem();
                        countryFilter = c.getCode().replace("\"","");
                        ((MainActivity)getActivity()).setCountryFilter(countryFilter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(((MainActivity)getActivity()), R.string.error_load_countries, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(((MainActivity)getActivity()));
        rQueue.add(request);
    }

    /*
     *  Get Categories From WP REST API
     */
    private void getCategories() {
        StringRequest request = new StringRequest(Request.Method.GET, Config.CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                JsonObject categories = (JsonObject) gson.fromJson(s, JsonObject.class);
                listCategories = JSONParser.parseCategories(categories);

                Category c = new Category();
                c.setName(getString(R.string.category_prompt));
                c.setImage("http://toofestival.es/media/2014/01/Festivales.jpg");
                c.setId(0);
                listCategories.add(0,c);
                CategoriesAdapter adapter = new CategoriesAdapter(((MainActivity)getActivity()), R.layout.listitems_layout, R.id.title, listCategories);
                spinner2.setAdapter(adapter);
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Category c=(Category) spinner2.getSelectedItem();
                        categoryFilter = Integer.toString(c.getId());
                        ((MainActivity)getActivity()).setCategoryFilter(categoryFilter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(((MainActivity)getActivity()), R.string.error_load_categories, Toast.LENGTH_LONG).show();
            }
        });
        rQueue = Volley.newRequestQueue(((MainActivity)getActivity()));
        rQueue.add(request);
    }

    /*
    public void loadCountries() {
        pDialogCountries = new ProgressDialog(MainActivity.this);
        pDialogCountries.setMessage(getString(R.string.loading_countries));
        pDialogCountries.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialogCountries.show();
        StringRequest request = new StringRequest(Request.Method.GET, Config.COUNTRIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                JsonArray countries = (JsonArray) gson.fromJson(s, JsonArray.class);
                listCountries = JSONParser.parseCountries(countries);
                listCountriesDetail.put(getString(R.string.countries), listCountries);

                countriesListTitle = new ArrayList<String>();
                countriesListTitle.add(getString(R.string.countries));

                Country c = new Country();
                c.setCode("");
                c.setName(getString(R.string.country_prompt));
                c.setFlag("https://art-decor.org/mediawiki/images/a/a4/Flag_eu.svg");
                listCountries.add(0,c);
                CountriesAdapter adapter = new CountriesAdapter(MainActivity.this, R.layout.listitems_layout, R.id.title, listCountries);
                spinner1.setAdapter(adapter);

                spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Country c=(Country) spinner1.getSelectedItem();
                        countriesFilter = new ArrayList();
                        countriesFilter.add(c.getCode().replace("\"",""));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });


                countriesAdapter = new ExpandableListCountriesAdapter(MainActivity.this, countriesListTitle, listCountriesDetail);
                countriesView.setAdapter(countriesAdapter);
                countriesView.setChildIndicator(null);
                countriesView.setChildDivider(getResources().getDrawable(R.color.color_white));
                countriesView.setDivider(getResources().getDrawable(R.color.color_white));
                countriesView.setDividerHeight(10);
                countriesView.setPadding(30,0,0,0);
                countriesView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                countriesListTitle.get(groupPosition) + " List Expanded.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                countriesView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                countriesListTitle.get(groupPosition) + " List Collapsed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });

                countriesView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        CheckedTextView checkbox = (CheckedTextView) v.findViewById(R.id.country_name);
                        checkbox.toggle();
                        StringBuffer responseText = new StringBuffer();
                        responseText.append("The following countries were selected...\n");
                        int len = countriesAdapter.getChildrenCount(groupPosition);
                        boolean isChecked = ((CheckedTextView) v).isChecked();
                        if (isChecked) {
                            ((Country) countriesAdapter.getChild(groupPosition, childPosition)).setSelected("1");
                        } else {
                            ((Country) countriesAdapter.getChild(groupPosition, childPosition)).setSelected("0");
                        }
                        countriesFilter = new ArrayList();
                        for (int i = 0; i < len; i++) {
                            if (((Country) countriesAdapter.getChild(groupPosition, i)).getSelected() == "1") {
                                //do your task/work
                                String item = String.valueOf(listCountries.get(i).getCode()).replace("\"","");
                                countriesFilter.add(item);
                                responseText.append(item + " ");
                            }
                        }
                        countriesAdapter.ITEMS_CHECKED = countriesFilter.size();
                        loadPosts();
                        Toast.makeText(getApplicationContext(),
                                responseText, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });

                countriesAdapter = new CountriesAdapter(MainActivity.this,R.layout.list_countries,listCountries);
                countriesView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                countriesView.setAdapter(countriesAdapter);
                countriesView.setOnItemClickListener(new CategoryClickListener());

                pDialogCountries.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, R.string.error_load_countries, Toast.LENGTH_LONG).show();
            }
        });

        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }


    private void loadCategories() {
        pDialogCategories = new ProgressDialog(MainActivity.this);
        pDialogCategories.setMessage(getString(R.string.loading_categories));
        pDialogCategories.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialogCategories.show();
        StringRequest request = new StringRequest(Request.Method.GET, Config.CATEGORY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                gson = new Gson();
                JsonObject categories = (JsonObject) gson.fromJson(s, JsonObject.class);
                listCategories = JSONParser.parseCategories(categories);

                listCategoriesDetail.put(getString(R.string.categories), listCategories);
                categoriesListTitle = new ArrayList<String>();
                categoriesListTitle.add(getString(R.string.categories));

                Category c = new Category();
                c.setName(getString(R.string.category_prompt));
                c.setId(0);
                listCategories.add(0,c);
                CategoriesAdapter adapter = new CategoriesAdapter(MainActivity.this, R.layout.listitems_layout, R.id.title, listCategories);
                spinner2.setAdapter(adapter);
                spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Category c=(Category) spinner2.getSelectedItem();
                        categoriesFilter = new ArrayList();
                        categoriesFilter.add(c.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

                /*
                categoriesAdapter = new ExpandableListCategoriesAdapter(MainActivity.this, categoriesListTitle, listCategoriesDetail);
                categoriesView.setAdapter(categoriesAdapter);
                categoriesView.setChildDivider(getResources().getDrawable(R.color.color_white));
                categoriesView.setDivider(getResources().getDrawable(R.color.color_white));
                categoriesView.setDividerHeight(10);
                categoriesView.setPadding(30,0,0,0);
                categoriesView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                categoriesListTitle.get(groupPosition) + " List Expanded.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                categoriesView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                categoriesListTitle.get(groupPosition) + " List Collapsed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });

                categoriesView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        CheckedTextView checkbox = (CheckedTextView) v.findViewById(R.id.category_name);
                        checkbox.toggle();

                        StringBuffer responseText = new StringBuffer();
                        responseText.append("The following categories were selected...\n");
                        int len = categoriesAdapter.getChildrenCount(groupPosition);
                        boolean isChecked = ((CheckedTextView) v).isChecked();
                        if (isChecked) {
                            ((Category) categoriesAdapter.getChild(groupPosition, childPosition)).setSelected("1");
                        } else {
                            ((Category) categoriesAdapter.getChild(groupPosition, childPosition)).setSelected("0");
                        }
                        categoriesFilter = new ArrayList();
                        for (int i = 0; i < len; i++) {
                            if (((Category) categoriesAdapter.getChild(groupPosition, i)).getSelected() == "1") {
                                //do your task/work
                                String item = String.valueOf(listCategories.get(i).getId());
                                categoriesFilter.add(item);
                                responseText.append(item + " ");
                            }
                        }
                        categoriesAdapter.ITEMS_CHECKED = categoriesFilter.size();
                        //categoriesAdapter.setChildrenCountChecked(parent, categoriesFilter.size());
                        loadPosts();
                        Toast.makeText(getApplicationContext(),
                                responseText, Toast.LENGTH_LONG).show();
                        return false;
                    }
                });

                int index = 0;
                categories = new String[listCategories.size()];
                categoriesId = new String[listCategories.size()];
                for (Map.Entry<String,JsonElement> entry : listCategories.entrySet()) {
                    JsonObject array = entry.getValue().getAsJsonObject();
                    categories[index]= String.valueOf(array.get("name")).replace("\"","");
                    categoriesId[index]= String.valueOf(array.get("id"));
                    index++;
                }

                categoriesAdapter = new ArrayAdapter<ArrayList<Category>>(MainActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, listCategories);

                categoriesAdapter = new CategoriesAdapter(MainActivity.this,R.layout.list_categories,listCategories);
                categoriesView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                categoriesView.setAdapter(categoriesAdapter);
                categoriesView.setOnItemClickListener(new CategoryClickListener());

                pDialogCategories.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, R.string.error_load_categories, Toast.LENGTH_LONG).show();
            }
        });

        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }
    */
}

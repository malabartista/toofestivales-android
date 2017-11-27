package es.toofestival.toofestivales.util;

import android.text.Html;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import es.toofestival.toofestivales.model.Category;
import es.toofestival.toofestivales.model.Country;
import es.toofestival.toofestivales.model.Post;

/**
 * Utility class used to parse JSON data
 */
public class JSONParser {
    private static final String	TAG	= "JSONParser";

    /**
     * Parse JSON data and return an ArrayList of Category objects
     *
     * @param jsonObject JSON data
     * @return A list of Category objects
     */
    public static ArrayList<Category> parseCategories(JsonObject jsonObject) {
        ArrayList<Category> categoryArrayList = new ArrayList<>();

        // Create "All" category
        Category all = new Category();
        /*
        all.setId(0);
        all.setName("Todos");
        categoryArrayList.add(all);
        */
        for (Map.Entry<String,JsonElement> entry : jsonObject.entrySet()) {
            JsonObject catObj = entry.getValue().getAsJsonObject();
            Category c = new Category();
            c.setId(Integer.parseInt(catObj.get("id").toString()));
            c.setName(catObj.get("name").toString().replace("\"",""));
            c.setImage(catObj.get("image_url").toString().replace("\"",""));
            categoryArrayList.add(c);
        }
        return categoryArrayList;
    }

    /**
     * Parse JSON data and return an ArrayList of Country objects
     *
     * @param jsonArray JSON data
     * @return A list of Country objects
     */
    public static ArrayList<Country> parseCountries(JsonArray jsonArray) {
        ArrayList<Country> countryArrayList = new ArrayList<>();

        for (JsonElement country: jsonArray) {
            Country c = new Country();
            c.setCode(String.valueOf(country.getAsJsonObject().get("alpha2Code")));
            JsonObject translations = country.getAsJsonObject().get("translations").getAsJsonObject();
            c.setName(String.valueOf(translations.get("es")).replace("\"",""));
            c.setFlag(String.valueOf(country.getAsJsonObject().get("flag")));
            countryArrayList.add(c);
        }
        Collections.sort(countryArrayList, new Comparator<Country>() {
            @Override
            public int compare(Country c1, Country c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
        return countryArrayList;
    }

    /**
     * Parse JSON data and return an ArrayList of Post objects
     *
     * @param jsonObject JSON data
     * @return A list of Post objects
     */
    public static ArrayList<Post> parsePosts(JSONObject jsonObject) {
        ArrayList<Post> posts = new ArrayList<>();

        try{
            JSONArray postArray = jsonObject.getJSONArray("posts");
            // Go through each post
            for (int i = 0; i < postArray.length(); i++) {
                JSONObject postObject = postArray.getJSONObject(i);

                Post post = new Post();
                // Configure the Post object
                post.setTitle(postObject.optString("title", "N/A"));
                // Use a default thumbnail if one doesn't exist
                post.setThumbnailUrl(postObject.optString("thumbnail",
                        Config.DEFAULT_THUMBNAIL_URL));
                post.setCommentCount(postObject.optInt("comment_count", 0));
                //post.setViewCount(postObject.getJSONObject("custom_fields")
                //        .getJSONArray("post_views_count").getString(0));

                post.setDate(postObject.optString("date", "N/A"));
                post.setContent(postObject.optString("content", "N/A"));
                post.setAuthor(postObject.getJSONObject("author").optString("name", "N/A"));
                post.setId(postObject.optInt("id"));
                post.setUrl(postObject.optString("url"));

                JSONObject featuredImages = postObject.optJSONObject("thumbnail_images");
                if (featuredImages != null) {
                    post.setFeaturedImageUrl(featuredImages.optJSONObject("full")
                            .optString("url", Config.DEFAULT_THUMBNAIL_URL));
                }

                posts.add(post);
            }
        } catch (JSONException e) {
            Log.d(TAG, "----------------- Json Exception");
            Log.d(TAG, e.getMessage());
            return null;
        }

        return posts;
    }

    /**
     * Parse JSON data and return an ArrayList of Post objects
     *
     * @param mapPost JSON data
     * @return A list of Post objects
     */
    public static Post parsePost(Map<String, Object> mapPost) {
        String id = (String) mapPost.get("event_id");
        String post_id = (String) mapPost.get("post_id");
        String title = (String) mapPost.get("event_name");
        String location_name = (String) mapPost.get("location_name");
        String location_town = (String) mapPost.get("location_town");
        String location_country = (String) mapPost.get("location_country");
        String location_lat = (String) mapPost.get("location_latitude");
        String location_long = (String) mapPost.get("location_longitude");
        String url = "http://toofestival.es/festivales/" + (String) mapPost.get("event_slug");
        String featuredImageURL = (String) mapPost.get("image");
        String gallery_path = (String) mapPost.get("gallery_path");
        ArrayList thumbnail = (ArrayList) mapPost.get("thumbnail");
        ArrayList categories = (ArrayList) mapPost.get("categories");
        ArrayList posters = (ArrayList) mapPost.get("posters");
        String thumbnailImageURL = "";
        if(thumbnail!=null) thumbnailImageURL = (String) thumbnail.get(0);
        String video = (String) mapPost.get("video");
        String poster = (String) mapPost.get("poster");
        String weblink = (String) mapPost.get("weblink");
        String ticket = (String) mapPost.get("ticket");
        String startDate = (String) mapPost.get("event_start_date");
        String endDate = (String) mapPost.get("event_end_date");
        String viewCount = (String) mapPost.get("post_views");
        /*
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = format.parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd-MM-yyyy");
        startDate = format.format(newDate);
        */

        ArrayList<String> categoriesList = new ArrayList<>();
        for (int j = 0; j < categories.size(); j++) {
            Map<String,Object> mMap = (Map<String,Object>) categories.get(j);
            categoriesList.add(mMap.get("name").toString());
        }
        ArrayList<String> postersList = new ArrayList<>();
        for (int j = 0; j < posters.size(); j++) {
            Map<String,Object> mMap = (Map<String,Object>) posters.get(j);
            postersList.add("http://toofestival.es/" + gallery_path + "/" + mMap.get("filename").toString());
        }

        // Set Post
        Post p = new Post();
        p.setId(Integer.parseInt(id));
        p.setPostId(Integer.parseInt(post_id));
        p.setTitle(Html.fromHtml(title).toString());
        p.setCategories(categoriesList);
        p.setFeaturedImageUrl(featuredImageURL);
        p.setThumbnailUrl(thumbnailImageURL);
        p.setLocation_name(location_name);
        p.setLocation_town(location_town);
        p.setLocation_country(location_country);
        p.setLocation_lat(location_lat);
        p.setLocation_long(location_long);
        p.setDate(getEventDates(startDate, endDate));
        p.setUrl(url);
        p.setUrlOficial(weblink);
        p.setUrlTickets(ticket);
        p.setVideo(video);
        p.setPoster(poster);
        p.setPosters(postersList);
        p.setViewCount(viewCount);

        return p;
    }

    public static String getEventDates(String startDate, String endDate){
        Locale currentLocale = Locale.getDefault();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd MMMMM yyyy");
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormat.forPattern("yyyy-MM-dd"));
        String result = "";
        if (!endDate.isEmpty() && sd != ed ) {
            //LocalDate date = formatter.parseLocalDate("18/08/2012");
            //Event has an end date and end date is different than start date

            if (sd.getYear() == ed.getYear()) {
                // Start and end are in same year
                if (sd.getMonthOfYear() == ed.getMonthOfYear()) {
                    //Start and end are in the same month
                    DateTimeFormatter fmt1 = DateTimeFormat.forPattern("dd");
                    result = fmt1.print(sd) + '-'  + fmt.print(ed);
                    if (sd.getDayOfWeek() == ed.getDayOfWeek()) {
                        //Start and end are on the same day
                        result = fmt.print(sd);
                    }
                } else {
                    //Start and end are in different months
                    DateTimeFormatter fmt1 = DateTimeFormat.forPattern("dd MMMMM");
                    result = fmt1.print(sd) + '-' + fmt.print(ed);
                }
            } else {
                //Start and end are in different years
                result = fmt.print(sd) + '-' + fmt.print(ed);
            }
        } else {
            // No end date, or start and end date are the same
            result = fmt.print(sd);
        }
        return result;
    }
}

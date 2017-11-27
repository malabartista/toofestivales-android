package es.toofestival.toofestivales.util;

/**
 * Configuration class
 */
public class Config {
    // Fill in your own WordPress URL, don't forget the "/" at the end
    public static final String BASE_URL = "http://toofestival.es/";
    public static final String POSTS_URL = BASE_URL + "wp-json/eventsmanager/v1/events/";
    public static final String COUNT_POSTS_URL = BASE_URL + "wp-json/eventsmanager/v1/eventstotal/";
    public static final String CATEGORY_URL = BASE_URL + "wp-json/eventsmanager/v1/categories/";
    public static final String COUNTRIES_URL = "https://restcountries.eu/rest/v2/alpha?codes=at;be;ch;cz;es;de;dk;ie;it;fr;gb;gr;hr;nl;no;pt";
    public static String POSTS_ORDERBY = "date";

    //public static String CATEGORY_URL = BASE_URL + "?json=get_category_index";
    public static final String DEFAULT_THUMBNAIL_URL = "http://i.imgur.com/D2aUC3s.png";
    public static final String YOUTUBE_API_KEY = "AIzaSyABqE-K-GXPlJbA8J4i-ZShwKDEOS3Vf-0";

    public static boolean isProductViewAsList = true;

}

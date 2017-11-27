package es.toofestival.toofestivales.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class Post implements ClusterItem {
    private String title;
    private String content;
    private String thumbnailUrl;
    private String featuredImageUrl = "";
    private String date;
    private String author;
    private String url;
    private String urlOficial;
    private String urlTickets;
    private String video;
    private String poster;
    private String snippet;
    private String location_name;
    private String location_town;
    private String location_country;
    private String location_lat;
    private String location_long;
    private String viewCount;
    private int commentCount;
    private int id;
    private int postId;
    private int mediaId;
    private ArrayList<String> categories;
    private ArrayList<String> posters;
    private LatLng position;

    public Post(){

    }
    public Post(double lat, double lng) {
        position = new LatLng(lat, lng);
        title = null;
        snippet = null;
    }

    public Post(double lat, double lng, String title, String snippet) {
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    /**
     * Override equals method to help Set decide if two posts are the same
     *
     * @param o Another object
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Post && this.getId() == (((Post) o).getId());
    }

    /**
     * Override hashCode method to help Set decide if two posts are the same
     */
    @Override
    public int hashCode() {
        //return this.getId();
        return Integer.valueOf(this.getId()).hashCode();
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) { this.featuredImageUrl = featuredImageUrl; }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() { return postId; }

    public void setPostId(int postId) { this.postId = postId; }

    public String getUrl() { return url; }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlTickets() { return urlTickets; }

    public void setUrlTickets(String urlTickets) { this.urlTickets = urlTickets; }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSnippet() { return snippet; }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_town() {
        return location_town;
    }

    public void setLocation_town(String location_town) {
        this.location_town = location_town;
    }

    public String getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public String getLocation_long() {
        return location_long;
    }

    public void setLocation_long(String location_long) {
        this.location_long = location_long;
    }

    public String getUrlOficial() {
        return urlOficial;
    }

    public void setUrlOficial(String urlOficial) {
        this.urlOficial = urlOficial;
    }

    public String getLocation_country() {
        return location_country;
    }

    public void setLocation_country(String location_country) { this.location_country = location_country; }

    public ArrayList<String> getPosters() { return posters; }

    public void setPosters(ArrayList<String> posters) { this.posters = posters; }
}

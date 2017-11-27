package es.toofestival.toofestivales.model;

public class Event {
    public String title;
    public String description;
    public int imageId;

    public Event(String title, String description, int imageId) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
    }

}
package sergi.ivan.carles.client;

/**
 * Created by Carles on 11/12/2016.
 */

public class Song {
    private long points;
    private String name;
    private String artist;

    public Song(long points, String name, String artist) {

        this.points = points;
        this.name = name;
        this.artist = artist;
    }

    public long getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) { this.artist = artist; }
}

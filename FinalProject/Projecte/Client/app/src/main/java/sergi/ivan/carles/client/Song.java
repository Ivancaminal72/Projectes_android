package sergi.ivan.carles.client;

/**
 * Created by Carles on 11/12/2016.
 */

public class Song {
    private int groupPosition;
    private long points;
    private String name;
    private String artist;

    public Song(int groupPosition, long points, String name, String artist) {
        this.groupPosition = groupPosition;
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

    public int getGroupPosition() {return groupPosition;}

    public void setArtist(String artist) { this.artist = artist; }
}

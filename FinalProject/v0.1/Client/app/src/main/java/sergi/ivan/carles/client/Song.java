package sergi.ivan.carles.client;

/**
 * Created by Carles on 11/12/2016.
 */

public class Song {
    private Integer votes;
    private String name;
    private String artist;

    public Song(Integer votes, String name, String artist) {

        this.votes = votes;
        this.name = name;
        this.artist = artist;
    }

    public Integer getVotes() {
        return votes;
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

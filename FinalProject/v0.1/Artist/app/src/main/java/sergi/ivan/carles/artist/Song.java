package sergi.ivan.carles.artist;

/**
 * Created by Ivan on 06/12/2016.
 */

public class Song {
    private Integer songId;
    private String name;
    private String artist;

    public Song(Integer songId, String name, String artist) {

        this.songId = songId;
        this.name = name;
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }
}

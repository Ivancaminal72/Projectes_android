package sergi.ivan.carles.artist;


public class Song {
    private String id;
    private boolean checked;
    private String name;
    private String artist;

    public Song(String id, String name, String artist) {

        this.id = id;
        this.name = name;
        this.artist = artist;
    }

    public String getId() {return id;}
    public String getName() {
        return name;
    }
    public String getArtist() {
        return artist;
    }
    public boolean isChecked() {return checked;}
    public void setChecked(boolean checked) {this.checked = checked;}
    public void toggleChecked() {
        this.checked = !this.checked;
    }
}

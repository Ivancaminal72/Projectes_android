package sergi.ivan.carles.artist;

import static java.lang.System.arraycopy;
import static sergi.ivan.carles.artist.ActualEventActivity.GROUP_MAX_SIZE;


public class Group {
    private String key;
    private String name;
    private String [] songIds;
    private long [] points;

    public Group(String key, String name, String[] songIds) {
        this.key = key;
        this.name = name;
        this.songIds = new String[GROUP_MAX_SIZE];
        arraycopy(songIds,0,this.songIds,0,GROUP_MAX_SIZE);
        this.points = new long[GROUP_MAX_SIZE];
    }

    public String getKey() {return key;}

    public void setKey(String key) {this.key = key;}

    public String getName() {
        return name;
    }

    public String[] getSongIds() {
        return songIds;
    }

    public long[] getPoints() {
        return points;
    }

}

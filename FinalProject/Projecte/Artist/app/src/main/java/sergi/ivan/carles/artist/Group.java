package sergi.ivan.carles.artist;

import static java.lang.System.arraycopy;
import static sergi.ivan.carles.artist.ActualEventActivity.GROUP_MAX_SIZE;


public class Group {
    private String Id;
    private String name;
    private String [] songIds;
    private long [] points;

    public Group(String id, String name, String[] songIds) {
        this.Id = id;
        this.name = name;
        this.songIds = new String[GROUP_MAX_SIZE];
        arraycopy(songIds,0,this.songIds,0,GROUP_MAX_SIZE);
        this.points = new long[GROUP_MAX_SIZE];
    }

    public String getId() {return Id;}

    public void setId(String id) {this.Id = id;}

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

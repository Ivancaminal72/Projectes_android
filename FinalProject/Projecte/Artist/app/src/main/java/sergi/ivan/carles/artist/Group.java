package sergi.ivan.carles.artist;

import static java.lang.System.arraycopy;

/**
 * Created by Ivan on 02/12/2016.
 */

public class Group {
    private static final int GROUP_MAX_SIZE = 4;
    private String name;
    private int[] songIds;
    private long [] points;

    public Group(String name, int[] songIds) {
        this.name = name;
        this.songIds = new int[GROUP_MAX_SIZE];
        arraycopy(songIds,0,this.songIds,0,GROUP_MAX_SIZE);
        this.points = new long[]{0,5,10,15};
    }

    public String getName() {
        return name;
    }

    public int[] getSongIds() {
        return songIds;
    }

    public long[] getPoints() {
        return points;
    }

}

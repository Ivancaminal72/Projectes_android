package sergi.ivan.carles.artist;

import static java.lang.System.arraycopy;


public class Group {
    private static final int GROUP_MAX_SIZE = 4;
    private String name;
    private String [] songIds;
    private long [] points;

    public Group(String name, String[] songIds) {
        this.name = name;
        this.songIds = new String[GROUP_MAX_SIZE];
        arraycopy(songIds,0,this.songIds,0,GROUP_MAX_SIZE);
        this.points = new long[]{0,5,10,15};
    }

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

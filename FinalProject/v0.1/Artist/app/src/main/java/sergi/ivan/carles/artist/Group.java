package sergi.ivan.carles.artist;

import static java.lang.System.arraycopy;

/**
 * Created by Ivan on 02/12/2016.
 */

public class Group {
    private static final int GROUP_MAX_SIZE = 4;
    private String name;
    private String [] songkeys;
    private int [] points;

    public Group(String name, String[] songkeys) {
        this.name = name;
        this.songkeys = new String[GROUP_MAX_SIZE];
        arraycopy(songkeys,0,this.songkeys,0,GROUP_MAX_SIZE);
        this.points = new int[]{0,0,0,0};
    }

    public String getName() {
        return name;
    }
}

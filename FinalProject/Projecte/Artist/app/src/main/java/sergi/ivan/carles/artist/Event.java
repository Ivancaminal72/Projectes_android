package sergi.ivan.carles.artist;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String name;
    private Date startDate;
    private Date endDate;
    private String place;
    private String room;
    private ArrayList<int[]> groupList;

    public Event(String name, Date startDate, Date endDate, String place) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
    }

    public Event(String name, Date startDate, Date endDate, String place, String room) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getRoom() {
        return room;
    }

    public String getPlace() {
        return place;
    }

    public ArrayList<int[]> getGroupList() {
        return groupList;
    }

    public ArrayList<int[]> addgroup(int[] group){
        this.groupList.add(group);
        return groupList;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

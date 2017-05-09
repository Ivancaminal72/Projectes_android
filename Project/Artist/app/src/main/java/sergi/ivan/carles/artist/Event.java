package sergi.ivan.carles.artist;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private String id;
    private String name;
    private Date startDate;
    private Date endDate;
    private String place;
    private String room;
    private ArrayList<String> groupIds;

    public Event(String id, String name, Date startDate, Date endDate, String place) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
    }


    public String getId() {return id;}

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

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setId(String id) {this.id = id;}

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        if(this.groupIds == null){this.groupIds = new ArrayList<>();}
        this.groupIds = groupIds;}

    public void addGroupId(String groupId){
        if(this.groupIds == null){this.groupIds = new ArrayList<>();}
        this.groupIds.add(groupId);
    }

}

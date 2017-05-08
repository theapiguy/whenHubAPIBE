package ootb.com.whenhubbe;

/**
 * Created by Keith on 5/2/2017.
 */

public class EventObject {

    private String period;
    private String startDate;
    private String startTimezone;
    private String endDate;
    private String endTimezone;
    private String name;
    private String eventID;
    private String scheduleID;
    private String description;

    public String getEventCity() {
        return eventCity;
    }

    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }

    public String getEventRegion() {
        return eventRegion;
    }

    public void setEventRegion(String eventRegion) {
        this.eventRegion = eventRegion;
    }

    private String eventCity;
    private String eventRegion;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventObject(){

    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTimezone() {
        return startTimezone;
    }

    public void setStartTimezone(String startTimezone) {
        this.startTimezone = startTimezone;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTimezone() {
        return endTimezone;
    }

    public void setEndTimezone(String endTimezone) {
        this.endTimezone = endTimezone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(String scheduleID) {
        this.scheduleID = scheduleID;
    }
}

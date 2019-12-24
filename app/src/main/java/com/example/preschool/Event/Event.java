package com.example.preschool.Event;

public class Event {
   String nameEvent;
    String timeStart;
    String timeEnd;

    String position;
    String description;
    public Event() {
    }


    public Event(String nameEvent, String timeStart, String timeEnd, String position, String description) {
        this.nameEvent = nameEvent;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;

        this.position = position;
        this.description = description;
    }

    public String getNameEvent() {
        return nameEvent;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }



    public String getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }
}

package com.example.preschool.TimeTable;

import java.io.Serializable;

public class TimeTable implements Serializable {
    String timeStart,timeEnd, description;

    public TimeTable() {
    }

    public TimeTable(String timeStart, String timeEnd, String description) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.description = description;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

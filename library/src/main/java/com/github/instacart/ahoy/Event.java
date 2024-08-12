package com.github.instacart.ahoy;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.util.Date;
import java.util.Map;

public class Event implements Parcelable {
    private String id;
    private String name;
    private Map<String, Object> properties;
    private Date time;

    public Event(String id, String name, Map<String, Object> properties, Date time) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.time = time;
    }

    public Event(String name, Map<String, Object> properties) {
        this(null, name, properties, new Date());
    }

    protected Event(Parcel in) {
        id = in.readString();
        name = in.readString();
        properties = (Map<String, Object>) in.readSerializable();
        time = new Date(in.readLong());
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeSerializable((java.io.Serializable) properties);
        dest.writeLong(time.getTime());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Date getTime() {
        return time;
    }

    // Returns a Map<String, Object> representation of the Event object
    public Map<String, Object> toMap() {
        Map<String, Object> requestBody = new ArrayMap<>();
        requestBody.put("id", id);
        requestBody.put("name", name);
        requestBody.put("properties", properties);
        requestBody.put("time", time);
        return requestBody;
    }
}

package com.github.instacart.ahoy;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.github.instacart.ahoy.utils.MapTypeAdapter;
import com.github.instacart.ahoy.utils.TypeUtil;
import com.google.auto.value.AutoValue;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@AutoValue
public abstract class Event implements Parcelable {

    public abstract String id();
    public abstract String name();
    @ParcelAdapter(MapTypeAdapter.class) public abstract Map<String, Object> properties();
    public abstract Date time();

    public static Event create(String name, Map<String, Object> properties) {
        String id = UUID.randomUUID().toString();
        properties = TypeUtil.ifNull(properties, Collections.emptyMap());
        Date time = new Date();
        return new AutoValue_Event(id, name, Collections.unmodifiableMap(properties), time);
    }

    public static Builder builder() {
        return new AutoValue_Event.Builder()
                .time(new Date())
                .properties(new ArrayMap<>());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(String id);
        public abstract Builder name(String name);
        public abstract Builder properties(Map<String, Object> properties);
        public abstract Builder time(Date time);
        public abstract Event build();
    }

    // Returns a Map<String, Object> representation of the Event object
    public Map<String, Object> toMap() {
        Map<String, Object> requestBody = new android.util.ArrayMap<>();
        requestBody.put("id", id());
        requestBody.put("name", name());
        requestBody.put("properties", properties());
        requestBody.put("time", time());
        return requestBody;
    }
}

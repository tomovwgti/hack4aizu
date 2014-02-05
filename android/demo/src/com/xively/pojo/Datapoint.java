
package com.xively.pojo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Datapoint {

    private String value;
    private String at;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAt() {
        return at;
    }

    public Date getAtLoacl() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = format.parse(at);
            return date;
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
    }

    public void setAt(String at) {
        this.at = at;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

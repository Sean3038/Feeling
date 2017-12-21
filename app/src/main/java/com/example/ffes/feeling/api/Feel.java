package com.example.ffes.feeling.api;

/**
 * Created by Ffes on 2017/12/21.
 */

public class Feel {
    float heartRate;
    float temperature;
    float humidity;
    String date;

    public Feel(){}

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeartRate(float heartRate) {
        this.heartRate = heartRate;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getTemperature() {
        return temperature;
    }
}

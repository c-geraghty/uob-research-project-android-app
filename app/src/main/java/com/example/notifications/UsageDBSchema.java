package com.example.notifications;

public class UsageDBSchema {

    private int id;
    private String date;
    private int usageInMillis;

    public UsageDBSchema(int id, String date, int usageInMillis) {
        this.id = id;
        this.date = date;
        this.usageInMillis = usageInMillis;
    }

    public UsageDBSchema() {
    }

    @Override
    public String toString() {
        return "UsageDBSchema{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", usageInMillis=" + usageInMillis +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUsageInMillis() {
        return usageInMillis;
    }

    public void setUsageInMillis(int usageInMillis) {
        this.usageInMillis = usageInMillis;
    }
}

package com.example.myapplication;

public class JobItem {
    private String id;
    private String title;
    private String company;
    private String location;
    private String salary;
    private String workType;

    public JobItem() {
    }

    public JobItem(String id, String title, String company, String location, String salary, String workType) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.workType = workType;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public String getWorkType() {
        return workType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }
}


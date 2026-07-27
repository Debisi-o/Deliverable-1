package com.adebisi.coursecompass.model;

public enum Category {
    BUSINESS("Business"),
    DATA_SCIENCE("Data Science"),
    DESIGN("Design"),
    MARKETING("Marketing"),
    PROGRAMMING("Programming");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

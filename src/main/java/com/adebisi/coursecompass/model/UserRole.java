package com.adebisi.coursecompass.model;

public enum UserRole {
    ADMIN("Administrator"),
    INSTRUCTOR("Instructor"),
    STUDENT("Student");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAuthority() {
        return "ROLE_" + name();
    }
}

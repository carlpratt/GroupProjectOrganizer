package com.cs407.groupprojectorganizer;

/**
 * This class describes each user of the project
 */
public class AppUser {

    //User Information Variables
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String facebook;
    private String google;
    private int position;
    private boolean discoverable;
    private boolean promptApproval;

    public AppUser() {}

    public AppUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
        this.email = "";
        this.phone = "";
        this.facebook = "";
        this.google = "";
        this.discoverable = false;
        this.promptApproval = false;
    }

    public AppUser(String uid, String name, String email, String phone, String facebook, String google, int position) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.facebook = facebook;
        this.google = google;
        this.position = position;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean getDiscoverable() {return true;}

    public void setDiscoverable(boolean bool) {this.discoverable = bool;}

    public boolean getPromptApproval() {return true;}

    public void setPromptApproval(boolean bool) {this.promptApproval = bool;}

}


package com.cs407.groupprojectorganizer;

public class Project {

    private String pid;
    private String projTitle;
    private String projDescription;
    private String projOwner;
    private int position;

    public Project() {}

    public Project(String pid, String projTitle, String projDescription, String projOwner, int position) {
        this.pid = pid;
        this.projTitle = projTitle;
        this.projDescription = projDescription;
        this.projOwner = projOwner;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProjTitle() {
        return projTitle;
    }

    public void setProjTitle(String projTitle) {
        this.projTitle = projTitle;
    }

    public String getProjDescription() {
        return projDescription;
    }

    public void setProjDescription(String projDescription) {
        this.projDescription = projDescription;
    }

    public String getProjOwner() {
        return projOwner;
    }

    public void setProjOwner(String projOwner) {
        this.projOwner = projOwner;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



}

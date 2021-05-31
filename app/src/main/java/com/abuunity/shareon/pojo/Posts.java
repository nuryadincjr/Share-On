package com.abuunity.shareon.pojo;

import com.abuunity.shareon.adapter.ToolsAdapter;

import java.util.ArrayList;
import java.util.List;

public class Posts {

    private String postid;
    private String title;
    private String description;
    private String imageurl;
    private String publisher;
    private List<String> toolsList;
    private List<String> stepsList;

    public Posts() {
    }

    public Posts(String postid, String title, String description, String imageurl, String publisher, List<String> toolsList, List<String> stepsList) {
        this.postid = postid;
        this.title = title;
        this.description = description;
        this.imageurl = imageurl;
        this.publisher = publisher;
        this.toolsList = toolsList;
        this.stepsList = stepsList;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getToolsList() {
        return toolsList;
    }

    public void setToolsList(List<String> toolsList) {
        this.toolsList = toolsList;
    }

    public List<String> getStepsList() {
        return stepsList;
    }

    public void setStepsList(List<String> stepsList) {
        this.stepsList = stepsList;
    }
}

package com.danale.localfile.bean;

import android.net.Uri;

import com.danale.localfile.constant.MediaType;

import java.io.File;
import java.io.Serializable;

/**
 * Created by kevin on 9/26/16.
 */

public class Media implements Serializable, Comparable<Media>{

    private boolean selected;

    private String url;

    private Uri uri;

    private String description;

    private MediaType mediaType;


    public Media(String url) {
        this.url = url;
    }

    public Media(Uri uri) {
        this.uri = uri;
    }

    public Media(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public Media(Uri uri, String description) {
        this.uri = uri;
        this.description = description;
    }


    public String getUrl() {
        return url;
    }

    public Uri getUri() {
        return uri;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDescription() {
        return description;
    }

    public void setMediaType(MediaType mediaType){
        this.mediaType = mediaType;
    }

    public MediaType getMediaType(){
        return this.mediaType;
    }
    @Override
    public String toString() {
        return "Media{" +
                "url='" + url + '\'' +
                ", uri=" + uri +
                ", description='" + description + '\'' +
                '}';
    }


    private File getFile(){

        return new File(this.getUri().getPath());
    }

    private long getTime() {
        return getFile().lastModified();
    }

    @Override
    public int compareTo(Media o) {
        if(this.getFile().lastModified() > o.getFile().lastModified()){
            return -1;
        }else if(this.getFile().lastModified() == o.getFile().lastModified()){
            return 0;
        }else {
            return 1;
        }
    }
}

package com.inclass.tripchat.Activity.POJO;

import java.util.ArrayList;

/**
 * Created by Nikhil on 18/04/2017.
 */

public class Trip {
    String title, location, imageUrl;
    String creator, creatorID, tripId;
    ArrayList<String> friendArrayList;
    Boolean active;
    ArrayList<Places> placesArrayList;
    Places destination;

    public Trip() {
    }

    public Places getDestination() {
        return destination;
    }

    public void setDestination(Places destination) {
        this.destination = destination;
    }

    public ArrayList<Places> getPlacesArrayList() {
        return placesArrayList;
    }

    public void setPlacesArrayList(ArrayList<Places> placesArrayList) {
        this.placesArrayList = placesArrayList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public ArrayList<String> getFriendArrayList() {
        return friendArrayList;
    }

    public void setFriendArrayList(ArrayList<String> friendArrayList) {
        this.friendArrayList = friendArrayList;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

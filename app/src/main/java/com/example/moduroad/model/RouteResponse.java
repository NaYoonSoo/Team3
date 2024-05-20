package com.example.moduroad.model;

import java.util.Collections;
import java.util.List;

public class RouteResponse {
    private List<List<Double>> route;
    private String time;
    private String distance;
    private List<Obstacle> obstacles; // 장애물 정보 추가

    // Constructor with default values
    public RouteResponse() {
        this.route = Collections.emptyList();
        this.time = "";
        this.distance = "";
        this.obstacles = Collections.emptyList();
    }

    // Getter and Setter for obstacles
    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public List<List<Double>> getRoute() {
        return route;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public void setRoute(List<List<Double>> route) {
        this.route = route;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}

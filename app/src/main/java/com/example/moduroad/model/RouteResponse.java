package com.example.moduroad.model;

import java.util.List;

public class RouteResponse {
    private List<List<Double>> route;
    private String time; // 추가된 예상 소요 시간 프로퍼티
    private String distance; // 거리도 String 타입으로 저장합니다.

    // Public getter를 추가하거나 접근 제어자를 변경하세요.
    public List<List<Double>> getRoute() {
        return route;
    }

    public String getTime() {
        return time;
    }


    public String getDistance() {
        return distance;
    }
    // 필요한 경우, setter도 추가하세요.
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
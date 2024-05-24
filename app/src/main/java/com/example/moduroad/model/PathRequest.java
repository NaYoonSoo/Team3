package com.example.moduroad.model;

public class PathRequest {
    private double start_lat; // 출발지 위도
    private double start_lon; // 출발지 경도
    private double end_lat;   // 도착지 위도
    private double end_lon;   // 도착지 경도
    private String type;      // 경로 유형 ("normal", "wheelchair", "elder")

    // 생성자
    public PathRequest(double start_lat, double start_lon, double end_lat, double end_lon, String type) {
        this.start_lat = start_lat;
        this.start_lon = start_lon;
        this.end_lat = end_lat;
        this.end_lon = end_lon;
        this.type = type; // 새로운 필드 추가
    }

    // getter 메소드
    public double getStartLat() {
        return start_lat;
    }

    public double getStartLon() {
        return start_lon;
    }

    public double getEndLat() {
        return end_lat;
    }

    public double getEndLon() {
        return end_lon;
    }

    public String getType() {
        return type; // type의 getter 추가
    }

    // setter 메소드 (필요한 경우 추가)
    public void setStartLat(double start_lat) {
        this.start_lat = start_lat;
    }

    public void setStartLon(double start_lon) {
        this.start_lon = start_lon;
    }

    public void setEndLat(double end_lat) {
        this.end_lat = end_lat;
    }

    public void setEndLon(double end_lon) {
        this.end_lon = end_lon;
    }

    public void setType(String type) {
        this.type = type; // type의 setter 추가
    }
}


package com.example.moduroad.network;

import com.example.moduroad.model.PathRequest;
import com.example.moduroad.model.RouteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/find-path")
    Call<RouteResponse> findPath(@Body PathRequest request);
}
package com.rorlig.redirector.retrofit;


import com.rorlig.redirector.model.DummyRequest;
import com.rorlig.redirector.model.DummyResult;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * @author gaurav gupta
 * Redirector Service
 */
public interface RedirectorService {

    @GET("/api/redirector")
    Call<DummyResult> getRedirector();

    @POST("/api/redirector")
    Call<DummyResult> postRedirector(@Body DummyRequest hello);

}

package com.fyp.smhs.Models.WebServices;

import com.fyp.smhs.Models.DirectionPlaceModel.DirectionResponseModel;
import com.fyp.smhs.Models.GooglePlaceModel.GoogleResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<GoogleResponseModel> getNearByPlaces(@Url String url);

    @GET
    Call<DirectionResponseModel> getDirection(@Url String url);
}

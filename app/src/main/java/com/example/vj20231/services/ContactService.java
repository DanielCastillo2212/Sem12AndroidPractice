package com.example.vj20231.services;

import com.example.vj20231.ImageUpload.ImageUploadRequest;
import com.example.vj20231.ImageUpload.ImageUploadResponse;
import com.example.vj20231.entities.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ContactService {
    @GET("contact")
    Call<List<Contact>> getAllContact();

    @GET("contact/{id}")
    Call<Contact> findUser(@Path("id") int id);

    @POST("contact")
    Call<Contact> create(@Body Contact contact);

    @PUT("contact/{id}")
    Call <Contact> updateContacto(@Path("id") int id, @Body Contact contact);

    @DELETE("contact/{id}")
    Call<Void> delete(@Path("id") int id);

    @POST("image")
    Call<ImageUploadResponse> uploadImage(@Body ImageUploadRequest request);

}

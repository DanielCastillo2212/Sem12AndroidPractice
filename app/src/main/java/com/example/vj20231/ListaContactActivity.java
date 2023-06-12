package com.example.vj20231;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.vj20231.adapters.ContactAdapter;
import com.example.vj20231.entities.Contact;
import com.example.vj20231.services.ContactService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaContactActivity extends AppCompatActivity {

    private ContactService contactService;
    private ContactAdapter adapter;

    //private static final int REQUEST_CODE_ACTUALIZAR_CONTACTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contact);

        RecyclerView rvListaContact = findViewById(R.id.rvListaContact);
        rvListaContact.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactAdapter(new ArrayList<>());
        rvListaContact.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        contactService = retrofit.create(ContactService.class);

        Call<List<Contact>> call = contactService.getAllContact();

        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful()) {
                    List<Contact> data = response.body();
                    Log.i("MAIN_APP", new Gson().toJson(data));
                    adapter.setContacts(data);
                } else {
                    Log.e("MAIN_APP", "Request failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.e("MAIN_APP", "Request failed: " + t.getMessage());
            }
        });

    }
}
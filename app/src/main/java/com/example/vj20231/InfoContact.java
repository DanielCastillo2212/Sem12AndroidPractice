package com.example.vj20231;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vj20231.services.ContactService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoContact extends AppCompatActivity {
    TextView tvNombreContact, tvNumeroContact;
    Button btnEliminar, btnActualizar;
    ContactService contactService;
    int idContacto;

    private String nombreActual;
    private String numeroActual;
    private String imagenActual;

    private static final int REQUEST_CODE_ACTUALIZAR_CONTACTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_contact);

        tvNombreContact = findViewById(R.id.tvNombreContact);
        tvNumeroContact = findViewById(R.id.tvNumeroContact);
        ImageView ivContactImage = findViewById(R.id.ivContactImage);

        btnEliminar = findViewById((R.id.btnEliminar));
        btnActualizar = findViewById((R.id.btnActualizar));

        nombreActual = getIntent().getStringExtra("nombre");
        numeroActual = getIntent().getStringExtra("numero");
        imagenActual = getIntent().getStringExtra("imagen");

        // Obtener el ID del intent
        idContacto = getIntent().getIntExtra("id", 0);
        String nombreContacto = getIntent().getStringExtra("nombre");
        String numeroContacto = getIntent().getStringExtra("numero");
        String imagenContacto = getIntent().getStringExtra("imagen");

        // Mostrar los datos en los TextView e ImageView correspondientes
        tvNombreContact.setText(nombreContacto);
        tvNumeroContact.setText(numeroContacto);
        Picasso.get().load(imagenContacto).into(ivContactImage);

        // Crear una instancia de Retrofit y ContactService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        contactService = retrofit.create(ContactService.class);

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método delete del ContactService para eliminar el contacto por su ID
                Call<Void> call = contactService.delete(idContacto);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Eliminación exitosa, puedes realizar cualquier acción adicional necesaria
                            Toast.makeText(InfoContact.this, "Contacto eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            // Enviar resultado a ListaContactActivity
                            finish();
                            Intent intent = new Intent(InfoContact.this, ListaContactActivity.class);
                            startActivity(intent);
                        } else {
                            // Error en la eliminación, muestra un mensaje de error o realiza una acción de manejo de errores
                            Log.e("INFO_CONTACT", "Error al eliminar el contacto: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Error en la solicitud, muestra un mensaje de error o realiza una acción de manejo de errores
                        Log.e("INFO_CONTACT", "Error al realizar la solicitud: " + t.getMessage());
                    }
                });
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoContact.this, ActualizarContacto.class);
                intent.putExtra("id", idContacto);
                intent.putExtra("nombre", nombreActual);
                intent.putExtra("numero", numeroActual);
                intent.putExtra("imagen", imagenActual);
                startActivityForResult(intent, REQUEST_CODE_ACTUALIZAR_CONTACTO);
            }
        });
    }

}
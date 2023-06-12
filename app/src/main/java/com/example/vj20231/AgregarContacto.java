package com.example.vj20231;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vj20231.ImageUpload.ImageUploadRequest;
import com.example.vj20231.ImageUpload.ImageUploadResponse;
import com.example.vj20231.adapters.ContactAdapter;
import com.example.vj20231.entities.Contact;
import com.example.vj20231.services.ContactService;
import com.example.vj20231.services.ImageUploadService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AgregarContacto extends AppCompatActivity {

    private EditText etNombre;
    private EditText etNumero;
    private Button btnAgregar;
    private Button btnAgregarImagen;

    private ContactService contactService;

    private ContactAdapter adapter;

    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        etNombre = findViewById(R.id.etNombre);
        etNumero = findViewById(R.id.etNumero);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnAgregarImagen = findViewById(R.id.btnAgregarImagen);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64779d129233e82dd53beed7.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        contactService = retrofit.create(ContactService.class);

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String numero = etNumero.getText().toString();

                if (!nombre.isEmpty() && !numero.isEmpty()) {
                    // Crear un nuevo objeto Contact con los datos ingresados
                    Contact contact = new Contact();
                    contact.setNameContact(nombre);
                    contact.setNumberContact(numero);

                    if (!nombre.isEmpty() && !numero.isEmpty()) {
                        // ...

                        if (selectedImageUri != null) {
                            String imageBase64 = convertImageToBase64(selectedImageUri);
                            contact.setImgContact(imageBase64);

                            // Subir la imagen a la API
                            uploadImageToApi(imageBase64);
                        }

                        // ...
                    } else {
                        Toast.makeText(AgregarContacto.this, "Ingrese un nombre y número válidos", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(AgregarContacto.this);
        builder.setTitle("Seleccionar imagen");
        builder.setItems(new CharSequence[]{"Cámara", "Galería"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Opción de la cámara seleccionada
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        // El permiso de cámara está concedido, abrir la cámara
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    } else {
                        // Solicitar permiso de cámara si no está concedido
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    }
                    break;
                case 1:
                    // Opción de la galería seleccionada
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // El permiso de almacenamiento está concedido, abrir la galería
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY);
                    } else {
                        // Solicitar permiso de almacenamiento si no está concedido
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
                    }
                    break;
            }
        });

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void uploadImageToApi(String base64Image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://demo-upn.bit2bittest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageUploadService imageUploadService = retrofit.create(ImageUploadService.class);

        ImageUploadRequest request = new ImageUploadRequest(base64Image);

        Call<ImageUploadResponse> call = imageUploadService.uploadImage(request);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    // La imagen se ha subido correctamente
                    ImageUploadResponse uploadResponse = response.body();
                    String imageUrl = uploadResponse.getImageUrl();

                    // Agregar el contacto con la URL de la imagen
                    agregarContactoConImagen(imageUrl);
                } else {
                    // Error al subir la imagen
                    Toast.makeText(AgregarContacto.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                // Error de conexión
                Toast.makeText(AgregarContacto.this, "Error de conexión al subir la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso de cámara fue concedido, abrir la cámara
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "El permiso de cámara es requerido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // Obtener la URI de la imagen seleccionada desde la galería
            selectedImageUri = data.getData();
            Toast.makeText(AgregarContacto.this, "Imagen agregada correctamente", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            // Obtener la imagen capturada por la cámara
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                selectedImageUri = getImageUri(imageBitmap);
                Toast.makeText(AgregarContacto.this, "Imagen agregada correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void agregarContactoConImagen(String imageUrl) {
        String nombre = etNombre.getText().toString();
        String numero = etNumero.getText().toString();

        if (!nombre.isEmpty() && !numero.isEmpty()) {
            // Crear un nuevo objeto Contact con los datos ingresados
            Contact contact = new Contact();
            contact.setNameContact(nombre);
            contact.setNumberContact(numero);
            contact.setImgContact("https://demo-upn.bit2bittest.com/"+imageUrl); // Asignar la URL de la imagen

            // Llamar al método create para guardar el nuevo contacto en MockAPI
            Call<Contact> createCall = contactService.create(contact);
            createCall.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Call<Contact> call, Response<Contact> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AgregarContacto.this, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad después de agregar el contacto
                    } else {
                        Toast.makeText(AgregarContacto.this, "Error al agregar el contacto", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Contact> call, Throwable t) {
                    Toast.makeText(AgregarContacto.this, "Error al agregar el contacto", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AgregarContacto.this, "Ingrese un nombre y número válidos", Toast.LENGTH_SHORT).show();
        }
    }

}




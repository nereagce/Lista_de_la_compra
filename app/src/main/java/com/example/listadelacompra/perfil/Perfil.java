package com.example.listadelacompra.perfil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.listadelacompra.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Perfil extends AppCompatActivity {
    private static final int CODIGO_FOTO_ARCHIVO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);



        //Leer del fichero en nombre de usuario
        BufferedReader ficherointerno = null;
        String nombreUsuario="";
        try {
            ficherointerno = new BufferedReader(new InputStreamReader(
                    openFileInput("nombreUsuario.txt")));
            nombreUsuario = ficherointerno.readLine();
            ficherointerno.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView username = (TextView) findViewById(R.id.usernameText);
        username.setText(nombreUsuario);
        //Guardar los datos que vamos a enviar a la tarea
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .build();

        //Solicitar la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(DescargarFotoBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) { //Cuando termine
                        //Recogemos el resultado, que será el titulo de la imagen
                        String result = status.getOutputData().getString("result");

                        //Subir la imagen al Cloud Storage de Firebase
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference islandRef = storageRef.child(result+".jpg");

                        final long ONE_MEGABYTE = 1024 * 1024;
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                //Nos devolverá la imagen y la cargaremos en el imageView
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                imageView.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });

                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);



    }

    public void editarFoto(View view){
        //Solicitar el permiso necesario para utilizar la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }
        else {
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            //Abrir la cámara
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CODIGO_FOTO_ARCHIVO);
            }
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Se ejecutará cuando el usuario haya hecho la foto
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {
            //Cargamos la imagen
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();


            //Leer del fichero en nombre de usuario
            BufferedReader ficherointerno = null;
            String nombreUsuario="";
            try {
                ficherointerno = new BufferedReader(new InputStreamReader(
                        openFileInput("nombreUsuario.txt")));
                nombreUsuario = ficherointerno.readLine();
                ficherointerno.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();

            String titulo=nombreUsuario+"_"+dtf.format(now);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Crear la referencia al storage
            StorageReference storageRef = storage.getReference();
            StorageReference mountainsRef = storageRef.child(titulo+".jpg");
            StorageReference mountainImagesRef = storageRef.child("images/"+titulo+".jpg");

            UploadTask uploadTask = mountainsRef.putBytes(fototransformada);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });

//Guardar los datos que vamos a enviar a la tarea
            Data datos = new Data.Builder()
                    .putString("user", nombreUsuario)
                    .putString("titulo", titulo)
                    .build();

            //Solicitar la ejecución de la tarea
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(SubirFotoBBDD.class).setInputData(datos).build();
            WorkManager.getInstance(getApplicationContext()).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, status -> {
                        if (status != null && status.getState().isFinished()) { //Cuando termine
                            //Recogemos el resultado, que será el titulo de la imagen
                            String result = status.getOutputData().getString("result");
                        }
                    });

            WorkManager.getInstance(getApplicationContext()).enqueue(otwr);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100:{
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, CODIGO_FOTO_ARCHIVO);
                    }
                }
                else {
                    // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA
                    Intent i = new Intent (this, Perfil.class);
                    startActivity(i);
                }
                return;
            }
        }
    }

}

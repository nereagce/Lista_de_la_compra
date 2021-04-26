package com.example.listadelacompra.registro;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

public class RegistroBBDD extends Worker {

    public RegistroBBDD(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Recoger los datos enviados
        String user= getInputData().getString("user");
        String nom= getInputData().getString("nom");
        String email= getInputData().getString("email");
        String pass= getInputData().getString("pass");

        //Lanzar la petición al servidor
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/ngonzalez114/WEB/registro.php";
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user", user)
                    .appendQueryParameter("nom", nom)
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("pass", pass);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();


            int statusCode = urlConnection.getResponseCode();
            Log.i("Response message",urlConnection.getResponseMessage());
            Log.i("Status code", String.valueOf(statusCode));
            if (statusCode == 200) {//Si la respuesta es 200 OK
                //Guardar el resultado en un String
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                Log.i("Result","."+result);
                inputStream.close();
                //Devolver el resultado
                Data datos = new Data.Builder()
                        .putString("result",result)
                        .build();
                return Result.success(datos);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
        }

        return Result.failure();
    }
}

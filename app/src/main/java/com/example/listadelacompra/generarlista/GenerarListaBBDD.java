package com.example.listadelacompra.generarlista;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GenerarListaBBDD extends Worker {

    public GenerarListaBBDD(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Recogemos el nombre de usuario que hemos enviado a la tarea
        String user = getInputData().getString("user");;


        //Hacemos la petición al servidor
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/ngonzalez114/WEB/generarLista.php";
        HttpURLConnection urlConnection = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("user", user);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Recogemos el código de la respuesta
            int statusCode = urlConnection.getResponseCode();
            Log.i("Response message", urlConnection.getResponseMessage());
            Log.i("Status code", String.valueOf(statusCode));
            if (statusCode == 200) { //Si es 200 OK
                //Recoger el resultado en un String
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                Log.i("Result", result);
                inputStream.close();
                //Devolver el resultado
                Data datos = new Data.Builder()
                        .putString("result", result)
                        .build();
                return Result.success(datos);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Si el código de respuesta no es 200 OK nos dirá que la tarea ha fallado
        return Result.failure();
    }
}

package com.example.listadelacompra.perfil;

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

public class DescargarFotoBBDD extends Worker {
    public DescargarFotoBBDD(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Recogemos los datos que hemos enviado
        String user = getInputData().getString("user");

        //Lanzamos la petición al servidor con los datos necesarios
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/ngonzalez114/WEB/descargarFoto.php";
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

            //Recibimos la respuesta y la devolvemos
            int statusCode = urlConnection.getResponseCode();
            Log.i("Response message", urlConnection.getResponseMessage());
            Log.i("Status code", String.valueOf(statusCode));
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                Log.i("Result", result);
                inputStream.close();
                Data datos = new Data.Builder()
                        .putString("result", result)
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

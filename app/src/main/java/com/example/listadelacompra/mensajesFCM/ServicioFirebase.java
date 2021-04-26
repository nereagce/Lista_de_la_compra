package com.example.listadelacompra.mensajesFCM;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {
    public ServicioFirebase() {

    }
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {

        }
        if (remoteMessage.getNotification() != null) {
            Log.i("Message Notification Body: ", remoteMessage.getNotification().getBody());
        }

    }

    @Override
    public void onNewToken(String s) { //Cuando se cree un nuevo token
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.i("TOKEN:",token);
                    }
                });
        //Suscribimos al usuario al tema 'listacompra' que utilizaremos para mandar notificaciones a todos los usuarios
        FirebaseMessaging.getInstance().subscribeToTopic("listacompra");

    }

}

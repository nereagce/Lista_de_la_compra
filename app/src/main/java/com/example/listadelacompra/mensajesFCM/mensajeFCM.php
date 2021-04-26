<?php
#Creamos la cabecera con la clave e indicando que los datos irán en formato json
$cabecera= array(
'Authorization: key=AAAA9EqJroY:APA91bEHQzwKUEI_NhZM-NQ7MuLbw1j3F96i4jkTqqxtRnEeXW3zzx-BD4cYNKMtNxwd7cs29cm525ImD-R3a1icYEn6ZXNFpp8CiTtcmclclnJq81ddFxgXvV-uV_oLnlyZragpOH1N',
'Content-Type: application/json'
);

#indicamos qué dirá la notificación y a quién se le enviará (en este caso a todos los usuarios, ya que todos están suscritos al tema 'listacompra')
$msg = array (
    #'to' => 'drAzgF_MTPg:APA91bHz1782SC8Jxcii0Q8S6BOifShw0yMPDUuAuxJHD4JF6MFFpz2HnO-hQIx0yJ8sjNbE33Kwv9FTvL20yz96eYBANysGlmTjGwYezhVDP5t9bz4flONY2VPINjZ3HYB_FSa7w0oY',
    'condition' => "'listacompra' in topics",
    'notification' => array (
        'body' => '¿Hace cuánto que no haces la compra? ',
        'title' => '¡Hola!'
    )
);
#Convertimos el mensaje a formato json
$msgJSON= json_encode ( $msg);
echo $msgJSON;

$ch = curl_init(); #inicializar el handler de curl
#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );
#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );
#ejecutar la llamada
$resultado= curl_exec( $ch );

if (curl_errno($ch)) {
    print curl_error($ch);
    echo "Error";
}

#cerrar el handler de curl
curl_close( $ch );

#Devolvemos el resultado
echo $resultado;

?>
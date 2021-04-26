<?php
$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xngonzalez114"; #el usuario para esa base de datos
$DB_PASS="HVM89VyRJU"; #la clave para ese usuario
$DB_DATABASE="Xngonzalez114_ListaDeLaCompra"; #la base de datos a la que hay que conectarse
# Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);
#Comprobamos conexión
if (mysqli_connect_errno($con)) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}else{
    $user = $_POST['user'];

    #Conseguir el titulo de la imagen a descargar
    $consulta = "SELECT imagen FROM Usuarios WHERE usuario='$user'";
    $resultado = $con->query($consulta);
    $fila = $resultado->fetch_assoc();
    echo $fila['imagen'];
}
?>
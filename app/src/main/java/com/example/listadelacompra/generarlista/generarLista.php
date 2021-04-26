<?php

#Realizamos la conexión con la base de datos
$DB_SERVER="localhost";
$DB_USER="Xngonzalez114";
$DB_PASS="HVM89VyRJU";
$DB_DATABASE="Xngonzalez114_ListaDeLaCompra";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if (mysqli_connect_errno($con)) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}else{
    #Recogemos el nombre de usuario que hemos enviado
    $user = $_POST['user'];

    Reslizamos la consulta para conseguir los productos de un usuario
    $consulta = "SELECT p.id,p.cantMin,p.nombre FROM Productos AS p WHERE p.userID='$user'";
    $resultado = $con->query($consulta);
    $output=array();
    while($fila = $resultado->fetch_assoc()){ #Para todos los productos
        $id = $fila['id'];
        $cantMin = $fila['cantMin'];
        $nombre = $fila['nombre'];

        #Conseguimos la cantidad total del producto
        $consulta = "SELECT SUM(c.cant) AS suma FROM Cantidades AS c WHERE c.productoID=$id";
        $resultado2 = $con->query($consulta);
        $fila2 = $resultado2->fetch_assoc();
        $cantSum = $fila2['suma'];

        #Si la cantidad total es menor o igual a la mínima establecida por el usuario, guardaremos el producto en un array
        if($cantSum<=$cantMin){
            $arrayresultados = array(
            'nombre' => $nombre,
            );
            array_push($output,$arrayresultados);
        }
    }
    #Convertimos el array en json y lo devolvemos como respuesta
    echo json_encode($output);
}



?>
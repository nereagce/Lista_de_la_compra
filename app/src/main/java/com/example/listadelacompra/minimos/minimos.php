<?php
#Realizar la conexión con l abase de datos
$DB_SERVER="localhost";
$DB_USER="Xngonzalez114";
$DB_PASS="HVM89VyRJU";
$DB_DATABASE="Xngonzalez114_ListaDeLaCompra";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if (mysqli_connect_errno($con)) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}else{
    #Recoger los datos que hemos enviado
    $user=$_POST["user"];
    $funcion=$_POST["funcion"];
    $nombre=$_POST["nombre"];
    $cant=$_POST["cant"];


    if($funcion == "conseguir"){ # Si la función es conseguir
        #Realizar la consulta para conseguir las cantidades minimas establecidas por el usuario
        $consulta = "SELECT nombre,cantMin FROM Productos WHERE userID='$user'";
        $resultado = $con->query($consulta);
        $output=array();
        #Guardar los resultados en un array
        while($fila = $resultado->fetch_assoc()){
            $arrayresultados = array(
            'nombre' => $fila["nombre"],
            'cantMin' => $fila["cantMin"],
            );
            array_push($output,$arrayresultados);
        }
        #Convertir el array a formato json para devolverlo
        echo json_encode($output);

    }else{ Si la funcion es añadir
        #Conseguir el id del producto
        $consulta = "SELECT id FROM Productos WHERE userID='$user' AND nombre='$nombre'";
        $resultado = $con->query($consulta);
        if($resultado -> num_rows > 0){ #Si lo ha encontrado
            #Llevar a cabo la actualización
            $fila = $resultado->fetch_assoc();
            $id = $fila["id"];

            $consulta = "UPDATE Productos SET cantMin=$cant WHERE id=$id";
            $con->query($consulta);
        }else{#Si no lo ha encontrado
            #Indicar que no existe
            echo("No existe");
        }
    }


}
?>
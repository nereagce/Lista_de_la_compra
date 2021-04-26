<?php
#Realizar la conexión con la base de datos
$DB_SERVER="localhost";
$DB_USER="Xngonzalez114";
$DB_PASS="HVM89VyRJU";
$DB_DATABASE="Xngonzalez114_ListaDeLaCompra";

$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if (mysqli_connect_errno($con)) {
    echo 'Error de conexion: ' . mysqli_connect_error();
    exit();
}else{
    #Recoger los datos necesarios
    $user=$_POST["user"];
    $conseguir=$_POST["conseguir"];
    $añadir=$_POST["añadir"];
    $eliminar=$_POST["eliminar"];
    $nombre=$_POST["nombre"];
    $caducidad=$_POST["caducidad"];
    $cant=$_POST["cant"];

    if($conseguir == "true"){ #Si la función a ejecutar es conseguir
        #Relizar la consulta para recoger los productos de usuario
        $consulta = "SELECT n.nombre, c.caducidad, c.cant FROM (SELECT p.id AS id, p.nombre FROM Productos AS p WHERE userID='$user') AS n INNER JOIN Cantidades AS c ON n.id = c.productoID";
        $resultado = $con->query($consulta);
        $output=array();
        #Guardar los productos en un array
        while($fila = $resultado->fetch_assoc()){
            $arrayresultados = array(
            'nombre' => $fila["nombre"],
            'caducidad' => $fila["caducidad"],
            'cant' => $fila["cant"],
            );
            array_push($output,$arrayresultados);
        }
        #Devolver el array en formato json
        echo json_encode($output);

    }
    if($eliminar="true"){ #Si hay que eliminar
    echo "ELIMINAR";
         #Buscar en la base de datos el producto
         $consulta = "SELECT id,cantMin FROM Productos WHERE userID='$user' AND nombre='$nombre'";
         $resultado = $con->query($consulta);
         if($resultado -> num_rows > 0){#Si existe
             $fila = $resultado->fetch_assoc();
             $id = $fila["id"];
             $cantMin = $fila["cantMin"];
             #Conseguir la cantidad total del producto que posee el usuario
             $consulta = "SELECT SUM(c.cant) AS suma FROM Cantidades AS c WHERE c.productoID=$id";
             $resultado = $con->query($consulta);
             $fila = $resultado->fetch_assoc();
             $cantSum = $fila['suma'];
             #Conseguir las filas de cantidades que hacen referencia a dicho producto
             $consulta = "SELECT c.id,c.cant FROM Cantidades AS c WHERE c.productoID=$id";
             $resultado = $con->query($consulta);
             if($cant > $cantSum){#Si la cantidad que se quiere eliminar es maypr a la existente, notificarlo
                 echo "Cantidad mayor";
             }else{ #Si es igual o menor
                 $quedan = $cantSum - $cant;
                 while($fila = $resultado->fetch_assoc()){ #Para todas las filas
                     $cantHay = $fila['cant'];
                     $idCant = $fila['id'];
                     if( $cant >= $cantHay){ #Si la cnatidad a eliminares mayor o igual a la de la fila
                         #Eliminar la fila y restar la cantidad eliminada
                         $cant = $cant - $cantHay;
                         $consulta = "DELETE FROM Cantidades WHERE id=$idCant";
                         $con->query($consulta);
                     }else{ # Si es menor
                         #Actualizar la fila con el nuevo valor
                         $update = $cantHay - $cant;
                         $cant = 0;
                         $consulta = "UPDATE Cantidades SET cant=$update WHERE id=$idCant";
                         $con->query($consulta);
                     }
                 }
                 if($quedan<$cantMin){ #Si despues de eliminar, la cantidad restante es menor a la mínima, notificamos
                     echo "Minimo";
                 }
             }
         }else{ #Si el producto no existe, lo notificaremos
             echo("No existe");
         }
    }
    if($añadir="true"){ #Si hay que añadir un producto
        echo "AÑADIR";
        #Comprobamos si existe
        $consulta = "SELECT id FROM Productos WHERE userID='$user' AND nombre='$nombre'";
        $resultado = $con->query($consulta);
        if($resultado -> num_rows > 0){ #Si existe
            $fila = $resultado->fetch_assoc();
            $id = $fila["id"];
            #Comprobar si existe un registro con la misma fecha de caducidad
            $consulta = "SELECT c.cant FROM Cantidades AS c WHERE c.productoID=$id AND c.caducidad='$caducidad'";
            $resultado = $con->query($consulta);
            if($resultado -> num_rows > 0){#Si existe
                #Actualizar el registro sumandole la cantidad introducida por el usuario
                $fila = $resultado->fetch_assoc();
                $cantidad = $fila["cant"];
                $update = $cant+$cantidad;
                $consulta = "UPDATE Cantidades SET cant=$update WHERE productoID=$id AND caducidad='$caducidad'";
                $con->query($consulta);
            }else{ # Si no existe
                #Crear un nuevo registro
                $consulta = "INSERT INTO Cantidades (cant,caducidad,productoID) VALUES ($cant,'$caducidad',$id)";
                $con->query($consulta);
            }
        }else{#Si el producto no existe
            #Crear el registro del producto
            $consulta = "INSERT INTO Productos (nombre,cantMin,userID) VALUES ('$nombre',0,'$user')";
            $con->query($consulta);

            #Conseguir el id que se le ha asignado
            $consulta = "SELECT id FROM Productos WHERE userID='$user' AND nombre='$nombre'";
            $resultado = $con->query($consulta);
            $fila = $resultado->fetch_assoc();
            $prodID = $fila["id"];

            #Hacer el registro de la cantidad
            $consulta = "INSERT INTO Cantidades (cant,caducidad,productoID) VALUES ($cant,'$caducidad',$prodID)";
            $con->query($consulta);
        }
    }
}
?>
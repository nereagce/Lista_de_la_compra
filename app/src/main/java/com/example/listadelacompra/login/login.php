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
    #Recogemos los datos enviados
    $user=$_POST["user"];
    $pass=$_POST["pass"];

    $hash=hash('sha256',$hash);
    #Hacemos la consulta para comprobar si exite la conbinación usuario/contraseña
    $query = "SELECT usuario, contraseña FROM Usuarios WHERE usuario=? AND contraseña=?";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ss", $user, $hash);
    $stmt->execute();
    $emaitza = $stmt->get_result();
    if ( $emaitza->num_rows==1) {#Si la respuesta tiene una línea, existe
        echo "Login";
    } else { #En caso contrario, devuelve el error
        echo "Error: " . $sql . "<br>" . $con->error;
    }

}

?>
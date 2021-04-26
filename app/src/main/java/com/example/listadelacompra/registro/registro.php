<?php
#Realizar la conexión con la base de datos
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
    #Recoger los datos
    $user=$_POST["user"];
    $nom=$_POST["nom"];
    $email=$_POST["email"];
    $pass=$_POST["pass"];

    $hash=hash('sha256',$pass);
    # Ejecutar la sentencia SQL
    $query = "INSERT INTO Usuarios(usuario,nombre,email,contraseña) VALUES (?, ?, ?, ?)";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ssss", $user, $nom,$email,$hash);

    #Comprobar si ha hecho bien el insert y notificarlo
    if ($stmt->execute() === TRUE) {
        echo "New record created successfully";
    } else {
        echo "Error: " . $sql . "<br>" . $con->error;
    }
}
?>
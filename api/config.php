<?php
$host = "localhost";
$database = "estacionamiento_utp";
$user = "root";
$password = "";

try {
    $pdo = new PDO(
        "mysql:host=$host;dbname=$database;charset=utf8mb4",
        $user,
        $password,
        [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        ]
    );
} catch (PDOException $error) {
    http_response_code(500);
    echo json_encode([
        "ok" => false,
        "message" => "Error de conexion con MySQL. Revisa XAMPP y phpMyAdmin.",
        "detail" => $error->getMessage()
    ]);
    exit;
}

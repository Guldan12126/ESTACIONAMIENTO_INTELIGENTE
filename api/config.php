<?php
$host = "localhost";
$database = "estacionamiento_utp";
$user = "root";
$password = "";

// Clave que debe enviar el script de la camara (header X-Camera-Key).
// Debe coincidir EXACTAMENTE con CAMERA_API_KEY en el script de Python.
define("CAMERA_API_KEY", "utp-sgei-camara-2026");

// Carpeta publica donde se guarda el ultimo frame que envia la camara,
// para que el panel de admin lo muestre como una imagen normal.
define("CAMERA_FRAME_DIR", dirname(__DIR__) . DIRECTORY_SEPARATOR . "camera_feed");
define("CAMERA_FRAME_FILE", "latest.jpg");

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

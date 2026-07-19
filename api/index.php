<?php
        session_start();
header("Content-Type: application/json; charset=utf-8");

require_once __DIR__ . "/config.php";

$action = $_GET["action"] ?? "";
$input = json_decode(file_get_contents("php://input"), true) ?? [];

function respond($data, $status = 200) {
    http_response_code($status);
    echo json_encode($data);
    exit;
}

function clean_text($value) {
    return trim(strip_tags((string) $value));
}

function monitor_log($level, $module, $message, $context = []) {
    $level = strtoupper($level);
    $logDir = dirname(__DIR__) . DIRECTORY_SEPARATOR . "logs";
    if (!is_dir($logDir)) {
        mkdir($logDir, 0777, true);
    }

    $contextText = $context ? " " . json_encode($context, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) : "";
    $line = date("Y-m-d H:i:s") . " " . $level . " [PHP] " . $module . " - " . $message . $contextText . PHP_EOL;
    file_put_contents($logDir . DIRECTORY_SEPARATOR . "estacionamiento.log", $line, FILE_APPEND | LOCK_EX);

    if ($level === "ERROR") {
        file_put_contents($logDir . DIRECTORY_SEPARATOR . "errores.log", $line, FILE_APPEND | LOCK_EX);
    }
}

function log_info($module, $message, $context = []) {
    monitor_log("INFO", $module, $message, $context);
}

function log_warn($module, $message, $context = []) {
    monitor_log("WARN", $module, $message, $context);
}

function log_error_event($module, $message, $context = []) {
    monitor_log("ERROR", $module, $message, $context);
}

function current_user() {
    return $_SESSION["user"] ?? null;
}

function require_login() {
    $user = current_user();
    if (!$user) {
        log_warn("Autenticacion", "Intento de acceso sin sesion activa");
        respond(["ok" => false, "message" => "Debes iniciar sesion."], 401);
    }
    return $user;
}

function require_admin() {
    global $action;
    $user = require_login();
    if ($user["rol"] !== "administrador") {
        log_warn("Panel Administrador", "Acceso denegado a accion administrativa", [
            "accion" => $action,
            "usuario" => $user["codigo"]
        ]);
        respond(["ok" => false, "message" => "Solo el administrador puede realizar esta accion."], 403);
    }
    log_info("Panel Administrador", "Acceso administrativo autorizado", [
        "accion" => $action,
        "usuario" => $user["codigo"]
    ]);
    return $user;
}

function valid_codigo($codigo) {
    return preg_match('/^u\d{8}$/', $codigo) === 1;
}

function normalize_placa($placa) {
    return strtoupper(str_replace("-", "", clean_text($placa)));
}

function valid_placa($placa) {
    return preg_match('/^[A-Z]{3}\d{3}$/', $placa) === 1 || preg_match('/^\d{4}[A-Z]{2}$/', $placa) === 1;
}

function valid_tipo($tipo) {
    return in_array($tipo, ["auto", "moto"], true);
}

function validate_notice($titulo, $mensaje, $categoria) {
    $categories = ["horario", "seguridad", "mantenimiento", "norma", "emergencia"];
    $badWords = ["idiota", "tonto", "mierda", "carajo"];
    $joined = strtolower($titulo . " " . $mensaje);

    if (!$titulo || !$mensaje || !in_array($categoria, $categories, true)) {
        return "Titulo, mensaje y categoria son obligatorios.";
    }

    if (mb_strlen($titulo) < 4 || mb_strlen($mensaje) < 10) {
        return "El titulo debe tener minimo 4 caracteres y el mensaje minimo 10.";
    }

    if (mb_strlen($titulo) > 80 || mb_strlen($mensaje) > 200) {
        return "El titulo no debe pasar 80 caracteres y el mensaje no debe pasar 200.";
    }

    if (preg_match('/^\d+$/', $titulo . $mensaje)) {
        return "El aviso no puede contener solo numeros.";
    }

    if (!preg_match('/[a-zA-Z]/', $titulo . $mensaje)) {
        return "El aviso debe contener texto valido.";
    }

    if (preg_match('/(.)\1{5,}/i', $joined) || preg_match('/(ja){4,}|(asdf){2,}/i', $joined)) {
        return "El aviso parece texto repetido o de prueba.";
    }

    foreach ($badWords as $word) {
        if (str_contains($joined, $word)) {
            return "El aviso contiene palabras no permitidas.";
        }
    }

    return "";
}

function fetch_all_data($pdo) {
    $user = current_user();
    $isAdmin = $user && $user["rol"] === "administrador";

    $spaces = $pdo->query("
        SELECT
            e.id,
            e.codigo_espacio AS codigo,
            e.codigo_espacio AS idVisual,
            e.zona AS zone,
            e.estado AS status,
            e.placa,
            e.hora_ingreso AS horaIngresoRaw,
            DATE_FORMAT(e.hora_ingreso, '%H:%i') AS horaIngreso,
            u.codigo AS usuarioCodigo,
            u.nombre AS usuarioNombre,
            v.tipo AS tipoVehiculo
        FROM espacios e
        LEFT JOIN usuarios u ON u.id = e.usuario_id
        LEFT JOIN vehiculos v ON v.placa = e.placa AND v.estado = 'dentro'
        ORDER BY e.zona, CAST(SUBSTRING(e.codigo_espacio, 2) AS UNSIGNED)
    ")->fetchAll();

    if ($isAdmin) {
        $stmt = $pdo->query("
            SELECT
                v.id, v.placa, v.tipo, v.estado,
                e.codigo_espacio AS espacio,
                u.codigo AS usuarioCodigo,
                u.nombre AS usuarioNombre,
                DATE_FORMAT(v.hora_ingreso, '%H:%i') AS horaIngreso
            FROM vehiculos v
            INNER JOIN espacios e ON e.id = v.espacio_id
            INNER JOIN usuarios u ON u.id = v.usuario_id
            WHERE v.estado = 'dentro'
            ORDER BY v.hora_ingreso DESC
        ");
        $vehicles = $stmt->fetchAll();
    } else {
        $stmt = $pdo->prepare("
            SELECT
                v.id, v.placa, v.tipo, v.estado,
                e.codigo_espacio AS espacio,
                u.codigo AS usuarioCodigo,
                u.nombre AS usuarioNombre,
                DATE_FORMAT(v.hora_ingreso, '%H:%i') AS horaIngreso
            FROM vehiculos v
            INNER JOIN espacios e ON e.id = v.espacio_id
            INNER JOIN usuarios u ON u.id = v.usuario_id
            WHERE v.estado = 'dentro' AND v.usuario_id = ?
            ORDER BY v.hora_ingreso DESC
        ");
        $stmt->execute([$user["id"]]);
        $vehicles = $stmt->fetchAll();
    }

    $notices = $pdo->query("
        SELECT a.id, a.titulo, a.mensaje, a.categoria, DATE_FORMAT(a.creado_en, '%d/%m/%Y %H:%i') AS fecha
        FROM avisos a
        ORDER BY a.creado_en DESC, a.id DESC
    ")->fetchAll();

    if ($isAdmin) {
        $stmt = $pdo->query("
            SELECT
                h.tipo AS type,
                h.placa,
                h.tipo_vehiculo AS tipoVehiculo,
                e.codigo_espacio AS espacio,
                u.nombre AS usuarioNombre,
                r.nombre AS registradoPor,
                DATE(h.fecha_hora) AS date,
                DATE_FORMAT(h.fecha_hora, '%d/%m/%Y %H:%i') AS dateTime
            FROM historial_movimientos h
            INNER JOIN espacios e ON e.id = h.espacio_id
            INNER JOIN usuarios u ON u.id = h.usuario_id
            INNER JOIN usuarios r ON r.id = h.registrado_por
            ORDER BY h.fecha_hora DESC
            LIMIT 80
        ");
        $history = $stmt->fetchAll();
    } else {
        $stmt = $pdo->prepare("
            SELECT
                h.tipo AS type,
                h.placa,
                h.tipo_vehiculo AS tipoVehiculo,
                e.codigo_espacio AS espacio,
                u.nombre AS usuarioNombre,
                u.nombre AS registradoPor,
                DATE(h.fecha_hora) AS date,
                DATE_FORMAT(h.fecha_hora, '%d/%m/%Y %H:%i') AS dateTime
            FROM historial_movimientos h
            INNER JOIN espacios e ON e.id = h.espacio_id
            INNER JOIN usuarios u ON u.id = h.usuario_id
            WHERE h.usuario_id = ?
            ORDER BY h.fecha_hora DESC
            LIMIT 50
        ");
        $stmt->execute([$user["id"]]);
        $history = $stmt->fetchAll();
    }

    $users = [];
    if ($isAdmin) {
        $users = $pdo->query("
            SELECT id, codigo, nombre, rol
            FROM usuarios
            ORDER BY nombre
        ")->fetchAll();
    }

    return [
        "spaces" => $spaces,
        "vehicles" => $vehicles,
        "notices" => $notices,
        "history" => $history,
        "users" => $users
    ];
}

try {
    if ($action === "login") {
        $codigo = strtolower(clean_text($input["codigo"] ?? ""));
        $password = (string) ($input["password"] ?? "");

        if (!valid_codigo($codigo)) {
            log_warn("Login", "Codigo con formato invalido", ["codigo" => $codigo]);
            respond(["ok" => false, "message" => "El codigo debe tener formato u seguido de 8 numeros."], 400);
        }

        $stmt = $pdo->prepare("SELECT id, codigo, nombre, password_hash, rol FROM usuarios WHERE codigo = ?");
        $stmt->execute([$codigo]);
        $user = $stmt->fetch();

        if (!$user || !password_verify($password, $user["password_hash"])) {
            log_warn("Login", "Credenciales incorrectas", ["codigo" => $codigo]);
            respond(["ok" => false, "message" => "Codigo o contrasena incorrectos."], 401);
        }

        unset($user["password_hash"]);
        $_SESSION["user"] = $user;
        log_info("Login", "Inicio de sesion exitoso", ["codigo" => $user["codigo"], "rol" => $user["rol"]]);
        respond(["ok" => true, "user" => $user, "data" => fetch_all_data($pdo)]);
    }

    if ($action === "logout") {
        $user = current_user();
        log_info("Login", "Cierre de sesion", ["codigo" => $user["codigo"] ?? "anonimo"]);
        session_destroy();
        respond(["ok" => true]);
    }

    if ($action === "data") {
        $user = require_login();
        log_info("Panel", "Consulta de datos del panel principal", ["codigo" => $user["codigo"], "rol" => $user["rol"]]);
        respond(["ok" => true, "data" => fetch_all_data($pdo)]);
    }

    if ($action === "register_vehicle") {
        $actor = require_login();
        $placa = normalize_placa($input["placa"] ?? "");
        $tipo = strtolower(clean_text($input["tipo"] ?? ""));
        $codigoDueno = strtolower(clean_text($input["dueno"] ?? ""));
        $codigoEspacio = clean_text($input["espacio"] ?? "");

        if (!valid_placa($placa)) {
            log_warn("Registro de vehiculos", "Placa invalida", ["placa" => $placa, "usuario" => $actor["codigo"]]);
            respond(["ok" => false, "message" => "Placa invalida. Usa ABC-123, ABC123, 1234-AB o 1234AB."], 400);
        }

        if (!valid_tipo($tipo)) {
            log_warn("Registro de vehiculos", "Tipo de vehiculo invalido", ["tipo" => $tipo, "usuario" => $actor["codigo"]]);
            respond(["ok" => false, "message" => "Solo se permite registrar autos y motos."], 400);
        }

        if ($actor["rol"] === "alumno") {
            $codigoDueno = $actor["codigo"];
        } elseif (!valid_codigo($codigoDueno)) {
            log_warn("Registro de vehiculos", "Codigo de dueno invalido", ["dueno" => $codigoDueno]);
            respond(["ok" => false, "message" => "Codigo UTP del dueno invalido."], 400);
        }

        $stmt = $pdo->prepare("SELECT id, codigo, nombre FROM usuarios WHERE codigo = ?");
        $stmt->execute([$codigoDueno]);
        $owner = $stmt->fetch();
        if (!$owner) {
            log_warn("Registro de vehiculos", "Usuario dueno no existe", ["dueno" => $codigoDueno]);
            respond(["ok" => false, "message" => "El usuario dueno no existe."], 400);
        }

        $stmt = $pdo->prepare("SELECT id FROM vehiculos WHERE placa = ? AND estado = 'dentro'");
        $stmt->execute([$placa]);
        if ($stmt->fetch()) {
            log_warn("Entrada de vehiculos", "Placa con ingreso activo duplicado", ["placa" => $placa]);
            respond(["ok" => false, "message" => "Esta placa ya tiene un ingreso activo."], 400);
        }

        $pdo->beginTransaction();

        if ($codigoEspacio) {
            $stmt = $pdo->prepare("SELECT id, estado FROM espacios WHERE codigo_espacio = ? FOR UPDATE");
            $stmt->execute([$codigoEspacio]);
        } else {
            $stmt = $pdo->query("SELECT id, estado FROM espacios WHERE estado = 'disponible' ORDER BY codigo_espacio LIMIT 1 FOR UPDATE");
        }

        $space = $stmt->fetch();
        if (!$space || $space["estado"] !== "disponible") {
            $pdo->rollBack();
            log_warn("Gestion de espacios", "Intento de ocupar espacio no disponible", [
                "placa" => $placa,
                "espacio" => $codigoEspacio ?: "automatico"
            ]);
            respond(["ok" => false, "message" => "Solo puedes registrar en un espacio disponible."], 400);
        }

        $stmt = $pdo->prepare("
            INSERT INTO vehiculos (placa, tipo, usuario_id, espacio_id, estado, hora_ingreso)
            VALUES (?, ?, ?, ?, 'dentro', NOW())
        ");
        $stmt->execute([$placa, $tipo, $owner["id"], $space["id"]]);

        $stmt = $pdo->prepare("
            UPDATE espacios
            SET estado = 'ocupado', placa = ?, usuario_id = ?, hora_ingreso = NOW()
            WHERE id = ?
        ");
        $stmt->execute([$placa, $owner["id"], $space["id"]]);

        $stmt = $pdo->prepare("
            INSERT INTO historial_movimientos (tipo, placa, tipo_vehiculo, espacio_id, usuario_id, registrado_por, fecha_hora)
            VALUES ('ingreso', ?, ?, ?, ?, ?, NOW())
        ");
        $stmt->execute([$placa, $tipo, $space["id"], $owner["id"], $actor["id"]]);

        $pdo->commit();
        log_info("Registro de vehiculos", "Vehiculo registrado correctamente", [
            "placa" => $placa,
            "tipo" => $tipo,
            "dueno" => $owner["codigo"]
        ]);
        log_info("Entrada de vehiculos", "Entrada de vehiculo registrada", [
            "placa" => $placa,
            "espacioId" => $space["id"],
            "registradoPor" => $actor["codigo"]
        ]);
        respond(["ok" => true, "message" => "Ingreso registrado correctamente.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "exit_vehicle") {
        $actor = require_login();
        $placa = normalize_placa($input["placa"] ?? "");

        $pdo->beginTransaction();

        $stmt = $pdo->prepare("
            SELECT v.id, v.tipo, v.usuario_id, v.espacio_id
            FROM vehiculos v
            WHERE v.placa = ? AND v.estado = 'dentro'
            FOR UPDATE
        ");
        $stmt->execute([$placa]);
        $vehicle = $stmt->fetch();

        if (!$vehicle) {
            $pdo->rollBack();
            log_warn("Salida de vehiculos", "No se encontro ingreso activo", ["placa" => $placa]);
            respond(["ok" => false, "message" => "No se encontro un ingreso activo para esa placa."], 404);
        }

        if ($actor["rol"] !== "administrador" && (int) $vehicle["usuario_id"] !== (int) $actor["id"]) {
            $pdo->rollBack();
            log_warn("Salida de vehiculos", "Salida denegada por permisos", [
                "placa" => $placa,
                "usuario" => $actor["codigo"]
            ]);
            respond(["ok" => false, "message" => "Solo puedes registrar salida de tu propio vehiculo."], 403);
        }

        $stmt = $pdo->prepare("UPDATE vehiculos SET estado = 'fuera', hora_salida = NOW() WHERE id = ?");
        $stmt->execute([$vehicle["id"]]);

        $stmt = $pdo->prepare("
            UPDATE espacios
            SET estado = 'disponible', placa = NULL, usuario_id = NULL, hora_ingreso = NULL
            WHERE id = ?
        ");
        $stmt->execute([$vehicle["espacio_id"]]);

        $stmt = $pdo->prepare("
            INSERT INTO historial_movimientos (tipo, placa, tipo_vehiculo, espacio_id, usuario_id, registrado_por, fecha_hora)
            VALUES ('salida', ?, ?, ?, ?, ?, NOW())
        ");
        $stmt->execute([$placa, $vehicle["tipo"], $vehicle["espacio_id"], $vehicle["usuario_id"], $actor["id"]]);

        $pdo->commit();
        log_info("Salida de vehiculos", "Salida registrada correctamente", [
            "placa" => $placa,
            "registradoPor" => $actor["codigo"]
        ]);
        log_info("Pagos", "Pago asociado a salida registrado como evento", ["placa" => $placa]);
        respond(["ok" => true, "message" => "Salida registrada correctamente.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "toggle_reserve") {
        require_admin();
        $codigoEspacio = clean_text($input["espacio"] ?? "");

        $stmt = $pdo->prepare("SELECT estado FROM espacios WHERE codigo_espacio = ?");
        $stmt->execute([$codigoEspacio]);
        $space = $stmt->fetch();

        if (!$space || $space["estado"] === "ocupado") {
            log_warn("Reservas", "Reserva rechazada para espacio inexistente u ocupado", ["espacio" => $codigoEspacio]);
            respond(["ok" => false, "message" => "Solo se puede reservar o liberar un espacio no ocupado."], 400);
        }

        $newStatus = $space["estado"] === "reservado" ? "disponible" : "reservado";
        $stmt = $pdo->prepare("UPDATE espacios SET estado = ? WHERE codigo_espacio = ?");
        $stmt->execute([$newStatus, $codigoEspacio]);
        log_info("Reservas", "Estado de reserva actualizado", ["espacio" => $codigoEspacio, "estado" => $newStatus]);
        log_info("Gestion de espacios", "Espacio actualizado", ["espacio" => $codigoEspacio, "estado" => $newStatus]);
        respond(["ok" => true, "message" => "Estado actualizado.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "add_notice") {
        $actor = require_admin();
        $titulo = clean_text($input["titulo"] ?? "");
        $mensaje = clean_text($input["mensaje"] ?? "");
        $categoria = clean_text($input["categoria"] ?? "");
        $error = validate_notice($titulo, $mensaje, $categoria);

        if ($error) {
            log_warn("Panel Administrador", "Aviso rechazado por validacion", ["motivo" => $error]);
            respond(["ok" => false, "message" => $error], 400);
        }

        $stmt = $pdo->prepare("INSERT INTO avisos (titulo, mensaje, categoria, creado_por) VALUES (?, ?, ?, ?)");
        $stmt->execute([$titulo, $mensaje, $categoria, $actor["id"]]);
        log_info("Panel Administrador", "Aviso guardado", ["titulo" => $titulo, "categoria" => $categoria]);
        respond(["ok" => true, "message" => "Aviso guardado.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "delete_notice") {
        require_admin();
        $id = (int) ($input["id"] ?? 0);

        if ($id <= 0) {
            log_warn("Panel Administrador", "Intento de eliminar aviso invalido", ["id" => $id]);
            respond(["ok" => false, "message" => "Aviso invalido."], 400);
        }

        $stmt = $pdo->prepare("DELETE FROM avisos WHERE id = ?");
        $stmt->execute([$id]);

        if ($stmt->rowCount() === 0) {
            log_warn("Panel Administrador", "Aviso no encontrado al eliminar", ["id" => $id]);
            respond(["ok" => false, "message" => "El aviso ya no existe."], 404);
        }

        log_info("Panel Administrador", "Aviso retirado", ["id" => $id]);
        respond(["ok" => true, "message" => "Aviso retirado correctamente.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "save_user") {
        require_admin();
        $id = (int) ($input["id"] ?? 0);
        $codigo = strtolower(clean_text($input["codigo"] ?? ""));
        $nombre = clean_text($input["nombre"] ?? "");
        $password = (string) ($input["password"] ?? "");
        $rol = clean_text($input["rol"] ?? "") === "administrador" ? "administrador" : "alumno";

        if (!valid_codigo($codigo) || mb_strlen($nombre) < 3) {
            log_warn("Registro de usuarios", "Datos invalidos al guardar usuario", ["codigo" => $codigo]);
            respond(["ok" => false, "message" => "Codigo UTP o nombre invalido."], 400);
        }

        $stmt = $pdo->prepare("SELECT id FROM usuarios WHERE codigo = ? AND id <> ?");
        $stmt->execute([$codigo, $id]);
        if ($stmt->fetch()) {
            log_warn("Registro de usuarios", "Codigo duplicado al guardar usuario", ["codigo" => $codigo]);
            respond(["ok" => false, "message" => "Ese codigo UTP ya esta registrado."], 400);
        }

        if ($id > 0) {
            if ($password) {
                $hash = password_hash($password, PASSWORD_DEFAULT);
                $stmt = $pdo->prepare("UPDATE usuarios SET codigo = ?, nombre = ?, password_hash = ?, rol = ? WHERE id = ?");
                $stmt->execute([$codigo, $nombre, $hash, $rol, $id]);
            } else {
                $stmt = $pdo->prepare("UPDATE usuarios SET codigo = ?, nombre = ?, rol = ? WHERE id = ?");
                $stmt->execute([$codigo, $nombre, $rol, $id]);
            }
        } else {
            if (strlen($password) < 6) {
                log_warn("Registro de usuarios", "Contrasena demasiado corta", ["codigo" => $codigo]);
                respond(["ok" => false, "message" => "La contrasena debe tener minimo 6 caracteres."], 400);
            }
            $hash = password_hash($password, PASSWORD_DEFAULT);
            $stmt = $pdo->prepare("INSERT INTO usuarios (codigo, nombre, password_hash, rol) VALUES (?, ?, ?, ?)");
            $stmt->execute([$codigo, $nombre, $hash, $rol]);
        }

        log_info("Registro de usuarios", "Usuario guardado correctamente", [
            "codigo" => $codigo,
            "rol" => $rol,
            "modo" => $id > 0 ? "edicion" : "creacion"
        ]);
        respond(["ok" => true, "message" => "Usuario guardado correctamente.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "delete_user") {
        $actor = require_admin();
        $id = (int) ($input["id"] ?? 0);

        if ($id === (int) $actor["id"]) {
            log_warn("Registro de usuarios", "Intento de eliminar usuario propio", ["id" => $id]);
            respond(["ok" => false, "message" => "No puedes eliminar tu propio usuario."], 400);
        }

        $stmt = $pdo->prepare("SELECT id FROM vehiculos WHERE usuario_id = ? AND estado = 'dentro'");
        $stmt->execute([$id]);
        if ($stmt->fetch()) {
            log_warn("Registro de usuarios", "No se puede eliminar usuario con vehiculo dentro", ["id" => $id]);
            respond(["ok" => false, "message" => "No se puede eliminar un usuario con vehiculo dentro."], 400);
        }

        $stmt = $pdo->prepare("DELETE FROM usuarios WHERE id = ?");
        $stmt->execute([$id]);
        log_info("Registro de usuarios", "Usuario eliminado", ["id" => $id, "administrador" => $actor["codigo"]]);
        respond(["ok" => true, "message" => "Usuario eliminado.", "data" => fetch_all_data($pdo)]);
    }

    if ($action === "camera_status") {
        $sentKey = $_SERVER["HTTP_X_CAMERA_KEY"] ?? "";
        if (!hash_equals(CAMERA_API_KEY, $sentKey)) {
            log_warn("Camara", "Clave de camara invalida al actualizar espacios");
            respond(["ok" => false, "message" => "Clave de camara invalida."], 401);
        }

        $espacios = $input["espacios"] ?? [];
        if (!is_array($espacios) || !$espacios) {
            respond(["ok" => false, "message" => "No se recibieron espacios."], 400);
        }

        $actualizados = [];
        foreach ($espacios as $codigoEspacio => $estadoDetectado) {
            $codigoEspacio = clean_text($codigoEspacio);
            $estadoDetectado = strtolower(clean_text($estadoDetectado));

            if (!in_array($estadoDetectado, ["disponible", "ocupado"], true)) {
                continue;
            }

            $stmt = $pdo->prepare("SELECT estado, placa, usuario_id FROM espacios WHERE codigo_espacio = ?");
            $stmt->execute([$codigoEspacio]);
            $space = $stmt->fetch();

            if (!$space) {
                continue;
            }

            // No tocar espacios reservados manualmente por el admin.
            if ($space["estado"] === "reservado") {
                continue;
            }

            // No tocar espacios que ya tienen un vehiculo registrado formalmente
            // (con placa/usuario), eso solo se libera con "exit_vehicle".
            if ($space["placa"] !== null || $space["usuario_id"] !== null) {
                continue;
            }

            if ($space["estado"] === $estadoDetectado) {
                continue;
            }

            $stmt = $pdo->prepare("UPDATE espacios SET estado = ? WHERE codigo_espacio = ?");
            $stmt->execute([$estadoDetectado, $codigoEspacio]);
            $actualizados[] = ["espacio" => $codigoEspacio, "estado" => $estadoDetectado];
        }

        if ($actualizados) {
            log_info("Camara", "Espacios actualizados por deteccion de camara", ["cambios" => $actualizados]);
        }

        respond(["ok" => true, "actualizados" => $actualizados]);
    }

    if ($action === "camera_frame") {
        $sentKey = $_SERVER["HTTP_X_CAMERA_KEY"] ?? "";
        if (!hash_equals(CAMERA_API_KEY, $sentKey)) {
            log_warn("Camara", "Clave de camara invalida al subir frame");
            respond(["ok" => false, "message" => "Clave de camara invalida."], 401);
        }

        $bytes = file_get_contents("php://input");
        if (!$bytes) {
            respond(["ok" => false, "message" => "No se recibio ninguna imagen."], 400);
        }

        if (!is_dir(CAMERA_FRAME_DIR)) {
            mkdir(CAMERA_FRAME_DIR, 0777, true);
        }

        $path = CAMERA_FRAME_DIR . DIRECTORY_SEPARATOR . CAMERA_FRAME_FILE;
        file_put_contents($path, $bytes);

        respond(["ok" => true]);
    }

    log_warn("API", "Accion no encontrada", ["accion" => $action]);
    respond(["ok" => false, "message" => "Accion no encontrada."], 404);
} catch (Throwable $error) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    log_error_event("Excepciones", "Error en el servidor PHP", [
        "accion" => $action,
        "mensaje" => $error->getMessage(),
        "archivo" => $error->getFile(),
        "linea" => $error->getLine()
    ]);

    respond([
        "ok" => false,
        "message" => "Error en el servidor PHP.",
        "detail" => $error->getMessage()
    ], 500);
}

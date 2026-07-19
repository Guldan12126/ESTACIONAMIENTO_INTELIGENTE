const API_URL = "api/index.php";
const CODE_REGEX = /^u\d{8}$/;
const CAR_PLATE_REGEX = /^[A-Z]{3}\d{3}$/;
const MOTO_PLATE_REGEX = /^\d{4}[A-Z]{2}$/;
const NOTICE_CATEGORIES = ["horario", "seguridad", "mantenimiento", "norma", "emergencia"];

let currentUser = null;
let selectedSpace = "";
let deferredInstallPrompt = null;
let db = { users: [], vehicles: [], spaces: [], notices: [], history: [] };

const viewTitles = {
  inicio: "Inicio",
  mapa: "Mapa",
  registro: "Registro",
  vehiculos: "Vehiculos",
  perfil: "Perfil"
};

const $ = (selector) => document.querySelector(selector);

document.addEventListener("DOMContentLoaded", () => {
  try {
    bindEvents();
    registerServiceWorker();
    toggleInstallButtons(false);
    $("#loginScreen").classList.remove("hidden");
    $("#appScreen").classList.add("hidden");
  } catch (error) {
    console.error("Error al iniciar la app:", error);
    showToast("No se pudo iniciar la app. Revisa XAMPP y la consola.", "error");
  } finally {
    hideSplash();
  }
});

window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  deferredInstallPrompt = event;
  toggleInstallButtons(true);
});

window.addEventListener("appinstalled", () => {
  deferredInstallPrompt = null;
  toggleInstallButtons(false);
});

function bindEvents() {
  $("#loginForm").addEventListener("submit", login);
  $("#logoutBtn").addEventListener("click", logout);
  $("#logoutBtnMobile").addEventListener("click", logout);
  $("#vehicleForm").addEventListener("submit", registerVehicle);
  $("#noticeForm").addEventListener("submit", addNotice);
  $("#userForm").addEventListener("submit", saveUser);
  $("#cancelUserEdit").addEventListener("click", resetUserForm);
  $("#closeModal").addEventListener("click", closeModal);
  $("#spaceModal").addEventListener("click", (event) => {
    if (event.target.id === "spaceModal") closeModal();
  });
  $("#installBtn").addEventListener("click", installApp);
  $("#installBtnTop").addEventListener("click", installApp);
  $("#placa").addEventListener("input", normalizePlateInput);
  $("#loginCodigo").addEventListener("input", (event) => {
    event.target.value = event.target.value.toLowerCase().replace(/\s/g, "");
  });

  document.querySelectorAll("[data-view-target]").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.viewTarget));
  });
}

async function apiRequest(action, data = {}) {
  const response = await fetch(`${API_URL}?action=${action}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });

  let result;
  try {
    result = await response.json();
  } catch {
    throw new Error("Respuesta invalida del servidor. Revisa Apache, PHP y MySQL.");
  }

  if (!result.ok) {
    throw new Error(result.message || "Error en el servidor.");
  }

  return result;
}

function updateDatabase(newData) {
  if (!newData) return;
  db = newData;
}

function hideSplash(immediate = false) {
  const splash = $("#splashScreen");
  if (!splash || splash.classList.contains("hidden")) return;

  if (immediate) {
    splash.classList.add("fade-out", "hidden");
    return;
  }

  setTimeout(() => {
    splash.classList.add("fade-out");
    setTimeout(() => splash.classList.add("hidden"), 500);
  }, 650);
}

function registerServiceWorker() {
  if ("serviceWorker" in navigator) {
    navigator.serviceWorker.register("service-worker.js").catch(() => {});
  }
}

async function installApp() {
  if (isStandaloneApp()) {
    showToast("La app ya esta instalada.", "ok");
    return;
  }

  if (!deferredInstallPrompt) {
    showToast("En Chrome usa el menu y elige Instalar app o Agregar a pantalla principal.", "ok");
    return;
  }

  deferredInstallPrompt.prompt();
  await deferredInstallPrompt.userChoice;
  deferredInstallPrompt = null;
  toggleInstallButtons(false);
}

function toggleInstallButtons(canInstall) {
  $("#installBtnTop").classList.toggle("hidden", !canInstall);
  $("#installBtn").disabled = false;

  if (isStandaloneApp()) {
    $("#installBtn").textContent = "App instalada";
    return;
  }

  $("#installBtn").textContent = canInstall ? "Instalar app" : "Ver como instalar";
}

function isStandaloneApp() {
  return window.matchMedia("(display-mode: standalone)").matches || window.navigator.standalone === true;
}

async function login(event) {
  event.preventDefault();
  const codigo = $("#loginCodigo").value.trim().toLowerCase();
  const password = $("#loginPassword").value.trim();

  if (!CODE_REGEX.test(codigo)) {
    showMessage("#loginMessage", "El codigo debe empezar con u y tener 8 numeros.", "error");
    return;
  }

  try {
    const result = await apiRequest("login", { codigo, password });
    currentUser = result.user;
    updateDatabase(result.data);
    $("#loginForm").reset();
    showMessage("#loginMessage", "", "");
    showApp();
    hideSplash(true);
  } catch (error) {
    showMessage("#loginMessage", error.message, "error");
  }
}

async function logout() {
  try {
    await apiRequest("logout");
  } catch {
    // Si el servidor no responde, igual cerramos la interfaz local.
  }

  stopDataPolling();
  toggleCameraFeed(false);
  currentUser = null;
  selectedSpace = "";
  db = { users: [], vehicles: [], spaces: [], notices: [], history: [] };
  $("#loginScreen").classList.remove("hidden");
  $("#appScreen").classList.add("hidden");
  closeModal();
  showView("inicio");
}

function showApp() {
  $("#loginScreen").classList.add("hidden");
  $("#appScreen").classList.remove("hidden");
  $("#welcomeTitle").textContent = `Bienvenido, ${currentUser.nombre}`;
  $("#userRoleBadge").textContent = currentUser.rol;
  $("#profileName").textContent = currentUser.nombre;
  $("#profileCode").textContent = `${currentUser.codigo} - ${currentUser.rol}`;
  applyRolePermissions();
  showView("inicio");
  renderAll();
  startDataPolling();
}

let dataPollInterval = null;
const INTERVALO_POLLING_DATOS_MS = 4000;

function startDataPolling() {
  if (dataPollInterval) clearInterval(dataPollInterval);
  dataPollInterval = setInterval(pollData, INTERVALO_POLLING_DATOS_MS);
}

function stopDataPolling() {
  if (dataPollInterval) {
    clearInterval(dataPollInterval);
    dataPollInterval = null;
  }
}

async function pollData() {
  if (!currentUser) return;
  try {
    const result = await apiRequest("data");
    updateDatabase(result.data);
    renderDashboard();
    renderSpaceSelect();
    renderMap();
    renderVehiclesTable();
  } catch (error) {
    // Si falla un ciclo de polling (ej. se cerro sesion en otra pestana),
    // no interrumpimos al usuario con un toast; simplemente se reintenta
    // en el siguiente ciclo.
    console.warn("No se pudo refrescar datos automaticamente:", error.message);
  }
}

function showView(viewId) {
  document.querySelectorAll(".app-view").forEach((view) => {
    view.classList.toggle("active", view.id === viewId);
  });

  document.querySelectorAll("[data-view-target]").forEach((button) => {
    button.classList.toggle("active", button.dataset.viewTarget === viewId);
  });

  $("#mobileTitle").textContent = viewTitles[viewId] || "UTP Parking";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function applyRolePermissions() {
  const isAdmin = isAdministrator();
  $("#noticeAdminPanel").classList.toggle("hidden", !isAdmin);
  $("#usuarios").classList.toggle("hidden", !isAdmin);
  $("#duenoLabel").classList.toggle("hidden", !isAdmin);
  $("#cameraAdminPanel").classList.toggle("hidden", !isAdmin);

  if (!isAdmin) {
    $("#dueno").value = currentUser.codigo;
  }

  toggleCameraFeed(isAdmin);
}

let cameraFeedInterval = null;

function toggleCameraFeed(isAdmin) {
  if (cameraFeedInterval) {
    clearInterval(cameraFeedInterval);
    cameraFeedInterval = null;
  }

  if (!isAdmin) return;

  refreshCameraFeed();
  cameraFeedInterval = setInterval(refreshCameraFeed, 3000);
}

function refreshCameraFeed() {
  const img = $("#cameraFeed");
  const badge = $("#cameraStatusBadge");
  if (!img) return;

  const testImg = new Image();
  testImg.onload = () => {
    img.src = `camera_feed/latest.jpg?t=${Date.now()}`;
    if (badge) {
      badge.textContent = "En vivo";
      badge.classList.add("badge-live");
    }
  };
  testImg.onerror = () => {
    if (badge) {
      badge.textContent = "Sin conexion";
      badge.classList.remove("badge-live");
    }
  };
  testImg.src = `camera_feed/latest.jpg?t=${Date.now()}`;
}

function isAdministrator() {
  return currentUser?.rol === "administrador";
}

function renderAll() {
  renderDashboard();
  renderSpaceSelect();
  renderMap();
  renderVehiclesTable();
  renderNotices();
  renderReports();
  renderUsers();
}

function renderDashboard() {
  const stats = getSpaceStats();
  const today = localDateText();
  const todayEntries = db.history.filter((item) => item.type === "ingreso" && item.date === today).length;

  $("#totalSpaces").textContent = db.spaces.length;
  $("#availableSpaces").textContent = stats.disponible;
  $("#occupiedSpaces").textContent = stats.ocupado;
  $("#reservedSpaces").textContent = stats.reservado;
  $("#todayVehicles").textContent = todayEntries;
}

function getSpaceStats() {
  return db.spaces.reduce((stats, space) => {
    stats[space.status] = (stats[space.status] || 0) + 1;
    return stats;
  }, { disponible: 0, ocupado: 0, reservado: 0 });
}

function renderSpaceSelect() {
  const available = db.spaces.filter((space) => space.status === "disponible");
  const options = [`<option value="">Automatico</option>`]
    .concat(available.map((space) => `<option value="${space.codigo}">${space.codigo}</option>`));

  $("#espacio").innerHTML = options.join("");

  if (selectedSpace && available.some((space) => space.codigo === selectedSpace)) {
    $("#espacio").value = selectedSpace;
  }
}

function renderMap() {
  const stats = getSpaceStats();
  $("#mapAvailable").textContent = stats.disponible;
  $("#mapOccupied").textContent = stats.ocupado;
  $("#mapReserved").textContent = stats.reservado;

  const zones = ["A", "B", "C"].map((zone) => {
    const buttons = db.spaces
      .filter((space) => space.zone === zone)
      .map((space) => `
        <button class="parking-slot ${space.status}" onclick="selectSpace('${space.codigo}')">
          <span>${escapeHTML(space.codigo)}</span>
          <small>${escapeHTML(space.status)}</small>
        </button>
      `)
      .join("");

    return `
      <div class="zone">
        <div class="zone-title">Zona ${zone}</div>
        <div class="slots">${buttons}</div>
      </div>
    `;
  });

  $("#parkingMap").innerHTML = `
    ${zones[0] || ""}
    <div class="road">
      <span class="arrow one">&uarr;</span>
      <span class="arrow two">&uarr;</span>
      <span class="arrow three">&uarr;</span>
    </div>
    <div class="zone-group">
      ${zones[1] || ""}
      ${zones[2] || ""}
    </div>
  `;
}

function selectSpace(spaceCode) {
  const space = db.spaces.find((item) => item.codigo === spaceCode);
  if (!space) return;

  selectedSpace = space.status === "disponible" ? spaceCode : "";
  renderSpaceSelect();

  if (space.status === "disponible") {
    openAvailableSpaceModal(space);
    return;
  }

  if (space.status === "reservado") {
    openReservedSpaceModal(space);
    return;
  }

  openOccupiedSpaceModal(space);
}

function openAvailableSpaceModal(space) {
  const adminReserve = isAdministrator()
    ? `<button class="btn dark full" type="button" onclick="toggleReserve('${space.codigo}')">Reservar espacio</button>`
    : "";

  $("#modalContent").innerHTML = `
    <p class="eyebrow">Disponible</p>
    <h3>Espacio ${escapeHTML(space.codigo)}</h3>
    <form class="form-stack modal-form" onsubmit="registerVehicleFromModal(event, '${space.codigo}')">
      <label>
        Placa
        <input id="modalPlaca" type="text" placeholder="ABC-123" required>
      </label>
      <label>
        Tipo
        <select id="modalTipo" required>
          <option value="auto">Auto</option>
          <option value="moto">Moto</option>
        </select>
      </label>
      <label class="${isAdministrator() ? "" : "hidden"}">
        Codigo UTP del dueno
        <input id="modalDueno" type="text" value="${escapeHTML(currentUser.codigo)}">
      </label>
      <p id="modalMessage" class="message"></p>
      <button class="btn primary full" type="submit">Registrar aqui</button>
      ${adminReserve}
    </form>
  `;
  $("#modalPlaca").addEventListener("input", normalizePlateInput);
  openModal();
}

function openReservedSpaceModal(space) {
  const adminButton = isAdministrator()
    ? `<button class="btn primary full" onclick="toggleReserve('${space.codigo}')">Cambiar a disponible</button>`
    : "";

  $("#modalContent").innerHTML = `
    <p class="eyebrow">Reservado</p>
    <h3>Espacio ${escapeHTML(space.codigo)}</h3>
    <p class="muted">Espacio reservado</p>
    ${adminButton}
  `;
  openModal();
}

function openOccupiedSpaceModal(space) {
  const canExit = isAdministrator() || space.usuarioCodigo === currentUser.codigo;
  const exitButton = canExit
    ? `<button class="btn primary full" onclick="registerExit('${space.placa}')">Registrar salida</button>`
    : "";

  $("#modalContent").innerHTML = `
    <p class="eyebrow">Ocupado</p>
    <h3>Espacio ${escapeHTML(space.codigo)}</h3>
    <div class="space-info">
      <p>Placa: <strong>${escapeHTML(formatPlate(space.placa))}</strong></p>
      <p>Tipo: <strong>${escapeHTML(space.tipoVehiculo || "-")}</strong></p>
      <p>Usuario: <strong>${escapeHTML(space.usuarioNombre || "-")}</strong></p>
      <p>Hora de ingreso: <strong>${escapeHTML(space.horaIngreso || "-")}</strong></p>
      <p>Tiempo estacionado: <strong>${escapeHTML(getParkedTime(space.horaIngresoRaw))}</strong></p>
      ${exitButton}
    </div>
  `;
  openModal();
}

function openModal() {
  $("#spaceModal").classList.remove("hidden");
}

function closeModal() {
  $("#spaceModal").classList.add("hidden");
}

async function registerVehicle(event) {
  event.preventDefault();
  const placa = normalizePlate($("#placa").value);
  const tipo = $("#tipo").value;
  const dueno = isAdministrator() ? $("#dueno").value.trim().toLowerCase() : currentUser.codigo;
  const espacio = $("#espacio").value;

  const error = validateVehicleForm({ placa, tipo, dueno });
  if (error) {
    showMessage("#vehicleMessage", error, "error");
    return;
  }

  try {
    const result = await apiRequest("register_vehicle", { placa, tipo, dueno, espacio });
    updateDatabase(result.data);
    selectedSpace = "";
    $("#vehicleForm").reset();
    if (!isAdministrator()) $("#dueno").value = currentUser.codigo;
    showMessage("#vehicleMessage", result.message, "ok");
    renderAll();
  } catch (error) {
    showMessage("#vehicleMessage", error.message, "error");
  }
}

async function registerVehicleFromModal(event, espacio) {
  event.preventDefault();
  const placa = normalizePlate($("#modalPlaca").value);
  const tipo = $("#modalTipo").value;
  const dueno = isAdministrator() ? $("#modalDueno").value.trim().toLowerCase() : currentUser.codigo;

  const error = validateVehicleForm({ placa, tipo, dueno });
  if (error) {
    showMessage("#modalMessage", error, "error");
    return;
  }

  try {
    const result = await apiRequest("register_vehicle", { placa, tipo, dueno, espacio });
    updateDatabase(result.data);
    selectedSpace = "";
    closeModal();
    showToast(result.message, "ok");
    renderAll();
  } catch (error) {
    showMessage("#modalMessage", error.message, "error");
  }
}

function validateVehicleForm({ placa, tipo, dueno }) {
  if (!isValidPlate(placa)) return "Placa invalida. Usa ABC-123, ABC123, 1234-AB o 1234AB.";
  if (!["auto", "moto"].includes(tipo)) return "Solo se permite registrar autos y motos.";
  if (!CODE_REGEX.test(dueno)) return "Codigo UTP invalido. Ejemplo: u22210840.";
  return "";
}

async function registerExit(placa) {
  try {
    const result = await apiRequest("exit_vehicle", { placa });
    updateDatabase(result.data);
    closeModal();
    showToast(result.message, "ok");
    renderAll();
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function toggleReserve(spaceCode) {
  if (!isAdministrator()) return;

  try {
    const result = await apiRequest("toggle_reserve", { espacio: spaceCode });
    updateDatabase(result.data);
    closeModal();
    showToast(result.message, "ok");
    renderAll();
  } catch (error) {
    showToast(error.message, "error");
  }
}

function renderVehiclesTable() {
  const vehicles = db.vehicles;

  $("#vehiclesTable").innerHTML = vehicles.length
    ? vehicles.map((vehicle) => `
        <tr>
          <td>${escapeHTML(formatPlate(vehicle.placa))}</td>
          <td>${escapeHTML(vehicle.tipo)}</td>
          <td>${escapeHTML(vehicle.espacio)}</td>
          <td><span class="status-pill active">${escapeHTML(vehicle.estado)}</span></td>
          <td>${escapeHTML(vehicle.horaIngreso)}</td>
          <td><button class="btn small primary" onclick="registerExit('${vehicle.placa}')">Salida</button></td>
        </tr>
      `).join("")
    : `<tr><td colspan="6">No hay vehiculos dentro del estacionamiento.</td></tr>`;
}

function renderNotices() {
  $("#noticeList").innerHTML = db.notices.length
    ? db.notices.map((notice) => `
        <div class="notice-item">
          <div class="notice-head">
            <span class="notice-category">${escapeHTML(notice.categoria)}</span>
            ${isAdministrator() ? `<button class="btn small danger" onclick="deleteNotice(${notice.id})">Retirar</button>` : ""}
          </div>
          <strong>${escapeHTML(notice.titulo)}</strong>
          <p>${escapeHTML(notice.mensaje)}</p>
        </div>
      `).join("")
    : `<div class="notice-item">No hay avisos registrados.</div>`;
}

async function addNotice(event) {
  event.preventDefault();
  if (!isAdministrator()) return;

  const titulo = $("#noticeTitle").value.trim();
  const mensaje = $("#noticeMessage").value.trim();
  const categoria = $("#noticeCategory").value;
  const error = validateNotice(titulo, mensaje, categoria);

  if (error) {
    showMessage("#noticeMessageStatus", error, "error");
    return;
  }

  try {
    const result = await apiRequest("add_notice", { titulo, mensaje, categoria });
    updateDatabase(result.data);
    $("#noticeForm").reset();
    showMessage("#noticeMessageStatus", result.message, "ok");
    renderNotices();
  } catch (error) {
    showMessage("#noticeMessageStatus", error.message, "error");
  }
}

async function deleteNotice(id) {
  if (!isAdministrator()) return;
  if (!confirm("Deseas retirar este aviso?")) return;

  try {
    const result = await apiRequest("delete_notice", { id });
    updateDatabase(result.data);
    showToast(result.message, "ok");
    renderAll();
  } catch (error) {
    showToast(error.message, "error");
  }
}

function validateNotice(titulo, mensaje, categoria) {
  const joined = `${titulo} ${mensaje}`.toLowerCase();
  const badWords = ["idiota", "tonto", "mierda", "carajo"];

  if (!titulo || !mensaje || !NOTICE_CATEGORIES.includes(categoria)) return "Completa titulo, mensaje y categoria.";
  if (titulo.length < 4 || mensaje.length < 10) return "El titulo y mensaje son demasiado cortos.";
  if (titulo.length > 80 || mensaje.length > 200) return "El aviso supera el limite permitido.";
  if (/^\d+$/.test(titulo + mensaje)) return "El aviso no puede ser solo numeros.";
  if (!/[a-zA-Z]/.test(titulo + mensaje)) return "El aviso debe contener texto valido.";
  if (/(.)\1{5,}/i.test(joined) || /(ja){4,}|(asdf){2,}/i.test(joined)) return "Evita texto repetido o de prueba.";
  if (badWords.some((word) => joined.includes(word))) return "El aviso contiene palabras no permitidas.";
  return "";
}

function renderReports() {
  const today = localDateText();
  const stats = getSpaceStats();
  const todayEntries = db.history.filter((item) => item.type === "ingreso" && item.date === today);
  const todayExits = db.history.filter((item) => item.type === "salida" && item.date === today);

  $("#reportEntries").textContent = todayEntries.length;
  $("#reportExits").textContent = todayExits.length;
  $("#reportAvailable").textContent = stats.disponible;
  $("#reportReserved").textContent = stats.reservado;
  $("#reportInside").textContent = db.vehicles.length;
  $("#reportTopSpace").textContent = getMostUsedSpace(db.history);

  $("#historyTable").innerHTML = db.history.length
    ? db.history.slice(0, 30).map((item) => `
        <tr>
          <td>${escapeHTML(item.type)}</td>
          <td>${escapeHTML(formatPlate(item.placa))}</td>
          <td>${escapeHTML(item.espacio)}</td>
          <td>${escapeHTML(item.dateTime)}<br><small>${escapeHTML(item.usuarioNombre || "")}</small></td>
        </tr>
      `).join("")
    : `<tr><td colspan="4">Todavia no hay movimientos registrados.</td></tr>`;
}

function getMostUsedSpace(history) {
  const counts = history
    .filter((item) => item.type === "ingreso")
    .reduce((acc, item) => {
      acc[item.espacio] = (acc[item.espacio] || 0) + 1;
      return acc;
    }, {});

  return Object.keys(counts).sort((a, b) => counts[b] - counts[a])[0] || "-";
}

function localDateText() {
  return new Date().toLocaleDateString("en-CA");
}

async function saveUser(event) {
  event.preventDefault();
  if (!isAdministrator()) return;

  const id = $("#editUserId").value;
  const codigo = $("#newCodigo").value.trim().toLowerCase();
  const nombre = $("#newNombre").value.trim();
  const password = $("#newPassword").value.trim();
  const rol = $("#newRol").value;

  if (!CODE_REGEX.test(codigo)) {
    showMessage("#userMessage", "Codigo UTP invalido. Ejemplo: u22210840.", "error");
    return;
  }

  if (nombre.length < 3) {
    showMessage("#userMessage", "El nombre debe tener minimo 3 caracteres.", "error");
    return;
  }

  if (!id && password.length < 6) {
    showMessage("#userMessage", "La contrasena debe tener minimo 6 caracteres.", "error");
    return;
  }

  try {
    const result = await apiRequest("save_user", { id, codigo, nombre, password, rol });
    updateDatabase(result.data);
    resetUserForm();
    showMessage("#userMessage", result.message, "ok");
    renderUsers();
  } catch (error) {
    showMessage("#userMessage", error.message, "error");
  }
}

function editUser(id) {
  const user = db.users.find((item) => Number(item.id) === Number(id));
  if (!user) return;

  $("#editUserId").value = user.id;
  $("#newCodigo").value = user.codigo;
  $("#newNombre").value = user.nombre;
  $("#newPassword").value = "";
  $("#newRol").value = user.rol;
  showMessage("#userMessage", "Editando usuario. Deja la contrasena vacia para no cambiarla.", "ok");
}

async function deleteUser(id) {
  if (!isAdministrator()) return;
  if (!confirm("Deseas eliminar este usuario?")) return;

  try {
    const result = await apiRequest("delete_user", { id });
    updateDatabase(result.data);
    showMessage("#userMessage", result.message, "ok");
    renderUsers();
  } catch (error) {
    showMessage("#userMessage", error.message, "error");
  }
}

function resetUserForm() {
  $("#editUserId").value = "";
  $("#userForm").reset();
}

function renderUsers() {
  if (!isAdministrator()) {
    $("#usersTable").innerHTML = "";
    return;
  }

  $("#usersTable").innerHTML = db.users.map((user) => `
    <tr>
      <td>${escapeHTML(user.codigo)}</td>
      <td>${escapeHTML(user.nombre)}</td>
      <td>${escapeHTML(user.rol)}</td>
      <td>
        <button class="btn small dark" onclick="editUser(${user.id})">Editar</button>
        <button class="btn small primary" onclick="deleteUser(${user.id})">Eliminar</button>
      </td>
    </tr>
  `).join("");
}

function normalizePlateInput(event) {
  event.target.value = event.target.value.toUpperCase().replace(/[^A-Z0-9-]/g, "");
}

function normalizePlate(plate) {
  return plate.toUpperCase().replace(/[^A-Z0-9]/g, "");
}

function isValidPlate(plate) {
  return CAR_PLATE_REGEX.test(plate) || MOTO_PLATE_REGEX.test(plate);
}

function formatPlate(plate = "") {
  const clean = normalizePlate(plate);
  if (CAR_PLATE_REGEX.test(clean)) return `${clean.slice(0, 3)}-${clean.slice(3)}`;
  if (MOTO_PLATE_REGEX.test(clean)) return `${clean.slice(0, 4)}-${clean.slice(4)}`;
  return plate || "-";
}

function getParkedTime(rawDate) {
  if (!rawDate) return "-";
  const start = new Date(String(rawDate).replace(" ", "T"));
  if (Number.isNaN(start.getTime())) return "-";
  const minutes = Math.max(0, Math.floor((Date.now() - start.getTime()) / 60000));
  const hours = Math.floor(minutes / 60);
  const rest = minutes % 60;
  return hours ? `${hours}h ${rest}min` : `${rest}min`;
}

function showMessage(selector, text, type) {
  const element = $(selector);
  element.textContent = text;
  element.className = `message ${type}`;
}

function showToast(text, type = "ok") {
  let toast = $("#toast");
  if (!toast) {
    toast = document.createElement("div");
    toast.id = "toast";
    document.body.appendChild(toast);
  }

  toast.textContent = text;
  toast.className = `toast ${type} show`;
  setTimeout(() => toast.classList.remove("show"), 3200);
}

function escapeHTML(value = "") {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}
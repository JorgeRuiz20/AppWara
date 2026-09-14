# WARA - Gestión de Trabajadores (Aplicativo Móvil Android)

Aplicativo móvil nativo para Android desarrollado en **Kotlin** bajo las mejores prácticas de la industria, implementando **Jetpack Compose**, **Material Design 3**, **Clean Architecture** y el patrón reactivo **MVI (Model-View-Intent)**.

La solución se encuentra integrada y conectada en producción a la API REST de WARA desplegada en Render Cloud y respaldada por MySQL 8.0 en Aiven Cloud.

---

## 🚀 Arquitectura y Stack Tecnológico

* **Lenguaje:** Kotlin 2.0 (JVM 17+)
* **UI Framework:** Jetpack Compose (Declarativo, 100% libre de XMLs)
* **Sistema de Diseño:** Material Design 3 (Theming corporativo, Dark/Light mode ready)
* **Patrón Arquitectónico:** Clean Architecture (Capas *Domain*, *Data* y *Presentation*) combinada con **MVI** (*Model-View-Intent*) y Flujo Unidireccional de Datos (**UDF**).
* **Gestión Asíncrona y Estado:** Kotlin Coroutines, `StateFlow` y `collectAsStateWithLifecycle()`.
* **Red y API Client:** Retrofit 2, OkHttp 3, Gson Converter y logging interceptor.
* **Persistencia Local Segura:** Jetpack DataStore Preferences para la custodia reactiva y asíncrona del token de sesión JWT y credenciales.
* **Navegación:** Jetpack Navigation Compose con integración profunda de `SavedStateHandle` para refresco instantáneo de estado entre pantallas.
* **Testing:** JUnit 4, Kotlinx Coroutines Test y Mockito / Fake Repositories.

---

## 🌐 Conectividad Backend en Producción

El aplicativo apunta por defecto al servicio cloud en alta disponibilidad:
* **API Base URL:** `https://backendwara-kvnz.onrender.com/`
* **Health Check:** `https://backendwara-kvnz.onrender.com/api/health`
* **Seguridad:** Comunicación cifrada vía HTTPS con tokens **JWT Bearer**.

---

## 📱 Funcionalidades Implementadas

1. **Autenticación y Registro:**
   * Registro e inicio de sesión de operadores con validación estricta de contraseña (mínimo 8 caracteres, al menos 1 mayúscula y 1 símbolo especial).
   * Persistencia automática y reactiva de sesión con Jetpack DataStore Preferences.
   * Cierre de sesión seguro con purga integral de credenciales y tokens.
2. **Dashboard de Trabajadores:**
   * Listado en tarjetas modernas con datos clave (Nombres, Apellidos, DNI, Edad).
   * Barra de búsqueda reactiva en tiempo real con filtrado por DNI (según requerimiento de evaluación).
3. **Gestión de Personal (CRUD Completo):**
   * Alta de nuevos colaboradores con exactamente 4 campos esenciales: **Nombre**, **Apellido**, **DNI** (8 dígitos numéricos) y **Edad** (18 a 80 años).
   * Edición y actualización reactiva de datos del trabajador.
   * Baja lógica (*Soft Delete* `Activo = false`) con confirmación preventiva.
4. **Resiliencia y Experiencia de Usuario (UX):**
   * Notificaciones inmediatas mediante *Snackbars*.
   * Actualización instantánea del catálogo tras guardar o editar sin necesidad de recarga manual.
   * Soporte a márgenes del sistema (*Edge-to-Edge* y `navigationBarsPadding`).
   * Tolerancia ampliada de 60 segundos ante *Cold-Starts* de la nube y selector dinámico de servidores.

---

## 🛠️ Requisitos para Compilación y Ejecución

* **Android Studio:** Ladybug / Koala / Iguana / Hedgehog o superior.
* **Android Gradle Plugin (AGP):** 8.7+
* **Gradle:** 8.9+
* **Java Development Kit (JDK):** JDK 17 o 21 configurado en Android Studio (`Settings > Build, Execution, Deployment > Build Tools > Gradle`).
* **Dispositivo de Prueba:** Emulador o terminal físico con Android 8.0 (API 26) o superior (recomendado Android 12+ / API 31+).

### Instrucciones de Clonación y Ejecución

```bash
# 1. Clonar el repositorio
git clone https://github.com/JorgeRuiz20/AppWara.git

# 2. Abrir el proyecto en Android Studio
# Seleccionar la carpeta raíz 'AppWara' o 'Wara'

# 3. Sincronizar Gradle
# File -> Sync Project with Gradle Files

# 4. Compilar y ejecutar pruebas unitarias
./gradlew test

# 5. Ejecutar en dispositivo / emulador
./gradlew installDebug
```

---

## 📁 Estructura del Proyecto

```
app/src/main/java/com/example/wara/
├── core/               # Constantes, Resultado (Resource) y Validadores
├── data/
│   ├── local/          # Room DB (offline cache) y DataStore (sesión JWT)
│   ├── remote/         # Retrofit ApiService, DTOs e Interceptors
│   └── repository/     # Implementación de repositorios Auth y Trabajador
├── di/                 # Manual DI Container (AppContainer)
├── domain/
│   ├── model/          # Modelos de negocio (Usuario, Trabajador, AuthToken)
│   ├── repository/     # Contratos e interfaces de repositorio
│   └── usecase/        # Casos de uso de autenticación y gestión de trabajadores
├── presentation/
│   ├── auth/           # LoginScreen, RegisterScreen y ViewModels
│   ├── components/     # Diálogos de servidor, confirmación, error y barras
│   ├── navigation/     # NavGraph y rutas Compose
│   └── trabajador/     # Listado, formulario crear/editar y ViewModels
├── ui/theme/           # Color, Tipografía, Formas y Tema Material 3
└── MainActivity.kt     # Activity principal Edge-to-Edge
```

---

## 👨‍💻 Autor

* **Jorge Ruiz Tapia** - Desarrollador Senior Full Stack (.NET Core / Android Kotlin)
* **Repositorio Backend:** [https://github.com/JorgeRuiz20/BackendWara.git](https://github.com/JorgeRuiz20/BackendWara.git)

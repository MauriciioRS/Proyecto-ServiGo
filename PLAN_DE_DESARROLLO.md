# Plan de Desarrollo - ServiGo App

## 📋 Estado Actual de la Aplicación

### ✅ Lo que YA está implementado:
1. **Autenticación básica**
   - Login con validaciones
   - Registro de usuarios (3 pasos) con validaciones
   - Usuario admin/admin para acceso rápido
   - Logout funcional

2. **Interfaz de Usuario (UI)**
   - Navegación entre fragments
   - Layouts completos para todas las pantallas
   - Adapters para RecyclerView (Mensajes, Historial)
   - Diseño Material Design

3. **Datos de Ejemplo (Hardcodeados)**
   - MensajesFragment: 4 mensajes de ejemplo
   - TrabajosFragment: 3 trabajos de ejemplo

### ❌ Lo que FALTA implementar:

## 🎯 Funcionalidades Pendientes

### 1. **ExplorarFragment** (Búsqueda de Profesionales)
**Estado actual:** Solo tiene un SearchView que no hace nada

**Qué implementar:**
- [ ] Lista de profesionales disponibles (RecyclerView)
- [ ] Búsqueda funcional por nombre, oficio, ubicación
- [ ] Filtros por categoría/oficio
- [ ] Ver perfil del profesional al hacer clic
- [ ] Sistema de calificaciones/estrellas
- [ ] Distancia/ubicación del profesional

**Datos necesarios:**
- Modelo `Profesional` con: nombre, oficio, calificación, ubicación, precio, foto
- Lista de profesionales disponibles

---

### 2. **FavoritosFragment** (Profesionales Favoritos)
**Estado actual:** Layout vacío, sin funcionalidad

**Qué implementar:**
- [ ] Lista de profesionales marcados como favoritos
- [ ] Agregar/quitar de favoritos desde ExplorarFragment
- [ ] Persistencia de favoritos (guardar en BD)
- [ ] Mensaje cuando no hay favoritos

**Datos necesarios:**
- Relación Usuario ↔ Profesional (favoritos)
- Base de datos o SharedPreferences para guardar favoritos

---

### 3. **TrabajosFragment** (Historial de Trabajos)
**Estado actual:** Muestra 3 trabajos hardcodeados

**Qué implementar:**
- [ ] Cargar trabajos del usuario actual desde BD
- [ ] Filtrar por estado (Finalizado, En Proceso, Cancelado)
- [ ] Ver detalles del trabajo al hacer clic
- [ ] Crear nuevo trabajo/solicitud
- [ ] Actualizar estado del trabajo
- [ ] Calificar trabajo completado

**Datos necesarios:**
- Modelo `Trabajo` completo con: id, cliente, profesional, servicio, fecha, precio, estado, descripción
- Relación con usuario actual

---

### 4. **MensajesFragment** (Chat/Mensajería)
**Estado actual:** Muestra 4 mensajes hardcodeados con filtros por tabs

**Qué implementar:**
- [ ] Pantalla de chat individual al hacer clic en un mensaje
- [ ] Enviar/recibir mensajes en tiempo real
- [ ] Notificaciones de nuevos mensajes
- [ ] Marcar mensajes como leídos
- [ ] Historial de conversaciones
- [ ] Adjuntar fotos/documentos

**Datos necesarios:**
- Modelo `Conversacion` y `MensajeChat`
- Sistema de mensajería (WebSocket o polling)

---

### 5. **PerfilFragment** (Perfil de Usuario)
**Estado actual:** Solo tiene botón de logout

**Qué implementar:**
- [ ] Mostrar datos del usuario actual (nombre, email, foto)
- [ ] Editar perfil (cambiar nombre, foto, dirección)
- [ ] Ver estadísticas (trabajos completados, calificación promedio)
- [ ] Configuración de cuenta
- [ ] Cambiar contraseña
- [ ] Si es profesional: gestionar servicios, precios, disponibilidad

**Datos necesarios:**
- Datos del usuario actual guardados
- Formulario de edición de perfil

---

## 🗄️ Opciones de Almacenamiento de Datos

### Opción 1: Base de Datos Local (Room/SQLite) ⭐ RECOMENDADO PARA EMPEZAR
**Ventajas:**
- Funciona sin internet
- Rápido de implementar
- Ideal para MVP/prototipo
- Datos persisten localmente

**Qué implementar:**
- Room Database con entidades:
  - `Usuario` (id, nombre, email, contraseña, tipo, foto)
  - `Profesional` (id, nombre, oficio, calificación, ubicación, precio)
  - `Trabajo` (id, clienteId, profesionalId, servicio, fecha, precio, estado)
  - `Mensaje` (id, conversacionId, remitenteId, texto, fecha)
  - `Favorito` (usuarioId, profesionalId)

**Implementación:**
```kotlin
// Ejemplo de estructura
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val password: String,
    val tipo: String // "Cliente", "Contratista", "Ambos"
)
```

---

### Opción 2: Backend/API REST
**Ventajas:**
- Datos sincronizados entre dispositivos
- Escalable
- Funcionalidad en tiempo real
- Múltiples usuarios

**Qué implementar:**
- API REST con endpoints:
  - `POST /auth/login`
  - `POST /auth/register`
  - `GET /profesionales` (con búsqueda y filtros)
  - `GET /trabajos` (del usuario)
  - `GET /mensajes` (conversaciones)
  - `POST /favoritos` (agregar/quitar)
  - `PUT /perfil` (actualizar datos)

**Tecnologías sugeridas:**
- Backend: Node.js, Python (Django/Flask), Java (Spring Boot)
- Base de datos: PostgreSQL, MySQL, MongoDB
- Autenticación: JWT tokens

---

### Opción 3: Firebase (Google)
**Ventajas:**
- Backend como servicio (no necesitas crear servidor)
- Autenticación integrada
- Base de datos en tiempo real
- Notificaciones push fáciles

**Qué implementar:**
- Firebase Authentication
- Firestore Database
- Cloud Storage (para fotos)
- Cloud Messaging (notificaciones)

---

## 📝 Plan de Implementación Recomendado

### Fase 1: Base de Datos Local (Room) - 2-3 semanas
1. **Semana 1:**
   - Implementar Room Database
   - Crear entidades y DAOs
   - Guardar datos de registro en BD
   - Cargar datos del usuario en PerfilFragment

2. **Semana 2:**
   - Implementar ExplorarFragment con datos de BD
   - Sistema de favoritos (agregar/quitar)
   - Cargar trabajos del usuario en TrabajosFragment

3. **Semana 3:**
   - Sistema básico de mensajería (sin tiempo real)
   - Completar PerfilFragment
   - Testing y corrección de bugs

### Fase 2: Funcionalidades Avanzadas - 2-3 semanas
1. Búsqueda avanzada con filtros
2. Sistema de calificaciones
3. Notificaciones locales
4. Mejoras de UI/UX

### Fase 3: Backend (Opcional) - 4-6 semanas
1. Crear API REST
2. Integrar Retrofit en la app
3. Sincronización de datos
4. Mensajería en tiempo real

---

## 🛠️ Próximos Pasos Inmediatos

### Para empezar AHORA mismo:

1. **Implementar Room Database**
   ```kotlin
   // Agregar dependencias en build.gradle.kts
   implementation("androidx.room:room-runtime:2.6.1")
   kapt("androidx.room:room-compiler:2.6.1")
   ```

2. **Crear entidades básicas:**
   - UsuarioEntity
   - ProfesionalEntity
   - TrabajoEntity

3. **Implementar ExplorarFragment funcional:**
   - Cargar lista de profesionales desde BD
   - Búsqueda básica funcionando

4. **Sistema de favoritos:**
   - Agregar/quitar favoritos
   - Mostrar en FavoritosFragment

---

## 📊 Prioridades

### 🔴 ALTA PRIORIDAD:
1. Base de datos local (Room)
2. ExplorarFragment funcional
3. Sistema de favoritos
4. Cargar datos del usuario en PerfilFragment

### 🟡 MEDIA PRIORIDAD:
1. TrabajosFragment con datos reales
2. MensajesFragment básico (sin tiempo real)
3. Búsqueda avanzada

### 🟢 BAJA PRIORIDAD:
1. Mensajería en tiempo real
2. Notificaciones push
3. Backend/API REST
4. Sistema de pagos

---

## 💡 Recomendación Final

**Empezar con Room Database (Opción 1)** porque:
- Es lo más rápido de implementar
- No requiere servidor ni backend
- Funciona offline
- Perfecto para MVP y pruebas
- Puedes migrar a backend después si es necesario

¿Quieres que implemente alguna de estas funcionalidades ahora?


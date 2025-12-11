# EconomyApp API – Backend (Spring Boot)

## 📌 Descripción
Este repositorio contiene la **API de EconomyApp**, una aplicación destinada a la gestión económica familiar.  
El backend está desarrollado con **Spring Boot** y proporciona una API REST segura para administrar:

- Usuarios y autenticación (JWT)
- Categorías y subcategorías
- Ingresos y gastos
- Ahorros y objetivos
- Transferencias entre categorías
- Generación de informes PDF
- Balances mensuales y globales

---

## 🧱 Arquitectura del Proyecto

La API sigue una arquitectura en capas, organizada de la siguiente forma:

```
src/main/java/com/eltiosento/economyapp
├── auth/                 # Registro, login y gestión de JWT
├── balance/              # Cálculo de balances mensuales y globales
├── configuration/        # Configuración: seguridad, Swagger, CORS, ModelMapper
├── controller/           # Endpoints REST públicos
├── dto/                  # Data Transfer Objects y convertidores
├── error/                # Manejo global de errores y excepciones personalizadas
├── jwt/                  # Filtros y servicios para validación de tokens
├── model/                # Entidades JPA (User, Expense, Income, Category…)
├── report/               # Generación de informes en PDF
├── repository/           # Interfaces JpaRepository
├── service/              # Lógica de negocio
├── transfer/             # Transferencias entre categorías
└── uploads/              # Subida y gestión de imágenes
```
Directorio destinado a almacenar las imagenes de perfil de los usuarios:
```
mediafiles
```

---

## 🔐 Autenticación y Seguridad

El sistema utiliza **Spring Security + JWT**:

- Los *Admins* tienen control total sobre el sistema.
- Los *Users* pueden consultar balances y movimientos.
- Los *Guests* pueden registrarse, pero no acceden a información hasta ser autorizados.

---
## ⚙️ Configuración
```
src/main/resources/
├── application.properties                 #Fichero de configuración de Spring

```
En este fichero se pueden cambiar las distintas configuraciones como, la llave para los tokens o el tiempo de expiración, el puerto de escucha, etc.


## 📚 Documentación de la API

Disponible en Swagger:

```
http://localhost:9090
```

---


## 👨‍💻 Autor

Proyecto desarrollado por **Vicent Roselló** como proyecto de final de ciclo de DAW. Aplicación educativa para la gestión económica familiar.

---

## 📄 Licencia

Uso personal y educativo.

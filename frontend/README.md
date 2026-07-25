# Sistema de Login - Frontend

Frontend del sistema de autenticación desarrollado con React para el proyecto del Grupo CRE.

## Integrantes

- Carlos
- Jhonel

## Tecnologías utilizadas

- React
- React Router DOM
- Axios
- Vite
- JavaScript
- CSS
- Lucide React

## Funcionalidades

- Inicio de sesión.
- Registro de usuarios.
- Validación de credenciales.
- Gestión de sesiones.
- Protección de rutas.
- Bloqueo de usuarios después de varios intentos fallidos.
- Reactivación de usuarios desde el panel de administración.
- Integración con el backend desarrollado en Spring Boot.

## Requisitos

- Node.js 18 o superior
- npm

## Instalación

Clonar el repositorio:

```bash
git clone <https://github.com/jhair7u7/sistema_login_grupocre.git>
```

Entrar al proyecto:

```bash
cd sistema_login_grupocre-frontend-react
```

Instalar dependencias:

```bash
npm install
```

## Ejecutar el proyecto

```bash
npm run dev
```

La aplicación estará disponible en:

```
http://localhost:5173
```

## Conexión con el Backend

El frontend consume la API REST desarrollada en Spring Boot.

Asegúrese de que el backend esté ejecutándose antes de iniciar la aplicación.

## Estructura del proyecto

```
src/
│── components/
│── context/
│── hooks/
│── pages/
│── services/
│── assets/
│── App.jsx
│── main.jsx
```

## Características

- Diseño responsive.
- Manejo de autenticación mediante contexto.
- Navegación protegida.
- Comunicación con API REST.
- Código modular y reutilizable.

## Autor

Proyecto desarrollado como parte del Sistema de Login del Grupo CRE.
# Amiguteka

**Amiguteka** es una plataforma web diseñada para los amantes del crochet y los amigurumis, que permite gestionar patrones, guardar favoritos y mantener un perfil personalizado. Este proyecto fue desarrollado como parte del proyecto final del FP de Desarrollo de Aplicaciones Web.

## 🌟 Características principales

- **Gestión de patrones**: Crear, editar, visualizar y eliminar patrones de amigurumis.
- **Favoritos**: Guardar patrones preferidos para acceder a ellos rápidamente.
- **Perfil de usuario**: Editar información personal, cambiar la contraseña y eliminar la cuenta.
- **Responsive Design**: Adaptado para dispositivos móviles y escritorios.
- **Seguridad**: Autenticación y autorización personalizada para proteger la información del usuario.

## 🛠️ Tecnologías utilizadas

### Front-end
- **HTML5, CSS3, JavaScript**
- **Skeleton**: Framework CSS ligero para un diseño responsivo.
- **Thymeleaf**: Motor de plantillas para integrar datos en el lado del servidor.

### Back-end
- **Spring Boot**: Framework para la gestión del servidor y lógica de negocio.
- **Firebase**: Base de datos NoSQL para almacenamiento de datos.


### Despliegue
- **Tomcat embebido**: Para ejecutar la aplicación.
- **Debian**: Sistema operativo utilizado en el servidor.

## ⚙️ Instalación y configuración

### Prerrequisitos
- Java 17 o superior.
- Maven instalado.

- Cuenta en Firebase para la configuración de la base de datos.

# Paso 1: Clonar el repositorio
git clone https://github.com/tu-usuario/amiguteka.git
cd amiguteka

# Paso 2: Configurar el archivo de propiedades
# Edita el archivo 'application.properties' para agregar los detalles de tu base de datos y Firebase.
nano src/main/resources/application.properties

# Paso 3: Construir el proyecto con Maven
mvn clean install

# Paso 4: Ejecutar la aplicación
mvn spring-boot:run

# Paso 5: Acceder a la aplicación
# Abre tu navegador y ve a:
# http://localhost:8080

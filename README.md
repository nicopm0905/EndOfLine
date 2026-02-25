# End Of Line - Board Game

 Proyecto realizado para la asignatura **Diseño y Pruebas** (Grado en Ingeniería del Software, Universidad de Sevilla).
 
 **Curso académico:** 2025/2026  
 **Grupo/Equipo:** L5-3

 ## Descripción del Proyecto
 **End of Line** es un juego de estrategia online donde los jugadores deben extender su línea en el tablero intentando bloquear a sus oponentes. El proyecto incluye varios modos de juego como Versus, Battle Royale, Puzzle Solitario y Cooperativo, implementados con una arquitectura moderna de SPA.

 ## Tecnologías Utilizadas
 * **Backend:** Spring Boot 3.x (Java 21)
 * **Frontend:** React JS
 * **Base de Datos:** H2 (In-memory para desarrollo)
 * **Seguridad:** Spring Security con JWT
 * **Build Tools:** Maven & npm

 ## Estructura del Proyecto
 * `src/main/java`: Código fuente del Backend (Controladores, Servicios, Entidades).
 * `src/test/java`: Pruebas Unitarias e Integración (JUnit 5, Mockito).
 * `frontend/`: Código fuente del Frontend (React).
 * `docs/deliverables`: Documentación entregable (D1, D2, D3, D4).

 ## Ejecución Local

 ### Prerrequisitos
 * Java 21
 * Node.js 18+
 * Maven

 ### Pasos para ejecutar
 1. **Clonar el repositorio:**
    ```bash
    git clone https://github.com/gii-is-DP1/dp1-2025-2026-l5-3-25.git
    cd dp1-2025-2026-l5-3-25
    ```

 2. **Ejecutar el Backend & Frontend (Todo en uno):**
    Maven está configurado para construir el frontend automáticamente.
    ```bash
    ./mvnw spring-boot:run
    ```
    La aplicación estará disponible en: [http://localhost:8080](http://localhost:8080)

 3. **Ejecutar Frontend por separado (Desarrollo):**
    ```bash
    cd frontend
    npm install
    npm start
    ```
    Frontend disponible en: [http://localhost:3000](http://localhost:3000)

 ## Documentación
 Toda la documentación detallada del proyecto se encuentra en la carpeta `docs/deliverables`:
 * [D1 - Análisis de Requisitos](docs/deliverables/D1/Análisis%20de%20requisitos%20del%20sistema.md)
 * [D2 - Diseño del Sistema](docs/deliverables/D2/Diseño%20del%20Sistema.md)
 * [D3 - Plan de Pruebas](docs/deliverables/D3/Plan%20de%20Pruebas.md)
 * [D4 - Uso de IA](docs/deliverables/D4/Uso%20de%20IA.md)

 ## Miembros del Equipo
 - **Álvaro de Pablos Sánchez**
 - **Carmen Camacho Montes**
 - **Francisco Casasola Calzadilla**
 - **Alejandro Pichardo Martínez**
 - **Juan Pozo Gracia**
 - **Nicolás Pérez Martín**

# Documentación del Uso de IA en el Proyecto
**Asignatura:** Diseño y Pruebas (Grado en Ingeniería del Software, Universidad de Sevilla)  
**Curso académico:** 2025/2026
**Grupo/Equipo:** L5-03  
**Nombre del proyecto:** EndOfLine
**Repositorio:** https://github.com/gii-is-DP1/dp1-2025-2026-l5-3-25
**Integrantes (máx. 6):** 
Juan Pozo García (DKK2084 / juapozgar@alum.us.es)
Carmen Camacho Montes (JBV8381 carcammon@alum.us.es)
Alejandro Pichardo Martinez (alepicmar/alepicmar@alum.us.es)
Nicolás Pérez Martín (YGC9995/nicpermar@alum.us.es)
Francisco Casasola Calzadilla (QHR9543 / fracascal@alum.us.es)
Alvaro de Pablos Sanchez (wbk2747 / alvpabsan@alum.us.es)

## 1. Introducción

Este documento describe el uso que se ha echo de la IA en el proyecto. El objetivo es ser transparentes sobre el uso de IA realizado. Como recordatorio, al alumnado incluimos un resumen de lo indicado en el Syllabus de la asignatura:

### Declaración de Política y Compromiso

> **Principios guía generales (resumen):**  
> - **Dimensión Cognitiva:** El trabajo con IA **no** debe reducir su capacidad de pensar con claridad; úsela para **facilitar**—no obstaculizar—el aprendizaje.  
> - **Dimensión Ética :** La utilización de IA debe ser **transparente** y **alineada** con la integridad académica.

**Normas específicas de la asignatura:**
- ✅ **IA para código:** Se permite usar tecnología generativa para **completar o generar ejemplos de código** en las tareas, pero **debe citarse explícitamente** la procedencia del mismo. Así mismo el alumno debe **entender** y poder **modificar bajo demanda** cualquier código entregado, siendo el responsable de cualquier comportamiento del código que ha conmitado, ante el profesor y sus compañeros. Recuerde: **Usted es responsable** de dicho código.
- ❌ **IA para narrativa:** Salvo indicación en contrario, **no** se permite usar IA generativa para **redactar narrativa** de las entregas. Se puede usar como **recurso** durante el proceso, **no** para **responder por usted** a los ejercicios.

**Marco ético US:** Consulte y cumpla lo indicado en **Guías de Ética e IA** de la US: https://guiasbus.us.es/ia/etica

**Rellenar este documento es Obligatorio:** La **documentación del uso de IA** es un **entregable** del proyecto.

## Resumen por Sprint (1–4)
### Sprint 1 — Resumen de uso de IA

Usos registrados: 4 <!-- nº -->

Ámbitos principales: Ayuda con diagrama conceptuales, estilo de mockups y esqueleto de plantilla css <!-- p.ej., generación de pruebas, esqueletos de código, análisis y resolución de errores -->

Valor aportado: Nos ha proporcionado una gran ayuda en cuanto al modelo conceptual y todos los estilos css que no dominamos bien.<!-- síntesis -->

Riesgos relevantes y mitigaciones: Riesgos de repetición de información o desajuste con lo pedido. <!-- síntesis -->

Lecciones aprendidas: Hay que tener mucho cuidado con todo el contenido que nos porporciona y verificarlo previamente ya que tiene gran riesgo de equivocación <!-- síntesis -->

Checklist de cumplimiento de uso ético de la IA del sprint 1:

- [x] Toda interacción significativa está en el Registro Detallado con enlace a conversación.

- [x] No se usó IA para narrativa (o hay autorización documentada).

- [x] Toda pieza aceptada fue comprendida y verificada por humanos (tests/revisión).

- [x] Citas/Atribuciones incluidas cuando corresponde.

- [x] Se usó la IA sin dar datos personales/sensibles que puedieran quedar expuestos a herramientas externas.

### Sprint 2 — Resumen de uso de IA

Usos registrados: 7 

Ámbitos principales:
- Depuración y arreglo de backend (endpoints, controladores y DTOs).
- Generación y ajuste de plantillas CSS y estilos a partir de mockups.
- Soporte para diseño de DTOs para evitar bucles en JSON.
- Asistencia en implementación del módulo social (Friendship).
- Resolución de conflictos Git y ajustes de navegación/frontend.
- Ayuda en trazabilidad entre historias de usuario y pruebas unitarias.

Valor aportado:
- Aceleró la creación de estilos y plantillas CSS que el equipo no dominaba.
- Identificó soluciones prácticas para errores del backend y propuso DTOs/ajustes para serialización.
- Facilitó la generación de tests y la matriz de trazabilidad, y agilizó la resolución de conflictos de integración.

Riesgos relevantes y mitigaciones:
- Riesgo de aceptar soluciones incompletas o no alineadas con la arquitectura del proyecto. Mitigado mediante revisión y pruebas por varios integrantes.
- Riesgo de decisiones de diseño asumidas por la IA. Mitigado validando visualmente y ajustando manualmente.
- Riesgo de cambios que rompan integraciones. Mitigado con ejecución de tests locales y revisión de logs.

Lecciones aprendidas:
- Tenemos que verificar siempre las propuestas de la IA y someterlas a pruebas y revisión humana antes de aceptar.
- La IA es muy útil para generar un esqueleto y sugerencias rápidas (CSS, DTOs, diagnósticos), pero requiere adaptación al contexto del proyecto ya que muchas veces falla o no nos proporciona lo que queremos realmente.

Checklist de cumplimiento de uso ético de la IA del sprint 2:

- [x] Toda interacción significativa está en el Registro Detallado con enlace a conversación.

- [x] No se usó IA para narrativa (o hay autorización documentada).

- [x] Toda pieza aceptada fue comprendida y verificada por humanos (tests/revisión).

- [x] Citas/Atribuciones incluidas cuando corresponde.

- [x] Se usó la IA sin dar datos personales/sensibles que puedieran quedar expuestos a herramientas externas.

### Sprint 3 — Resumen de uso de IA

Usos registrados: 16

Ámbitos principales:
- **Lógica de Backend y Arquitectura:** Estructura del sistema de energías, lógica del tablero, gestión de turnos/rondas, iniciativa aleatoria y configuraciones de seguridad.
- **Frontend y UI:** Estilos CSS para estadísticas, componentes visuales (EnergyCard, Modales), interfaz de chat y lista de amigos.
- **Funcionalidades en Tiempo Real:** Implementación de WebSockets para el Chat y el estado Online/Offline de amigos.
- **Calidad y Documentación:** Generación y corrección de pruebas unitarias, matriz de trazabilidad, análisis de patrones de diseño y limpieza de código.

Valor aportado:
- **Aceleración del Desarrollo:** Facilitó el andamiaje rápido de características complejas como WebSockets y la estructura base del sistema de energías.
- **Mejora de la Calidad:** Ayudó significativamente en la cobertura de pruebas unitarias y en la resolución de errores de integración y seguridad.
- **Coherencia Visual:** Generación de estilos CSS consistentes para nuevos módulos, asegurando una apariencia uniforme.
- **Comprensión Arquitectónica:** Asistencia valiosa para identificar y documentar patrones de diseño y mantener la estructura del proyecto alineada con buenas prácticas.

Riesgos relevantes y mitigaciones:
- **Desalineación Arquitectónica:** Riesgo de soluciones genéricas incompatibles con los requisitos del curso (ej. backend inicial). Mitigado mediante reescritura manual y adaptación a los estándares.
- **Errores Lógicos y Bugs:** Riesgos de bucles infinitos en controladores UI o NullPointers en lógica backend. Mitigado mediante pruebas manuales visuales, logs y validaciones adicionales.
- **Alucinaciones en Documentación:** Riesgo de referencias a tests inexistentes en la trazabilidad. Mitigado con verificación manual de la existencia de archivos.
- **Brechas de Seguridad:** Riesgo en configuraciones generadas automáticamente. Mitigado restringiendo rutas y verificando manualmente la configuración de Spring Security.

Lecciones aprendidas:
- **Verificación Imperativa:** La IA es excelente para esqueletos, pero la lógica de negocio central requiere revisión y ajuste humano riguroso.
- **Importancia del Contexto:** La calidad de las respuestas mejora drásticamente al proporcionar contexto específico de errores y código existente.
- **Proceso Iterativo:** El éxito reside en un ciclo de generación-prueba-corrección, donde la IA actúa como copiloto y no como piloto automático.
- **Complejidad de Lógica de Juego:** Para mecánicas nucleares (como el recálculo de turnos), las sugerencias de la IA suelen requerir adaptación manual significativa para funcionar correctamente.

Checklist de cumplimiento de uso ético de la IA del sprint 2:

- [x] Toda interacción significativa está en el Registro Detallado con enlace a conversación.

- [x] No se usó IA para narrativa (o hay autorización documentada).

- [x] Toda pieza aceptada fue comprendida y verificada por humanos (tests/revisión).

- [x] Citas/Atribuciones incluidas cuando corresponde.

- [x] Se usó la IA sin dar datos personales/sensibles que puedieran quedar expuestos a herramientas externas.

Aquí tienes el bloque de código Markdown completo y corregido para el Sprint 4 y el Anexo B, listo para copiar y pegar sin que se rompa el formato en GitHub.

Markdown

### Sprint 4 — Resumen de uso de IA

Usos registrados: 13

Ámbitos principales:
- **Nuevas Mecánicas de Juego:** Implementación del modo *Team Battle*, lógica de asignación de equipos.
- **Refactorización y Arquitectura:** Limpieza de controladores.
- **Modo Espectador:** Creación de interfaces específicas.
- **Estética y UI Avanzada:** Unificación de estilos visuales bajo estética *Neon/Cyberpunk* y mejora de la responsividad en vistas de perfil y autenticación.
  
Valor aportado:
- **Modularidad:** Facilitó la transición hacia una arquitectura más limpia mediante la separación estricta de responsabilidades (Controller/Service).
- **Experiencia de Usuario:** Logró una interfaz más profesional y coherente en todo el proyecto y permitió la implementación rápida del modo espectador.
- **Robustez en Reglas de Juego:** Ayudó a identificar casos de borde en las reglas de colocación de cartas en el modo por equipos.
  
Riesgos relevantes y mitigaciones:
- **Ruptura de Integridad Referencial:** Riesgo de errores en cascada al refactorizar el borrado de sesiones. Mitigado mediante diagnóstico de errores de claves foráneas y restauración de lógica de borrado.
- **Inconsistencia en Equipos:** Riesgo de partidas desequilibradas en *Team Battle*. Mitigado con validaciones transaccionales en el backend.
- **Estilos Rotos:** Riesgos de visualización en diferentes dispositivos al aplicar estilos globales.

Lecciones aprendidas:
- **Refactorización Guiada:** La IA es una herramienta excepcional para proponer estructuras de servicios a partir de controladores saturados, ahorrando tiempo en tareas repetitivas.
- **Validación del Espectador:** Es crucial filtrar la información que se envía por WebSockets a los espectadores (uso de DTOs específicos) para evitar fugas de datos de otros jugadores.

Checklist de cumplimiento de uso ético de la IA del sprint 4:

- [x] Toda interacción significativa está en el Registro Detallado con enlace a conversación.
- [x] No se usó IA para narrativa (o hay autorización documentada).
- [x] Toda pieza aceptada fue comprendida y verificada por humanos (tests/revisión).
- [x] Citas/Atribuciones incluidas cuando corresponde.
- [x] Se usó la IA sin dar datos personales/sensibles que pudieran quedar expuestos

## Registro detallado de uso de AI por Sprint

**Use una fila por “uso realmente significativo”** (idea sugerida por la IA, trozo de código importante modificado, depuración de error que no eras capaz de resolver por tu cuenta, generación de pruebas para el código de producción, etc.). No incluya filas para detalles nímios como el autocompletado de variables o signaturas de métodos, o la generación de código simple (recorridos y procesamiento de estructuras de datos, formateo  y/o creación de estilos CSS, etc.).

### Sprint 1 registro detallado de uso de IA por sprint

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 1.1 | 26/09/2025 12:40 | 1 | Nicolás Pérez Martín | ChatGPT (GPT-5, OpenAI, 2025) | Aplicacion ChatGPT | https://chatgpt.com/share/68dac3ac-1218-8007-ade3-d779be630cb3 | documentación técnica | ficheros D1 | revisión entre varios miembros y profesora | riesgo de equivocación en las clases del diagrama | aceptado con cambios parciales |
| 1.2 | 01/10/2025 16:00 | 1 | Juan Pozo García | Perplexity (Mejor Perplexity) | Aplicacion Perplexity | https://www.perplexity.ai/search/contexto-nos-han-mandado-el-si-O_I_LW44TZWbohDIU1kEug#1 | documentación técnica | mockups | revision por integrantes | ninguno | aceptado pero con cambios parciales en la paleta de color
| 1.3 | 05/10/2025 | 1 | Juan Pozo García | Perplexity (Claude-Sonnet 4.5, Claude, 2025) --> | Aplicación Perplexity | https://www.perplexity.ai/search/necesito-que-me-ayudes-a-los-e-twv4o.7fR9aD3wtbdW1_vw?1=d#2 | codigo css como plantilla | ficheros y clases para Profile | revisión por integrantes | Riesgo de elementos mal posicionados o no bien colocados | aceptado con cambios parciales |
| 1.4 | 04/10/2025 | 1 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) --> | Aplicación ChatGPT | https://chatgpt.com/share/68e2e09a-4b4c-8012-aa85-7ae7ef2abfc5 | codigo css como plantilla | archivos para logros | revisión por integrantes | Riesgo de elementos mal posicionados o no bien colocados | aceptado con cambios parciales |
| 1.5 | 15/09/2025 | 1 | Alejandro Pichardo Martinez | ChatGPT Sora (GPT-5, OpenAI, 2025) --> | Web OpenAI |https://sora.chatgpt.com/g/gen_01k56hzh8hftmsb9s6zf6tf364 | creacion de logo | Barra de navegación | revisión por integrantes | Practicamente sin riesgo | aceptado con cambios parciales |
| 1.6 | 15/09/2025 | 1 | Alejandro Pichardo Martinez | ChatGPT Sora (GPT-5, OpenAI, 2025) --> | Web OpenAI |https://sora.chatgpt.com/g/gen_01k6jrdpbnfz991gezj3fcx9fs | creacion de fondo | Barra de navegación | revisión por integrantes | Practicamente sin riesgo | finalmente no utilizado |
| 1.7 | 02/10/2025 | 1 | Francisco Casasola Calzadilla | ChatGPT (GPT-5, OpenAI, 2025) | Aplicacion ChatGPT | Corrigme faltas de ortografía o mala estrutura formal del siguiente texto como una historia de usuario | Correción de ortografía y formato | HU | revision por integrantes | Riesgo de formato no deseado | Cambios en la estrutura de las historias de usuario |
| 1.8 | 02/10/2025 | 1 | Carmen Camacho Montes | ChatGPT (GPT-5, OpenAI, 2025) | Aplicacion ChatGPT | Pon estructurado y corrige ortografía de las Historias de Usuario | Correción de ortografía | HU | Revision | Riesgo de omisión de información | aceptado con cambios parciales |
-----------
### Sprint 2

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & modificaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 2.1 | 19/10/2025 12:00 | 2 | Juan Pozo García | ChatGPT (GPT-5, OpenAI, 2025) | web | https://chatgpt.com/share/68f5fa31-7c48-8011-98c5-f5e0d817f829 | ayuda con el diagrama de dominio | ficheros | revisión por varios integrantes del grupo | riesgo de mal entendidos con lo que teniamos pensado | aceptado pero con grandes cambios|
| 2.2 | 03/11/2025 00:20 | 2 | Juan Pozo García | Copilot (Claude-Sonet 4.5, Claude, 2025) | integración Visual Studio Code | Se le ha pedido como prompt que analice las distintas historias de usuarios de D1 y las relacione con las pruebas unitarias | ayuda con la amtriz de trazabilidad | ficheros | revisión por varios integrantes del grupo | riesgo de equivocaciones con las pruebas unitarias e historias de usuarios | aceptado pero con grandes cambios|
| 2.3 | 31/10/2025 20:30 | 2 | Nicolás Pérez Martín | Gemini 2.5 Pro (Google, 2025) | web | https://gemini.google.com/share/d07c122f5d4e | arreglo de distintos errores en el backend + creación de estilos CSS a partir de los mockups del sprint anterior | ficheros | revisión interna del equipo sobre la solución generada | riesgo de asumir decisiones de diseño sin alinear con el PO → mitigado consultando las reglas del diseño | aceptado con grandes cambios |
| 2.4 | 31/10/2025 20:30 | 2 | Nicolás Pérez Martín | Gemini 2.5 Pro (Google, 2025) | web | https://gemini.google.com/share/d07c122f5d4e | creacion de DTOs de las diferentes entidades para solucionar bucles en los JSON | ficheros | revisión interna del equipo sobre la solución generada | riesgo de asumir decisiones de diseño de as diferentes entidades | aceptado con pequeños cambios |
| 2.5 | 30/10/2025 19:00 | 2 | Carmen Camacho Montes | ChatGPT (GPT-5, OpenAI, 2025) | web | Conversaciones sobre implementación inicial del módulo *Friendship* (modelo, repositorio, servicio y controlador) | Diseño e implementación de la estructura base del módulo social (amistades) y configuración de relaciones entre entidades | ficheros |  revisión interna del equipo sobre la solución generada | Riesgo de malinterpretar relaciones entre entidades mitigado revisando el modelo de dominio | Aceptado con pequeños cambios |
| 2.6 | 31/10/2025 21:10 | 2 | Carmen Camacho Montes | ChatGPT (GPT-5, OpenAI, 2025) | web | Conversación sobre error `GET not supported` en `/api/v1/friendships` | Depuración de endpoint y ajuste de métodos HTTP para obtener amistades | Controladores y servicios del módulo social | revisión interna del equipo sobre la solución generada | Riesgo de endpoints incorrectos mitigado verificando logs y controladores | Aceptado sin incidencias |
| 2.7 | 02/11/2025 11:00–12:45 | 2 | Carmen Camacho Montes | ChatGPT (GPT-5, OpenAI, 2025) | web | Serie de consultas relacionadas con conflictos tras `git pull`, depuración del `fetch` en `friendshipList`, y corrección de la navegación al módulo social (`/friendships`) | Resolver conflictos de merge, arreglar peticiones API y restaurar la navegación hacia el módulo social desde `App.js` | Repositorio, código React (`friendshipList/index.js`, `App.js`, `PrivateRoute`) | revisión interna del equipo sobre la solución generada | Riesgo de pérdida de commits y ruptura de rutas mitigado con backups y pruebas locales | Aceptado y plenamente funcional |
<<<<<<< HEAD
| 2.8 | 31/10/2025 16:36 | 2 | Francisco Casasola Calzadilla | Gemini 2.5 Pro (Google, 2025) | web | Ayudame con las siguientes pruebas unitarias que he hecho sobre un proyecto que estamos haciendo un juego, mira si tengo bien la estructura y si hay datos que no correspondan con lo suyo | Ayuda con la estructura de las pruebas unitarias | distintas pruebas unitarias | revisión interna del equipo sobre la solución generada | Riesgo de pérdida de commits | Pruebas unitarias corregidas |
| 2.9 | 31/10/2025 12:25 | 2 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) | Aplicación ChatGPT | "Cómo manejar errores y enviar BadRequest con mensajes claros" | Manejo de excepciones | RestExceptionHandler.java | Pruebas manuales | Riesgo de respuestas inconsistentes; mitigado con handler global | Manejo de errores mejorado |
| 2.10 | 31/10/2025 12:25 | 2 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) | Aplicación ChatGPT | "Cómo estructurar servicios para separar lógica de juego y persistencia" | Estructura de servicios | GameSessionService.java, repositories | Revisión de arquitectura | Riesgo de acoplamiento; mitigado con refactor | Servicios organizados |
=======
| 2.8 | 24/10/2025 18:30 | 2 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) | web | Ayúdame a crear los endpoints REST para que los jugadores puedan unirse y salir de una partida, a partir de los service que ya he creado. Asegura la coherencia de datos. | Implementación de controladores joinGame y leaveGame | GameSessionRestController.java | Validado con pruebas unitarias | Posible duplicidad de llamadas WebSocket | Aceptado tras pequeñas correcciones |
| 2.9 | 31/10/2025 16:36 | 2 | Francisco Casasola Calzadilla | Gemini 2.5 Pro (Google, 2025) | web | Ayudame con las siguientes pruebas unitarias que he hecho sobre un proyecto que estamos haciendo un juego, mira si tengo bien la estructura y si hay datos que no correspondan con lo suyo | Ayuda con la estructura de las pruebas unitarias | distintas pruebas unitarias | revisión interna del equipo sobre la solución generada | Riesgo de pérdida de commits | Pruebas unitarias corregidas |
>>>>>>> main


### Sprint 3

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 3.1 | 10/11/2025 | 3 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) --> | Aplicación ChatGPT | “En mi proyecto End of Line estoy implementando un sistema de energías con cuatro efectos: BOOST (3 cartas en turno), BRAKE (1 carta), REVERSE (continuar la línea desde la penúltima carta propia si tiene una salida libre) y EXTRA_FUEL (robo adicional). El backend es Spring Boot con entidades GameSession, PlayerGameSession, PlacedCard y DTOs; el frontend es React. Quiero que me digas: Estructura de carpetas y archivos recomendada para mantener clara la lógica de energías (servicios, repositorios, DTOs, componentes de UI).Qué estados/flags debo guardar por jugador y cómo exponerlos en los DTO (energía restante, efecto activo, override de cartas, bandera de marcha atrás, robo extra, ronda en que gastó energía). Cómo validar cada efecto en backend: BOOST/BRAKE → límite de cartas por turno (override). REVERSE → solo si la penúltima carta tiene salida libre, anclando la colocación a esa penúltima con entrada/salida compatibles. EXTRA_FUEL → dónde y cuándo otorgar la carta extra. Cómo coordinar frontend/backend: Deshabilitar/permitir el botón de energía según turno, ronda y energía disponible. Calcular isMyTurn con los overrides reales. Mostrar el efecto activo y mensajes de error si el backend rechaza la acción. Estrategia de pruebas: casos para cada energía, flujos de turno, rondas y estados límite. Organiza la respuesta con pasos concretos y referencias a los archivos típicos (PlayerGameSessionService, PlacedCardService, GameSessionService, DTOs, index.js/React).” | Implementar energías | archivos backend y frontend relacionados con las cartas y el gameboard | revisión por integrantes | Riesgo de creación de código innecesario o validaciones erroneas | Organización correcta y buena planificación para la posterior implementación |
| 3.2 | 25/11/2025 | 3 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) --> | Aplicación ChatGPT | “Necesito identificar y comprender patrones de diseño en mi proyecto End of Line. El stack es Spring Boot en backend (entidades GameSession, PlayerGameSession, PlacedCard, servicios GameSessionService, PlayerGameSessionService, PlacedCardService, controladores REST) y React en frontend (componentes index.js, EnergyActionsMenu, GameBoardComponent, etc.). Dame una guía para: Analizar el código y detectar patrones GOF comunes (Factory, Strategy, Observer, Template Method) o patrones de arquitectura (DTO, Repository, Service Layer, Controller) que ya están presentes. Cómo documentar cada patrón encontrado: rol de cada clase, diagrama simple de relaciones, beneficios y compromisos. Qué señales buscar en mi código para reconocer patrones: uso de interfaces, inyección de dependencias, servicios que encapsulan reglas de negocio, repositorios que ocultan el acceso a datos, DTOs que separan dominio de transporte, hooks/componentes reutilizables en React. Estructura de un documento resumen: lista de patrones encontrados, ubicación de las clases/archivos, por qué se usan, qué mejorar, ejemplos de código. Responde con pasos concretos | explicación de qué patrones se usan en el proyecto y cómo encontrarlos | revisión del proyecto completo | revisión por integrantes | Riesgo de una explicación erronea | explicación coherente sobre los patrones existentes y cómo lograr encontrarlos |
| 3.3 | 02/11/2025 10:00 | 3 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) | web | Quiero implementar el servivio de WebSocket para el lobby que actualmente se actualiza con polling. Dime los pasos que he de seguir para este cambio. Mi utilizacion de polling actual es: (contenido de lobby) | Desarrollo de sistema notifyLobbyUpdate y conexión en tiempo real | GameSessionRestController.java, /lobby/index.js | Revisión grupal| Riesgo de actualizaciones desincronizadas | Aceptado tras revision y correcto funcionamiento |
| 3.4 | 10/11/2025 17:45 | 3 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) Codex | web | Ajusta mi css para reducir el tamaño de las cartas de la mano playerHand. Aparte quiero que se coloquen en vertical en vez de horizontal creando una nueva columna HandColumn entre el tablero y el chat | Ajuste de interfaz (reposicionamiento vertical de cartas y chat lateral) | PlayerHand.css, Game.css, playerHand.js | Verificado visualmente por el grupo | Riesgo de inconsistencias en resoluciones pequeñas | Aceptado sin cambios |
| 3.5 | 28/11/2025 18:45 | 3 | Juan Pozo García | Copilot (Gemini-3, GitHub Copilot)| integración VS Code | Se pidió ayuda para analizar y corregir los tests unitarios de GameSessionService, PlacedCardService, PlacedCardController, PlayerCardService, PlayerCardController y GameSessionController, incluyendo explicación de errores, generación de código de test, sugerencias de refactorización y revisión de lógica de excepciones. | Generación y depuración de tests unitarios | GameSessionServiceTest.java, GameSessionControllerTests.java, PlacedCardService.java, PlacedCardController.java, PlayerCardService.java, PlayerCardController.java y GameSessionController.java | Revisión y ejecución de los tests por el equipo | Riesgo de tests incorrectos o dependientes de lógica no cubierta; mitigado revisando los resultados y ajustando la lógica según los errores detectados | Aceptado con cambios parciales |
| 3.6 | 22/11/2025 19:10 | 3 | Juan Pozo García | Copilot (Gemini-3, GitHub Copilot) | integración VS Code | Se pidió a Copilot que generara un archivo CSS con estilos personalizados para el apartado de playerStatistics, especificando colores, tipografía y disposición de los elementos para mejorar la visualización de estadísticas de jugador en el frontend. | Generación de código CSS | rankingDetail.css, StatisticsGeneral.css y StatisticsList.css | Revisión visual y por el equipo, ajustes manuales en los estilos | Riesgo de estilos poco accesibles o incompatibles; mitigado revisando en varios navegadores y adaptando a la paleta del proyecto | Aceptado con cambios parciales |
| 3.7 | 30/11/2025 20:53 | 3 | Juan Pozo García | Copilot (Gemini-3, GitHub Copilot) | integración VS Code | Se solicitó a la IA que analizara el código fuente para identificar tests implementados, los mapeara con las Historias de Usuario correspondientes y actualizara el estado y la cobertura en la matriz de trazabilidad. | Análisis de código y Documentación | Matriz de trazabilidad entre Pruebas e Historias de Usuario.md, Plan de Pruebas.md | Verificación manual de los enlaces a los tests y coherencia de los datos de cobertura | Riesgo de alucinación de tests inexistentes; mitigado mediante la verificación de la existencia de los archivos y métodos citados | Aceptado |
| 3.8 | 16/11/2025 | 3 | Carmen Camacho Montes | Gemini 1.5 Pro (Plan Estudiante) | Chat Web | Generación de la estructura inicial del Backend para el tablero (entidades Board, PlacedCard). La IA proporcionó el esqueleto JPA básico, pero la lógica de relaciones y servicios fue reescrita manualmente | Desarrollo de Backend | PlacedCard.java, PlacedCardController.java, PlacedCardService.java, PlacedCardRepository.java, PlaceCardRequestDTO.java, GameSessionService.java | Verificación manual | Riesgo de código generado no conforme a la arquitectura del proyecto; mitigado reescribiendo la lógica según estándares de la asignatura | Aceptado con cambios parciales |
| 3.9 | 24/11/2025 | 3 | Carmen Camacho Montes | Gemini 1.5 Pro (Plan Estudiante) | Chat Web | Consulta sobre errores en la lógica de negocio (turnos y flujo de juego). La IA sugirió soluciones genéricas para el manejo de estados, que fueron adaptadas y corregidas manualmente utilizando la lógica de control de los proyectos base de la asignatura para garantizar la integridad de la partida. | Lógica de Negocio | GameSessionService.java, GameSessionController.java, BoardCell.js, GameBoardComponent.js | Tests manuales de flujo de juego | Riesgo de soluciones ineficientes o inseguras sugeridas por la IA; mitigado contrastando con otra IA (Copilot) | Aceptado con cambios parciales |
| 3.10 | 25/11/2025 | 3 | Carmen Camacho Montes | Gemini 1.5 Pro (Plan Estudiante) | Chat Web | Generación de validaciones para inputs de usuario y reglas de colocación. Se utilizó la IA para obtener puntos de errores en el código. | Implementación de Validaciones | PlacedCardService.java, PlayerCardController.java | Validación de Lógica de Negocio | No hubo riesgos, detectó los errores con éxito | Aceptado |
| 3.11 | 28/11/2025 | 3 | Carmen Camacho Montes | Gemini 1.5 Pro (Plan Estudiante) | Chat Web | Asistencia para la limpieza de código. El resultado de la IA se revisó, eliminando redundancias. | Limpieza de Código y Documentación | Todos los comentarios del pryecto | Revisión de Comentarios | Riesgo de corrección genérica o imprecisa; mitigado revisar con precisión los cambios | Aceptado |
| 3.12 |	14/11/2025 16:20 | 3 | Nicolás Pérez Martín |	Gemini 2.5 Pro (Google) | Chat Web	| Implementación del componente visual EnergyCard y su integración en el tablero de juego (GameBoard).	| Generación de código React y CSS. | EnergyCard.js, GameBoard.js, Game.css	| Corrección de ubicación (mover de sidebar al centro) y solución de error crítico de sintaxis (bucle JSX) que rompía el renderizado. | Riesgo de romper el layout existente; mitigado corrigiendo la estructura del DOM. | Aceptado con cambios
| 3.13 | 18/11/2025 11:30 | 3 | Nicolás Pérez Martín | Gemini 3.0 Pro (Google) | Chat Web | Implementación de chat en tiempo real usando WebSockets (STOMP/SockJS) para evitar recargas manuales. | Generación de configuración Backend y componente Frontend. | WebSocketConfig.java, ChatMessageController.java, GameChat.js / ChatMessage.js | Verificación de flujo de mensajes (envío REST -> broadcast WebSocket -> recepción cliente). | N/A | Aceptado
| 3.14 | 26/11/2025 18:15 | 3 | Nicolás Pérez Martín | Gemini 3.0 Pro (Google) | Chat Web | Implementación de lista de amigos con estado "Online/Offline" en tiempo real (HU-37) usando WebSockets. | Generación de lógica de eventos de conexión/desconexión y actualización visual en React. | WebSocketEventListener.java, FriendshipController.java, FriendshipList.js, WebSocketContext.js | Solución de problemas de estado inicial (persistencia en memoria del servidor) y conexión global mediante WebSocketProvider para mantener estado al navegar. | Privacidad: mostrar estado solo a amigos confirmados. | Aceptado
| 3.15 | 24/11/2025	| 3	| Nicolás Pérez Martín | Gemini 3.0 Pro (Google) | Web Chat | "Tengo que implementar el modo de juego de los puzzles, tenía pensado usar el patrón factory, por lo que necesito que me expliques cómo debo implementar el código y cómo usar las cartas que salen en el tablero para molestar al juagor" | Estructuración del backend mediante el patrón Factory Method para generar dinámicamente distintos niveles de puzzle sin duplicar lógica de inicialización. | PuzzleFactoryService.java, PuzzleDefinition.java, GameSessionService.java | Ejecución de tests de integración comprobando que al pedir el "Puzzle 1" se carga el tablero con los obstáculos (CB_BACK.png) en las coordenadas correctas. | Riesgo de complejidad accidental al crear clases para cada puzzle. Mitigado usando una estructura de datos (Map<Integer, PuzzleDefinition>) cargada al inicio (@PostConstruct). | Lógica de creación de niveles centralizada y desacoplada del flujo principal del juego.
| 3.16 | 15/11/2025 | 3 | Francisco Casasola Calzadilla | Gemini (Modelo LLM) | Web | Ayudame a saber que implementar de la lógica backend para sortear la iniciativa mediante cartas aleatorias. | Saber que controladores y servicios debo actualizar o implementar  | Servicios y Controladores de GameSession y PlayerGamesession | Revisión de compilación y lógica de Random | Riesgo: NullPointer si no hay cartas. Mitigación: Validaciones añadidas. | Método drawCardForInitiative implementado |
| 3.17 | 15/11/2025 | 3 | Francisco Casasola Calzadilla | Gemini (Modelo LLM) | Web | Ayudame a saber bien si he implementado bien este modal y ayudame para implementar el css | Saber si mi modal este bien implementado y ayudarme hacer su css | FirstTurnModal y css  | Test visual en navegador | Riesgo: Bucle infinito de renderizado. Mitigación: Uso de sessionStorage y useEffect acotado. | Modal funcional con animación de volteo de cartas. |
| 3.18 | 24/11/2025 | 3 | Francisco Casasola Calzadilla | Gemini (Modelo LLM) | Web | "Error 401", "Error 500", "NoResourceFoundException" | Solucionar errores de integración entre Frontend y Backend (Seguridad y Mapeo). | FirstTurnModal, SecurityConfiguration.java y GameSessionRestController.java  | Comprobación de logs del servidor y Network tab | Riesgo: Abrir brechas de seguridad. Mitigación: Restringir rutas específicas a authenticated. | API securizada y endpoint gameList correctamente enlazado. |
| 3.19 | 27/11/2025 | 3 | Francisco Casasola Calzadilla | Gemini (Modelo LLM) | Web | Ayudame a recalcular el turno dependiendo de la última carta echada | Implementar mecánica de Iniciativa Dinámica (reordenar turnos al cambiar de ronda). | GameSessionService.java (recalculateTurnOrder..., handleCardPlacement), PlacedCardRepository.java  | Jugar hasta ronda 2 y verificar cambio de orden. | Riesgo: No detectar fin de ronda. Mitigación: Lógica robusta en handleCardPlacement |Recálculo automático de turnos basado en la última carta jugada. |

### Sprint 4

| # | Fecha y hora | Sprint | Integrante(s) | **Herramienta & versión** | **Acceso** | **Enlace a conversación / Prompt** | **Finalidad** | **Artefactos afectados** | **Verificación humana** | **Riesgos & mitigaciones** | **Resultado** |
|---:|--------------|:-----:|---------------|----------------------------|------------|------------------------------------|---------------|---------------------------|--------------------------|-----------------------------|---------------|
| 4.1 | 08/01/2026 | 4 | Juan Pozo García | Antigravity (Google) | IDE Agent | "Necesito implementar el modo de juego TEAM BATTLE, sin embargo como podriamos añadir un selector de equipos en el lobby?", "y el JUMP LINE funciona?" | Implementación modo Team Battle: asignación de equipos equilibrada y mecánica 'Jump Line'. | JoinGameService.java, GameSessionService.java, frontend files | Verificación de reglas de negocio y flujo de juego. | Riesgo de inconsistencia en equipos. Mitigado con asignación automática y elección de equipos a deseo del jugador. | Aceptado |
| 4.2 | 12/01/2026 | 4 | Juan Pozo García | Antigravity (Google) | IDE Agent | "Podrias ponerme el mismo estilo (el de las estadísticas) en la pantalla de registro, login, logout, profile y editProfile" | Unificación de estilos visuales (Dashboard/Neon) en autenticación, perfil y listados de juegos. | StatisticsGeneral.css, auth/register, auth/login, profile/*, gameList/* | Verificación visual de los componentes y su responsividad. | Riesgo de desbordamiento de contenido o mala alineación. Mitigado ajustando CSS (max-width, flex-grow). | Aceptado |
| 4.3 |	08/01/2026 | 4 | Nicolás Pérez Martín |	Gemini (Google)	| Web Chat | "The method joinGame(Integer, String) is undefined..." / "He encontrado esto en el backend (JoinGameService), esto sí que debería usarlo no?" | Refactorización del servicio de invitaciones para reutilizar JoinGameService en lugar de lógica duplicada, asegurando validaciones de estado y notificaciones WebSocket. | GameInvitationService.java, JoinGameService.java, GameSessionService.java | Testeo manual de aceptación de invitaciones y revisión de logs de Spring. | Riesgo de dependencias circulares o transacciones anidadas. Mitigado usando inyección de dependencias correcta y @Transactional. | Código refactorizado e integrado correctamente. |
| 4.4 | 12/01/2026 | 4 | Nicolás Pérez Martín | Gemini (Google) | Web Chat | "Ayúdame a hacer que el texto se vea en el medio de la pantalla al jugador que se une a una partida, por medio de una invitación, que todavía no esta empezada por lo que su estado es PENDING desde el modo espectador (Spectate.js)" | Generación de interfaz gráfica (Frontend) para la vista de espectador, incluyendo estilos CSS neon/cyberpunk y superposiciones para estados de espera. | Spectate.js, spectate.css | Revisión visual de estilos y adaptabilidad (Media Queries) en navegador.	| Riesgo de estilos rotos en pantallas grandes. Mitigado corrigiendo el anidamiento de las @media queries. | Interfaz visual finalizada y responsiva. |
| 4.5 | 12/01/2026 | 4	| Nicolás Pérez Martín | Gemini (Google) | Web Chat	| "Necesito añadir una validación en el service para unirte a una partida meiante la invitación de un amigo, para que esta no funcione si ya están llenas todas las plazas ocupadas" | Implementación de regla de negocio crítica: impedir aceptar invitaciones si la partida ya ha alcanzado el aforo máximo de jugadores activos. | GameInvitationService.java, NotificationDrawer.js	| Test manual intentando unir un 3er jugador a partida de 2. Validación de mensaje de error en frontend. | Riesgo de condiciones de carrera (race conditions). Mitigado con validación transaccional en el momento de la escritura en DB. | Prevención de partidas inconsistentes (overbooking). |
| 4.6 | 11/01/2026 | 4 | Carmen Camacho Montes | Gemini Pro for students (Google) | Web Chat | "Diseña un flujo de WebSockets en Spring Boot para un modo espectador. Los espectadores deben entrar a una partida y recibir el estado del tablero en solo lectura cada vez que un jugador realice una acción." | Implementar la funcionalidad de observación en tiempo real, permitiendo que usuarios no activos sigan la partida sin interferir en la lógica de turnos. | frontend/src/spectate/index.js, PlacedCardController.java, GameSessionRestController.java | Comprobar que el espectador no tiene permisos para enviar mensajes a los canales de acción. | Riesgo: Fuga de información. Mitigación: Crear un DTO específico para espectadores que oculte datos sensibles. | Modo espectador funcional con actualización automática de interfaz. |
| 4.7 | 12/01/2026 | 4 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) | Aplicación ChatGPT | "Debo crear pruebas para el frontend de jest, quiero que me hagas una breve explicación de como debo realizarlos y en que debo pensar a la hora de realizar los test sobre los componentes frontend." | Guiarme a la hora de crear tests para el frontend con jest, explicandome su funcinamiento | archivos del frontend terminados en .test.js, asi como: gameList.test.js, friendship.test.js... | Verificación en todas las pruebas | Riesgo de creación de código innecesario o test erróneos. Mitigación: verificación humana y ejecución de pruebas | Organización mental y entendimiento sobre la creación de los jest. |
| 4.8 | 13/01/2026 | 4 | Álvaro de Pablos Sánchez | ChatGPT (GPT-5, OpenAI, 2025) | Aplicación ChatGPT | "en el modo team battle tengo un problema, no solo vale con poder colocar una carta donde coincida una salida y un hueco adyacente, sino que sea un hueco adyacente y una conexion con tu propia linea. porque ahora mismo en team battle, yo puedo colocar una carta en un puesto adyacente, pero si adyacente a ese hueco hay una carta con salida de otro color, yo puedo colocar la carta de mi color(rojo por ejemplo) que coincide entrada y salida con la adyacente(azul por ejemplo) sin que la entrada y salida coincidan con mi color (rojo) aunque sea adyacente a los dos. Como podría solucionarlo" | Ajustar la regla de colocación en Team Battle para exigir conexión con la propia línea al usar Jump Line y evitar conexiones con línea rival. | PlacedCardService.java, GameSessionService.java | Pruebas manuales en partidas Team Battle y modos clásicos | Riesgo de romper validaciones en otros modos; mitigado con pruebas manuales en varios modos. | Propuesta de ajuste de validación |
| 4.9 | 07/01/2026  | 4 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) | web | Quiero refactorizar los controladores JoinGame y LeaveGame para que solo contengan lógica propia del controlador y mover la lógica de negocio a servicios, manteniendo exactamente el mismo comportamiento funcional. ¿Qué código debería moverse y cómo estructurar el servicio? | Refactorización de JoinGame y LeaveGame, separación clara controller/service | JoinGameService.java, LeaveGameService.java, GameSessionRestController.java | Revisión individual | Riesgo de pérdida de lógica implícita en el controlador | Aceptado tras pruebas manuales y corrección de errores detectados |
| 4.10 | 07/01/2026 | 4 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) | web | Tengo un error al hacer leave de una partida ya iniciada: aparece una violación de clave foránea con placed_cards. Analiza mi refactorización y dime qué parte del flujo de borrado puede estar rompiendo la integridad referencial. | Diagnóstico y corrección del flujo de borrado de PlayerGameSession tras refactorización | LeaveGameService.java, PlayerGameSessionService.java | Revisión individual | Riesgo de corrupción de datos | Aceptado tras identificar y restaurar la lógica de borrado correcta |
| 4.11 | 09/01/2026 | 4 | Alejandro Pichardo Martínez | ChatGPT (GPT-5, OpenAI, 2025) | web | Quiero refactorizar el ChatMessageController para que la lógica de validación, persistencia y envío por WebSocket esté en el servicio y el controlador sea lo más ligero posible. Quiero evitar crear nuevas clases si no es estrictamente necesario. Como puedo estructurarlo? | Limpieza del controlador de chat y centralización de la lógica en el servicio | ChatMessageController.java, ChatMessageService.java | Revisión individual | Riesgo de romper el envío en tiempo real | Aceptado tras mantener funcionalidad completa del chat |
| 4.12 | 07/01/2026 | 4 | Francisco Casasola Calzadilla | Gemini (Google) | web | Quiero usar WebSocket en una partida, especificamente en el tablero y mazo, para que no se me recargue la página entera al poner una carta en el tablero o descartar mi carta. Como puedo implementarlo y usarlo ? | Poder jugar una partida sin tener que cargar la página constantemente | GameSessionController.java, GameSessionService.java, index.js (frontend/gameplay) | Revisión individual | Riesgo de romper el metodo de actualizacion de partida | Aceptado tras mantener funcionalidad completa del chat |
| 4.13 | 12/01/2026 | 4 | Francisco Casasola Calzadilla | Copilot | web | Este es mi diagrama de capas, he revisado todos los controladores, servicios y repositorios. Dime si hay alguna asociación que me he saltado y necesito implementar | Verificar si hay alguna asociación que me haya saltado | DiagramaDeCapas.drawio.svg | Revisión individual | Riesgo de implementar asociones inexsistentes o no implementar asociaciones existentes | Aceptado tras ver si eran ciertas las que me faltaban |

## Conclusiones finales sobre el uso de la IA en el proyecto
Aqui debéis reflexionar sobre el papel que ha tenido la IA en la realización de vuestro proyecto y las maneras que consideráis que son más adecuadas para su uso en este contexto. Si tenéis alguna curiosida o caso que sea reseñable y pueda ser útil que los profesor conozcamos de cara a orientar a otros compañeros a este respecto podéis incluirlas aquí también.

## Anexo A) Inventario de Herramientas de IA
|Herramienta|Versión/Modelo|Proveedor|Acceso (web/plugin/API)| Licencia/Plan | Observaciones|
|-----------|--------------|---------|-----------------------|---------------|--------------|
| ChatGPT (GPT-5) | 5 (2025) | OpenAI | aplicación | gratuito | Ninguna |
| ChatGPT (GPT-5) | 5 (2025) | OpenAI | aplicación | pro | Solo tres integrantes la poseen |
| Perplexity | Mejor (2025) | Perplexity | aplicación | pro | Dentro de Perplexity podemos escoger modelos como Claude Sonnet 4.5, el modelo Mejor hace enrrutamiento y escoge el mejor modelo |
| Antigravity | 3.0 Pro | Google | plugin | pro | Ninguna |
| Gemini | 3.0 Pro | Google | aplicación | pro | Ninguna |
| Github Copilot | Auto | Github | plugin | gratuito | Ninguna |

## Anexo B) Glosario de Finalidades

| Finalidad | Descripción |
| :--- | :--- |
| **Idea / Exploración** | Consultas para conceptualizar mecánicas, flujos de usuario o estilos visuales iniciales. |
| **Diseño Técnico** | Definición de arquitectura, diagramas de dominio, capas y estructuras de DTOs. |
| **Generación de Código** | Creación de fragmentos de código, componentes React o servicios Spring Boot. |
| **Depuración / Diagnóstico** | Identificación y resolución de errores (bugs), excepciones y fallos de integración. |
| **Generación de Pruebas** | Creación y corrección de tests unitarios, de integración o frontend (Jest/JUnit). |
| **Documentación Técnica** | Apoyo en artefactos no narrativos: matrices de trazabilidad, planes de prueba o HU. |
| **Refactorización** | Mejora de la estructura del código existente sin cambiar su funcionalidad. |
| **Ajuste de Interfaz (UI/UX)** | Generación y ajuste de CSS para mejorar la visualización y responsividad. |

# -*- coding: utf-8 -*-
"""
Genera el informe de la Experiencia 3 - Semana 8 (OAuth2.0, Docker y
docker-compose sobre el ecosistema de microservicios, actividad SUMATIVA
individual) sobre la plantilla oficial DUOC
(PBY2202_EFT_Plantilla_Informe_PDF.docx), reutilizando sus estilos y dejando
portada, indice/TOC y aviso legal intactos. Mismo formato que los informes
de las Semanas 6 y 7.

Uso:  python scripts/generar_informe_duoc.py
Requiere la plantilla en el nivel superior de la carpeta S8 (ultraDocumentBody):
  ../PBY2202_EFT_Plantilla_Informe_PDF.docx
Salida: Informe_Exp3_S8_Felipe_Penaloza.docx (en la raiz del proyecto).
"""
import os
from docx import Document
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml.ns import qn

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EVID = os.path.join(RAIZ, "evidencia")
PLANTILLA = os.path.join(os.path.dirname(RAIZ), "PBY2202_EFT_Plantilla_Informe_PDF.docx")
SALIDA = os.path.join(RAIZ, "Informe_Exp3_S8_Felipe_Penaloza.docx")

ESTUDIANTE = "Felipe Peñaloza Oyarzún"

if not os.path.exists(PLANTILLA):
    raise SystemExit(f"No se encontro la plantilla en: {PLANTILLA}")

doc = Document(PLANTILLA)
body = doc.element.body

anchor = None
for p in doc.paragraphs:
    pPr = p._p.find(qn("w:pPr"))
    if pPr is not None and pPr.find(qn("w:sectPr")) is not None:
        anchor = p._p
        break
if anchor is None:
    raise SystemExit("No se encontro el parrafo con el salto de seccion (sectPr).")

def set_text(paragraph, text):
    if not paragraph.runs:
        paragraph.add_run(text)
        return
    paragraph.runs[0].text = text
    for r in paragraph.runs[1:]:
        r.text = ""

paras = doc.paragraphs
set_text(paras[16], "Desarrollo Backend III (PBY2203)")
set_text(paras[17], "Desarrollando microservicios y resiliencia en la nube con Spring Cloud")

p18 = paras[18]
p18.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p18.add_run("OAuth2.0, Docker y docker-compose para el ecosistema del Banco XYZ")
r.font.size = Pt(14)

p19 = paras[19]
p19.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p19.add_run("Experiencia 3 - Semana 8 (actividad sumativa individual)\n"
                f"{ESTUDIANTE}\n"
                "Facilitador disciplinar: Ana Karina Villagran Ibarra")
r.font.size = Pt(11)

INICIO_EJEMPLO, FIN_EJEMPLO = 26, 48
elems_a_borrar = []
idx = 0
for child in list(body):
    tag = child.tag.split("}")[-1]
    if tag in ("p", "tbl"):
        if INICIO_EJEMPLO <= idx <= FIN_EJEMPLO:
            elems_a_borrar.append(child)
        idx += 1
for el in elems_a_borrar:
    el.getparent().remove(el)

def _mover(el):
    anchor.addprevious(el)

def h1(t):
    p = doc.add_paragraph(t, style="Heading 1"); _mover(p._p); return p

def h2(t):
    p = doc.add_paragraph(t, style="Heading 2"); _mover(p._p); return p

def par(t, bold=False):
    p = doc.add_paragraph(); rr = p.add_run(t); rr.bold = bold; _mover(p._p); return p

def bullet(t):
    p = doc.add_paragraph(style="List Paragraph"); p.add_run("•  " + t); _mover(p._p); return p

def code(t):
    p = doc.add_paragraph()
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.0
    rr = p.add_run(t)
    rr.font.name = "Consolas"; rr.font.size = Pt(8.5)
    rr.font.color.rgb = RGBColor(0x33, 0x33, 0x33)
    _mover(p._p); return p

def salto_pagina():
    p = doc.add_paragraph(); p.add_run().add_break(WD_BREAK.PAGE); _mover(p._p)

def imagen(nombre, ancho=6.0, pie=None):
    ruta = os.path.join(EVID, nombre)
    if not os.path.exists(ruta):
        par(f"[FALTA IMAGEN: {nombre}]", bold=True); return
    p = doc.add_paragraph(); p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(ruta, width=Inches(ancho)); _mover(p._p)
    if pie:
        cp = doc.add_paragraph(style="titulo-tabla"); cp.add_run(pie); _mover(cp._p)

_tn = [0]
def tabla(titulo, cabeceras, filas):
    _tn[0] += 1
    tp = doc.add_paragraph(style="titulo-tabla")
    tp.add_run(f"Tabla {_tn[0]}: {titulo}"); _mover(tp._p)
    t = doc.add_table(rows=1, cols=len(cabeceras)); t.style = "Table Grid"
    for i, c in enumerate(cabeceras):
        cell = t.rows[0].cells[i]; cell.text = c
        for p in cell.paragraphs:
            for rr in p.runs:
                rr.bold = True
    for fila in filas:
        celdas = t.add_row().cells
        for i, v in enumerate(fila):
            celdas[i].text = str(v)
    _mover(t._tbl)
    sp = doc.add_paragraph(); _mover(sp._p)

def archivo(nombre):
    ruta = os.path.join(EVID, nombre)
    if os.path.exists(ruta):
        with open(ruta, encoding="utf-8", errors="replace") as f:
            code(f.read().strip())
    else:
        par(f"[FALTA EVIDENCIA: {nombre}]", bold=True)

# ================================================================
#  CONTENIDO
# ================================================================
salto_pagina()

h1("1. Introduccion")
par("Este informe corresponde a la Experiencia 3, Semana 8: la actividad SUMATIVA individual "
    "'Desarrollando microservicios y resiliencia en la nube con Spring Cloud'. Es la etapa final "
    "del ecosistema del Banco XYZ construido en las semanas anteriores (Config Server, Eureka, "
    "API Gateway, tres microservicios de negocio, tolerancia a fallos con Resilience4j y "
    "mensajeria asincrona con Kafka). Esta semana agrega las tres piezas que faltaban para un "
    "entorno Cloud productivo: seguridad real con OAuth2.0, una imagen Docker por cada "
    "microservicio, y un docker-compose.yaml que orquesta el sistema completo con un solo "
    "comando.")
par("El informe esta organizado en el mismo orden que los seis criterios de la pauta "
    "sumativa (100 puntos). Cada afirmacion se respalda con salidas reales de ejecucion "
    "guardadas en la carpeta evidencia/: se construyeron las 7 imagenes Docker, se levanto el "
    "ecosistema completo con docker-compose, se obtuvo un token OAuth2.0 real y se probo contra "
    "endpoints protegidos, se forzo la apertura real de un Circuit Breaker, y se verifico el "
    "flujo de eventos Kafka de punta a punta.")

h1("2. Resultado de aprendizaje abordado")
par("RA3. Implementa arquitecturas de microservicios resilientes y seguras en la nube, "
    "aplicando mecanismos de seguridad con OAuth2.0, conteinerizacion y orquestacion con "
    "Docker, tolerancia a fallos y mensajeria asincrona, utilizando el ecosistema Spring "
    "Cloud.")

h1("3. Arquitectura de la solucion")
par("El sistema se compone de SIETE aplicaciones Spring Boot, cada una con su propio "
    "Dockerfile, orquestadas junto a Kafka y Kafka-UI por un unico docker-compose.yaml:")
tabla("Componentes del ecosistema",
      ["Componente", "Puerto", "Rol"],
      [["config-server", "8888", "Configuracion centralizada (Spring Cloud Config)"],
       ["eureka-server", "8761", "Descubrimiento de servicios (Netflix Eureka)"],
       ["auth-server", "9000", "Authorization Server OAuth2.0 (Spring Authorization Server) - NUEVO"],
       ["api-gateway", "8080", "Puerta de enlace unica (Spring Cloud Gateway)"],
       ["ms-cuentas", "8081", "Resource Server OAuth2 + Resilience4j + consumidor Kafka"],
       ["ms-transacciones", "8082", "Resource Server OAuth2 + Resilience4j + productor Kafka"],
       ["ms-tarjetas", "8083", "Resource Server OAuth2 + Resilience4j + consumidor Kafka"]])
code("Cliente --> API Gateway (8080) --> Resource Servers (cuentas/transacciones/tarjetas)\n"
     "                |                          ^  valida JWT contra el JWK Set\n"
     "                '--> auth-server (9000) ----'  emite JWT (client_credentials)\n"
     "\n"
     "Config Server (8888) . Eureka (8761) . Kafka 9092/9094 (bus de eventos)")
par("Los tres microservicios de negocio se construyen sobre el trabajo de las semanas 6 y 7: "
    "mantienen intactos sus modelos, repositorios, Circuit Breaker y productor/consumidores "
    "Kafka. Lo que cambia esta semana es la capa de seguridad (de JWT propio a OAuth2.0 real) "
    "y que todo el sistema ahora es conteinerizado y se despliega con un solo comando.")

h1("4. Criterio 1: Implementa OAuth2.0 con flujo funcional (20 puntos)")
par("Redaccion de la pauta: 'Implementa OAuth2.0 con flujo funcional que asegura la "
    "proteccion de datos y servicios'.")
par("Se incorpora un modulo nuevo, auth-server, construido con Spring Authorization Server: "
    "emite y firma los access tokens (JWT, algoritmo RS256) con una clave RSA generada al "
    "arrancar, y publica la clave publica en su JWK Set (/oauth2/jwks) para que los demas "
    "servicios puedan validar la firma sin conocer la clave privada.")
code("@Bean\n"
     "public RegisteredClientRepository registeredClientRepository() {\n"
     "    RegisteredClient clienteBanco = RegisteredClient.withId(UUID.randomUUID().toString())\n"
     "        .clientId(\"bancoxyz-client\")\n"
     "        .clientSecret(\"{bcrypt}\" + bcrypt.encode(\"bancoxyz-secret\"))\n"
     "        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)\n"
     "        .scope(\"cuentas.read\").scope(\"cuentas.write\")\n"
     "        .scope(\"transacciones.read\").scope(\"transacciones.write\")\n"
     "        .scope(\"tarjetas.read\").scope(\"tarjetas.write\")\n"
     "        .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(1)).build())\n"
     "        .build();\n"
     "    return new InMemoryRegisteredClientRepository(clienteBanco);\n"
     "}")
par("Se elimino por completo el JWT propio de la semana 7 (JwtUtil, JwtAuthFilter, "
    "AuthController) en los tres microservicios de negocio: ya no gestionan credenciales ni "
    "emiten tokens, solo los VALIDAN. Cada uno paso a ser un OAuth2 Resource Server:")
code("http.csrf(csrf -> csrf.disable())\n"
     "    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n"
     "    .authorizeHttpRequests(auth -> auth\n"
     "        .requestMatchers(\"/actuator/**\", \"/h2-console/**\").permitAll()\n"
     "        .anyRequest().authenticated())\n"
     "    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));")
par("Flujo demostrado: client_credentials (maquina-a-maquina), apropiado porque quien consume "
    "los microservicios son otros servicios de confianza (el BFF, el cajero), no un usuario "
    "final con sesion. Extracto real de evidencia/01_oauth2_flujo.txt:")
archivo("01_oauth2_flujo.txt")
par("El token obtenido es un JWT real firmado RS256 (header alg=RS256), con "
    "iss=http://auth-server:9000 y el scope solicitado. La llamada protegida con el token "
    "devuelve 200 con los datos reales de ms-cuentas; sin token o con un token invalido, el "
    "gateway (que reenvia la cabecera Authorization sin terminarla) deja que el propio "
    "Resource Server la rechace con 401.")

h1("5. Criterio 2: Crea imagenes Docker funcionales para todos los microservicios (20 puntos)")
par("Redaccion de la pauta: 'Crea imagenes Docker funcionales para todos los microservicios, "
    "asegurando portabilidad y despliegue eficiente'.")
par("Los SIETE modulos (incluido el nuevo auth-server) tienen su propio Dockerfile "
    "multi-stage: una etapa compila con Maven, y la imagen final solo contiene el JRE y el "
    "JAR ejecutable, sin el toolchain de compilacion.")
code("# ---- Etapa 1: compilacion con Maven ----\n"
     "FROM maven:3.9-eclipse-temurin-17 AS build\n"
     "WORKDIR /app\n"
     "COPY pom.xml .\n"
     "COPY src ./src\n"
     "RUN mvn -B clean package -DskipTests\n"
     "\n"
     "# ---- Etapa 2: imagen de ejecucion ----\n"
     "FROM eclipse-temurin:17-jre\n"
     "WORKDIR /app\n"
     "COPY --from=build /app/target/*.jar app.jar\n"
     "EXPOSE 8081\n"
     "ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]")
par("Cada modulo expone su propio puerto (8888, 8761, 9000, 8080, 8081, 8082, 8083) en el "
    "Dockerfile, coherente con lo configurado en su application.yml. Las 7 imagenes construidas "
    "y disponibles localmente (extracto de evidencia/04_docker_y_eureka.txt):")
archivo("04_docker_y_eureka.txt")

h1("6. Criterio 3: Configura docker-compose.yaml orquestando todo (20 puntos)")
par("Redaccion de la pauta: 'Configura el archivo docker-compose.yaml correctamente, "
    "orquestando todos los componentes necesarios de manera funcional'.")
par("Un unico docker-compose.yml levanta los OCHO contenedores (7 aplicaciones Spring Boot + "
    "Kafka; mas Kafka-UI como noveno, de apoyo) en una red bridge comun (bancoxyz-net), "
    "resolviendose entre si por nombre de servicio.")
tabla("Mecanismos de orquestacion usados",
      ["Mecanismo", "Para que"],
      [["depends_on con condition: service_healthy", "config-server, eureka-server y kafka "
        "deben estar sanos antes de que arranquen los servicios que dependen de ellos"],
       ["healthcheck por contenedor", "config-server/eureka-server: TCP al puerto propio. "
        "kafka: kafka-topics.sh --list"],
       ["Perfil Spring 'docker' (SPRING_PROFILES_ACTIVE)", "cada microservicio activa un "
        "documento YAML que apunta a los demas por nombre de contenedor en vez de localhost"],
       ["spring.cloud.config.retry (fail-fast + backoff)", "si el Config Server aun no "
        "responde al primer intento, el microservicio reintenta en vez de morir"],
       ["Imagenes bancoxyz/<modulo>:1.0.0, build: ./<modulo>", "cada servicio construye su "
        "propia imagen desde su Dockerfile al ejecutar docker compose up --build"]])
code("ms-cuentas:\n"
     "  build: ./ms-cuentas\n"
     "  image: bancoxyz/ms-cuentas:1.0.0\n"
     "  environment:\n"
     "    SPRING_PROFILES_ACTIVE: \"docker\"\n"
     "  depends_on:\n"
     "    config-server: { condition: service_healthy }\n"
     "    eureka-server: { condition: service_healthy }\n"
     "    kafka:         { condition: service_healthy }\n"
     "    auth-server:   { condition: service_started }")
par("Verificado con 'docker compose up --build' real: las 7 imagenes se construyen sin "
    "errores y los 8 contenedores (mas Kafka-UI) arrancan en el orden correcto, con los 5 "
    "servicios Spring (auth-server, api-gateway y los 3 microservicios) registrandose y "
    "quedando UP en Eureka (extracto de evidencia/04_docker_y_eureka.txt, seccion 2 y 3).")
imagen("05_eureka_dashboard.png", ancho=6.5,
       pie="Dashboard de Eureka: los 5 servicios Spring del ecosistema, todos UP")

h1("7. Criterio 4: Configura tolerancia a fallos con Resilience4j (20 puntos)")
par("Redaccion de la pauta: 'Configura mecanismos de tolerancia a fallos con Resilience4j "
    "correctamente en los microservicios, garantizando resiliencia ante fallos'.")
par("Se conserva integro el Circuit Breaker de las semanas 6 y 7 en los tres microservicios "
    "(cuentasCB, transaccionesCB, tarjetasCB), ahora probado con el sistema corriendo "
    "completamente en contenedores Docker y detras del API Gateway con autenticacion OAuth2 "
    "real (no solo en local). Se inyecto una falla real en CuentaService.listar() (revertida "
    "antes de esta entrega) y se repitio la peticion a traves del gateway, con token OAuth2 "
    "valido, 10 veces:")
archivo("03_resilience4j.txt")
par("El ciclo completo CLOSED -> OPEN -> CLOSED quedo demostrado con fallas reales, no solo "
    "con la configuracion: el cliente recibio siempre una respuesta controlada (200 con lista "
    "vacia via fallback), nunca un error 500 sin control; la decimo primera llamada ni "
    "siquiera llego al metodo real (notPermittedCalls); y al revertir la falla y reiniciar el "
    "contenedor, el circuito volvio a CLOSED y el servicio respondio con los datos reales.")

h1("8. Criterio 5: Integra mensajeria asincrona con Kafka (15 puntos)")
par("Redaccion de la pauta: 'Integra mensajeria asincrona con Kafka o JMS completamente "
    "funcional, permitiendo comunicacion eficiente y escalable entre microservicios'.")
par("Se conserva integro el patron Publish/Subscribe sobre Apache Kafka de la semana 7: "
    "ms-transacciones publica un evento TransaccionCreadaEvent por cada transaccion "
    "registrada; ms-cuentas y ms-tarjetas lo consumen de forma independiente, cada uno con su "
    "propio groupId. Ahora Kafka tambien esta dentro del docker-compose (imagen oficial "
    "apache/kafka, modo KRaft), con un listener interno (kafka:9092, para los contenedores) y "
    "uno externo (localhost:9094, para pruebas desde el host).")
par("Flujo real, a traves del API Gateway y con autenticacion OAuth2.0, probando dos "
    "transacciones consecutivas sobre la misma cuenta (extracto de "
    "evidencia/02_kafka_flujo.txt):")
archivo("02_kafka_flujo.txt")
par("El saldo de la cuenta 101 avanzo 5000 -> 5500 -> 5200, exactamente como predicen un "
    "credito de 500 y un debito de 300; ambos consumidores (cuentas y tarjetas) procesaron "
    "cada evento de forma independiente.")
imagen("06_kafkaui_topico.png", ancho=6.5,
       pie="Kafka-UI: topico transacciones, pestana Messages, con los 2 eventos JSON reales "
           "de la prueba anterior (monto 500.0 y 300.0 sobre la cuenta 101)")

h1("9. Problemas encontrados durante la verificacion")
par("Al ejecutar el sistema completo con Docker (no solo revisar el codigo) aparecieron dos "
    "defectos reales, no visibles en una revision estatica, ambos corregidos:")
tabla("Defectos detectados y su correccion",
      ["Sintoma observado", "Causa", "Correccion"],
      [["Los tres microservicios se quedaban colgados al arrancar en Docker, sin imprimir "
        "ni siquiera el banner de Spring Boot",
        "'spring.config.import' NO se reemplaza entre documentos de distinto perfil en el "
        "mismo application.yml: Spring Boot ACUMULA todas las ubicaciones activas. El import "
        "por defecto a 'localhost:8888' (valido solo fuera de Docker) seguia intentandose "
        "ADEMAS del de 'config-server:8888' (valido en Docker). Con fail-fast activo, cada "
        "fallo a localhost reiniciaba el ciclo de reintentos indefinidamente -y la causa "
        "quedaba oculta porque este fallo ocurre ANTES de que el sistema de logging este "
        "listo, en la fase de bootstrap de Spring Cloud Config-",
        "Se condiciono el import por defecto a 'on-profile: \"!docker\"' (se excluye "
        "explicitamente cuando el perfil es docker) y el import de Docker a "
        "'on-profile: docker', dejando un UNICO import activo segun el perfil. "
        "'spring.application.name' se dejo en un bloque sin condicion de perfil, para no "
        "perderlo al excluir el resto del documento"],
       ["El productor publicaba el evento pero ningun consumidor actualizaba el saldo",
        "El JsonSerializer de Kafka agrega por defecto un header __TypeId__ con el nombre de "
        "la clase del PRODUCTOR (com.bancoxyz.mstransacciones.event.TransaccionCreadaEvent); "
        "el consumidor, que tiene su PROPIA copia de esa clase en otro paquete, intenta cargar "
        "la clase del productor y falla (\"not in the trusted packages\")",
        "Productor con spring.json.add.type.headers: false (no envia el header); "
        "consumidores con spring.json.use.type.headers: false (ignoran cualquier header de "
        "tipo y deserializan siempre en su propia clase, segun spring.json.value.default.type)"]])
par("El primer defecto es particularmente dificil de diagnosticar sin ejecutar el sistema "
    "real: el sintoma (cero lineas de log, el contenedor parece \"colgado\") no tiene relacion "
    "aparente con la causa (una propiedad de Spring Boot que se acumula en vez de "
    "sobrescribirse). Se diagnostico forzando un fallo rapido "
    "(SPRING_CLOUD_CONFIG_RETRY_MAX_ATTEMPTS=1) para que la excepcion real saliera impresa al "
    "morir el proceso, revelando que efectivamente se intentaban DOS URLs de Config Server en "
    "cada arranque.")

h1("10. Criterio 6: Entrega de los aspectos clave del caso (5 puntos)")
par("Redaccion de la pauta: 'Entrega los 3 aspectos claves solicitados en el caso, para su "
    "correcta funcionalidad'. Los tres aspectos entregados:")
bullet("Codigo fuente: los 7 modulos Maven completos (config-server, eureka-server, "
       "auth-server, api-gateway, ms-cuentas, ms-transacciones, ms-tarjetas), cada uno "
       "compilable y empaquetable en una imagen Docker propia, mas el docker-compose.yaml que "
       "orquesta el conjunto.")
bullet("Documentacion: este informe, el README.md del proyecto (objetivo, arquitectura, como "
       "ejecutar, como probar cada criterio) y PROPUESTA_TECNICA.md (decisiones de diseno y "
       "su justificacion).")
bullet("Evidencia de ejecucion: salidas reales de 'mvn clean test' en los 7 modulos, del "
       "flujo OAuth2.0 completo, del flujo Kafka de punta a punta, del ciclo completo del "
       "Circuit Breaker con una falla real, y de las imagenes Docker + registro Eureka con el "
       "sistema corriendo en contenedores.")
h2("10.1 Pruebas automatizadas")
par("Se ejecuto 'mvn clean test' en los 7 modulos: cada uno pasa su prueba de carga de "
    "contexto Spring (@SpringBootTest, contextLoads). Extracto de "
    "evidencia/00_compilacion_tests.txt:")
archivo("00_compilacion_tests.txt")

h1("11. Instrucciones de ejecucion")
par("Requisito: Docker y Docker Compose. Se requieren al menos 6 GB de RAM para el motor de "
    "Docker (8 servicios Java + Kafka).")
code("# Desde la carpeta Exp3_S8_Felipe_Penaloza/\n"
     "docker compose up --build\n\n"
     "# Probar OAuth2.0: obtener un token y llamar un endpoint protegido\n"
     "curl -X POST http://localhost:9000/oauth2/token \\\n"
     "  -u \"bancoxyz-client:bancoxyz-secret\" \\\n"
     "  -d \"grant_type=client_credentials\" -d \"scope=cuentas.read\"\n\n"
     "curl http://localhost:8080/cuentas -H \"Authorization: Bearer <TOKEN>\"\n"
     "curl -i http://localhost:8080/cuentas   # sin token -> 401\n\n"
     "# Probar Kafka: registrar una transaccion (publica un evento)\n"
     "curl -X POST http://localhost:8080/transacciones \\\n"
     "  -H \"Authorization: Bearer <TOKEN>\" -H \"Content-Type: application/json\" \\\n"
     "  -d '{\"cuentaId\":101,\"monto\":500,\"tipo\":\"credito\"}'")
par("Puntos de acceso: Eureka en http://localhost:8761, API Gateway en "
    "http://localhost:8080, Auth Server en http://localhost:9000, Kafka-UI en "
    "http://localhost:8090. El detalle completo esta en el README.md.")

h1("12. Conclusion")
bullet("Criterio 1 (20 pts): OAuth2.0 real con Spring Authorization Server, flujo "
       "client_credentials demostrado de punta a punta -token, llamada protegida 200, sin "
       "token 401, token invalido 401- reemplazando por completo el JWT propio de la semana 7.")
bullet("Criterio 2 (20 pts): las 7 imagenes Docker multi-stage se construyen sin errores, "
       "livianas (solo JRE en la etapa final) y portables.")
bullet("Criterio 3 (20 pts): un unico docker-compose.yaml orquesta los 8 contenedores "
       "(mas Kafka-UI) con healthchecks, depends_on y perfiles, verificado con "
       "'docker compose up --build' real y los 5 servicios Spring quedando UP en Eureka.")
bullet("Criterio 4 (20 pts): Resilience4j probado con una falla real forzada a traves del "
       "gateway con autenticacion OAuth2.0, demostrando el ciclo completo CLOSED -> OPEN -> "
       "CLOSED.")
bullet("Criterio 5 (15 pts): Kafka funcional de punta a punta dentro del ecosistema "
       "conteinerizado, con el saldo de una cuenta avanzando correctamente a traves de dos "
       "transacciones consecutivas.")
bullet("Criterio 6 (5 pts): codigo fuente, documentacion y evidencia de ejecucion entregados.")
par("La verificacion real del sistema (no solo revision de codigo) permitio encontrar y "
    "corregir dos defectos que una revision estatica no habria detectado: la acumulacion de "
    "'spring.config.import' entre perfiles, y el header __TypeId__ de Kafka heredado de una "
    "version anterior de la configuracion. Ambos quedan documentados en la seccion 9 junto "
    "con su correccion.")

h1("13. Referencias")
bullet("Spring. Spring Authorization Server Reference. "
       "https://docs.spring.io/spring-authorization-server/reference/")
bullet("Spring. OAuth2 Resource Server. "
       "https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html")
bullet("Docker. Multi-stage builds. "
       "https://docs.docker.com/build/building/multi-stage/")
bullet("Docker. Compose file reference. "
       "https://docs.docker.com/compose/compose-file/")
bullet("Resilience4j. CircuitBreaker. "
       "https://resilience4j.readme.io/docs/circuitbreaker")
bullet("Spring. Spring for Apache Kafka Reference. "
       "https://docs.spring.io/spring-kafka/reference/")
bullet("Villagran, K. (s.f.). bank_legacy_data. "
       "https://github.com/KariVillagran/bank_legacy_data")

doc.save(SALIDA)
print("Informe generado en:", SALIDA)
print("Recordar: al abrir en Word, actualizar el Indice (clic derecho -> Actualizar campo -> "
      "Actualizar toda la tabla).")

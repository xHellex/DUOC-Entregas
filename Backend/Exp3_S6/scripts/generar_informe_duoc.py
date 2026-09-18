# -*- coding: utf-8 -*-
"""
Genera el informe de la Experiencia 3 - Semana 6 (microservicios y seguridad
en la nube con Spring Cloud, actividad FORMATIVA grupal) sobre la plantilla
oficial DUOC (PBY2202_EFT_Plantilla_Informe_PDF.docx), reutilizando sus
estilos y dejando portada, indice/TOC y aviso legal intactos.

Uso:  python scripts/generar_informe_duoc.py
Requiere la plantilla en el nivel superior de la carpeta S6:
  ../PBY2202_EFT_Plantilla_Informe_PDF.docx
Salida: Informe_Exp3_S6_Grupo3.docx (en la raiz del proyecto).
"""
import os
from docx import Document
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml.ns import qn

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
EVID = os.path.join(RAIZ, "evidencia")
PLANTILLA = os.path.join(os.path.dirname(RAIZ), "PBY2202_EFT_Plantilla_Informe_PDF.docx")
SALIDA = os.path.join(RAIZ, "Informe_Exp3_S6_Grupo3.docx")

INTEGRANTES = "Felipe Peñaloza"   # Grupo 3 (integrante unico)

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
set_text(paras[17], "Implementando microservicios y seguridad en la nube con Spring Cloud")

p18 = paras[18]
p18.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p18.add_run("Ecosistema Spring Cloud para el Banco XYZ")
r.font.size = Pt(14)

p19 = paras[19]
p19.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p19.add_run("Experiencia 3 - Semana 6 (actividad formativa grupal)\n"
                f"Grupo 3 - {INTEGRANTES}\n"
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

# ================================================================
#  CONTENIDO
# ================================================================
salto_pagina()

h1("1. Introduccion")
par("Este informe corresponde a la Experiencia 3, Semana 6: la actividad FORMATIVA grupal "
    "'Implementando microservicios y seguridad en la nube con Spring Cloud'. El proyecto "
    "migra la solucion del Banco XYZ -ya expuesta como Backend for Frontend en la Experiencia "
    "2- a una arquitectura de microservicios en la nube: configuracion centralizada, "
    "descubrimiento de servicios, un gateway como puerta de entrada unica, tolerancia a fallos "
    "y seguridad con JWT.")
par("Un punto relevante para la lectura de este informe: existe una diferencia entre lo que "
    "piden las instrucciones especificas y lo que evalua la pauta formativa. Las instrucciones "
    "piden, de forma literal, 'un config server consumido por 1 microservicio', 'registrar al "
    "menos 1 microservicio' e 'implementar 1 microservicio con tolerancia a fallos y sistema de "
    "autenticacion'. La pauta, en cambio, exige explicitamente 'registrar correctamente TRES "
    "microservicios' (criterio 2) e 'implementar 3 microservicios con tolerancia a fallos y "
    "sistema de autenticacion' (criterio 3) para el nivel Completamente Logrado. Esta entrega "
    "opta por la arquitectura completa -Config Server, Eureka, API Gateway y TRES "
    "microservicios (cuentas, transacciones, tarjetas)- para apuntar al 100% de la pauta, que "
    "es el instrumento con el que se calificara el trabajo.", bold=False)

h1("2. Resultado de aprendizaje abordado")
par("RA3. Implementa arquitecturas de microservicios resilientes y seguras en la nube, "
    "aplicando patrones de configuracion centralizada, descubrimiento de servicios, tolerancia "
    "a fallos y autenticacion, utilizando el ecosistema Spring Cloud.")

h1("3. Arquitectura de la solucion")
par("El sistema se compone de SEIS aplicaciones Spring Boot independientes, cada una con su "
    "propio proceso, puerto y pom.xml:")
tabla("Componentes del ecosistema",
      ["Componente", "Puerto", "Rol"],
      [["config-server", "8888", "Configuracion centralizada (Spring Cloud Config)"],
       ["eureka-server", "8761", "Descubrimiento de servicios (Netflix Eureka)"],
       ["api-gateway", "8080", "Puerta de enlace unica (Spring Cloud Gateway)"],
       ["ms-cuentas", "8081", "Microservicio de cuentas + Resilience4j + JWT"],
       ["ms-transacciones", "8082", "Microservicio de transacciones + Resilience4j + JWT"],
       ["ms-tarjetas", "8083", "Microservicio de tarjetas + Resilience4j + JWT"]])
code("Cliente ---> API Gateway (8080) ---> [ms-cuentas | ms-transacciones | ms-tarjetas]\n"
     "                                            |\n"
     "                todos se registran en ---> Eureka (8761)\n"
     "                todos toman su config de ---> Config Server (8888)")
par("Los datos de cada microservicio provienen de la migracion trabajada en la Experiencia 1 "
    "(repositorio bank_legacy_data del Banco XYZ): cuentas, transacciones y tarjetas, cargadas "
    "por un DataInitializer al arrancar cada servicio sobre su propia base H2.")

h1("4. Criterio 1: Config Server centralizado e integrado (nivel CL)")
par("Redaccion de la pauta: 'Configura un servidor funcional, centralizado y correctamente "
    "integrado con al menos un microservicio'. Este criterio no exige una cantidad especifica "
    "-coincide con las instrucciones-, pero en esta entrega el Config Server esta integrado "
    "con los TRES microservicios, no solo con uno.")
par("El Config Server corre en modo 'native' y sirve los archivos de la carpeta config-repo/ "
    "(un nivel arriba de config-server/, que es su directorio de trabajo al arrancar con "
    "'mvn spring-boot:run'):")
code("spring.cloud.config.server.native.search-locations: file:../config-repo")
par("Cada microservicio importa su configuracion al arrancar con una unica linea en su "
    "application.yml local:")
code("spring.config.import: optional:configserver:http://localhost:8888")
par("Verificado en la evidencia (seccion 9): una peticion directa a "
    "http://localhost:8888/ms-cuentas/default devuelve el archivo completo de propiedades "
    "(puerto, datasource, Eureka, Resilience4j, JWT) que ms-cuentas realmente usa al arrancar.")

h1("5. Criterio 2: Service Discovery con los tres microservicios (nivel CL)")
par("Redaccion de la pauta: 'Habilita un Service Discovery y registra correctamente TRES "
    "microservicios, demostrando integracion eficiente'.")
par("Eureka Server corre en el puerto 8761 con auto-registro deshabilitado "
    "(register-with-eureka: false, fetch-registry: false, ya que el propio servidor no es un "
    "cliente). Los tres microservicios y el API Gateway se registran al arrancar mediante "
    "@EnableDiscoveryClient y el cliente de Eureka, usando su nombre logico "
    "(spring.application.name) en lugar de una URL fija.")
par("Verificado en la evidencia: consultando http://localhost:8761/eureka/apps con los seis "
    "procesos corriendo, aparecen los CUATRO servicios que se registran -API-GATEWAY, "
    "MS-CUENTAS, MS-TRANSACCIONES y MS-TARJETAS- todos en estado UP.", bold=True)

h1("6. Criterio 3: Tolerancia a fallos y autenticacion en los tres microservicios (nivel CL)")
par("Redaccion de la pauta: 'Implementa 3 microservicios con tolerancia a fallos y sistema de "
    "autenticacion'.")
h2("6.1 Tolerancia a fallos (Resilience4j)")
par("Cada uno de los tres microservicios protege su endpoint principal con un Circuit Breaker "
    "de Resilience4j (spring-cloud-starter-circuitbreaker-resilience4j), con un nombre propio "
    "por servicio y un metodo de fallback que evita propagar el error:")
code("@GetMapping\n"
     "@CircuitBreaker(name = \"cuentasCB\", fallbackMethod = \"listarFallback\")\n"
     "public List<Cuenta> listar() { return service.listar(); }\n\n"
     "public List<Cuenta> listarFallback(Throwable t) {\n"
     "    log.warn(\"[FALLBACK] listar cuentas: {}\", t.getMessage());\n"
     "    return List.of();\n"
     "}")
tabla("Circuit Breaker por microservicio",
      ["Microservicio", "Nombre del CircuitBreaker", "Configuracion (config-repo)"],
      [["ms-cuentas", "cuentasCB", "sliding-window=10, failure-rate=50%, wait=10s"],
       ["ms-transacciones", "transaccionesCB", "sliding-window=10, failure-rate=50%, wait=10s"],
       ["ms-tarjetas", "tarjetasCB", "sliding-window=10, failure-rate=50%, wait=10s"]])
par("Verificado en la evidencia: con los tres microservicios corriendo, "
    "/actuator/circuitbreakers en cada puerto (8081/8082/8083) muestra su propio circuito "
    "registrado y en estado CLOSED (saludable), con el nombre exacto que usa su controller.")
par("No basta con que el CircuitBreaker este configurado: se demostro que realmente abre y "
    "protege al sistema. Se inyecto una falla deliberada en CuentaService.listar() (lanza una "
    "excepcion en cada llamada) y se repitio la peticion 10 veces (el tamano de la ventana "
    "deslizante configurado):", bold=False)
tabla("Ciclo completo del CircuitBreaker demostrado con fallas reales",
      ["Paso", "Accion", "Resultado observado"],
      [["1", "10 llamadas a /cuentas, todas fallan",
        "Cliente recibe 200 [] en cada una (fallback), nunca un 500 sin control"],
       ["2", "Estado del circuito tras las 10 fallas",
        "failureRate=100%, failedCalls=10, state=OPEN"],
       ["3", "11a llamada con el circuito abierto",
        "notPermittedCalls=1; log: 'CircuitBreaker cuentasCB is OPEN and does not permit "
        "further calls' -> ni siquiera llega al metodo real"],
       ["4", "Se revierte la falla y se reinicia el servicio",
        "state=CLOSED de nuevo; /cuentas vuelve a devolver los datos reales"]])
par("El detalle completo (cada llamada, cada transicion de estado, el log de fallback) queda "
    "en evidencia/02_tolerancia_a_fallos.txt. El mismo mecanismo (mismo tipo de CircuitBreaker, "
    "misma configuracion) esta activo en ms-transacciones y ms-tarjetas.")
h2("6.2 Autenticacion (JWT)")
par("Los TRES microservicios implementan su propio sistema de autenticacion (no solo uno): "
    "cada uno expone /auth/login (JwtUtil + AuthController) y valida el token Bearer en sus "
    "endpoints protegidos mediante un JwtAuthFilter que se registra antes del filtro estandar "
    "de Spring Security, en una configuracion completamente stateless:")
code("http.csrf(csrf -> csrf.disable())\n"
     "    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))\n"
     "    .authorizeHttpRequests(auth -> auth\n"
     "        .requestMatchers(\"/auth/**\", \"/actuator/**\").permitAll()\n"
     "        .anyRequest().authenticated())\n"
     "    .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);")
par("Los tres microservicios comparten el mismo jwt.secret, servido por el Config Server desde "
    "config-repo/: un token emitido por cualquiera de los tres es valido en los otros dos, sin "
    "que ninguno dependa de que otro este arriba para autenticar. El API Gateway enruta "
    "/auth/** hacia ms-cuentas unicamente por comodidad, para ofrecer un unico punto de login "
    "al cliente externo.")

h1("7. Criterio 4: Sistema de autenticacion y autorizacion funcional (nivel CL)")
par("Redaccion de la pauta: 'Implementa un sistema de autenticacion y autorizacion funcional, "
    "asegurando seguridad'.")
tabla("Comportamiento de seguridad verificado",
      ["Escenario", "Peticion", "Resultado"],
      [["Login valido", "POST /auth/login (admin/admin123) via gateway",
        "200, devuelve token JWT firmado HMAC-SHA384"],
       ["Consulta con token valido", "GET /cuentas, /transacciones, /tarjetas via gateway",
        "200, datos reales de cada microservicio"],
       ["Sin token", "GET /cuentas via gateway, sin header Authorization",
        "403 Forbidden"],
       ["Token invalido", "GET /cuentas via gateway, Authorization: Bearer token-invalido-123",
        "403 Forbidden (el filtro descarta la autenticacion al fallar la validacion)"]])
par("El API Gateway enruta cada grupo de rutas al microservicio correcto usando Eureka como "
    "mecanismo de descubrimiento (uri: lb://ms-cuentas, no una URL fija), balanceando por "
    "nombre logico del servicio:")
code("routes:\n"
     "  - id: ms-cuentas\n"
     "    uri: lb://ms-cuentas\n"
     "    predicates: [Path=/cuentas/**, /auth/**]\n"
     "  - id: ms-transacciones\n"
     "    uri: lb://ms-transacciones\n"
     "    predicates: [Path=/transacciones/**]\n"
     "  - id: ms-tarjetas\n"
     "    uri: lb://ms-tarjetas\n"
     "    predicates: [Path=/tarjetas/**]")

h1("8. Evidencia de ejecucion")
h2("8.1 Compilacion y pruebas automatizadas de los 6 modulos")
par("Cada uno de los seis modulos incluye una prueba de carga de contexto Spring "
    "(@SpringBootTest, contextLoads). En los tres microservicios la prueba desactiva el "
    "cliente de Eureka y la importacion del Config Server (eureka.client.enabled=false, "
    "spring.cloud.config.enabled=false) para poder validar el modulo de forma aislada, sin "
    "depender de que los otros procesos esten arriba. El WARN de 'Cannot execute request on "
    "any known server' que aparece al testear api-gateway solo es un intento de registro en "
    "Eureka durante el test; es esperado y no hace fallar la prueba, ya que Eureka no esta "
    "corriendo en ese momento.")
comp = os.path.join(EVID, "00_compilacion.txt")
if os.path.exists(comp):
    with open(comp, encoding="utf-8", errors="replace") as f:
        code(f.read().strip())
h2("8.2 Ecosistema completo corriendo (salida real)")
par("Los 6 procesos se levantaron simultaneamente en el orden documentado en el README "
    "(Config Server, Eureka, los tres microservicios y, al final, el Gateway) y se ejecutaron "
    "las siguientes pruebas contra el sistema real:")
pruebas = os.path.join(EVID, "01_pruebas_ecosistema.txt")
if os.path.exists(pruebas):
    with open(pruebas, encoding="utf-8", errors="replace") as f:
        code(f.read().strip())

h2("8.3 Tolerancia a fallos demostrada con una falla real inyectada")
par("Complementa la seccion 6.1: aqui esta el detalle completo del ciclo CLOSED -> fallback "
    "por cada llamada fallida -> OPEN -> recuperacion a CLOSED, provocado con una excepcion "
    "real en CuentaService (revertida antes de esta entrega).")
fallos = os.path.join(EVID, "02_tolerancia_a_fallos.txt")
if os.path.exists(fallos):
    with open(fallos, encoding="utf-8", errors="replace") as f:
        code(f.read().strip())

h1("9. Instrucciones de ejecucion")
par("Se requieren 6 terminales. Orden de arranque (importante: cada uno depende del anterior "
    "para funcionar bien, salvo Eureka que puede iniciar en paralelo al Config Server):")
code("cd config-server      && mvn spring-boot:run   # 1. primero\n"
     "cd eureka-server      && mvn spring-boot:run   # 2.\n"
     "cd ms-cuentas         && mvn spring-boot:run   # 3. (orden libre entre los 3 ms)\n"
     "cd ms-transacciones   && mvn spring-boot:run\n"
     "cd ms-tarjetas        && mvn spring-boot:run\n"
     "cd api-gateway        && mvn spring-boot:run   # 4. al final")
par("Verificar en http://localhost:8761 que los cuatro servicios (gateway + 3 ms) aparezcan "
    "registrados antes de probar el sistema.")
code("curl -X POST http://localhost:8080/auth/login \\\n"
     "  -H \"Content-Type: application/json\" -d '{\"usuario\":\"admin\",\"clave\":\"admin123\"}'\n\n"
     "curl http://localhost:8080/cuentas -H \"Authorization: Bearer <TOKEN>\"\n"
     "curl http://localhost:8080/transacciones -H \"Authorization: Bearer <TOKEN>\"\n"
     "curl http://localhost:8080/tarjetas -H \"Authorization: Bearer <TOKEN>\"\n\n"
     "curl -s -o /dev/null -w \"%{http_code}\" http://localhost:8080/cuentas  # -> 403, sin token")

h1("10. Conclusion")
par("La entrega cubre los cuatro criterios de la pauta formativa, apuntando en los cuatro al "
    "nivel Completamente Logrado:")
bullet("Config Server centralizado e integrado con los tres microservicios, verificado "
       "sirviendo la configuracion real de ms-cuentas (seccion 4).")
bullet("Service Discovery con los TRES microservicios registrados y UP en Eureka, junto al "
       "gateway (seccion 5).")
bullet("Los TRES microservicios con Circuit Breaker propio (Resilience4j, verificado en "
       "/actuator/circuitbreakers) y su propio sistema de autenticacion JWT, no solo uno "
       "(seccion 6).")
bullet("Autenticacion y autorizacion funcional de punta a punta a traves del gateway: login, "
       "acceso autorizado a los tres microservicios, y rechazo sin token o con token invalido "
       "(seccion 7).")
par("Se opto deliberadamente por la arquitectura completa -tres microservicios y el "
    "API Gateway- en lugar del minimo de 'un microservicio' que piden las instrucciones "
    "especificas, porque la pauta con la que se califica exige tres para el maximo logro en "
    "los criterios 2 y 3 (seccion 1).")

h1("11. Referencias")
bullet("Spring. (s.f.). Spring Cloud Config. "
       "https://docs.spring.io/spring-cloud-config/docs/current/reference/html/")
bullet("Spring. (s.f.). Spring Cloud Netflix (Eureka). "
       "https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/")
bullet("Spring. (s.f.). Spring Cloud Gateway. "
       "https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/")
bullet("Resilience4j. (s.f.). Circuit Breaker. "
       "https://resilience4j.readme.io/docs/circuitbreaker")
bullet("jjwt. (s.f.). Java JWT: JSON Web Token for Java and Android. "
       "https://github.com/jwtk/jjwt")
bullet("Villagran, K. (s.f.). bank_legacy_data. https://github.com/KariVillagran/bank_legacy_data")

doc.save(SALIDA)
print("Informe generado en:", SALIDA)
print("Recordar: al abrir en Word, actualizar el Indice (clic derecho -> Actualizar campo -> "
      "Actualizar toda la tabla).")

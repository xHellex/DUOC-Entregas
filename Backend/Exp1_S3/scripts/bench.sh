#!/usr/bin/env bash
# Corre el proyecto con distintos app.grid.size sobre volumen_xl y extrae las
# duraciones de cada job. La app levanta un servidor web que no termina solo,
# asi que se lanza en segundo plano y se mata cuando el 3er job completa.
set -u
cd "$(dirname "$0")/.."
OUT=bench-results.txt
: > "$OUT"

run_once() {
  local g=$1 rep=$2 log="run_g${1}_r${2}.log"
  : > "$log"
  timeout 360 mvn -q -o spring-boot:run \
    -Dspring-boot.run.arguments="--app.data.path=data/volumen_xl --app.grid.size=$g" \
    > "$log" 2>&1 &
  local mvnpid=$!
  local waited=0
  while kill -0 "$mvnpid" 2>/dev/null; do
    if grep -qa "estadosCuentaAnualesJob' completado\|estadosCuentaAnualesJob' finalizo\|APPLICATION FAILED" "$log"; then
      sleep 2; break
    fi
    sleep 3; waited=$((waited+3))
    [ "$waited" -gt 340 ] && break
  done
  taskkill //F //IM java.exe >/dev/null 2>&1
  wait "$mvnpid" 2>/dev/null
  {
    echo "=== grid.size=$g rep=$rep ==="
    grep -aE "Job '.*' (completado|finalizo)|duracion aprox:" "$log"
    grep -qa "APPLICATION FAILED" "$log" && echo "  !!! APPLICATION FAILED"
    echo
  } | tee -a "$OUT"
}

for g in 1 2 3 4; do
  for rep in 1 2; do
    run_once "$g" "$rep"
  done
done
echo "LISTO" | tee -a "$OUT"

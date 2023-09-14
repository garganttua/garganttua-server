#!/usr/bin/env bash

SCRIPT_PATH="${BASH_SOURCE}"
while [ -L "${SCRIPT_PATH}" ]; do
  TARGET="$(readlink "${SCRIPT_PATH}")"
  if [[ "${TARGET}" == /* ]]; then
    SCRIPT_PATH="$TARGET"
  else
    SCRIPT_PATH="$(dirname "${SCRIPT_PATH}")/${TARGET}"
  fi
done
BASH_DIR="$(dirname -- "$(readlink -f "${BASH_SOURCE}")")"
echo "BASH_SOURCE=${BASH_SOURCE}"
echo "SCRIPT_PATH=${SCRIPT_PATH}"
echo "BASH_DIR=${BASH_DIR}"

cd ${BASH_DIR}
cd ..

java -jar bin/garganttua-server-module-bootstrap-${project.version}.jar -manifest conf/garganttua-server-manifest.ggm -deploy deploy/ -tmp tmp/ -lib libs/ -confExt properties,ggc,yml -banner garganttua-server-banner.txt

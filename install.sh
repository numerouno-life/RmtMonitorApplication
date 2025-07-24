#!/bin/bash
# Установка локального jar-файла в локальный репозиторий Maven

mvn install:install-file \
  -Dfile=lib/jlibmodbus-1.2.9.5.jar \
  -DgroupId=com.intelligt.modbus \
  -DartifactId=jlibmodbus \
  -Dversion=1.2.9.5 \
  -Dpackaging=jar

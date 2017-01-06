#!/bin/bash
if [ "$1" != "run" ];
then
  ./setup.sh
fi
if [ $? -eq 0 ];
then
  cp target/glowstone-2017.0.1.1-SNAPSHOT.jar target/glowstone.jar
  (cd target && ../start.sh)
else
  echo "Glowstone did not compile."
fi

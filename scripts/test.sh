#!/bin/bash
if [ "$1" != "run" ];
then
  ./build.sh
fi
if [ $? -eq 0 ];
then
  (cd target && ../scripts/start.sh)
else
  echo "Glowstone did not build successfully."
fi

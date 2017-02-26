#!/bin/bash
if [ "$1" != "run" ];
then
  ./setup.sh
fi
if [ $? -eq 0 ];
then
  (cd target && ../start.sh)
else
  echo "Glowstone did not compile."
fi

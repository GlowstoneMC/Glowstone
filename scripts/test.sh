#!/bin/bash
# Run script within the directory
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

# Build or just do a straight run?
if [ "$1" != "run" ];
then
  # Build
  ./build.sh
fi
# Check if Maven built successfully
# or if we didn't build, just run
if [ $? -eq 0 ] || [ "$1" == "run" ];
then
  # Run Glowstone
  (cd ../target && ../scripts/start.sh)
else
  echo "Glowstone did not build successfully."
fi

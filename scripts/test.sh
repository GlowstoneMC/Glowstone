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
if [ "$1" != "run" ] && [ $? -eq 0 ];
then
  # Run Glowstone
  (cd ../target && ../scripts/start.sh)
else
  echo "Glowstone did not build successfully."
fi

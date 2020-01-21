#!/bin/sh
# Run script within the directory
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

# Build Glowstone
(cd .. && GRADLE_OPTS="-Xss4m" gradlew build)

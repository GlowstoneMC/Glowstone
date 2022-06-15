#!/bin/sh
# Run script within the directory
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

# Build Glowstone
(cd .. && ./gradlew -Dorg.gradle.parallel=true assemble --stacktrace)

#!/bin/sh
# Run script within the directory
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

# Build Glowstone
(cd .. && MAVEN_OPTS="-Xss4m -XX:+TieredCompilation -XX:TieredStopAtLevel=1" mvn -Dmaven.test.skip=true -T 1C -B package)

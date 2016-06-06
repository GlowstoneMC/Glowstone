#!/bin/sh
# Setup script for Glowstone++
(cd network && mvn install)
mvn package

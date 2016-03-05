#!/bin/sh
# Setup script for Glowstone++
(cd Glowkit && mvn install)
(cd network && mvn install)
mvn package

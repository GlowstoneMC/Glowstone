#!/bin/sh
# Setup script for Glowstone++
(cd Glowkit && mvn install)
mvn package

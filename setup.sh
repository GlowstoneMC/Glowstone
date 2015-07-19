#!/bin/sh
# Setup script for Glowstone++
cd Glowkit
mvn install
cd ..
mvn package

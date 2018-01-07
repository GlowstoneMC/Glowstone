#!/bin/sh
# Setup script for Glowstone
echo preparing environment
sudo apt-get update
apt-cache search maven
sudo apt-get install maven default-jre default-jdk
echo verifying environment
java -version
mvn -version
echo starting
mvn -T 1C -B package clean install

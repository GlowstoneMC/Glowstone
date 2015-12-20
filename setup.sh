#!/bin/sh
# Setup script for Glowstone++
(cd Glowkit && mvn install)
(cd FlowNetworking && mvn install)
mvn package

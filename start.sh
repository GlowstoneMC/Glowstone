#!/bin/sh
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -Xms1G -Xmx1G -XX:+UseG1GC -jar glowstone.jar

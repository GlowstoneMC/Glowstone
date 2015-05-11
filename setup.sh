# Setup script for Glowstone++
cd Glowkit
mvn install
cd ..
cd SpongeAPI
./gradlew
cd ..
mvn package

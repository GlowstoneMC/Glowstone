![Built with Love](http://forthebadge.com/images/badges/built-with-love.svg)
[![Join the Discord chat](https://img.shields.io/badge/discord-glowstone-738bd7.svg?style=flat-square)](https://discord.gg/TFJqhsC)
[![Build Status](https://circleci.com/gh/GlowstoneMC/Glowstone/tree/master.png)](https://circleci.com/gh/GlowstoneMC/Glowstone/tree/master)

#Glowstone

A fast, customizable and compatible open source Minecraft server.

##Introduction

Glowstone is a lightweight, from scratch, open source
[Minecraft](http://minecraft.net) server written in Java that supports plugins
written for the [Spigot](https://spigotmc.org) (and Bukkit) API.

The main goals of the project are to provide a lightweight implementation
of the Spigot API and Minecraft server where exact vanilla functionality is
not needed or higher performance is desired than the official software can
deliver. Glowstone makes use of a thread-per-world model and performs
synchronization only when necessitated by the Spigot API.

##Features

Glowstone has a few key advantages over CraftBukkit:
 * It is **100% open source**. While CraftBukkit and most other mods are open
   source, they rely on decompiled Minecraft source code. Glowstone's code is
   completely original.
 * Because of this, it is easy to contribute to Glowstone's development. The
   barrier of entry to contributions is lower because there is no need to work
   around decompiled source or maintain a minimal diff.
 * Glowstone supports all plugins written for the Bukkit and Spigot API natively. In
   practice, some plugins may try to make use of parts of the API which are not
   yet implemented, but in a completed state Glowstone would support all Bukkit plugins.
 * Glowstone's simplicity affords it a performance improvement over CraftBukkit
   and other servers, making it especially suited for situations where a large
   amount of players must be supported but vanilla game features are not needed.
 
However, there are several drawbacks:
 * Glowstone **is not finished**. Nothing is guaranteed to work, though many things
   are likely to. If in doubt, file an issue.
 * Bukkit plugins which expect the presence of CraftBukkit-specific code
   (that in the `org.bukkit.craftbukkit` or `net.minecraft.server` packages)
   will not work on Glowstone unless they are designed to fail gracefully.
 * Glowstone is not produced by the Bukkit team, and while we do make an effort
   to produce quality work, Glowstone does not undergo the same rigorious testing
   as the Bukkit project.
   
For a current list of features, [check the wiki](https://github.com/GlowstoneMC/Glowstone/wiki/Current-Features)

##Downloads

If you don't want to build from source, pre-built jar files are available to download from:

* **[Direct gserv.me download](https://bamboo.gserv.me/browse/GSPP-SRV/latestSuccessful/artifact/shared/Server-JAR/glowstone%2B%2B-1.10.2-SNAPSHOT.jar)** - recommended, direct link to latest build

* **[gserv.me](https://bamboo.gserv.me/browse/GSPP-SRV)** - all builds, no login required

* [![Build Status](https://circleci.com/gh/GlowstoneMC/Glowstone.svg?style=svg) **CircleCI**](https://circleci.com/gh/GlowstoneMC/Glowstone/tree/master) - click the latest build and then open the "Artifacts" tab (you must be logged in for this to show)

##Building


###1. Setup
After installing [Oracle JDK](http://oracle.com/technetwork/java/javase/downloads) (recommended) or [OpenJDK](http://openjdk.java.net/), and
[Maven](https://maven.apache.org), checkout the source:

```sh
git clone --recursive https://github.com/GlowstoneMC/Glowstone
cd Glowstone
```

###2. Build

```sh
./setup.sh
```

The final jar will be placed in `target/` named `glowstone-1.10.2-SNAPSHOT.jar`.

##Running

Running Glowstone is simple because its dependencies are shaded into the output
jar at compile time. Simply execute `java -jar glowstone-1.10.2-SNAPSHOT.jar` along with any
extra JVM options desired. A variety of command-line options are also available -
run `java -jar glowstone-1.10.2-SNAPSHOT.jar --help` for more information.

By default, configuration is stored in the `config/` subdirectory and logs
are stored in the `logs/` subdirectory. The main configuration file is
`config/glowstone.yml`, which replaces CraftBukkit's `server.properties` and
`bukkit.yml`. Settings from these two files will be copied over to Glowstone's
configuration during the default configuration generation process.

Glowstone uses [JLine](http://jline.sf.net) for console input and colored
console output. The JLine console can be disabled in the configuration if a
flat console is desired.

##Docs and Support

The best place to receive support is on [GitHub issues](https://github.com/GlowstoneMC/Glowstone/issues).
When reporting bugs, please retest and include whether the problem reproduces on:

* Earlier [builds](https://circleci.com/gh/GlowstoneMC/Glowstone) of Glowstone
* [Glowstone Legacy](https://github.com/GlowstoneMC/Glowstone-Legacy), if applicable

Javadocs can be generated by using the `mvn javadoc:javadoc` command and are
placed in the `target/site/apidocs/` directory, but these are incomplete
-in some places and in general the code is the best reference.

For documentation on the Glowkit API (an updated Bukkit which is used to
write plugins), see the Glowkit Javadocs
or visit Spigot's [Bukkit Javadocs](https://hub.spigotmc.org/javadocs/bukkit/).

##Credits

 * [The Minecraft Coalition](http://wiki.vg/) and [`#mcdevs`](https://github.com/mcdevs) -
   protocol and file formats research.
 * [The Bukkit team](https://bukkit.org) for their outstandingly well-designed
   plugin API.
 * [The SpigotMC team](https://spigotmc.org/) for updating and enhancing
   the Bukkit plugin API.
 * [The SpongePowered Team](https://www.spongepowered.org/) for
   creating the Sponge API.
 * [Trustin Lee](https://github.com/trustin) - author of the
   [Netty](http://netty.io/) library.
 * [Graham Edgecombe](https://github.com/grahamedgecombe/) - author of the
   original [Lightstone](https://github.com/grahamedgecombe/lightstone).
 * [Tad Hardesty](https://github.com/SpaceManiac) and [all the contributors](https://github.com/GlowstoneMC/Glowstone-Legacy/graphs/contributors) to Glowstone Legacy.
 * All the people behind [Maven](https://maven.apache.org/team-list.html) and [Java](https://java.net/people).
 * [Notch](http://notch.tumblr.com/) and
   [Mojang](http://mojang.com/about) - for making such an awesome game in the first
   place!

##Copyright

Glowstone is open-source software released under the MIT license. Please see
the `LICENSE` file for details.

Glowkit is open-source software released under the GPL license. Please see
the `LICENSE.txt` file in the Glowkit repository for details.


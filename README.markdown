Lightstone
==========

Introduction
------------

Lightstone is an open-source implementation of the
[Minecraft](http://minecraft.net) server software written in Java.

The official server software has some shortcomings such as the use of threaded,
synchronous I/O along with high CPU and RAM usage. Lightstone aims to be a
lightweight and high-performance alternative.

Building
--------

Lightstone can be built with the
[Java Development Kit](http://oracle.com/technetwork/java/javase/downloads) and
[Apache Ant](http://ant.apache.org).

Typing the command `ant` in the terminal will build the project.

Running
-------

Although not recommended, the server can be started via Ant. This is useful
for certain IDEs e.g. NetBeans which require an Ant target to run the project.

Typing the command `ant run` in the terminal will do this. It will also build
the project if the binaries are out of date.

Credits
-------

 * [The Minecraft Coalition](http://wiki.vg/wiki) - protocol and formats
   research.
 * [Trustin Lee](http://gleamynode.net) - author of the
   [Netty](http://jboss.org/netty) library.
 * [Notch](http://mojang.com/notch) - for making such a super awesome game in
   the first place!

Copyright
---------

Lightstone is open-source software released under the GPL license, please see
the `COPYING` and `LICENSE` files for details.

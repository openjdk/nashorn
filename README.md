Nashorn Engine
==============

Nashorn engine is an open source implementation of the
[ECMAScript Edition 5.1 Language Specification](https://es5.github.io).
It also implements many new features introduced in ECMAScript 6
including template strings; `let`, `const`, and block scope; iterators
and `for..of` loops; `Map`, `Set`, `WeakMap`, and `WeakSet` data types;
symbols; and binary and octal literals. It is written in Java and runs
on the Java Virtual Machine.

Nashorn used to be part of the JDK until Java 14. This project provides
a standalone version of Nashorn suitable for use with Java 11 and later.

Nashorn is free software, licensed under
[GPL v2 with the Classpath exception](https://github.com/openjdk/nashorn/blob/master/LICENSE),
just like the JDK.

Documentation
=============

[View the JavaDoc](https://www.javadoc.io/doc/org.openjdk.nashorn/nashorn-core).

Making Nashorn standalone is still a work in progress. There is no
standalone user's guides for it yet. The best current guides are
Nashorn-related documents last published by Oracle with Java 14:

  * [Nashorn User's Guide](https://docs.oracle.com/en/java/javase/14/nashorn/)
  * [Java Scripting Programmer's Guide](https://docs.oracle.com/en/java/javase/14/scripting/index.html)

(When browsing these guides, mentally substitute `org.openjdk.nashorn` in place of `jdk.scripting.nashorn` module name and `jdk.nashorn` package name.)


Getting Started
===============
Latest version of Nashorn is 15.3, available from [Maven Central](https://search.maven.org/artifact/org.openjdk.nashorn/nashorn-core/15.3/jar). You can check the [change log](CHANGELOG.md) to see what's new.

Nashorn is a JPMS module, so make sure it and its transitive dependencies (Nashorn depends on several ASM JARs) are on your application's module path, or appropriately added to a module layer, or otherwise configured as modules.

While standalone Nashorn is primarily meant to be used with Java 15 and later versions, it can also be used with Java versions 11 to 14 that have a built-in version of Nashorn too. See [this page](https://github.com/szegedi/nashorn/wiki/Using-Nashorn-with-different-Java-versions) for details on use when both versions are present.

Building From Source
====================
Nashorn uses Ant as its build system.
```
cd make/nashorn
ant jar
```
will download the dependencies and build the JAR file. Other notable targets are `test` for running its own internal test suite, or  `test262-parallel` for running the [official ECMA-262 test suite for ECMAScript 5.1](https://github.com/tc39/test262/tree/es5-tests). You will need to execute `ant get-test262` to download the tests into Nashorn's local test directory once.

Contributing
============

Nashorn is a project under the charter of the OpenJDK. The
[OpenJDK Bylaws](https://openjdk.java.net/bylaws) govern our work. The
Nashorn project membership can be found on the
[OpenJDK Census](https://openjdk.java.net/census#nashorn). We welcome
patches and involvement from individual contributors or companies. If
this is your first time contributing to an OpenJDK project, you will
need to review the rules on
[becoming a Contributor](https://openjdk.java.net/bylaws#contributor),
and sign the [Oracle Contributor Agreement](https://www.oracle.com/technetwork/community/oca-486395.html)
(OCA).

## Issue tracking

If you think you have found a bug in Nashorn, first make sure that you
are testing against the latest version - your issue may already have
been fixed. If not, search our
[issues list](https://bugs.openjdk.java.net/browse/JDK-8255842?jql=project%3DJDK%20AND%20component%3Dcore-libs%20AND%20Subcomponent%3Djdk.nashorn)
in the Java Bug System (JBS) in case a similar issue has already been
opened. More information on where and how to report a bug can be found
at [bugreport.java.com](https://bugreport.java.com/). Use component
"Core Libraries" and Subcomponent "jdk.nashorn" when filing an issue.

## Discussion

Discussion of Nashorn development happens on the
[nashorn-dev](https://mail.openjdk.java.net/mailman/listinfo/nashorn-dev)
mailing list.

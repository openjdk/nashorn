OpenJDK Nashorn Changelog
=========================

15.0 (2020.11.07)
-----------------
[`#3`](https://github.com/openjdk/nashorn/pull/3) [`JDK-8256506`](https://bugs.openjdk.java.net/browse/JDK-8256506) Create a standalone version of Nashorn for Java 15+

15.1 (2020.12.23)
-----------------
[`#5`](https://github.com/openjdk/nashorn/pull/5) [`JDK-8258147`](https://bugs.openjdk.java.net/browse/JDK-8258147) Modernize Nashorn code

[`#6`](https://github.com/openjdk/nashorn/pull/6) [`JDK-8233195`](https://bugs.openjdk.java.net/browse/JDK-8233195) Don't hoist block-scoped variables from dead code

[`#7`](https://github.com/openjdk/nashorn/pull/7) [`JDK-8244586`](https://bugs.openjdk.java.net/browse/JDK-8244586) Opportunistic type evaluation should gracefully handle undefined lets and consts

[`#8`](https://github.com/openjdk/nashorn/pull/8) [`JDK-8240299`](https://bugs.openjdk.java.net/browse/JDK-8240299) A possible bug about Object.setPrototypeOf()

[`#9`](https://github.com/openjdk/nashorn/pull/9) [`JDK-8258216`](https://bugs.openjdk.java.net/browse/JDK-8258216) Allow Nashorn to operate when not loaded as a JPMS module

15.1.1 (2020.12.30)
-------------------
[`#10`](https://github.com/openjdk/nashorn/pull/10) [`JDK-8258749`](https://bugs.openjdk.java.net/browse/JDK-8258749) Remove Dynalink tests from Standalone Nashorn

[`#11`](https://github.com/openjdk/nashorn/pull/11) [`JDK-8258787`](https://bugs.openjdk.java.net/browse/JDK-8258787) ScriptEngineFactory.getOutputStatement neither quotes nor escapes its argument

[`#12`](https://github.com/openjdk/nashorn/pull/12) [`JDK-8240298`](https://bugs.openjdk.java.net/browse/JDK-8240298) Array.prototype.pop, push, and reverse didn't call ToObject on their argument

15.2 (2021.02.13)
-----------------
No code changes, but the artifacts published on Maven Central are now compiled with Java 11 instead of Java 15. It is thus possible to use them with projects targeting Java 11+.

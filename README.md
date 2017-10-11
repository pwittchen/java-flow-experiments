# java-flow-experiments
In this repository, I'm experimenting with [Reactive Streams](http://www.reactive-streams.org), which are going to become part of the Java 9 in the `java.util.concurrent` package in Java 8 application and with [native Java 9 Reactive Streams](http://download.java.net/java/jdk9/docs/api/java/util/concurrent/Flow.html) as well.

This repository is prepared for my talk titled **Get ready for java.util.concurrent.Flow!** 

during [JDD](http://jdd.org.pl) 2017 Conference on 03.10.2017 in Kraków, Poland.

Presentation slides
-------------------

Presentation slides are published at:

https://speakerdeck.com/pwittchen/get-ready-for-java-dot-util-dot-concurrent-dot-flow

My blog posts related to JDD conference
------------------------------------
- [JDD 2017 - recap](http://blog.wittchen.biz.pl/jdd-2017-recap/)
- [JDD 2017 - an invitation](http://blog.wittchen.biz.pl/jdd-2017-get-ready-for-java-util-concurrent-flow/)

Java 8 project
--------------

Inside `java8` directory, I'm playing with two implementations of Reactive Streams:
- [RxJava2](https://github.com/ReactiveX/RxJava)
- [Project Reactor](https://projectreactor.io/) (it's going to be included in Spring 5)
- [Akka Stream](https://github.com/akka/akka/tree/master/akka-stream)

Project inside `java8` directory can be cleaned, compiled and run via Gradle Wrapper.

It contains only exploratory tests for different reactive libraries.

Java 9 project
--------------

Inside `java9` directory I'm playing with native Java 9 Reactive Streams interfaces. This project is based on Gradle. If you have problems with importing it into IntelliJ IDEA, choose Java 8 during import and switch Java version to 9 after import. In the case of problems with compilation, update `gradle.properties` file. 

### Known issues

There may be problems with executing unit tests with Java 9 and Gradle from CLI.

Git branches
------------
- `master` branch contains code snippets from presentation and exepriments
- `presentation` branch contains code snippets for Java 8 shown during the presentation and empty project in Java 9 for live coding

References
----------
- [Reactive Streams](http://www.reactive-streams.org/)
- [Reactive Streams Specification](https://github.com/reactive-streams/reactive-streams-jvm)
- [Reactive Streams TCK](https://github.com/reactive-streams/reactive-streams-jvm/tree/master/tck) (which all implementations *must* pass)

- [Reactive Manifesto](https://www.reactivemanifesto.org/)
- [Java 9 Reactive Streams](http://www.baeldung.com/java-9-reactive-streams)
- [Reactive Programming with JDK 9 Flow API](https://community.oracle.com/docs/DOC-1006738)
- [Flow Class JavaDoc](http://gee.cs.oswego.edu/dl/jsr166/dist/docs/java/util/concurrent/Flow.html)
- [Java 9 new Features: Reactive Streams](https://aboullaite.me/java-9-new-features-reactive-streams/)
- [Catching up RxJava with Java 9 Flow API](https://medium.com/@teachpendant/catching-up-rxjava-with-java-9-flow-api-b2e19ec40270)
- [Java 9 (Part 5): Flow With the New Reactive Streams, First Look](https://dzone.com/articles/java-9-tutorial-flow-with-the-new-reactive-streams)
- [Java 9 Flow API: asynchronous integer range source](http://akarnokd.blogspot.com/2017/03/java-9-flow-api-asynchronous-integer.html)
- [5 not so obvious things about RxJava](https://medium.com/@jagsaund/5-not-so-obvious-things-about-rxjava-c388bd19efbc)
- Reactive Streams implementations
  - [RxJava2](https://github.com/ReactiveX/RxJava)
  - [Project Reactor](https://projectreactor.io/)
  - [Akka Stream](https://github.com/akka/akka/tree/master/akka-stream)

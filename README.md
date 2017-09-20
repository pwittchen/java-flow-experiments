# java-flow-experiments
In this repository, I'm experimenting with [Reactive Streams](http://www.reactive-streams.org), which are going to become part of the Java 9 in the `java.util.concurrent` package in Java 8 application and with [native Java 9 Reactive Streams](http://download.java.net/java/jdk9/docs/api/java/util/concurrent/Flow.html) as well.

This repository was prepared for my talk titled **Get ready for java.util.concurrent.Flow!** 

during [JDD](http://jdd.org.pl) 2017 Conference in Kraków, Poland.

Java 8 project
--------------

Inside `java8` directory, I'm playing with two implementations of Reactive Streams:
- [RxJava2](https://github.com/ReactiveX/RxJava)
- [Project Reactor](https://projectreactor.io/) (it's going to be included in Spring 5)
- [Akka Stream](https://github.com/akka/akka/tree/master/akka-stream)

Projects inside `java8` directory can be cleaned, compiled and run via Gradle Wrapper.

Java 9 project
--------------

Inside `java9` directory I'm playing with native Java 9 Reactive Streams interfaces.
In this setup, you need to have installed Java 9 from Oracle in the following path: `/usr/lib/jvm/java-9-oracle/` (it was tested under Ubuntu Linux 16.04 LTS). Please use `java9/builder.sh` script to clean, compile and run the application. Call `./builder.sh -h` for help. To clean, build and run project call `./builder.sh -cbr`. You can also import this project in IntelliJ IDEA and do the same through IntelliJ IDEA. I haven't used Gradle in this project, because it's not compatible with Java 9 yet.

References
----------
- [Reactive Streams](http://www.reactive-streams.org/)
- [Reactive Manifesto](https://www.reactivemanifesto.org/)
- [Java 9 Reactive Streams](http://www.baeldung.com/java-9-reactive-streams)
- [Reactive Programming with JDK 9 Flow API](https://community.oracle.com/docs/DOC-1006738)
- [Flow Class JavaDoc](http://gee.cs.oswego.edu/dl/jsr166/dist/docs/java/util/concurrent/Flow.html)
- [Java 9 new Features: Reactive Streams](https://aboullaite.me/java-9-new-features-reactive-streams/)
- [Catching up RxJava with Java 9 Flow API](https://medium.com/@teachpendant/catching-up-rxjava-with-java-9-flow-api-b2e19ec40270)
- [Java 9 (Part 5): Flow With the New Reactive Streams, First Look](https://dzone.com/articles/java-9-tutorial-flow-with-the-new-reactive-streams)
- [Java 9 Flow API: asynchronous integer range source](http://akarnokd.blogspot.com/2017/03/java-9-flow-api-asynchronous-integer.html)
- Reactive Streams implementations
  - [RxJava2](https://github.com/ReactiveX/RxJava)
  - [Project Reactor](https://projectreactor.io/)
  - [Akka Stream](https://github.com/akka/akka/tree/master/akka-stream)

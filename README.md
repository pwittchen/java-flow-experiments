# java-flow-experiments
In this repository, I'm experimenting with [Reactive Streams](http://www.reactive-streams.org), which are going to become part of the Java 9 in the `java.util.concurrent` package.

Java 8 project
--------------

Inside `java8` directory, I'm playing with two implementations of Reactive Streams:
- [Project Reactor](https://projectreactor.io/) (it's going to be included in Spring 5)
- [RxJava2](https://github.com/ReactiveX/RxJava)

Projects inside `java8` directory can be cleaned, compiled and run via Gradle Wrapper.

Java 9 project
--------------

Inside `java9` directory I'm playing with native Java 9 Reactive Streams interfaces.
In this setup, you need to have installed Java 9 from Oracle in the following path: `/usr/lib/jvm/java-9-oracle/` (it was tested under Ubuntu Linux 16.04). Please use `java9/builder.sh` script to clean, compile and run the application. You can also import in IntelliJ IDEA and do the same through IntelliJ IDEA.

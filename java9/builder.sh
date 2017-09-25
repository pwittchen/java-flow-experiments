#!/usr/bin/env bash

# if your JVM path is different, change it below

if [ `uname` = "Darwin" ]; then
  export BUILDER_JVM_PATH="/Library/Java/JavaVirtualMachines/jdk1.9.0.jdk/Contents/Home"
fi

if [ `uname` = "Linux" ]; then
  export BUILDER_JVM_PATH="/usr/lib/jvm/java-9-oracle"
fi

export BUILDER_OUT_DIR="out/production/java9/"
export BUILDER_SRC_FILE="src/com/github/pwittchen/Main.java"
export BUILDER_MAIN_CLASS="com.github.pwittchen.Main"

function help() {
  echo ":help"
  echo "
     builder.sh is a simple shell script responsible
     for cleaning, building and running this Java 9 application

     Usage:
       -c    cleans the project (removes 'out/' directory)
       -b    builds (compiles) the project
       -r    runs the compiled project
       -h    shows help
  "
}

function clean() {
  echo ":clean"
  rm -rf $BUILDER_OUT_DIR
  mkdir -p $BUILDER_OUT_DIR
  echo ":clean SUCCESS"
}

function build() {
  echo ":build"
  $BUILDER_JVM_PATH/bin/javac -d $BUILDER_OUT_DIR $BUILDER_SRC_FILE
  echo ":build SUCCESS"
}

function run() {
  echo ":run"
  $BUILDER_JVM_PATH/bin/java -Dfile.encoding=UTF-8 -classpath $BUILDER_OUT_DIR $BUILDER_MAIN_CLASS
  echo ":run SUCCESS"
}

while getopts "hcbr" opt; do
    case "$opt" in
    h)
        help
        exit 0
        ;;
    c)  clean
        ;;
    b)  build
        ;;
    r)  run
        ;;
    esac
done

shift $((OPTIND-1))

[ "$1" = "--" ] && shift

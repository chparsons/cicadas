#!/bin/bash

jpath=/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin
appjar=../app/cicada.jar
libs=(../lib/core.jar ../lib/controlP5.jar ../lib/oscP5.jar)

cd bin
echo "go to "; pwd; 

echo "clean up..."
ls | grep [^makefile] | grep [^Manifest.txt] | xargs rm -r;

echo "extract libs..."
for lib in "${libs[@]}"; do $jpath/jar xvf $lib; done

echo "compile..."
$jpath/javac -sourcepath ../src -classpath . ../src/Main.java -d .

echo "make app jar..."
$jpath/jar cvfm $appjar Manifest.txt *
#$jpath/jar cvfe $appjar Main *

cd ..

echo "finished"; pwd;


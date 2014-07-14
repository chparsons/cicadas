#!/bin/bash
jpath=/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Commands
$jpath/java -d32 -jar -Djava.library.path=lib/native app/cicada.jar .

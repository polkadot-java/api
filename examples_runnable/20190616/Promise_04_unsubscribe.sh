#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)
cd ${basepath}
echo ${basepath}



java -cp "./:./lib/*" org.polkadot.example.promise.E04_Unsubscribe ws://127.0.0.1:9944

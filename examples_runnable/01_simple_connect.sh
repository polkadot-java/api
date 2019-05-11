#!/usr/bin/env bash
basepath=$(cd `dirname $0`; pwd)
cd ${basepath}
echo ${basepath}



java -cp "./:./lib/*" org.polkadot.example.SimpleConnect

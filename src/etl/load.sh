#!/bin/bash

CURDIR=$( pwd )
DIR=/data/Abbotsford/2013Extract

cd $DIR
for i in $( ls wd* )
do 
	echo "loading $i ..."; 
	mv $i waterdata.csv
	python $CURDIR/waterdataetl.py
	mv waterdata.csv loaded/$i
done

cd $CURDIR

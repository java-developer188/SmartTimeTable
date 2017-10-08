#! /bin/bash 
cd /home/sshaider/FypMs/DataCleaning
rm -rf input/*
rm -rf output/*

cd /home/sshaider/FypMs
rm -rf TimeTable.xlsx
mysql --user=root --password=haider -e "source partial.sql"

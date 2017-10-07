#! /bin/bash 
cd /home/sshaider/FypMs
mysql --user=root --password=haider -e "source schemaScript.sql"

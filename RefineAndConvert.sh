#! /bin/bash          
cd /home/sshaider/FypMs/DataCleaning
./openrefine-batch.sh -a input/ -b config/ -c output/ -f xlsx -i sheets=1,2,3,4 -RX
cd /home/sshaider/FypMs/DataCleaning/output
ssconvert BSCS.tsv BSCS.xlsx

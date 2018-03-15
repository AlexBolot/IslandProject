#!/usr/bin/env bash
mvn clean install;

testMap()
{
	mvn exec:java -Djava.awt.headless=true -Dexec.args="$*"
	tmp=${f%%.*}
	map=${tmp##*/}
	mkdir -p scriptResults/$map'_('$2,$3')_'$4/
	cp island.log scriptResults/$map'_('$2,$3')_'$4/island.log
	cp Explorer.svg scriptResults/$map'_('$2,$3')_'$4/Explorer.svg
	cp Explorer_ise.json scriptResults/$map'_('$2,$3')_'$4/Explorer_ise.json
	cp _pois.json scriptResults/$map'_('$2,$3')_'$4/_pois.json
}

CONTRACTS[0]="30000 15 WOOD 2000 FUR 100 QUARTZ 400 RUM 2"
CONTRACTS[1]="20000 7 WOOD 4000 QUARTZ 100"
CONTRACTS[2]="10000 4 FUR 500 QUARTZ 50 RUM 40"
CONTRACTS[3]="25000 14 FLOWER 500 QUARTZ 100 FRUITS 40 WOOD 1000"
CONTRACTS[4]="20000 7 QUARTZ 100 WOOD 3000"
CONTRACTS[5]="30000 30 QUARTZ 100 WOOD 5000 FUR 200 RUM 10"
CONTRACTS[6]="30000 30 WOOD 5000 FLOWER 2 PLANK 1000 QUARTZ 200"
CONTRACTS[7]="15000 7 WOOD 5000 QUARTZ 20 LEATHER 50"
CONTRACTS[8]="20000 3 WOOD 7000 QUARTZ 20 FLOWER 5 RUM 5"

index=0

for f in ressources/*.json; do 
	testMap $f '1' '1' 'SOUTH' ${CONTRACTS[index]};
	testMap $f '1' '1' 'EAST' ${CONTRACTS[index]};
	testMap $f '159' '159' 'WEST' ${CONTRACTS[index]};
	testMap $f '159' '159' 'NORTH' ${CONTRACTS[index]};
	testMap $f '1' '159' 'EAST' ${CONTRACTS[index]};
	testMap $f '1' '159' 'NORTH' ${CONTRACTS[index]};
	testMap $f '159' '1' 'SOUTH' ${CONTRACTS[index]};
	testMap $f '159' '1' 'WEST' ${CONTRACTS[index]};
	((index++))
done

respath=scriptResults/results.log
echo > $respath
for f in scriptResults/*/island.log; do
	map=$(echo $f | cut -d'/' -f 2)
 	echo $map >> $respath
	cat $f | awk  '$3 == "Report:" {i=1;next};i && i++' >> $respath
	echo >> $respath
done
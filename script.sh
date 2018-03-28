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

CONTRACTS[0]="30000 15 161D552A4A22E2A1 WOOD 2000 FUR 100 QUARTZ 400 RUM 2"
CONTRACTS[1]="20000 7 5B75D6CC8B576BD4 WOOD 4000 QUARTZ 100"
CONTRACTS[2]="10000 4 90FEE7AB6C50731A FUR 500 QUARTZ 50 RUM 40"
CONTRACTS[3]="25000 14 7D66DD29D822074E FLOWER 500 QUARTZ 100 FRUITS 40 WOOD 1000"
CONTRACTS[4]="20000 7 A78293813D85BA02 QUARTZ 100 WOOD 3000"
CONTRACTS[5]="30000 30 DEB2DE91045B55E9 QUARTZ 100 WOOD 5000 FUR 200 RUM 10"
CONTRACTS[6]="30000 30 B390555FC76E2010 WOOD 5000 FLOWER 2 PLANK 1000 QUARTZ 200"
CONTRACTS[7]="15000 7 49FCBBF35EDDC6EA WOOD 5000 QUARTZ 20 LEATHER 50"
CONTRACTS[8]="20000 3 FE25C1A672968594 WOOD 7000 QUARTZ 20 FLOWER 5 RUM 5"
CONTRACTS[9]="20000 12 AB3DB02B0F1D78C6 WOOD 4000 QUARTZ 20 FUR 1000 RUM 5"

ORIENTATIONS[0]="1 1 EAST"
ORIENTATIONS[1]="1 1 SOUTH"
ORIENTATIONS[2]="1 159 EAST"
ORIENTATIONS[3]="1 159 NORTH"
ORIENTATIONS[4]="159 1 SOUTH"
ORIENTATIONS[5]="159 1 WEST"
ORIENTATIONS[6]="159 159 NORTH"
ORIENTATIONS[7]="159 159 WEST"

index=0

for f in maps/*.json; do
    for ((i=0; i < ${#ORIENTATIONS[*]}; i++)); do
        testMap $f ${ORIENTATIONS[i]} ${CONTRACTS[index]};
    done
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
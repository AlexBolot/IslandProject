#!/usr/bin/env bash
mvn clean install;

testMap()
{
	mvn exec:java -Djava.awt.headless=true -Dexec.args="$*"
	tmp=${f%%.*}
	map=${tmp##*/}
	mkdir -p scriptResults/${map}/
	cp island.log scriptResults/${map}/island.log
	cp Explorer.svg scriptResults/${map}/Explorer.svg
	cp Explorer_ise.json scriptResults/${map}/Explorer_ise.json
	cp _pois.json scriptResults/${map}/_pois.json
}

CONTRACTS[0]="159 159 NORTH 30000 15 161D552A4A22E2A1 WOOD 2000 FUR 100 QUARTZ 400 RUM 2"
CONTRACTS[1]="1 1 EAST 20000 7 5B75D6CC8B576BD4 WOOD 4000 QUARTZ 100"
CONTRACTS[2]="1 1 SOUTH 10000 4 90FEE7AB6C50731A FUR 500 QUARTZ 50 RUM 40"
CONTRACTS[3]="159 159 WEST 25000 14 7D66DD29D822074E FLOWER 500 QUARTZ 100 FRUITS 40 WOOD 1000"
CONTRACTS[4]="1 1 SOUTH 20000 7 A78293813D85BA02 QUARTZ 100 WOOD 3000"
CONTRACTS[5]="159 159 WEST 30000 30 DEB2DE91045B55E9 QUARTZ 100 WOOD 5000 FUR 200 RUM 10"
CONTRACTS[6]="1 1 SOUTH 30000 30 B390555FC76E2010 WOOD 5000 FLOWER 2 PLANK 1000 QUARTZ 200"
CONTRACTS[7]="1 1 EAST 15000 7 49FCBBF35EDDC6EA WOOD 5000 QUARTZ 20 LEATHER 50"
CONTRACTS[8]="1 1 EAST 20000 3 FE25C1A672968594 WOOD 7000 QUARTZ 20 FLOWER 5 RUM 5"
CONTRACTS[9]="1 1 EAST 20000 12 AB3DB02B0F1D78C6 WOOD 4000 QUARTZ 20 FUR 1000 RUM 5"
CONTRACTS[10]="159 159 NORTH 15000 7 B8743F260B1D24EF WOOD 7000 SUGAR_CANE 400 QUARTZ 20 RUM 5 ORE 4"

index=0

for f in maps/*.json; do
    testMap ${f} ${CONTRACTS[index]};
    ((index++))
done

resPath=scriptResults/results.log
echo > ${resPath}
for f in scriptResults/*/island.log; do
	map=$(echo ${f} | cut -d'/' -f 2)
 	echo ${map} >> ${resPath}
	cat ${f} | awk  '$3 == "Report:" {i=1;next};i && i++' >> ${resPath}
	echo >> ${resPath}
done

mkdir -p CustomerValue
today=`date +%Y-%m-%d.%H:%M:%S`
cp ${resPath} CustomerValue/${today}.log
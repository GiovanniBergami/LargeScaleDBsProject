#!/bin/sh

bin/neo4j-admin database import full \
	--overwrite-destination \
	--nodes import/nodes.csv \
	--relationships import/ways.csv \
	--id-type=integer \
	--high-parallel-io=on \
	--max-off-heap-memory=16G \
	--threads=16

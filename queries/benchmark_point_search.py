from neo4j import GraphDatabase
import time
import random

driver = GraphDatabase.driver("bolt://localhost:7687", auth=("neo4j", "password"))
session = driver.session()

def make_query(typeV, lat, lng):
	session.execute_read(query, typeV, lat, lng)

def query(tx, typeV, lat, lng):
	res = tx.run(
"""
WITH point({latitude: $lat, longitude: $lng}) AS q
MATCH (p:Point)
MATCH (p)-[w:CONNECTS]->(r:Point)
WHERE point.distance(q, p.coord) < 100 AND
CASE
	WHEN $typeV = 'foot' THEN w.crossTimeFoot <> Infinity
	WHEN $typeV = 'bicycle' THEN w.crossTimeBicycle <> Infinity
	WHEN $typeV = 'car' THEN w.crossTimeMotorVehicle <> Infinity
END
RETURN p, point.distance(q, p.coord)
ORDER BY point.distance(q, p.coord) LIMIT 1
""", typeV=typeV, lat=lat, lng=lng)

MAX_LAT = 51.7314463;
MIN_LAT = 51.2268448;
MAX_LON = 0.399670;
MIN_LON = -0.6125035;

typeVs = ["foot", "bicycle", "car"]

mint = 0xfffffff
maxt = 0
sumt = 0

for i in range(1, 1000):
	lat = MIN_LAT + (MAX_LAT - MIN_LAT)*random.uniform(0, 1)
	lng = MIN_LON + (MAX_LON - MIN_LON)*random.uniform(0, 1)

	start_time = time.time()

	make_query(typeVs[i%3], lat, lng)

	ext = time.time() - start_time
	mint = min(ext, mint)
	maxt = max(ext, maxt)
	sumt = sumt + ext

print(mint, maxt, (sumt/1000))

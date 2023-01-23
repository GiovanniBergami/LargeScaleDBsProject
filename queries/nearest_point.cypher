WITH point({latitude: $lat, longitude: $lng}) AS q
MATCH (p:Point)
MATCH (p)-[w:CONNECTS]->(r:Point)
WHERE point.distance(q, p.coord) < 100 AND
CASE
	WHEN $type = 'foot' THEN w.crossTimeFoot <> Infinity
	WHEN $type = 'bicycle' THEN w.crossTimeBicycle <> Infinity
	WHEN $type = 'car' THEN w.crossTimeMotorVehicle <> Infinity
END
RETURN p, point.distance(q, p.coord)
ORDER BY point.distance(q, p.coord) LIMIT 1

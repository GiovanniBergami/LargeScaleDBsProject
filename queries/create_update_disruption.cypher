MERGE (d:Disruption {id: $id})
SET d.centrum = point({latitude: $lat, longitude: $lon})
SET d.radius = $radius
SET d.severity = $severity
SET d.ttl = $ttl
SET d.category = $category SET d.subCategory = $subcategory
SET d.comment = $comment
SET d.update = $update SET d.updateTime = $updateTime
SET d.closed = $closed
WITH d
MATCH (p: Point)
WHERE point.distance(p.coord, d.centrum) <= d.radius
MERGE (p)-[:IS_DISRUPTED {severity: $severity}]->(d)

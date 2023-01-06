MATCH (start: Point{id: 3716875498}) // King's cross
MATCH (end: Point{id: 2567779107}) // London Eye
CALL gds.shortestPath.astar.stream('myGraph', {
	sourceNode: start,
	targetNode: end,
	latitudeProperty: 'lat',
	longitudeProperty: 'lon',
	relationshipWeightProperty: 'crossTimeMotorVehicle',
	relationshipTypes: ['CONNECTS'],
	concurrency: 4
	})
YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path
UNWIND range(0, size(nodes(path)) - 1) AS i
RETURN gds.util.asNode(nodeIds[i]).id AS id
ORDER BY index

db.TransitStop.aggregate([
	// Unwind the routes array
	{ $unwind: "$routes" },
	// Count the number of disruptions of type "closure"
	{ $addFields: {
		closureCount: {
			$size: {
				$filter: {
					input: "$terminatedDisruptions",
			 as: "disruption",
			 cond: { $eq: ["$$disruption.typeDisruption", "closure"] }
				}
			}
		}
	}},
	// Create a document for each route-closureCount combination
	{ $project: {
		_id: 0,
		line: "$routes.line", // <- S'hanno più linee in una fermata!
		count: "$closureCount"
	}},
	// Group by line and sum the closure counts
	{ $group: {
		_id: "$line",
		total: { $sum: "$count" }
	}},
	// Sort the lines by total in descending order
	{ $sort: { total: -1 }}
])

use londonSafeTravel

db.PointOfInterest.insertOne({
	id: 12345,
	name: "Mosque",
	type: "religion",
	coordinates: {
		type: "Point",
		coordinates: [0.0, 0.0]
	}
});

db.PointOfInterest.insertOne({
        id: 9999,
        name: "Church",
        type: "religion",
        coordinates: {
                type: "Point",
                coordinates: [10.0, 5.0]
        }
});

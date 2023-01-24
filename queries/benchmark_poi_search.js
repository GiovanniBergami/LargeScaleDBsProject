const MAX_LAT = 51.7314463;
const MIN_LAT = 51.2268448;
const MAX_LON = 0.399670;
const MIN_LON = -0.6125035;

function generateRandomBox() {
    const result = {};

    result.minLong = MIN_LON + (MAX_LON - MIN_LON)*Math.random();
    result.minLat  = MIN_LAT + (MAX_LAT - MIN_LAT)*Math.random();

    result.maxLong = result.minLong + 0.0086530;
    result.maxLat = result.minLat + 0.0045566;

    return result;
}

function makeQuery(box) {
    const result = db.PointOfInterest.find(
        {
            "coordinates": {
                $geoWithin: { $geometry: {
                        type : "Polygon" ,
                        coordinates: [[
                            [box.minLong, box.minLat],
                            [box.maxLong, box.minLat],
                            [box.maxLong, box.maxLat],
                            [box.minLong, box.maxLat],
                            [box.minLong, box.minLat]
                        ]]
                    }
                }
            }
        });

    return result.explain("executionStats").executionStats.executionTimeMillis
}

function Result(index) {
    this.index = index;
    this.mint = 99999.0;
    this.maxt = 0.0;
    this.avgt = 0.0;

};

function bench(iter, result) {
    for(let i = 0; i < iter; i++) {
        const t = makeQuery(generateRandomBox());
        result.mint = Math.min(result.mint, t);
        result.maxt = Math.max(result.maxt, t);
        result.avgt += t;
    }

    result.avgt = result.avgt / iter;
}

const results = [];

db.PointOfInterest.dropIndex("coordinates_2dsphere");
const noIndexs = new Result(false);
bench(150, noIndexs);

// Create index
db.PointOfInterest.createIndex( { coordinates : "2dsphere" });
const yesIndexs = new Result(true);
bench(150, yesIndexs);

// Return to intellj
results.push(noIndexs, yesIndexs);
results;
#!/usr/bin/mongosh

use("londonSafeTravel");

const severities = ["Minimal", "Moderate", "Serious", "Severe"];
const categories = ["burst pipe", "collision", "broken lights", "tea party", "flat tyre", "applepie"]

for(let i = 0; i < 50; i++) {
    let disruption = {
        id: i,
        type: "road",

        start: "2022-04-05T" + ((i % 25) < 10 ? "0" : "") + (i % 25) + ":00:30",
        stop: "2022-04-06T19:33:33",

        coordinates: {
            type: "Point",
            coordinates: [i/10.0, i/10.0]
        },

        severity: severities[i % 4],
        category: categories[i % categories.length]
    }

    db.Disruption.insertOne(disruption);
}

#!/usr/bin/mongosh

use("londonSafeTravel");


// Indice su PointOfInterest
db.PointOfInterest.createIndex( { coordinates : "2dsphere" });

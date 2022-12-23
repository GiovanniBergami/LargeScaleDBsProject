CREATE CONSTRAINT FOR (p:Point)
REQUIRE (p.id) IS UNIQUE;

CREATE POINT INDEX FOR (p:Point) ON (p.coord);
// CREATE INDEX FOR (p:Point) ON (p.id);


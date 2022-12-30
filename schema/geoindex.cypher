//CREATE CONSTRAINT FOR (p:Point)
//REQUIRE (p.id) IS UNIQUE;

CREATE POINT INDEX FOR (p:Point) ON (p.coord);
CREATE POINT INDEX FOR (p:Point) ON (p.id);

CREATE INDEX FOR ()-[w:CONNECTS]->() ON (w.name)

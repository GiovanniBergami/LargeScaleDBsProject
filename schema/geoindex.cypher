//CREATE OR REPLACE DATABASE neo4j;

CREATE POINT INDEX FOR (p:Point) ON (p.coord);
CREATE INDEX FOR (p:Point) ON (p.id);

CREATE INDEX FOR ()-[w:CONNECTS]->() ON (w.name);

CALL gds.graph.project(
  'myGraph',
  'Point',
  'CONNECTS',
  {
    nodeProperties: ['latitude', 'longitude'],
    relationshipProperties: ['crossTimeMotorVehicle']
  }
);

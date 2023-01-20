MATCH (s:Point{id: $start})
MATCH (e:Point{id: $end})
CALL londonSafeTravel.route.anytime(s, e, $type, $maxspeed, 12.5)
YIELD index, node, time
RETURN index, node AS waypoint, time
ORDER BY index DESCENDING

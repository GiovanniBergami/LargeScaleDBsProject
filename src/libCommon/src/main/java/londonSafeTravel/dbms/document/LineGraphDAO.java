package londonSafeTravel.dbms.document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import londonSafeTravel.schema.document.LineGraphEntry;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

public class LineGraphDAO {
    // @TODO
    private final MongoCollection<LineGraphEntry> collection;

    public LineGraphDAO(ConnectionMongoDB connection) {
        MongoDatabase db = connection.giveDB();
        this.collection = db.getCollection("Disruption", LineGraphEntry.class);
    }

    public List<LineGraphEntry> computeGraph() {
        return computeGraph(null);
    }

    public List<LineGraphEntry> computeGraph(String category) {
        List<Bson> pipeline = new ArrayList<>();
        if(category != null)
            pipeline.add(Aggregates.match(Filters.eq("category", category)));


        pipeline.addAll(Arrays.asList(
        Aggregates.project(
    Projections.fields(
            new Document("start",
                    new Document("$multiply",
                            Arrays.asList(
                                    new Document("$floor",
                                            new Document("$divide",
                                                    Arrays.asList(
                                            new Document("$toLong", "$start"),
                                                        3600000
                                                            )
                                                    )
                                            ),
                                            3600
                                    )
                            )
                    ).append("end",
                        new Document("$min",
                                Arrays.asList(
                                        new Document("$multiply",
                                                Arrays.asList(
                                                new Document("$ceil",
                                                    new Document("$divide",
                                                            Arrays.asList(
                                                        new Document("$toLong", "$end"),
                                                                3600000
                                                                )
                                                            )
                                                        ),
                                                    3600
                                                )
                                            ),
                                    new Document("$multiply",
                                        Arrays.asList(
                                        new Document("$ceil",
                                        new Document("$divide",
                                                Arrays.asList(
                                                        new Date().getTime(),
                                                                3600000
                                                            )
                                                        )
                                                        ),
                                                        3600
                                                    )
                                            )
                                    )
                            )
                    ),
                    Projections.include("id"),
                    Projections.include("category")
            )
    ),
    Aggregates.project(
            Projections.fields(
                    Projections.computed("dates",
                            new Document("$map",
                                    new Document("input",
                                            new Document("$range",
                                Arrays.asList("$start", "$end", 3600)
                                            )
                                ).append("as", "i").append("in", "$$i")
                            )
                    ),
                    Projections.include("id"),
                    Projections.include("category")
            )

                ),
                Aggregates.unwind("$dates"),
                Aggregates.project(
                        Projections.fields(
                                Projections.computed("date",
                                    new Document("$toDate",
                                            new Document("$multiply",
                                                    Arrays.asList("$dates", 1000)
                                            )
                                )),
                                Projections.include("id"),
                                Projections.include("category")
                        )
                ),
                Aggregates.project(
                        Projections.fields(
Projections.computed("year", new Document("$year", "$date")),
Projections.computed("dayOfYear", new Document("$dayOfYear", "$date")),
Projections.computed("hour", new Document("$hour", "$date")),
Projections.include("id"),
Projections.include("category"
                        )
                )),
                Aggregates.match(
                        Filters.or(
Filters.ne("category", "Works"),
Filters.in("hour", Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18))
                    )
                ),
                Aggregates.group(
                        Projections.fields(
            Projections.computed("year", "$year"),
            Projections.computed("dayOfYear", "$dayOfYear"),
            Projections.computed("hour", "$hour")
                        ),
                        Accumulators.sum("count", 1L)
                ),
Aggregates.group("$_id.hour", Accumulators.avg("count","$count")),
Aggregates.sort(Sorts.ascending("_id"))
        ));

        //pipeline.forEach(bson -> System.out.println(bson.toBsonDocument().toJson()));

        return this.collection.aggregate(pipeline).into(new ArrayList<>());
    }

    public static void main(String[] argv) {
        var dao = new LineGraphDAO(new ConnectionMongoDB("mongodb://172.16.5.47"));

        dao.computeGraph().forEach(lineGraphEntry -> System.out.println(
                lineGraphEntry.hour + "\t" + lineGraphEntry.count)
        );
    }
}

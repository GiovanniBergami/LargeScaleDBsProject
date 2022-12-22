package londonSafeTravel.dbms.graph;

import londonSafeTravel.schema.graph.Point;
import londonSafeTravel.schema.graph.Way;
import org.neo4j.driver.*;

import static org.neo4j.driver.Values.parameters;

public class ManageWay {
    public static class InsertWayIntoGraph{
        private final Driver driver;

        public InsertWayIntoGraph(String uri, String user, String password) {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        }

        public void addWay(final Way w){
            try(Session session = driver.session()){
                session.writeTransaction(tx-> createWay(tx, w.getId()));    //potrebbero servire altri parametri (lista nodi?)
            }
        }

        public Void createWay(Transaction tx, long id){
            try( Session session=driver.session() ) {
                //Inserire la query per creare relationship
            }
            return null;
        }
    }
}

package londonSafeTravel.TestClient;
import londonSafeTravel.dbms.graph.ManageRouting;

import java.util.Scanner;

public class ClientTest {

    public void TestRouting(){
        System.out.println("Insert the ID of the starting point");
        Scanner scanner = new Scanner(System.in);
        long id1=scanner.nextLong();
        System.out.println("Insert the ID of the ending point");
        long id2=scanner.nextLong();
        ManageRouting test= new ManageRouting("neo4j://localhost:7687", "neo4j", "pass");
        test.route(id1,id2, "car").forEach(hop->{
            System.out.println("id "+hop);
        });
    }

    public void TestElementsInGivenArea(){

    }
}

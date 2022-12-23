package londonSafeTravel.schema.graph;

public class Way {
    private long id;
    private Point p1;
    private Point p2;
    private String name;
    private int maxSpeed;



    public Way(long id, Point p1, Point p2, String name, int maxSpeed) {
        this.id = id;
        this.p1 = p1;
        this.p2 = p2;
        this.name=name;
        this.maxSpeed=maxSpeed;
    }

    public long getId() {
        return id;
    }

    public Point getP1() {
        return p1;
    }

    public Point getP2() {
        return p2;
    }

    public String getName() {
        return name;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }
}

package londonSafeTravel.schema.graph;

public class Way {
    private long id;
    private long[] nodesId;

    public Way(long id, long[] nodesId) {
        this.id = id;
        this.nodesId = nodesId;
    }

    public long getId() {
        return id;
    }

    public long[] getNodesId() {
        return nodesId;
    }
}

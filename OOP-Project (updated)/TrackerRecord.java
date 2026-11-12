public abstract class TrackerRecord implements Summarizable {
    private final int id;

    protected TrackerRecord(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public abstract String getSummary();
}

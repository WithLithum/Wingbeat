package x.withlithum.wingbeat.util;

public class Counter {
    private int count;

    public Counter() {
        this.count = 0;
    }

    public Counter(int count) {
        this.count = count;
    }

    public int postIncrement() {
        return count++;
    }

    public int count() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}

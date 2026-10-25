/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        DisjointSet longChain = new DisjointSet(100000);
        for (int cell = 0; cell + 1 < 100000; cell++) longChain.union(cell, cell + 1);
        check(longChain.count() == 1, "long chain of 100000 cells is one set");
        check(longChain.connected(0, 99999), "ends of the long chain are connected");

        DisjointSet oneCellOnly = new DisjointSet(3);
        check(!oneCellOnly.union(2, 2), "union of a cell with itself returns false");
        check(oneCellOnly.count() == 3, "count does not change after union with itself");

        DisjointSet allJoined = new DisjointSet(10);
        for (int cell = 1; cell < 10; cell++) allJoined.union(0, cell);
        check(allJoined.count() == 1, "count is 1 after 9 unions of 10 cells");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
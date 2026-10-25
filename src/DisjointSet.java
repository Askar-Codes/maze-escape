/**
 * Mission 0. Disjoint set union (DSU).
 * Каждая клетка хранит ссылку на "родителя". Корень множества указывает сам на себя.
 */
public class DisjointSet {

    private final int[] parentOfCell;        // parentOfCell[x] = родитель клетки x (у корня - он сам)
    private final int[] sizeOfTreeWithRoot;  // sizeOfTreeWithRoot[root] = сколько клеток в дереве этого корня
    private int numberOfSets;                // сколько групп сейчас

    public DisjointSet(int numberOfCells) {
        parentOfCell = new int[numberOfCells];
        sizeOfTreeWithRoot = new int[numberOfCells];
        numberOfSets = numberOfCells;
        for (int cell = 0; cell < numberOfCells; cell++) {
            parentOfCell[cell] = cell;       // сначала каждая клетка - своя группа
            sizeOfTreeWithRoot[cell] = 1;
        }
    }

    /** The root of the set that contains x. Path compression: по дороге вешаем все клетки прямо на корень. */
    public int find(int x) {
        if (parentOfCell[x] != x) {
            parentOfCell[x] = find(parentOfCell[x]);
        }
        return parentOfCell[x];
    }

    /** Joins the sets of a and b. Returns true if they were different sets, false if they were already one set. */
    public boolean union(int a, int b) {
        int rootOfA = find(a);
        int rootOfB = find(b);
        if (rootOfA == rootOfB) {
            return false;                    // уже в одной группе
        }
        // union by size: маленькое дерево подвешиваем под большое
        if (sizeOfTreeWithRoot[rootOfA] < sizeOfTreeWithRoot[rootOfB]) {
            int temporary = rootOfA;
            rootOfA = rootOfB;
            rootOfB = temporary;
        }
        parentOfCell[rootOfB] = rootOfA;
        sizeOfTreeWithRoot[rootOfA] += sizeOfTreeWithRoot[rootOfB];
        numberOfSets--;
        return true;
    }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }

    /** Number of sets now. */
    public int count() {
        return numberOfSets;
    }
}
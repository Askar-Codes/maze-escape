import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k].
 */
public class Maze {

    /** Mission 1. Walls in the order of the file; break a wall if its two cells are in different groups. */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet groupsOfCells = new DisjointSet(w.cells());
        int[] brokenWallsInOrder = new int[w.cells() - 1];   // сломанных всегда ровно клеток - 1
        int howManyBrokenSoFar = 0;
        for (int wallIndex = 0; wallIndex < w.count(); wallIndex++) {
            boolean cellsWereInDifferentGroups = groupsOfCells.union(w.a()[wallIndex], w.b()[wallIndex]);
            if (cellsWereInDifferentGroups) {
                brokenWallsInOrder[howManyBrokenSoFar] = wallIndex;
                howManyBrokenSoFar++;
            }
        }
        return Arrays.copyOf(brokenWallsInOrder, howManyBrokenSoFar);
    }

    /** Mission 2. Kruskal: the same, but walls from the cheapest to the most expensive. */
    public static int[] cheapestMaze(Input.Walls w) {
        // список номеров стен, отсортированный по стоимости (от дешёвой к дорогой)
        Integer[] wallIndexesSortedByCost = new Integer[w.count()];
        for (int wallIndex = 0; wallIndex < w.count(); wallIndex++) {
            wallIndexesSortedByCost[wallIndex] = wallIndex;
        }
        Arrays.sort(wallIndexesSortedByCost, (first, second) -> Integer.compare(w.cost()[first], w.cost()[second]));

        DisjointSet groupsOfCells = new DisjointSet(w.cells());
        int[] brokenWalls = new int[w.cells() - 1];
        int howManyBrokenSoFar = 0;
        for (int wallIndex : wallIndexesSortedByCost) {
            boolean cellsWereInDifferentGroups = groupsOfCells.union(w.a()[wallIndex], w.b()[wallIndex]);
            if (cellsWereInDifferentGroups) {
                brokenWalls[howManyBrokenSoFar] = wallIndex;
                howManyBrokenSoFar++;
            }
        }
        return Arrays.copyOf(brokenWalls, howManyBrokenSoFar);
    }

    /** Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze. */
    public static long primCost(Input.Walls w) {
        int[][] wallsTouchingEachCell = listWallsTouchingEachCell(w);

        // очередь хранит номера стен; наверху всегда самая дешёвая стена
        PriorityQueue<Integer> wallsWaitingInQueue =
                new PriorityQueue<>((first, second) -> Integer.compare(w.cost()[first], w.cost()[second]));
        boolean[] cellIsAlreadyInMaze = new boolean[w.cells()];

        // начинаем с клетки 0
        cellIsAlreadyInMaze[0] = true;
        for (int wallIndex : wallsTouchingEachCell[0]) {
            wallsWaitingInQueue.add(wallIndex);
        }

        long totalCost = 0;
        while (!wallsWaitingInQueue.isEmpty()) {
            int cheapestWall = wallsWaitingInQueue.poll();
            int cellA = w.a()[cheapestWall];
            int cellB = w.b()[cheapestWall];
            if (cellIsAlreadyInMaze[cellA] && cellIsAlreadyInMaze[cellB]) {
                continue;                    // обе клетки уже в лабиринте - стена не нужна
            }
            int newCell = cellIsAlreadyInMaze[cellA] ? cellB : cellA;
            totalCost += w.cost()[cheapestWall];
            cellIsAlreadyInMaze[newCell] = true;
            for (int wallIndex : wallsTouchingEachCell[newCell]) {
                wallsWaitingInQueue.add(wallIndex);
            }
        }
        return totalCost;
    }

    /** result[cell] = номера всех стен, которые касаются этой клетки. */
    private static int[][] listWallsTouchingEachCell(Input.Walls w) {
        int[] howManyWallsTouchCell = new int[w.cells()];
        for (int wallIndex = 0; wallIndex < w.count(); wallIndex++) {
            howManyWallsTouchCell[w.a()[wallIndex]]++;
            howManyWallsTouchCell[w.b()[wallIndex]]++;
        }
        int[][] wallsTouchingEachCell = new int[w.cells()][];
        for (int cell = 0; cell < w.cells(); cell++) {
            wallsTouchingEachCell[cell] = new int[howManyWallsTouchCell[cell]];
        }
        int[] howManyAlreadyPlaced = new int[w.cells()];
        for (int wallIndex = 0; wallIndex < w.count(); wallIndex++) {
            int cellA = w.a()[wallIndex];
            int cellB = w.b()[wallIndex];
            wallsTouchingEachCell[cellA][howManyAlreadyPlaced[cellA]++] = wallIndex;
            wallsTouchingEachCell[cellB][howManyAlreadyPlaced[cellB]++] = wallIndex;
        }
        return wallsTouchingEachCell;
    }
}
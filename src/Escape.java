/**
 * Mission 3. Escape: DFS from cell 0 to the last cell in the maze of Mission 1.
 * DFS сделан без рекурсии: свой стек из массивов, поэтому на 1000x1000 переполнения нет.
 */
public class Escape {

    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int numberOfCells = w.cells();
        int numberOfColumns = w.cols();
        int startCell = 0;
        int exitCell = numberOfCells - 1;
        int[][] openNeighboursOfEachCell = Graphs.open(w, broken);

        // Dead ends: клетки ровно с одним открытым проходом
        int numberOfDeadEnds = 0;
        for (int cell = 0; cell < numberOfCells; cell++) {
            if (openNeighboursOfEachCell[cell].length == 1) {
                numberOfDeadEnds++;
            }
        }

        boolean[] cellWasVisited = new boolean[numberOfCells];
        int[] stackOfCells = new int[numberOfCells];          // путь от старта до текущей клетки
        int[] nextDirectionToTryForCellOnStack = new int[numberOfCells]; // 0=up, 1=right, 2=down, 3=left
        int stackSize = 0;
        int howManyCellsVisited = 0;

        stackOfCells[stackSize] = startCell;
        stackSize++;
        cellWasVisited[startCell] = true;
        howManyCellsVisited++;

        while (stackSize > 0 && stackOfCells[stackSize - 1] != exitCell) {
            int currentPosition = stackSize - 1;
            int currentCell = stackOfCells[currentPosition];
            int directionToTry = nextDirectionToTryForCellOnStack[currentPosition];

            if (directionToTry == 4) {
                stackSize--;                                  // все стороны проверены - отступаем назад
                continue;
            }
            nextDirectionToTryForCellOnStack[currentPosition]++;

            int neighbourCell = findNeighbourInDirection(currentCell, directionToTry, numberOfCells, numberOfColumns);
            if (neighbourCell == -1) continue;                                      // за краем лабиринта
            if (!contains(openNeighboursOfEachCell[currentCell], neighbourCell)) continue; // стена стоит
            if (cellWasVisited[neighbourCell]) continue;

            cellWasVisited[neighbourCell] = true;
            howManyCellsVisited++;
            stackOfCells[stackSize] = neighbourCell;
            nextDirectionToTryForCellOnStack[stackSize] = 0;
            stackSize++;
        }

        // На стеке сейчас лежит путь от старта до выхода
        int[] pathFromStartToExit = new int[stackSize];
        for (int i = 0; i < stackSize; i++) {
            pathFromStartToExit[i] = stackOfCells[i];
        }
        return new Result(pathFromStartToExit, howManyCellsVisited, numberOfDeadEnds);
    }

    /** Сосед клетки в стороне 0=up, 1=right, 2=down, 3=left; -1 если там край лабиринта. */
    private static int findNeighbourInDirection(int cell, int direction, int numberOfCells, int numberOfColumns) {
        int columnOfCell = cell % numberOfColumns;
        if (direction == 0) return cell - numberOfColumns >= 0 ? cell - numberOfColumns : -1;
        if (direction == 1) return columnOfCell + 1 < numberOfColumns ? cell + 1 : -1;
        if (direction == 2) return cell + numberOfColumns < numberOfCells ? cell + numberOfColumns : -1;
        return columnOfCell > 0 ? cell - 1 : -1;
    }

    private static boolean contains(int[] listOfCells, int searchedCell) {
        for (int cell : listOfCells) {
            if (cell == searchedCell) return true;
        }
        return false;
    }
}
/**
 * Missions 4 and 5. The slides: a directed graph.
 */
public class Zones {

    /** Mission 4. Kosaraju. zoneOf[cell] = номер зоны; зоны нумеруются в порядке нахождения во втором проходе. */
    public static int[] kosaraju(Input.Slides s) {
        int numberOfCells = s.n();
        int[][] slidesLeavingEachCell = Graphs.slidesFrom(s);
        int[][] slidesEnteringEachCell = Graphs.slidesInto(s);

        // ПРОХОД 1: обычный DFS по слайдам, записываем клетки в порядке "закончили обработку"
        int[] cellsInOrderOfFinishing = new int[numberOfCells];
        int howManyCellsFinished = 0;
        boolean[] cellWasVisitedInFirstPass = new boolean[numberOfCells];
        int[] stackOfCells = new int[numberOfCells];
        int[] nextSlideNumberToTryForCellOnStack = new int[numberOfCells];

        for (int startCell = 0; startCell < numberOfCells; startCell++) {
            if (cellWasVisitedInFirstPass[startCell]) continue;
            int stackSize = 0;
            stackOfCells[stackSize] = startCell;
            nextSlideNumberToTryForCellOnStack[stackSize] = 0;
            stackSize++;
            cellWasVisitedInFirstPass[startCell] = true;

            while (stackSize > 0) {
                int currentPosition = stackSize - 1;
                int currentCell = stackOfCells[currentPosition];
                int[] slidesFromCurrentCell = slidesLeavingEachCell[currentCell];

                if (nextSlideNumberToTryForCellOnStack[currentPosition] == slidesFromCurrentCell.length) {
                    cellsInOrderOfFinishing[howManyCellsFinished] = currentCell;   // клетка закончена
                    howManyCellsFinished++;
                    stackSize--;
                    continue;
                }
                int slideNumber = slidesFromCurrentCell[nextSlideNumberToTryForCellOnStack[currentPosition]];
                nextSlideNumberToTryForCellOnStack[currentPosition]++;
                int cellWhereSlideLeads = s.to()[slideNumber];
                if (!cellWasVisitedInFirstPass[cellWhereSlideLeads]) {
                    cellWasVisitedInFirstPass[cellWhereSlideLeads] = true;
                    stackOfCells[stackSize] = cellWhereSlideLeads;
                    nextSlideNumberToTryForCellOnStack[stackSize] = 0;
                    stackSize++;
                }
            }
        }

        // ПРОХОД 2: DFS по РАЗВЁРНУТЫМ слайдам, начиная с клетки, которая закончилась последней
        int[] zoneOfEachCell = new int[numberOfCells];
        for (int cell = 0; cell < numberOfCells; cell++) zoneOfEachCell[cell] = -1;   // -1 = зоны ещё нет
        int numberOfZonesFoundSoFar = 0;

        for (int position = numberOfCells - 1; position >= 0; position--) {
            int startCell = cellsInOrderOfFinishing[position];
            if (zoneOfEachCell[startCell] != -1) continue;

            int stackSize = 0;
            stackOfCells[stackSize] = startCell;
            stackSize++;
            zoneOfEachCell[startCell] = numberOfZonesFoundSoFar;
            while (stackSize > 0) {
                stackSize--;
                int currentCell = stackOfCells[stackSize];
                for (int slideNumber : slidesEnteringEachCell[currentCell]) {
                    int cellWhereSlideStarts = s.from()[slideNumber];
                    if (zoneOfEachCell[cellWhereSlideStarts] == -1) {
                        zoneOfEachCell[cellWhereSlideStarts] = numberOfZonesFoundSoFar;
                        stackOfCells[stackSize] = cellWhereSlideStarts;
                        stackSize++;
                    }
                }
            }
            numberOfZonesFoundSoFar++;       // эта зона найдена целиком, следующая получит следующий номер
        }
        return zoneOfEachCell;
    }

    /**
     * Mission 5. Идём по зонам в порядке их номеров (это топологический порядок).
     * [0] = flow stages (самая длинная цепочка зон), [1] = самый дешёвый путь от зоны старта до зоны выхода.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int startZone = zoneOf[0];
        int exitZone = zoneOf[s.n() - 1];
        long infinity = Long.MAX_VALUE / 4;

        // для каждой зоны: список слайдов, которые из неё выходят
        int[] howManySlidesLeaveZone = new int[zoneCount];
        for (int slideNumber = 0; slideNumber < s.count(); slideNumber++) {
            howManySlidesLeaveZone[zoneOf[s.from()[slideNumber]]]++;
        }
        int[][] slidesLeavingEachZone = new int[zoneCount][];
        for (int zone = 0; zone < zoneCount; zone++) {
            slidesLeavingEachZone[zone] = new int[howManySlidesLeaveZone[zone]];
        }
        int[] howManyAlreadyPlaced = new int[zoneCount];
        for (int slideNumber = 0; slideNumber < s.count(); slideNumber++) {
            int zoneWhereSlideStarts = zoneOf[s.from()[slideNumber]];
            slidesLeavingEachZone[zoneWhereSlideStarts][howManyAlreadyPlaced[zoneWhereSlideStarts]++] = slideNumber;
        }

        long[] longestChainEndingInZone = new long[zoneCount];  // сколько зон в самой длинной цепочке, заканчивающейся здесь
        long[] cheapestCostFromStartZoneToZone = new long[zoneCount];
        for (int zone = 0; zone < zoneCount; zone++) {
            longestChainEndingInZone[zone] = 1;
            cheapestCostFromStartZoneToZone[zone] = infinity;
        }
        cheapestCostFromStartZoneToZone[startZone] = 0;

        for (int zone = 0; zone < zoneCount; zone++) {           // порядок номеров зон
            for (int slideNumber : slidesLeavingEachZone[zone]) {
                int zoneWhereSlideLeads = zoneOf[s.to()[slideNumber]];
                if (zoneWhereSlideLeads == zone) continue;       // слайд внутри зоны - не считаем
                longestChainEndingInZone[zoneWhereSlideLeads] =
                        Math.max(longestChainEndingInZone[zoneWhereSlideLeads], longestChainEndingInZone[zone] + 1);
                if (cheapestCostFromStartZoneToZone[zone] < infinity) {
                    long costThroughThisSlide = cheapestCostFromStartZoneToZone[zone] + s.time()[slideNumber];
                    cheapestCostFromStartZoneToZone[zoneWhereSlideLeads] =
                            Math.min(cheapestCostFromStartZoneToZone[zoneWhereSlideLeads], costThroughThisSlide);
                }
            }
        }

        long flowStages = 0;
        for (int zone = 0; zone < zoneCount; zone++) {
            flowStages = Math.max(flowStages, longestChainEndingInZone[zone]);
        }
        return new long[]{flowStages, cheapestCostFromStartZoneToZone[exitZone]};
    }
}
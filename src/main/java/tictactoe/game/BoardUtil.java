package tictactoe.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BoardUtil {

    private BoardUtil() {}

    private static final int NUMBER_ROWS = 3;
    private static final int NUMBER_COLUMNS = 3;

    public static List<List<String>> createEmpty() {
        List<List<String>> rows = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < NUMBER_ROWS; rowIndex++) {
            List<String> row = new ArrayList<>();
            for (int columnIndex = 0; columnIndex < NUMBER_COLUMNS; columnIndex++) {
                row.add(BoardTile.EMPTY.toString());
            }
            rows.add(row);
        }

        return rows;
    }

    /**
     * There are 8 possible lines in tic tac toe, 3 horizontal, 3 vertical & 2 diagonal. Any of those 8 may win a game.
     * @param gameRows the rows that represent a game in progress. e.g.
     *         [
     *             ["x", "o", ""],
     *             ["x", "o", ""],
     *             ["", "", ""]
     *         ]
     * @return all possible lines of strings through the game board.
     */
    public static List<List<String>> getAllPossibleLines(List<List<String>> gameRows) {
        final List<List<String>> allPossibleLines = new ArrayList<>();

        allPossibleLines.addAll(gameRows);
        allPossibleLines.addAll(getAllColumns(gameRows));
        allPossibleLines.add(getDiagonal1(gameRows));
        allPossibleLines.add(getDiagonal2(gameRows));

        return allPossibleLines;
    }

    public static List<List<String>> getAllColumns(List<List<String>> gameRows) {
        final List<List<String>> allColumns = new ArrayList<>();

        for (int columnIndex = 0; columnIndex < NUMBER_COLUMNS; columnIndex++) {
            List<String> columnLine = new ArrayList<>();
            for (List<String> row : gameRows) {
                columnLine.add(row.get(columnIndex));
            }
            allColumns.add(columnLine);
        }

        return allColumns;
    }

    private static List<String> getDiagonal1(List<List<String>> gameRows) {
        return Arrays.asList(
                gameRows.get(0).get(0),
                gameRows.get(1).get(1),
                gameRows.get(2).get(2)
        );
    }

    private static List<String> getDiagonal2(List<List<String>> gameRows) {
        return Arrays.asList(
                gameRows.get(0).get(2),
                gameRows.get(1).get(1),
                gameRows.get(2).get(0)
        );
    }
}
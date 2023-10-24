package tictactoe.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BoardUtil {

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
    public static List<List<String>> getAllPossibleLines(List<List<String>> gameRows)
    {
        final List<List<String>> allPossibleLines = new ArrayList<>();

        // add all rows
        for (int rowIndex = 0; rowIndex < NUMBER_ROWS; rowIndex++)
            allPossibleLines.add(gameRows.get(rowIndex));

        // add all columns
        for (int columnIndex = 0; columnIndex < NUMBER_COLUMNS; columnIndex++) {
            List<String> columnLine = new ArrayList<>();
            for (List<String> row : gameRows)
                columnLine.add(row.get(columnIndex));
            allPossibleLines.add(columnLine);
        }

        // add all diagonals
        List<String> diagonal1 = Arrays.asList(gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2));
        allPossibleLines.add(diagonal1);

        List<String> diagonal2 = Arrays.asList(gameRows.get(0).get(2), gameRows.get(1).get(1), gameRows.get(2).get(0));
        allPossibleLines.add(diagonal2);

        return allPossibleLines;
    }
}

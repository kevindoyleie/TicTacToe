package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tictactoe.game.entity.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static tictactoe.game.BoardUtil.getAllColumns;

@Service
/*
 * The computer player who receives a {@link tictactoe.game.entity.Game} and makes a move.
 */
public class ComputerPlayerService {

    private final GameService gameService;
    Random random = new Random();

    @Autowired
    public ComputerPlayerService(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * For the given Game, try to make a play.
     *
     * @param game {@link Game} the game state, including who plays next.
     */
    public void takeTurn(Game game) {
        String tileId;

        // try to get winning turns
        tileId = getWinningTile(game);

        // try to get blocking turns
        if (tileId == null)
            tileId = getBlockingTile(game);

        // No winning or blocking turn available
        if (tileId == null)
            tileId = this.getRandomEmptyTile(game);

        if (tileId != null)
            this.gameService.takeTurn(game, tileId);
    }

    /**
     * For the game in progress, is there a blocking tile we should play to prevent the opponent winning?
     * @param game {@link Game} the game state, including who plays next.
     * @return nullable {@link String} in the format "{row index}-{column index}".
     */
    String getBlockingTile(Game game) {
        final List<String> available = getAvailableTiles(game);
        if (available.isEmpty())
            return null;
        
        final Game.PlayerNumber nextMove = game.getNextMove();
        if (nextMove != null) {
            final BoardTile boardTile = gameService.getPlayersBoardTile(nextMove);
            return findTileToBlock(game, boardTile);
        } else {
            return null;
        }
    }




    /**
     * For the game in progress, is there a winning tile we should play?
     * @param game {@link Game} the game state, including who plays next.
     * @return nullable {@link String} in the format "{row index}-{column index}".
     */
    String getWinningTile(Game game) {
        final List<String> available = getAvailableTiles(game);
        if (available.isEmpty())
            return null;

        final Game.PlayerNumber nextMove = game.getNextMove();
        if (nextMove != null) {
            final BoardTile boardTile = gameService.getPlayersBoardTile(nextMove);
            return findTileToWin(game, boardTile);
        } else {
            return null;
        }

    }

    /**
     * Just return any random empty tile.
     * @param game {@link Game} the game state, including who plays next.
     * @return nullable {@link String} in the format "{row index}-{column index}".
     */
    String getRandomEmptyTile(Game game) {
        final List<String> available = getAvailableTiles(game);

        if (available.isEmpty()) {
            return null;
        }

        int randomNum = this.random.nextInt(available.size());
        return available.get(randomNum);
    }

    private List<String> getAvailableTiles(Game game) {
        final List<List<String>> rows = game.getRows();
        List<String> available = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < rows.size(); columnIndex++) {
                String tileValue = row.get(columnIndex);
                if (tileValue.isEmpty())
                    available.add(rowIndex + "-" + columnIndex);
            }
        }
        return available;
    }

    private String findTileToWin(Game game, BoardTile boardTile)
    {
        final List<List<String>> gameRows = game.getRows();
        // add all rows
        String tileId = processAllRowsForPossibleWin(boardTile, gameRows);
        // add all columns
        if (tileId == null)
            tileId = processAllColumnsForPossibleWin(boardTile, gameRows);
        // add all diagonals
        if (tileId == null)
            tileId = processDiagonal1ForPossibleWin(boardTile, gameRows);
        if (tileId == null)
            tileId = processDiagonal2ForPossibleWin(boardTile, gameRows);
        return tileId;
    }

    private String processAllRowsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        for (int rowIndex = 0; rowIndex < gameRows.size(); rowIndex++) {
            List<String> row = gameRows.get(rowIndex);
            if (numberOfPlayerTilesInLine(boardTile, row) == 2) {
                for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                    String tile = row.get(colIndex);
                    if (tile.isEmpty()) {
                        tileId = rowIndex + "-" + colIndex;
                        break;
                    }
                }
            }
        }
        return tileId;
    }

    private String processAllColumnsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        final List<List<String>> allColumns = getAllColumns(gameRows);
        for (int columnIndex = 0; columnIndex < allColumns.size(); columnIndex++) {
            List<String> column = allColumns.get(columnIndex);
            if (numberOfPlayerTilesInLine(boardTile, column) == 2) {
                for (int rowIndex = 0; rowIndex < column.size(); rowIndex++) {
                    String tile = column.get(rowIndex);
                    if (tile.isEmpty()) {
                        tileId = rowIndex + "-" + columnIndex;
                        break;
                    }
                }
            }
        }
        return tileId;
    }

    private String processDiagonal1ForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        List<String> diagonal1 = Arrays.asList(gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2));
        if (numberOfPlayerTilesInLine(boardTile, diagonal1) ==2) {
            for (int i = 0; i < diagonal1.size(); i++) {
                String tile = diagonal1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-2";
                    break;
                }
            }
        }
        return tileId;
    }

    private String processDiagonal2ForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        List<String> diagonal2 = Arrays.asList(gameRows.get(0).get(2), gameRows.get(1).get(1), gameRows.get(2).get(0));
        if (numberOfPlayerTilesInLine(boardTile, diagonal2) ==2) {
            for (int i = 0; i < diagonal2.size(); i++) {
                String tile = diagonal2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-0";
                    break;
                }
            }
        }
        return tileId;
    }

    private String findTileToBlock(Game game, BoardTile boardTile)
    {
        final List<List<String>> gameRows = game.getRows();
        // rows
        String tileId = processAllRowsForPossibleBlock(boardTile, gameRows);
        // columns
        if (tileId == null)
            tileId = processAllColumnsForPossibleBlock(boardTile, gameRows);
        // diagonals
        if (tileId == null)
            tileId = processDiagonal1ForPossibleBlock(boardTile, gameRows);
        if (tileId == null)
            tileId = processDiagonal2ForPossibleBlock(boardTile, gameRows);
        return tileId;
    }

    private String processAllRowsForPossibleBlock(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        for (int rowIndex = 0; rowIndex < gameRows.size(); rowIndex++) {
            List<String> row = gameRows.get(rowIndex);
            final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, row) - numberOfEmptyTiles(row);
            if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTiles(row) == 1) {
                for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                    String tile = row.get(colIndex);
                    if (tile.isEmpty()) {
                        tileId = rowIndex + "-" + colIndex;
                        break;
                    }
                }
            }
        }
        return tileId;
    }

    private String processAllColumnsForPossibleBlock(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        final List<List<String>> allColumns = getAllColumns(gameRows);
        for (int columnIndex = 0; columnIndex < allColumns.size(); columnIndex++) {
            List<String> column = allColumns.get(columnIndex);
            final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, column) - numberOfEmptyTiles(column);
            if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTiles(column) == 1) {
                for (int rowIndex = 0; rowIndex < column.size(); rowIndex++) {
                    String tile = column.get(rowIndex);
                    if (tile.isEmpty()) {
                        tileId = rowIndex + "-" + columnIndex;
                        break;
                    }
                }
            }
        }
        return tileId;
    }

    private String processDiagonal1ForPossibleBlock(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        List<String> diagonal1 = Arrays.asList(gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2));
        final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, diagonal1) - numberOfEmptyTiles(diagonal1);
        if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTiles(diagonal1) == 1) {
            for (int i = 0; i < diagonal1.size(); i++) {
                String tile = diagonal1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-2";
                    break;
                }
            }
        }
        return tileId;
    }

    private String processDiagonal2ForPossibleBlock(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        List<String> diagonal2 = Arrays.asList(gameRows.get(0).get(2), gameRows.get(1).get(1), gameRows.get(2).get(0));
        final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, diagonal2) - numberOfEmptyTiles(diagonal2);
        if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTiles(diagonal2) == 1) {
            for (int i = 0; i < diagonal2.size(); i++) {
                String tile = diagonal2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-0";
                    break;
                }
            }
        }
        return tileId;
    }

    private long numberOfEmptyTiles(List<String> line) {
        return line.stream().filter(String::isEmpty).count();
    }
    private long numberOfOppositionTiles(BoardTile boardTile, List<String> line) {
        return line.stream().filter(tile -> !(tile.equals(boardTile.toString()))).count();
    }

    private long numberOfPlayerTilesInLine(BoardTile boardTile, List<String> line) {
        return line.stream().filter(tile -> (tile.equals(boardTile.toString()))).count();
    }

    /**
     * Helper method for unit test
     */
    public void takeTurnForComputer(Game game, String tileId) {
        this.gameService.takeTurn(game, tileId);
    }
}

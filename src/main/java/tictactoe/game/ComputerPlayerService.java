package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tictactoe.game.entity.Game;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Optional<String> tileId;

        // try to get winning turns
        tileId = getWinningTile(game);

        // try to get blocking turns
        if (tileId.isEmpty())
            tileId = getBlockingTile(game);

        // No winning or blocking turn available
        if (tileId.isEmpty())
            tileId = getRandomEmptyTile(game);

        tileId.ifPresent(id -> this.gameService.takeTurn(game, id));
    }

    /**
     * For the game in progress, is there a blocking tile we should play to prevent the opponent winning?
     *
     * @param game {@link Game} the game state, including who plays next.
     * @return a {@link Optional} of {@link String} in the format "{row index}-{column index}".
     */
    Optional<String> getBlockingTile(Game game) {
        final List<String> available = getAvailableTiles(game);
        if (available.isEmpty())
            return Optional.empty();

        final Game.PlayerNumber nextPlayer = game.getNextMove();
        if (nextPlayer != null) {
            final BoardTile boardTile = getPlayerBoardTile(nextPlayer);
            return Optional.ofNullable(findTileToBlock(game, boardTile));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Get the Board Tile for the specified Player.
     *
     * @param player {@link Game.PlayerNumber} the player.
     * @return {@link BoardTile} for the player.
     */
    private BoardTile getPlayerBoardTile(Game.PlayerNumber player) {
        return gameService.getPlayersBoardTile(player);
    }


    /**
     * For the game in progress, is there a winning tile we should play?
     *
     * @param game {@link Game} the game state, including who plays next.
     * @return an Optional of {@link String} in the format "{row index}-{column index}".
     */
    Optional<String> getWinningTile(Game game) {
        final List<String> availableTiles = getAvailableTiles(game);
        if (availableTiles.isEmpty()) {
            return Optional.empty();
        }
        final Optional<Game.PlayerNumber> nextPlayer = Optional.ofNullable(game.getNextMove());
        return nextPlayer.map(gameService::getPlayersBoardTile)
                .map(boardTile -> findTileToWin(game, boardTile));
    }

    /**
     * Just return any random empty tile.
     *
     * @param game {@link Game} the game state, including who plays next.
     * @return Optional {@link String} in the format "{row index}-{column index}".
     */
    Optional<String> getRandomEmptyTile(Game game) {
        final List<String> available = getAvailableTiles(game);
        if (available.isEmpty()) {
            return Optional.empty();
        }
        int randomNum = this.random.nextInt(available.size());
        return Optional.of(available.get(randomNum));
    }

    private List<String> getAvailableTiles(Game game) {
        final List<List<String>> rows = game.getRows();
        List<String> availableTiles = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < rows.size(); columnIndex++) {
                String tileValue = row.get(columnIndex);
                if (tileValue.isEmpty()) {
                    availableTiles.add(generateTile(rowIndex, columnIndex));
                }
            }
        }
        return availableTiles;
    }

    private String generateTile(int rowIndex, int columnIndex) {
        return rowIndex + "-" + columnIndex;
    }


    private String findTileToWin(Game game, BoardTile boardTile) {
        final List<List<String>> gameRows = game.getRows();
        // add all rows
        Optional<String> tileId = processRowsForPossibleWin(boardTile, gameRows);
        // add all columns
        tileId = tileId.or(() -> processColumnsForPossibleWin(boardTile, gameRows));
        // add all diagonals
        tileId = tileId.or(() -> processDiagonal1ForWin(boardTile, gameRows));
        tileId = tileId.or(() -> processDiagonal2ForWin(boardTile, gameRows));
        return tileId.orElse(null);
    }

    // Change the return type of the subsequent methods to Optional<String>
    private Optional<String> processRowsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId =  processAllRowsForPossibleWin(boardTile, gameRows);
        return Optional.ofNullable(tileId);
    }

    private Optional<String> processColumnsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = processAllColumnsForPossibleWin(boardTile, gameRows);
        return Optional.ofNullable(tileId);
    }

    private Optional<String> processDiagonal1ForWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = processDiagonal1ForPossibleWin(boardTile, gameRows);
        return Optional.ofNullable(tileId);
    }

    private Optional<String> processDiagonal2ForWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = processDiagonal2ForPossibleWin(boardTile, gameRows);
        return Optional.ofNullable(tileId);
    }

    private List<String> getColumn(List<List<String>> gameRows, int columnIndex) {
        List<String> column = new ArrayList<>();
        for (List<String> row : gameRows) {
            column.add(row.get(columnIndex));
        }
        return column;
    }

    private boolean checkWinCondition(List<String> line, BoardTile boardTile) {
        // Check win condition logic here
        if (numberOfPlayerTilesInLine(boardTile, line) == 2) {
            for (String tile : line) {
                if (tile.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }


    private List<String> findEmptyTileInLine(List<String> line) {
        //find an empty tile in given line
        return line.stream()
                .filter(String::isEmpty)
                .collect(Collectors.toList());
    }

    private String processAllRowsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        for (int rowIndex = 0; rowIndex < gameRows.size() && tileId == null; rowIndex++) {
            List<String> row = gameRows.get(rowIndex);
            List<String> emptyTiles = findEmptyTileInLine(row);
            boolean hasTwoPlayerTiles = numberOfPlayerTilesInLine(boardTile, row) == 2;
            if (hasTwoPlayerTiles && !emptyTiles.isEmpty()) {
                int columnIndex = row.indexOf(emptyTiles.get(0));
                tileId = rowIndex + "-" + columnIndex;
            }
        }
        return tileId;
    }

    private String processAllColumnsForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        final List<List<String>> allColumns = getAllColumns(gameRows);
        for (int columnIndex = 0; columnIndex < allColumns.size(); columnIndex++) {
            List<String> column = allColumns.get(columnIndex);
            if (numberOfPlayerTilesInLine(boardTile, column) == 2) {
                String tileId = findEmptyTileIdInColumn(column, columnIndex);
                if (tileId != null) {
                    return tileId;
                }
            }
        }
        return null;
    }

    private String findEmptyTileIdInColumn(List<String> column, int columnIndex) {
        for (int rowIndex = 0; rowIndex < column.size(); rowIndex++) {
            String tile = column.get(rowIndex);
            if (tile.isEmpty()) {
                return convertToTileId(rowIndex, columnIndex);
            }
        }
        return null;
    }

    private String convertToTileId(int rowIndex, int columnIndex) {
        return rowIndex + "-" + columnIndex;
    }

    private String processDiagonal1ForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        String[] diagonal1 = {gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2)};
        if (numberOfPlayerTilesInLine(boardTile, diagonal1) == 2) {
            for (int i = 0; i < diagonal1.length; i++) {
                if (diagonal1[i].isEmpty()) {
                    tileId = getTileId(i);
                    break;
                }
            }
        }
        return tileId;
    }

    private String getTileId(int index) {
        return index + "-" + index;
    }

    private static final Map<Integer, String> INDEX_TO_TILE_ID_MAP = Map.of(0, "0-2", 1, "1-1", 2, "2-0");

    private String processDiagonal2ForPossibleWin(BoardTile boardTile, List<List<String>> gameRows) {
        String[] diagonal2 = IntStream.rangeClosed(0, 2).mapToObj(i -> gameRows.get(i).get(2 - i)).toArray(String[]::new);

        if (numberOfPlayerTilesInLine(boardTile, diagonal2) == 2) {
            for (int i = 0; i < diagonal2.length; i++) {
                if (diagonal2[i].isEmpty()) {
                    return INDEX_TO_TILE_ID_MAP.get(i);
                }
            }
        }

        return null;
    }

    private long numberOfPlayerTilesInLine(BoardTile boardTile, String[] line) {
        return (int) Arrays.stream(line).filter(tile -> (tile.equals(boardTile.toString()))).count();
    }

    private String findTileToBlock(Game game, BoardTile boardTile)
    {
        final List<List<String>> gameRows = game.getRows();
        if (gameRows != null) {
            // rows
            String tileId = processAllRowsForPossibleBlock(boardTile, gameRows);
            // columns
            if (tileId == null) {
                tileId = processAllColumnsForPossibleBlock(boardTile, gameRows);
            }
            // diagonals
            if (tileId == null) {
                tileId = processDiagonal1ForPossibleBlock(boardTile, gameRows);
            }
            if (tileId == null) {
                tileId = processDiagonal2ForPossibleBlock(boardTile, gameRows);
            }
            return tileId;
        }
        return null;
    }

    private String processAllRowsForPossibleBlock(BoardTile boardTile, List<List<String>> gameRows) {
        String tileId = null;
        for (int rowIndex = 0; rowIndex < gameRows.size(); rowIndex++) {
            List<String> row = gameRows.get(rowIndex);
            int numberOfEmptyTilesInRow = (int) numberOfEmptyTiles(row); // Calculate the number of empty tiles once
            final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, row) - numberOfEmptyTilesInRow;
            if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTilesInRow == 1) {
                for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                    String tile = row.get(colIndex);
                    if ("".equals(tile)) { // Use equals() to check for empty strings
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
            final long numberOfEmptyTiles = numberOfEmptyTiles(column);
            final long numberOfOppositionTilesInLine = numberOfOppositionTiles(boardTile, column) - numberOfEmptyTiles;
            if (numberOfOppositionTilesInLine == 2 && numberOfEmptyTiles == 1) {
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

package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tictactoe.game.entity.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
/**
 * The computer player who receives a {@link tictactoe.game.entity.Game} and makes a move.
 */
public class ComputerPlayerService {

    private final GameService gameService;

    @Autowired
    public ComputerPlayerService(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * For the given Game, try to make a play.
     *
     * @param game {@link Game} the game state, including who plays next.
     * @return
     */
    public void takeTurn(Game game) {
        String tileId;

        // todo - try to get winning turns
        tileId = getWinningTile(game);

        // todo - try to get blocking turns
        if (tileId == null)
            tileId = getBlockingTile(game);

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
        // todo - try to get blocking turns
        final List<String> available = getAvailableTiles(game);
        if (available.isEmpty())
            return null;

        // todo check for a possible winning tile for opposing player and block it
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
        // todo - try to get winning turns
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

        int randomNum = new Random().nextInt(available.size());
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

    private String findTileToWin(Game game, BoardTile boardTile) {
        String tileId = null;

        final List<List<String>> gameRows = game.getRows();
        // add all rows
        List<String> row1 = gameRows.get(0);
        if (numberOfPlayerTiles(boardTile, row1) == 2) {
            for (int i = 0; i < row1.size(); i++) {
                String tile = row1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "0-1";
                    else if (i == 2)
                        tileId = "0-2";
                }
            }
        }
        List<String> row2 = gameRows.get(1);
        if (numberOfPlayerTiles(boardTile, row2) == 2) {
            for (int i = 0; i < row2.size(); i++) {
                String tile = row2.get(i);
                if (tile.isEmpty())
                    if (i == 0)
                        tileId = "1-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "1-2";
            }
        }
        List<String> row3 = gameRows.get(2);
        if (numberOfPlayerTiles(boardTile, row3) == 2) {
            for (int i = 0; i < row3.size(); i++) {
                String tile = row3.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "2-0";
                    else if (i == 1)
                        tileId = "2-1";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }

        // add all columns
        List<String> column1 = Arrays.asList(row1.get(0), row2.get(0), row3.get(0));
        if (numberOfPlayerTiles(boardTile, column1) == 2) {
            for (int i = 0; i < column1.size(); i++) {
                String tile = column1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-0";
                    else if (i == 2)
                        tileId = "2-0";
                }
            }
        }
        List<String> column2 = Arrays.asList(row1.get(1), row2.get(1), row3.get(1));
        if (numberOfPlayerTiles(boardTile, column2) == 2) {
            for (int i = 0; i < column2.size(); i++) {
                String tile = column2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-1";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-1";
                }
            }
        }
        List<String> column3 = Arrays.asList(row1.get(2), row2.get(2), row3.get(2));
        if (numberOfPlayerTiles(boardTile, column3) == 2) {
            for (int i = 0; i < column3.size(); i++) {
                String tile = column3.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "1-2";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }
        // add all diagonals
        List<String> diagonal1 = Arrays.asList(row1.get(0), row2.get(1), row3.get(2));
        if (numberOfPlayerTiles(boardTile, diagonal1) == 2) {
            for (int i = 0; i < diagonal1.size(); i++) {
                String tile = diagonal1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }
        List<String> diagonal2 = Arrays.asList(row1.get(2), row2.get(1), row3.get(0));
        if (numberOfPlayerTiles(boardTile, diagonal2) == 2) {
            for (int i = 0; i < diagonal2.size(); i++) {
                String tile = diagonal2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-0";
                }
            }
        }

        return tileId;
    }

    private String findTileToBlock(Game game, BoardTile boardTile) {

        // TODO analyse all lines for possible winning moves
        String tileId = null;
        final List<List<String>> gameRows = game.getRows();
        // rows
        List<String> row1 = gameRows.get(0);
        final long numberOfOppositionTilesInRow1 = numberOfOppositionTiles(boardTile, row1) - numberOfEmptyTiles(row1);
        if (numberOfOppositionTilesInRow1 == 2 && (numberOfEmptyTiles(row1) == 1)) {
            for (int i = 0; i < row1.size(); i++) {
                String tile = row1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-0";
                    else if (i == 2)
                        tileId = "2-0";
                }
            }

        }
        List<String> row2 = gameRows.get(1);
        final long numberOfOppositionTilesInRow2 = numberOfOppositionTiles(boardTile, row2) - numberOfEmptyTiles(row2);
        if (numberOfOppositionTilesInRow2 == 2 && (numberOfEmptyTiles(row2) == 1)) {
            for (int i = 0; i < row2.size(); i++) {
                String tile = row2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "1-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "1-2";
                }
            }
        }
        List<String> row3 = gameRows.get(2);
        final long numberOfOppositionTilesInRow3 = numberOfOppositionTiles(boardTile, row3) - numberOfEmptyTiles(row3);
        if (numberOfOppositionTilesInRow3 == 2 && (numberOfEmptyTiles(row3) == 1)) {
            for (int i = 0; i < row3.size(); i++) {
                String tile = row3.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "2-1";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }
        // columns
        List<String> column1 = Arrays.asList(row1.get(0), row2.get(0), row3.get(0));
        final long numberOfOppositionTilesInCol1 = numberOfOppositionTiles(boardTile, column1) - numberOfEmptyTiles(column1);
        if (numberOfOppositionTilesInCol1 == 2 && numberOfEmptyTiles(column1) == 1) {
            for (int i = 0; i < column1.size(); i++) {
                String tile = column1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "0-1";
                    else if (i == 2)
                        tileId = "0-2";
                }
            }
        }
        List<String> column2 = Arrays.asList(row1.get(1), row2.get(1), row3.get(1));
        final long numberOfOppositionTilesInCol2 = numberOfOppositionTiles(boardTile, column2) - numberOfEmptyTiles(column2);
        if (numberOfOppositionTilesInCol2 == 2 && numberOfEmptyTiles(column2) == 1) {
            for (int i = 0; i < column2.size(); i++) {
                String tile = column2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "1-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-1";
                }
            }
        }
        List<String> column3 = Arrays.asList(row1.get(2), row2.get(2), row3.get(2));
        final long numberOfOppositionTilesInCol3 = numberOfOppositionTiles(boardTile, column3) - numberOfEmptyTiles(column3);
        if (numberOfOppositionTilesInCol3 == 2 && numberOfEmptyTiles(column3) == 1) {
            for (int i = 0; i < column3.size(); i++) {
                String tile = column3.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "2-0";
                    else if (i == 1)
                        tileId = "2-1";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }
        // add all diagonals
        List<String> diagonal1 = Arrays.asList(gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2));
        final long numberOfOppositionTilesInDiagonal1 = numberOfOppositionTiles(boardTile, diagonal1) - numberOfEmptyTiles(diagonal1);
        if (numberOfOppositionTilesInDiagonal1 == 2 && numberOfEmptyTiles(diagonal1) == 1) {
            for (int i = 0; i < diagonal1.size(); i++) {
                String tile = diagonal1.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-0";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-2";
                }
            }
        }
        List<String> diagonal2 = Arrays.asList(gameRows.get(0).get(2), gameRows.get(1).get(1), gameRows.get(2).get(0));
        final long numberOfOppositionTilesInDiagonal2 = numberOfOppositionTiles(boardTile, diagonal2) - numberOfEmptyTiles(diagonal2);
        if (numberOfOppositionTilesInDiagonal2 == 2 && numberOfEmptyTiles(diagonal2) == 1) {
            for (int i = 0; i < diagonal2.size(); i++) {
                String tile = diagonal2.get(i);
                if (tile.isEmpty()) {
                    if (i == 0)
                        tileId = "0-2";
                    else if (i == 1)
                        tileId = "1-1";
                    else if (i == 2)
                        tileId = "2-0";
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

    private long numberOfPlayerTiles(BoardTile boardTile, List<String> line) {
        return line.stream().filter(tile -> (tile.equals(boardTile.toString()))).count();
    }

    /**
     * Helper method for unit test
     */
    public void takeTurn(Game game, String tileId) {
        this.gameService.takeTurn(game, tileId);
    }
}

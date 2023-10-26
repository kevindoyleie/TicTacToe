package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tictactoe.game.entity.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    private String findTileToWin(Game game, BoardTile boardTile) {
        final List<List<String>> gameRows = game.getRows();
        // rows
        List<String> row1 = gameRows.get(0);
        List<String> row2 = gameRows.get(1);
        List<String> row3 = gameRows.get(2);
        // columns
        List<String> column1 = Arrays.asList(row1.get(0), row2.get(0), row3.get(0));
        List<String> column2 = Arrays.asList(row1.get(1), row2.get(1), row3.get(1));
        List<String> column3 = Arrays.asList(row1.get(2), row2.get(2), row3.get(2));
        // add all diagonals
        List<String> diagonal1 = Arrays.asList(row1.get(0), row2.get(1), row3.get(2));
        List<String> diagonal2 = Arrays.asList(row1.get(2), row2.get(1), row3.get(0));
        
        String tileId = processRow1ForWinningMove(boardTile, row1);
        if (tileId == null)
            tileId = processRow2ForWinningMove(boardTile, row2);
        if (tileId == null)
            tileId = processRow3ForWinningMove(boardTile, row3);
        if (tileId == null)
            tileId = processColumn1ForWinningMove(boardTile, column1);
        if (tileId == null)
            tileId = processColumn2ForWinningMove(boardTile, column2);
        if (tileId == null)
            tileId = processColumn3ForWinningMove(boardTile, column3);
        if (tileId == null)
            tileId = processDiagonal1ForWinningMove(boardTile, diagonal1);
        if (tileId == null)
            tileId = processDiagonal2ForWinningMove(boardTile, diagonal2);
        return tileId;
    }

    private String processRow1ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_2;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_3;
        }
        return tileId;
    }

    private String processRow2ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_4;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_6;
        }
        return tileId;
    }

    private String processRow3ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_7;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_8;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processColumn1ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_4;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_7;
        }
        return tileId;
    }

    private String processColumn2ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_2;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_8;
        }
        return tileId;
    }

    private String processColumn3ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_3;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_6;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processDiagonal1ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processDiagonal2ForWinningMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasWinningMove(boardTile, line);
        if (gameData.isPossibleWinner()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_3;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_7;
        }
        return tileId;
    }

    private GameData lineHasWinningMove(BoardTile boardTile, List<String> line) {
        GameData gameData = new GameData();
        if (numberOfPlayerTilesInLine(boardTile, line) == 2) {
            for (int i = 0; i < line.size(); i++) {
                String tile = line.get(i);
                if (tile.isEmpty()) {
                    gameData.setPossibleWinner(true);
                    gameData.setIndex(i);
                    break;
                }
            }
        }
        return gameData;
    }

    private String findTileToBlock(Game game, BoardTile boardTile)
    {
        final List<List<String>> gameRows = game.getRows();
        // rows
        List<String> row1 = gameRows.get(0);
        List<String> row2 = gameRows.get(1);
        List<String> row3 = gameRows.get(2);
        // columns
        List<String> column1 = Arrays.asList(row1.get(0), row2.get(0), row3.get(0));
        List<String> column2 = Arrays.asList(row1.get(1), row2.get(1), row3.get(1));
        List<String> column3 = Arrays.asList(row1.get(2), row2.get(2), row3.get(2));
        // diagonals
        List<String> diagonal1 = Arrays.asList(gameRows.get(0).get(0), gameRows.get(1).get(1), gameRows.get(2).get(2));
        List<String> diagonal2 = Arrays.asList(gameRows.get(0).get(2), gameRows.get(1).get(1), gameRows.get(2).get(0));

        String tileId = processRow1ForBlockingMove(boardTile, row1);
        if (tileId == null)
            tileId = processRow2ForBlockingMove(boardTile, row2);
        if (tileId == null)
            tileId = processRow3ForBlockingMove(boardTile, row3);
        if (tileId == null)
            tileId = processColumn1ForBlockingMove(boardTile, column1);
        if (tileId == null)
            tileId = processColumn2ForBlockingMove(boardTile, column2);
        if (tileId == null)
            tileId = processColumn3ForBlockingMove(boardTile, column3);
        if (tileId == null)
            tileId = processDiagonal1ForBlockingMove(boardTile, diagonal1);
        if (tileId == null)
            tileId = processDiagonal2ForBlockingMove(boardTile, diagonal2);
        return tileId;

    }

    private String processRow1ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_2;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_3;
        }
        return tileId;
    }

    private String processRow2ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_4;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_6;
        }
        return tileId;
    }

    private String processRow3ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_7;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_8;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processColumn1ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_4;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_7;
        }
        return tileId;
    }

    private String processColumn2ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_2;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_8;
        }
        return tileId;
    }

    private String processColumn3ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_3;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_6;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processDiagonal1ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_1;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_9;
        }
        return tileId;
    }

    private String processDiagonal2ForBlockingMove(BoardTile boardTile, List<String> line) {
        String tileId = null;
        GameData gameData = lineHasBlockingMove(boardTile, line);
        if (gameData.isPossibleBlocker()) {
            if (gameData.getIndex() == 0)
                tileId = TileConstants.TILE_3;
            else if (gameData.getIndex() == 1)
                tileId = TileConstants.TILE_5;
            else if (gameData.getIndex() == 2)
                tileId = TileConstants.TILE_7;
        }
        return tileId;
    }

    private GameData lineHasBlockingMove(BoardTile boardTile, List<String> line) {
        GameData gameData = new GameData();
        final long numberOfOppositionTilesInDiagonal2 = numberOfOppositionTiles(boardTile, line) - numberOfEmptyTiles(line);
        if (numberOfOppositionTilesInDiagonal2 == 2 && numberOfEmptyTiles(line) == 1) {
            for (int i = 0; i < line.size(); i++) {
                String tile = line.get(i);
                if (tile.isEmpty()) {
                    gameData.setPossibleBlocker(true);
                    gameData.setIndex(i);
                    break;
                }
            }
        }
        return gameData;
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

    private static class GameData {
        private boolean possibleWinner;
        private boolean possibleBlocker;
        private int index;

        public GameData() {
            this.possibleWinner = false;
            this.possibleBlocker = false;
        }

        public boolean isPossibleWinner() {
            return possibleWinner;
        }

        public void setPossibleWinner(boolean possibleWinner) {
            this.possibleWinner = possibleWinner;
        }

        public boolean isPossibleBlocker() {
            return possibleBlocker;
        }

        public void setPossibleBlocker(boolean possibleBlocker) {
            this.possibleBlocker = possibleBlocker;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}

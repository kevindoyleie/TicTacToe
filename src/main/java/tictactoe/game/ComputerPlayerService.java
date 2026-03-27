package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tictactoe.game.entity.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
/*
 * The computer player who receives a {@link tictactoe.game.entity.Game} and makes a move.
 */
public class ComputerPlayerService {

    private static final int BOARD_SIZE = 3;
    private static final String CENTER_TILE = "1-1";
    private static final List<String> CORNER_TILES = List.of("0-0", "0-2", "2-0", "2-2");

    private final GameService gameService;
    private final Random random = new Random();

    @Autowired
    public ComputerPlayerService(final GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * For the given Game, try to make a play.
     *
     * Priority:
     * 1. win if possible
     * 2. block the opponent if needed
     * 3. take center if available
     * 4. create a fork if possible
     * 5. block opponent forks
     * 6. take an opposite corner if available
     * 7. take any corner if available
     * 8. make a random valid move
     *
     * @param game {@link Game} the game state, including who plays next.
     */
    public void takeTurn(Game game) {
        Optional<String> tileId = getWinningTile(game)
                .or(() -> getBlockingTile(game))
                .or(() -> getPreferredTile(game))
                .or(() -> getForkTile(game))
                .or(() -> getBlockingForkTile(game))
                .or(() -> getOppositeCornerTile(game))
                .or(() -> getCornerTile(game))
                .or(() -> getRandomEmptyTile(game));

        tileId.ifPresent(id -> gameService.takeTurn(game, id));
    }

    Optional<String> getBlockingTile(Game game) {
        Game.PlayerNumber nextPlayer = game.getNextMove();
        if (nextPlayer == null) {
            return Optional.empty();
        }

        BoardTile opponentTile = getOpponentTile(gameService.getPlayersBoardTile(nextPlayer));
        return findCriticalMove(game.getRows(), opponentTile);
    }

    Optional<String> getWinningTile(Game game) {
        Game.PlayerNumber nextPlayer = game.getNextMove();
        if (nextPlayer == null) {
            return Optional.empty();
        }

        BoardTile boardTile = gameService.getPlayersBoardTile(nextPlayer);
        return findCriticalMove(game.getRows(), boardTile);
    }

    Optional<String> getPreferredTile(Game game) {
        return isTileAvailable(game, CENTER_TILE) ? Optional.of(CENTER_TILE) : Optional.empty();
    }

    Optional<String> getForkTile(Game game) {
        Game.PlayerNumber nextPlayer = game.getNextMove();
        if (nextPlayer == null) {
            return Optional.empty();
        }

        BoardTile boardTile = gameService.getPlayersBoardTile(nextPlayer);
        return findForkMove(game.getRows(), boardTile);
    }

    Optional<String> getBlockingForkTile(Game game) {
        Game.PlayerNumber nextPlayer = game.getNextMove();
        if (nextPlayer == null) {
            return Optional.empty();
        }

        BoardTile opponentTile = getOpponentTile(gameService.getPlayersBoardTile(nextPlayer));
        return findForkBlockMove(game.getRows(), opponentTile);
    }

    Optional<String> getOppositeCornerTile(Game game) {
        for (String cornerTile : CORNER_TILES) {
            String oppositeCorner = getOppositeCorner(cornerTile);
            if (isTileAvailable(game, oppositeCorner) && isCornerOwnedByOpponent(game, cornerTile)) {
                return Optional.of(oppositeCorner);
            }
        }
        return Optional.empty();
    }

    Optional<String> getCornerTile(Game game) {
        for (String tileId : CORNER_TILES) {
            if (isTileAvailable(game, tileId)) {
                return Optional.of(tileId);
            }
        }
        return Optional.empty();
    }

    Optional<String> getRandomEmptyTile(Game game) {
        List<String> available = getAvailableTiles(game);
        if (available.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(available.get(random.nextInt(available.size())));
    }

    private Optional<String> findForkMove(List<List<String>> rows, BoardTile tileToMatch) {
        for (String tileId : getAvailableTiles(rows)) {
            if (createsFork(rows, tileId, tileToMatch)) {
                return Optional.of(tileId);
            }
        }
        return Optional.empty();
    }

    private Optional<String> findForkBlockMove(List<List<String>> rows, BoardTile opponentTile) {
        List<String> forkMoves = new ArrayList<>();
        List<String> availableTiles = getAvailableTiles(rows);

        for (String tileId : availableTiles) {
            if (createsFork(rows, tileId, opponentTile)) {
                forkMoves.add(tileId);
            }
        }

        if (forkMoves.isEmpty()) {
            return Optional.empty();
        }

        if (forkMoves.size() == 1) {
            return Optional.of(forkMoves.get(0));
        }

        for (String tileId : availableTiles) {
            if (!forkMoves.contains(tileId)) {
                return Optional.of(tileId);
            }
        }

        return Optional.empty();
    }

    private boolean createsFork(List<List<String>> rows, String tileId, BoardTile tileToMatch) {
        List<List<String>> simulatedRows = copyRows(rows);
        applyMove(simulatedRows, tileId, tileToMatch.toString());

        int threats = 0;
        for (Line line : getLines(simulatedRows)) {
            if (countMatchingTiles(line.tiles, tileToMatch.toString()) == 2
                    && countEmptyTiles(line.tiles) == 1) {
                threats++;
            }
        }

        return threats >= 2;
    }

    private List<List<String>> copyRows(List<List<String>> rows) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> row : rows) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

    private void applyMove(List<List<String>> rows, String tileId, String value) {
        String[] indices = tileId.split("-");
        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);
        rows.get(rowIndex).set(columnIndex, value);
    }

    private int countMatchingTiles(List<String> line, String value) {
        int count = 0;
        for (String tile : line) {
            if (tile.equals(value)) {
                count++;
            }
        }
        return count;
    }

    private int countEmptyTiles(List<String> line) {
        int count = 0;
        for (String tile : line) {
            if (tile.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private String getOppositeCorner(String tileId) {
        switch (tileId) {
            case "0-0": return "2-2";
            case "0-2": return "2-0";
            case "2-0": return "0-2";
            case "2-2": return "0-0";
            default: throw new IllegalArgumentException("Not a corner tile: " + tileId);
        }
    }

    private boolean isCornerOwnedByOpponent(Game game, String tileId) {
        String[] indices = tileId.split("-");
        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);

        return !game.getRows().get(rowIndex).get(columnIndex).isEmpty()
                && CORNER_TILES.contains(tileId);
    }

    private boolean isTileAvailable(Game game, String tileId) {
        String[] indices = tileId.split("-");
        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);

        return game.getRows().get(rowIndex).get(columnIndex).isEmpty();
    }

    private BoardTile getOpponentTile(BoardTile boardTile) {
        return boardTile == BoardTile.X ? BoardTile.O : BoardTile.X;
    }

    private Optional<String> findCriticalMove(List<List<String>> rows, BoardTile tileToMatch) {
        for (Line line : getLines(rows)) {
            Optional<String> move = findMoveInLine(line, tileToMatch);
            if (move.isPresent()) {
                return move;
            }
        }
        return Optional.empty();
    }

    private Optional<String> findMoveInLine(Line line, BoardTile tileToMatch) {
        int matchingTiles = 0;
        int emptyIndex = -1;

        for (int i = 0; i < line.tiles.size(); i++) {
            String tile = line.tiles.get(i);
            if (tile.equals(tileToMatch.toString())) {
                matchingTiles++;
            } else if (tile.isEmpty()) {
                emptyIndex = i;
            }
        }

        if (matchingTiles == 2 && emptyIndex >= 0) {
            return Optional.of(line.tileIds.get(emptyIndex));
        }

        return Optional.empty();
    }

    private List<Line> getLines(List<List<String>> rows) {
        List<Line> lines = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < BOARD_SIZE; rowIndex++) {
            lines.add(new Line(
                    rows.get(rowIndex),
                    List.of(rowIndex + "-0", rowIndex + "-1", rowIndex + "-2")
            ));
        }

        for (int columnIndex = 0; columnIndex < BOARD_SIZE; columnIndex++) {
            List<String> column = new ArrayList<>();
            for (int rowIndex = 0; rowIndex < BOARD_SIZE; rowIndex++) {
                column.add(rows.get(rowIndex).get(columnIndex));
            }
            lines.add(new Line(
                    column,
                    List.of("0-" + columnIndex, "1-" + columnIndex, "2-" + columnIndex)
            ));
        }

        lines.add(new Line(
                List.of(rows.get(0).get(0), rows.get(1).get(1), rows.get(2).get(2)),
                List.of("0-0", "1-1", "2-2")
        ));

        lines.add(new Line(
                List.of(rows.get(0).get(2), rows.get(1).get(1), rows.get(2).get(0)),
                List.of("0-2", "1-1", "2-0")
        ));

        return lines;
    }

    private List<String> getAvailableTiles(Game game) {
        return getAvailableTiles(game.getRows());
    }

    private List<String> getAvailableTiles(List<List<String>> rows) {
        List<String> availableTiles = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                if (row.get(columnIndex).isEmpty()) {
                    availableTiles.add(rowIndex + "-" + columnIndex);
                }
            }
        }

        return availableTiles;
    }

    private static final class Line {
        private final List<String> tiles;
        private final List<String> tileIds;

        private Line(List<String> tiles, List<String> tileIds) {
            this.tiles = tiles;
            this.tileIds = tileIds;
        }
    }

    /**
     * Helper method for unit test
     */
    public void takeTurnForComputer(Game game, String tileId) {
        this.gameService.takeTurn(game, tileId);
    }
}
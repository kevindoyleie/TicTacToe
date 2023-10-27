package tictactoe.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.Game.GameState;
import tictactoe.game.entity.Game.PlayerNumber;
import tictactoe.game.entity.Game.PlayerType;
import tictactoe.game.entity.GameRepository;
import tictactoe.user.entity.AppUser;

import java.util.List;

import static tictactoe.game.BoardUtil.getAllPossibleLines;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Game create(AppUser appUser, boolean playerGoFirst) {

        gameRepository.deleteUserGames(appUser);

        Game game = new Game();
        game.setAppUser(appUser);
        game.setState(GameState.IN_PROGRESS);
        game.setNextMove(PlayerNumber.PLAYER_1);

        if (playerGoFirst) {
            game.setPlayer1Type(PlayerType.HUMAN);
            game.setPlayer2Type(PlayerType.COMPUTER);
        } else {
            game.setPlayer1Type(PlayerType.COMPUTER);
            game.setPlayer2Type(PlayerType.HUMAN);
        }

        game.setRows(BoardUtil.createEmpty());

        gameRepository.save(game);

        return game;
    }

    public Game getLastGame(AppUser appUser) {
        return gameRepository.findFirstByAppUserOrderByIdDesc(appUser);
    }

    /**
     * On the given {@link Game}, place the next move based on tileId.
     * @param game {@link Game} which includes the state of play.
     * @param tileId {@link String} in the format "{row index}-{column index}", eg. "0-0" is the top left, "2-2" bottom right.
     */
    public void takeTurn(Game game, String tileId) {
        if (game.getState() != GameState.IN_PROGRESS) {
            return;
        }

        String[] indices = tileId.split("-");
        if (indices.length != 2) {
            return;
        }

        final BoardTile tile = getPlayersBoardTile(game.getNextMove());

        if (game.getNextMove() == PlayerNumber.PLAYER_1) {
            game.setNextMove(PlayerNumber.PLAYER_2);
        } else {
            game.setNextMove(PlayerNumber.PLAYER_1);
        }

        int rowIndex = Integer.parseInt(indices[0]);
        int columnIndex = Integer.parseInt(indices[1]);
        game.getRows().get(rowIndex).set(columnIndex, tile.toString());

        GameState state = evaluateGameState(game.getRows());
        game.setState(state);
        if (state != GameState.IN_PROGRESS) {
            game.setNextMove(null); // Game over. Null out
        }

        gameRepository.save(game);
    }

    /**
     * Players use the same letter, return it.
     * @param playerNumber the player whose letter we want.
     * @return {@link BoardTile} for the player.
     */
    public BoardTile getPlayersBoardTile(PlayerNumber playerNumber) {
        switch (playerNumber) {
            case PLAYER_1: return BoardTile.X;
            case PLAYER_2: return BoardTile.O;
            default: throw new IllegalArgumentException("What player number is this?" + playerNumber);
        }
    }

    /**
     * Check the game, has anyone won?
     * @param rows the rows that make up a game.
     * @return {@link GameState}, PLAYER_1_WIN, PLAYER_2_WIN, IN_PROGRESS, DRAW
     */
    private GameState evaluateGameState(List<List<String>> rows)
    {
        final List<List<String>> lines = getAllPossibleLines(rows);

        for (List<String> line : lines) {
            String firstTile = line.get(0);
            if (firstTile.isEmpty())
                continue;

            if (isAWinner(line, firstTile))
                return firstTile.equals(BoardTile.X.toString()) ? GameState.PLAYER_1_WIN : GameState.PLAYER_2_WIN;

        }

        for (List<String> row : rows)
            if (row.stream().anyMatch(String::isEmpty))
                return GameState.IN_PROGRESS;

        return GameState.DRAW; // no connected lines for a winner AND all tiles are taken
    }

    public static boolean isAWinner(List<String> line, String firstTile) {
        return line.stream().allMatch(tile -> tile.equals(firstTile));
    }
}

package tictactoe.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.GameRepository;
import tictactoe.user.entity.AppUser;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GameScenarioTest
{
    private GameService gameService;
    private ComputerPlayerService computerPlayerService;

    @Mock
    private Game game;
    @Mock
    private GameRepository mockRepository;

    @BeforeEach
    public void setUp() {
        gameService = new GameService(mockRepository);
        computerPlayerService = new ComputerPlayerService(gameService);

        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "", ""),
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        lenient().when(game.getRows()).thenReturn(rows);
    }

    @Test
    void gameScenario_whenComputerTryToWin() {
        Game game = gameService.create(new AppUser(), true);

        // Player move to start
        gameService.takeTurn(game, "0-0");
        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "x", "", "",
                "", "", "",
                "", "", ""
        );//@formatter:on

        // Computer move
        computerPlayerService.takeTurn(game, "0-1");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "x", "o", "",
                "", "", "",
                "", "", ""
        );//@formatter:on

        final List<List<String>> one = game.getRows();

        // Player move
        gameService.takeTurn(game, "0-2");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "x", "o", "x",
                "", "", "",
                "", "", ""
        );//@formatter:on

        // Computer Move
        computerPlayerService.takeTurn(game, "1-1");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "x", "o", "x",
                "", "o", "",
                "", "", ""
        );//@formatter:on

        final List<List<String>> two = game.getRows();

        // Player Move
        gameService.takeTurn(game, "2-2");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "x", "o", "x",
                "", "o", "",
                "", "", "x"
        );//@formatter:on

        // Computer should try to win
        computerPlayerService.takeTurn(game);

        assertNull(game.getNextMove());
        assertThat(game.getState()).isEqualTo(Game.GameState.PLAYER_2_WIN);
        assertRows(//@formatter:off
                game,
                "x", "o", "x",
                "", "o", "",
                "", "o", "x"
        );//@formatter:on
    }

    @Test
    void gameScenario_whenDraw() {
        Game game = gameService.create(new AppUser(), true);

        // Player move to start
        gameService.takeTurn(game, "1-1");
        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "", "", "",
                "", "x", "",
                "", "", ""
        );//@formatter:on

        // Computer move
        computerPlayerService.takeTurn(game, "0-0");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "", "",
                "", "x", "",
                "", "", ""
        );//@formatter:on

        final List<List<String>> one = game.getRows();

        // Player move
        gameService.takeTurn(game, "1-2");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "", "",
                "", "x", "x",
                "", "", ""
        );//@formatter:on

        // Computer Move - From here it is trying to block
        computerPlayerService.takeTurn(game);

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "", "",
                "o", "x", "x",
                "", "", ""
        );//@formatter:on

        final List<List<String>> two = game.getRows();

        // Player Move
        gameService.takeTurn(game, "2-0");

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "", "",
                "o", "x", "x",
                "x", "", ""
        );//@formatter:on

        computerPlayerService.takeTurn(game);

        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "", "o",
                "o", "x", "x",
                "x", "", ""
        );//@formatter:on

        final List<List<String>> three = game.getRows();

        gameService.takeTurn(game, "0-1");
        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_2);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "x", "o",
                "o", "x", "x",
                "x", "", ""
        );//@formatter:on


        computerPlayerService.takeTurn(game);
        assertThat(game.getNextMove()).isEqualTo(Game.PlayerNumber.PLAYER_1);
        assertThat(game.getState()).isEqualTo(Game.GameState.IN_PROGRESS);
        assertRows(//@formatter:off
                game,
                "o", "x", "o",
                "o", "x", "x",
                "x", "o", ""
        );//@formatter:on

        gameService.takeTurn(game, "2-2");
        assertNull(game.getNextMove());
        assertThat(game.getState()).isEqualTo(Game.GameState.DRAW);
        assertRows(//@formatter:off
                game,
                "o", "x", "o",
                "o", "x", "x",
                "x", "o", "x"
        );//@formatter:on
    }

    /**
     * Helper assert method to verify expected game rows.
     */
    private void assertRows(Game game, String... tiles) {
        List<List<String>> rows = game.getRows();
        assertThat(rows.get(0)).containsExactly(tiles[0], tiles[1], tiles[2]);
        assertThat(rows.get(1)).containsExactly(tiles[3], tiles[4], tiles[5]);
        assertThat(rows.get(2)).containsExactly(tiles[6], tiles[7], tiles[8]);
    }
}

package tictactoe.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tictactoe.game.entity.Game;
import tictactoe.game.entity.Game.PlayerNumber;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComputerPlayerServiceTest {

    private ComputerPlayerService computerPlayerService;

    @Mock
    private Game game;

    @Mock
    private GameService gameService;

    @BeforeEach
    void setUp() {
        computerPlayerService = new ComputerPlayerService(gameService);
        when(game.getRows()).thenReturn(board(
                row("", "", ""),
                row("", "", ""),
                row("", "", "")
        ));
    }

    @Test
    void testTakeTurn_PrioritizesWinningOverEverythingElse() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        when(game.getRows()).thenReturn(board(
                row("x", "x", ""),
                row("", "", ""),
                row("", "", "")
        ));

        assertComputerPlays("0-2");
    }

    @Test
    void testTakeTurn_PrioritizesBlockingOverOtherNonWinningMoves() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        when(game.getRows()).thenReturn(board(
                row("o", "o", ""),
                row("", "", ""),
                row("", "", "")
        ));

        assertComputerPlays("0-2");
    }

    @Test
    void testTakeTurn_PrioritizesCenterWhenNoWinOrBlockExists() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        when(game.getRows()).thenReturn(board(
                row("x", "", ""),
                row("", "", ""),
                row("", "", "o")
        ));

        assertComputerPlays("1-1");
    }

    @Test
    void testTakeTurn_PrioritizesOppositeCornerOverCorner() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        when(game.getRows()).thenReturn(board(
                row("o", "", ""),
                row("", "x", ""),
                row("", "", "")
        ));

        assertComputerPlays("2-2");
    }

    @Test
    void testTakeTurn_PrioritizesCornerOverRandom() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        when(game.getRows()).thenReturn(board(
                row("", "o", ""),
                row("o", "x", "o"),
                row("", "o", "x")
        ));

        assertComputerPlays("0-0");
    }

    @Test
    void testGetPreferredTile_CenterAvailable_ReturnsCenter() {
        Optional<String> tileId = computerPlayerService.getPreferredTile(game);
        assertThat(tileId).contains("1-1");
    }

    @Test
    void testGetPreferredTile_CenterTaken_ReturnsEmpty() {
        when(game.getRows()).thenReturn(board(
                row("", "", ""),
                row("", "x", ""),
                row("", "", "")
        ));

        Optional<String> tileId = computerPlayerService.getPreferredTile(game);
        assertThat(tileId).isEmpty();
    }

    @Test
    void testGetCornerTile_CornerAvailable_ReturnsCorner() {
        when(game.getRows()).thenReturn(board(
                row("", "o", "x"),
                row("o", "x", "o"),
                row("x", "o", "x")
        ));

        Optional<String> tileId = computerPlayerService.getCornerTile(game);
        assertThat(tileId).contains("0-0");
    }

    @Test
    void testGetCornerTile_NoCornersAvailable_ReturnsEmpty() {
        when(game.getRows()).thenReturn(board(
                row("x", "o", "x"),
                row("o", "", "o"),
                row("x", "o", "x")
        ));

        Optional<String> tileId = computerPlayerService.getCornerTile(game);
        assertThat(tileId).isEmpty();
    }

    @Test
    void testGetBlockingTileNoBlockingMove() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        Optional<String> blockingTile = computerPlayerService.getBlockingTile(game);
        assertThat(blockingTile.isEmpty());
    }

    @Test
    void testGetWinningTileNoWinningMove() {
        givenPlayerOneTurn();
        givenPlayerOneTile(BoardTile.X);
        Optional<String> winningTile = computerPlayerService.getWinningTile(game);
        assertThat(winningTile.isEmpty());
    }

    @Test
    void testGetRandomEmptyTile() {
        Optional<String> tileId = computerPlayerService.getRandomEmptyTile(game);
        assertThat(tileId.isEmpty());
    }

    private void givenPlayerOneTurn() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
    }

    private void givenPlayerOneTile(BoardTile tile) {
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(tile);
    }

    private void assertComputerPlays(String expectedTileId) {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo(expectedTileId);
    }

    private List<String> row(String a, String b, String c) {
        return Arrays.asList(a, b, c);
    }

    private List<List<String>> board(List<String> r1, List<String> r2, List<String> r3) {
        return Arrays.asList(r1, r2, r3);
    }
}
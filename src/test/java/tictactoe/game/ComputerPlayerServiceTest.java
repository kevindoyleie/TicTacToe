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
    public void setUp() {
        computerPlayerService = new ComputerPlayerService(gameService);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "", ""),
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        lenient().when(game.getRows()).thenReturn(rows);
    }

    @Test
    void testTakeTurn() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        computerPlayerService.takeTurn(game);
        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    void testTakeTurn_PrioritizesForkOverCorner() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);

        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "", ""),
                Arrays.asList("", "o", ""),
                Arrays.asList("", "", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("0-2");
    }

    @Test
    void testTakeTurn_PrioritizesBlockingFork() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);

        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "", ""),
                Arrays.asList("", "o", ""),
                Arrays.asList("", "", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        Optional<String> blockingForkTile = computerPlayerService.getBlockingForkTile(game);
        assertThat(blockingForkTile).isEmpty();
    }

    @Test
    void testTakeTurn_PrioritizesWinningOverEverythingElse() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "x", ""),
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("0-2");
    }

    @Test
    void testTakeTurn_PrioritizesBlockingOverCenterAndCorner() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("o", "o", ""),
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("0-2");
    }

    @Test
    void testTakeTurn_PrioritizesCenterOverCorner() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "", ""),
                Arrays.asList("", "", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("1-1");
    }

    @Test
    void testTakeTurn_PrioritizesOppositeCornerOverCorner() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("o", "", ""),
                Arrays.asList("", "x", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("2-2");
    }

    @Test
    void testTakeTurn_PrioritizesCornerOverRandom() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "o", ""),
                Arrays.asList("o", "x", "o"),
                Arrays.asList("", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        computerPlayerService.takeTurn(game);

        verify(gameService).takeTurn(any(), captor.capture());
        assertThat(captor.getValue()).isEqualTo("0-0");
    }

    @Test
    void testGetPreferredTile_CenterAvailable_ReturnsCenter() {
        Optional<String> tileId = computerPlayerService.getPreferredTile(game);
        assertThat(tileId).contains("1-1");
    }

    @Test
    void testGetPreferredTile_CenterTaken_ReturnsEmpty() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "", ""),
                Arrays.asList("", "x", ""),
                Arrays.asList("", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        Optional<String> tileId = computerPlayerService.getPreferredTile(game);
        assertThat(tileId).isEmpty();
    }

    @Test
    void testGetCornerTile_CornerAvailable_ReturnsCorner() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "o", "x"),
                Arrays.asList("o", "x", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        Optional<String> tileId = computerPlayerService.getCornerTile(game);
        assertThat(tileId).contains("0-0");
    }

    @Test
    void testGetCornerTile_NoCornersAvailable_ReturnsEmpty() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", "x"),
                Arrays.asList("o", "", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        Optional<String> tileId = computerPlayerService.getCornerTile(game);
        assertThat(tileId).isEmpty();
    }

    @Test
    void testGetBlockingTileNoBlockingMove() {
        lenient().when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        Optional<String> blockingTile = computerPlayerService.getBlockingTile(game);
        assertThat(blockingTile.isEmpty());
    }

    @Test
    void testGetWinningTileNoWinningMove() {
        lenient().when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        Optional<String> winningTile = computerPlayerService.getWinningTile(game);
        assertThat(winningTile.isEmpty());
    }

    @Test
    void testGetRandomEmptyTile() {
        Optional<String> tileId = computerPlayerService.getRandomEmptyTile(game);
        assertThat(tileId.isEmpty());
    }
}
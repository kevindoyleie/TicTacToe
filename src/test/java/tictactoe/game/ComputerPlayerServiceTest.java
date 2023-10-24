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
        final String tileId = captor.getValue();
        assertThat(tileId).isNotNull();
    }

    @Test
    void testGetBlockingTileNoBlockingMove() {
        lenient().when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is no blocking move
        assertThat(blockingTile).isNull();
    }

    @Test
    void testGetBlockingTileDiagonalBlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("o", "", "x"),
                Arrays.asList("", "o", ""),
                Arrays.asList("x", "", "")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("2-2");
    }

    @Test
    void testGetBlockingTileDiagonal2BlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "", "o"),
                Arrays.asList("", "o", ""),
                Arrays.asList("", "", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("2-0");
    }

    @Test
    void testGetBlockingTileRowBlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "x", ""),
                Arrays.asList("", "x", ""),
                Arrays.asList("", "o", "o")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("0-2");
    }

    @Test
    void testGetBlockingTileRow1BlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "", "x"),
                Arrays.asList("o", "o", ""),
                Arrays.asList("o", "x", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("1-2");
    }

    @Test
    void testGetBlockingTileRow2BlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "", ""),
                Arrays.asList("o", "x", "x"),
                Arrays.asList("o", "", "o")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("2-1");
    }

    @Test
    void testGetBlockingTileColumnBlockingMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", ""),
                Arrays.asList("", "o", ""),
                Arrays.asList("", "", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String blockingTile = computerPlayerService.getBlockingTile(game);
        // There is a blocking move at bottom right
        assertThat(blockingTile).isEqualTo("2-1");
    }

    @Test
    void testGetWinningTileNoWinningMove() {
        lenient().when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        String winningTile = computerPlayerService.getWinningTile(game);
        // There is no winning move
        assertThat(winningTile).isNull();
    }

    @Test
    void testGetWinningTileDiagonalWinningMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "o", ""),
                Arrays.asList("", "x", ""),
                Arrays.asList("", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String winningTile = computerPlayerService.getWinningTile(game);
        // There is a winning move at top left
        assertThat(winningTile).isEqualTo("0-0");
    }

    @Test
    void testGetWinningTileRowWinningMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("", "o", ""),
                Arrays.asList("", "o", ""),
                Arrays.asList("", "x", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String winningTile = computerPlayerService.getWinningTile(game);
        // There is a winning move at top left
        assertThat(winningTile).isEqualTo("2-0");
    }

    @Test
    void testGetWinningTileColumnWinningMove() {
        when(game.getNextMove()).thenReturn(PlayerNumber.PLAYER_1);
        when(gameService.getPlayersBoardTile(PlayerNumber.PLAYER_1)).thenReturn(BoardTile.X);
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("o", "", ""),
                Arrays.asList("", "o", "x"),
                Arrays.asList("", "", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String winningTile = computerPlayerService.getWinningTile(game);
        // There is a winning move at top left
        assertThat(winningTile).isEqualTo("0-2");
    }

    @Test
    void testGetRandomEmptyTile() {
        String tileId = computerPlayerService.getRandomEmptyTile(game);
        assertThat(tileId).isNotNull();
    }

    @Test
    void getRandomAvailableTile_OnlyCenterTileAvailable_PickCenterTile() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", "x"),
                Arrays.asList("o", "", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String tileId = computerPlayerService.getRandomEmptyTile(game);
        assertThat(tileId).isEqualTo("1-1");
    }

    @Test
    void getRandomAvailableTile_AllTilesTaken_ReturnNull() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("x", "o", "x"),
                Arrays.asList("o", "x", "o"),
                Arrays.asList("x", "o", "x")
        );//@formatter:on
        when(game.getRows()).thenReturn(rows);

        String tileId = computerPlayerService.getRandomEmptyTile(game);
        assertThat(tileId).isNull();
    }
}

package tictactoe.game;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BoardUtilTest {

    @Test
    void createEmpty_Call_Empty3x3Board() {
        List<List<String>> rows = BoardUtil.createEmpty();
        assertThat(rows).hasSize(3);
        assertThat(rows.get(0)).hasSize(3).containsExactly("", "", "");
        assertThat(rows.get(1)).hasSize(3).containsExactly("", "", "");
        assertThat(rows.get(2)).hasSize(3).containsExactly("", "", "");
    }

    @Test
    void getAllLines_Call_ReturnsCorrectLines() {
        List<List<String>> rows = Arrays.asList(//@formatter:off
                Arrays.asList("a", "b", "c"),
                Arrays.asList("d", "e", "f"),
                Arrays.asList("g", "h", "i")
        );//@formatter:on

        final List<List<String>> allPossibleLines = BoardUtil.getAllPossibleLines(rows);

        // rows
        assertThat(allPossibleLines.get(0)).containsExactly("a", "b", "c");
        assertThat(allPossibleLines.get(1)).containsExactly("d", "e", "f");
        assertThat(allPossibleLines.get(2)).containsExactly("g", "h", "i");

        // columns
        assertThat(allPossibleLines.get(3)).containsExactly("a", "d", "g");
        assertThat(allPossibleLines.get(4)).containsExactly("b", "e", "h");
        assertThat(allPossibleLines.get(5)).containsExactly("c", "f", "i");

        // diagonals
        assertThat(allPossibleLines.get(6)).containsExactly("a", "e", "i");
        assertThat(allPossibleLines.get(7)).containsExactly("c", "e", "g");

        // Overall size
        assertThat(allPossibleLines)
                .withFailMessage(() -> String.format(
                        "There are 8 possible lines through a tic-tac-toe board, got: %s", allPossibleLines.size())
                )
                .hasSize(8);
    }
}

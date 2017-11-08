package qirkat;


import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

public class MoreBoardTests {

    // the string representation of this is
    // "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w"
    // feel free to modify this to however you want to represent your board.
    private final char[][] boardRepr = new char[][]{
            {'b', 'b', 'b', 'b', 'b'},
            {'b', 'b', 'b', 'b', 'b'},
            {'b', 'b', '-', 'w', 'w'},
            {'w', 'w', 'w', 'w', 'w'},
            {'w', 'w', 'w', 'w', 'w'}
    };

    private final PieceColor currMove = PieceColor.WHITE;

    /**
     * @return the String representation of the initial state. This will
     * be a string in which we concatenate the values from the bottom of
     * board upwards, so we can pass it into setPieces. Read the comments
     * in Board#setPieces for more information.
     *
     * For our current boardRepr, the String returned by getInitialRepresentation is
     * "  w w w w w\n  w w w w w\n  b b - w w\n  b b b b b\n  b b b b b"
     *
     * We use a StringBuilder to avoid recreating Strings (because Strings
     * are immutable).
     */
    private String getInitialRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("  ");
        for (int i = boardRepr.length - 1; i >= 0; i--) {
            for (int j = 0; j < boardRepr[0].length; j++) {
                sb.append(boardRepr[i][j] + " ");
            }
            sb.deleteCharAt(sb.length() - 1);
            if (i != 0) {
                sb.append("\n  ");
            }
        }
        return sb.toString();
    }

    // create a new board with the initial state.
    private Board getBoard() {
        Board b = new Board();
        b.setPieces(getInitialRepresentation(), currMove);
        return b;
    }

    // reset board b to initial state.
    private void resetToInitialState(Board b) {
        b.setPieces(getInitialRepresentation(), currMove);
    }


    // Additional custom tests below //
    @Ignore
    public void testJumpPossible() {
        Board b = getBoard();
        //initial state cannot jump
        assertFalse(b.jumpPossible());
        //make a series of moves until jumpIsPossible
        for (String s : jumpPossible1) {
            b.makeMove(Move.parseMove(s));
        }
        assertTrue(b.jumpPossible());
    }
    //Sequences of move to get to a specific configuration
    private static final String[] jumpPossible1 =
            { "c2-c3", "c4-c2",
                    "c1-c3"
            };
    @Test
    public void testMakeMove() {
        Board b1 = getBoard();
        Board b2 = new Board();
        //make a series of moves on one board
        for (String s : jumpPossible1) {
            b1.makeMove(Move.parseMove(s));
        }
        //set up another board that looks like it using set pieces
        b2.setPieces("ww-wwww-wwbbwwwbb-bbbbbbb", PieceColor.WHITE);
        assertEquals(b1.toString(), b2.toString());
    }

    @Test
    public void testToString() {
        String expected =
                "  b - w - b\n" +
                "  - - - - -\n" +
                "  b - w w w\n" +
                "  - w - - -\n" +
                "  b w - b b";
        String expectedLegend =
                "5  b - w - b\n" +
                "4  - - - - -\n" +
                "3  b - w w w\n" +
                "2  - w - - -\n" +
                "1  b w - b b\n" +
                "   a b c d e";
        String expectedConfig = "bw-bb-w---b-www-----b-w-b";
        Board b1 = new Board();
        b1.setPieces(expectedConfig, PieceColor.WHITE);
        assertEquals(expected, b1.toString());
        assertEquals(expectedLegend, b1.toString(true));

    }
    @Test
    public void testClear() {
        Board b1 = new Board();
        String initBoard = "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";
        b1.setPieces("wwbwww--ww--wwwb--bbbb-bb", PieceColor.BLACK );
        b1.clear();
        assertEquals(b1.whoseMove(), PieceColor.WHITE);
        assertEquals(initBoard, b1.toString());

    }

    @Test
    public void testWhoseMove() {
        Board b1 = new Board();
        assertEquals(PieceColor.WHITE, b1.whoseMove());
        b1.setWhoseMove(PieceColor.BLACK);
        assertEquals(PieceColor.BLACK, b1.whoseMove());
    }

    @Test
    public void testSet() {
        Board b1 = new Board();
        b1.setPieces("wwbwww--ww--wwwb--bbbb-bb", PieceColor.WHITE);
        String expected = "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";
        assertEquals(expected, b1.toString());
    }

    @Test
    public void testGet() {
        //FIXME
    }


    @Test
    public void testUndo() {

    }


}

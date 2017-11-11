package qirkat;


import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MoreBoardTests {

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    private static final String[] GAME1 =
            { "c2-c3", "c4-c2",
                    "c1-c3", "a3-c1",
                    "c3-a3", "c5-c4",
                    "a3-c5-c3",
            };

    private static final String[] GAME2 =
            { "c2-c3", "c4-c2",
                    "c1-c3", "a3-c1",
                    "c3-a3", "c5-c4",
            };

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
    @Test
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
    public void testUndo2() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        makeMoves(b1, GAME2);
        b0.undo();
        assertEquals(b0.toString(), b1.toString());
    }

    private static int[] initDirection =
            {0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0
            };

    @Test
    public void TestSetDirections1() {
        Board b0 = new Board();
        assertTrue(Arrays.equals(initDirection, b0.getDirections()));
        makeMoves(b0, GAME2);
        assertTrue(Arrays.equals(initDirection, b0.getDirections()));
    }

    @Test
    public void TestSeDirections2() {
        //FIXME
    }

    @Test
    public void testJumpPossible1() {
        Board b1 = new Board();
        assertFalse(b1.jumpPossible());
        String config1 = "wwbwww--ww--wwwb--bbbb-bb";
        b1.setPieces(config1, PieceColor.WHITE);
        assertTrue(b1.jumpPossible(14));
        assertFalse(b1.jumpPossible(18));
        assertFalse(b1.jumpPossible(1));
    }

    @Test
    public void testJumpPossible2() {
        //FIXME
        Board b1 = new Board();
        assertFalse(b1.jumpPossible());
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.BLACK);
        b1.makeMoveHelper(Move.parseMove("c3-e3"));
        assertTrue(b1.jumpPossible(14));
    }

    @Test
    public void testValidHorizontal() {

    }

    @Test
    public void testValidDiagonal() {

    }

    @Test
    public void testEqualsMove() {
        Move mov1 = Move.move('a', '1', 'b', '1');
        Move mov2 = Move.move('a', '1', 'b', '1');
        assertEquals(mov1, mov2);
    }

    @Test
    public void testGetJumpsHelper1() {
        Board b1 = new Board();
        String config1 = "---w-w---w--bw-w---w----w";
        b1.setPieces(config1, PieceColor.BLACK);
        ArrayList<Move> expected = new ArrayList<>();
        expected.add(Move.parseMove("c3-e3-e1-c1"));
        ArrayList<Move> actual = b1.getJumpsHelper(12, b1);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }

    }

    @Test
    public void testGetJumpsHelper2() {
        Board b1 = new Board();
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.BLACK);
        ArrayList<Move> expected = new ArrayList<>();
        expected.add(Move.parseMove("c3-a3-a1"));
        expected.add(Move.parseMove("c3-a3-a5"));
        ArrayList<Move> actual = b1.getJumpsHelper(12, b1);
        System.out.println(actual);
        assertEquals(expected.get(0).toString(), actual.get(1).toString());
        assertEquals(expected.get(1).toString(), actual.get(0).toString());
    }

    @Test
    /** tests that getJumpsHelper, getJumps, and jumpPossible return empty list,
     *  when specificed position piece != whoseMove()
     */
    public void testGetJumpsHelper3() {
        Board b1 = new Board();
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.WHITE);
        ArrayList<Move> actual = b1.getJumpsHelper(12, b1);
        assertTrue(actual.size() == 0);
        assertFalse(b1.jumpPossible(12));
    }

    @Test
    /** if white's turn, should not be able to move black's piece. **/
    public void testCheckJump1() {
        Board b1 = new Board();
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.WHITE);
        boolean actual = b1.checkJump(Move.parseMove("c3-a3"), false);
        assertEquals(false, actual);
    }

    @Test
    /** if black's turn and allow partial, move should be valid. **/
    public void testCheckJump2() {
        Board b1 = new Board();
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.BLACK);
        boolean actual = b1.checkJump(Move.parseMove("c3-a3-a1"), true);
        assertEquals(true, actual);
    }

    @Test
    /** if black's turn and partial is false, move should not be valid. **/
    public void testCheckJump3() {
        Board b1 = new Board();
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.BLACK);
        boolean actual = b1.checkJump(Move.parseMove("c3-a3"), false);
        assertEquals(false, actual);
    }

    @Test
    /** test if tail jump has more jumps and allow partial is false. **/
    public void testheckJump4() {
        Board b1 = new Board();
        String config1 = "-----------wb--w-----w-w-";
        b1.setPieces(config1, PieceColor.BLACK);
        boolean actual = b1.checkJump(Move.parseMove("c3-a3-a5-c5"), false);
        assertEquals(false, actual);
    }


    //EDGE cases! Must pass these!!!
    @Test
    /** make sure the jumps follow the lines of the board. **/
    public void testJumpPossibleDiagonal1() {
        Board b1 = new Board();
        String noDiagonalJump = "wwwwww-b-wb-wwwbb-bbbbbbb";
        b1.setPieces(noDiagonalJump, PieceColor.WHITE);
        System.out.println(b1);
        assertFalse(b1.jumpPossible(3));

    }

    @Test
    /** make sure the jumps follow the lines of the board. **/
    public void testGetJumpsDiagonal2() {
        Board b1 = new Board();
        String noDiagonalJump = "--w----ww----b-----------";
        b1.setPieces(noDiagonalJump, PieceColor.BLACK);
        Move expected = Move.parseMove("d3-d1-b1");
        ArrayList<Move> actual = b1.getJumpsHelper(13, b1);
        assertTrue(actual.size() == 1);
        assertEquals(expected.toString(), actual.get(0).toString());
    }


}

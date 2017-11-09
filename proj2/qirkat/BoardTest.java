package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

/** Tests of the Board class.
 *  @author
 */
public class BoardTest {

    private static final String INIT_BOARD =
        "  b b b b b\n  b b b b b\n  b b - w w\n  w w w w w\n  w w w w w";

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

    private static final String GAME1_BOARD =
        "  b b - b b\n  b - - b b\n  - - w w w\n  w - - w w\n  w w b w w";

    private static void makeMoves(Board b, String[] moves) {
        for (String s : moves) {
            b.makeMove(Move.parseMove(s));
        }
    }

    @Test
    public void testInit1() {
        Board b0 = new Board();
        assertEquals(INIT_BOARD, b0.toString());
    }

    @Test
    public void testMoves1() {
        Board b0 = new Board();
        makeMoves(b0, GAME1);
        assertEquals(GAME1_BOARD, b0.toString());
    }

    @Test
    public void testUndo() {
        Board b0 = new Board();
        Board b1 = new Board(b0);
        makeMoves(b0, GAME1);
        Board b2 = new Board(b0);
        for (int i = 0; i < GAME1.length; i += 1) {
            b0.undo();
        }
        assertEquals("failed to return to start", b1, b0);
        makeMoves(b0, GAME1);
        assertEquals("second pass failed to reach same position", b2, b0);
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
    public void testJumpPossible() {
        Board b1 = new Board();
        assertFalse(b1.jumpPossible());
        String config1 = "wwbwww--ww--wwwb--bbbb-bb";
        b1.setPieces(config1, PieceColor.WHITE);
        assertTrue(b1.jumpPossible(14));
        assertTrue(b1.jumpPossible(18));
        assertFalse(b1.jumpPossible(1));
    }

    @Test
    public void testValidHorizontal() {

    }

    @Test
    public void testValidDiagonal() {

    }
}

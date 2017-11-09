package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.ArrayList;

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
    public void testJumpPossible1() {
        Board b1 = new Board();
        assertFalse(b1.jumpPossible());
        String config1 = "wwbwww--ww--wwwb--bbbb-bb";
        b1.setPieces(config1, PieceColor.WHITE);
        assertTrue(b1.jumpPossible(14));
        assertTrue(b1.jumpPossible(18));
        assertFalse(b1.jumpPossible(1));
    }

    @Test
    public void testJumpPossible2() {
        //FIXME
        Board b1 = new Board();
        assertFalse(b1.jumpPossible());
        String config1 = "---w-w---w-wbw-w---w----w";
        b1.setPieces(config1, PieceColor.WHITE);
        b1.makeMoveHelper(Move.parseMove("c3-e3"));
        System.out.println(b1.toString());
        assertTrue(b1.jumpPossible(14));
        System.out.println(b1.allOneJumpsFromK(14, b1));
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
        expected.add(Move.parseMove("c3-e3-e1-c1"));
        expected.add(Move.parseMove("c3-a3-a5-a1"));
        expected.add(Move.parseMove("c3-a3-a5-a1"));
        ArrayList<Move> actual = b1.getJumpsHelper(12, b1);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i).toString(), actual.get(i).toString());
        }
    }
}

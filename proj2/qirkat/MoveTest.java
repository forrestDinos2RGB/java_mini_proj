/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author
 */
public class MoveTest {

    @Test
    public void testMove1() {
        Move m = move('a', '3', 'b', '2');
        assertNotNull(m);
        assertFalse("move should not be jump", m.isJump());
    }

    @Test
    public void testJump1() {
        Move m = move('a', '3', 'a', '5');
        assertNotNull(m);
        assertTrue("move should be jump", m.isJump());
    }

    @Test
    public void testString() {
        assertEquals("a3-b2", move('a', '3', 'b', '2').toString());
        assertEquals("a3-a5", move('a', '3', 'a', '5').toString());
        assertEquals("a3-a5-c3", move('a', '3', 'a', '5',
                                      move('a', '5', 'c', '3')).toString());
    }

    @Test
    public void testParseString() {
        assertEquals("a3-b2", parseMove("a3-b2").toString());
        assertEquals("a3-a5", parseMove("a3-a5").toString());
        assertEquals("a3-a5-c3", parseMove("a3-a5-c3").toString());
        assertEquals("a3-a5-c3-e1", parseMove("a3-a5-c3-e1").toString());
    }

    @Test
    public void testJumpedRow() {
        Move m = move('a', '1', 'c', '3');
        assertEquals('2', m.jumpedRow());
        Move m2 = move('a', '1', 'c', '1');
        assertEquals('1', m2.jumpedRow());
    }

    @Test
    public void testJumpedCol() {
        Move m = move('a', '1', 'c', '3');
        assertEquals('b', m.jumpedCol());
        Move m2 = move('a', '3', 'a', '5');
    }

    //Additional tests below

    @Test
    public void TestIsLeftMove() {
        Move left = Move.move('c', '1', 'b', '1', null);
        assertTrue(left.isLeftMove());
    }

    @Test
    public void TestIsRightMove() {
        Move right = Move.move('c', '2', 'd', '2', null);
        assertTrue(right.isRightMove());
    }

    @Test
    public void TestNextJump() {
        Move jump = Move.parseMove("a3-a5-c5-c3-c1-a1");
        assertTrue(jump.isJump());
        while (jump.jumpTail() != null) {
            jump = jump.jumpTail();
            assertTrue(jump.isJump());
        }
    }
}

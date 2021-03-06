package qirkat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author
 */
class Board extends Observable {

    /** A new, cleared board at the start of the game. */
    Board() {
        _allPieces = new PieceColor[Move.SIDE * Move.SIDE];
        _direction = new int[Move.SIDE * Move.SIDE];
        clear();
    }

    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        setPieces(clearedBoard, WHITE);
        _direction = new int[Move.SIDE * Move.SIDE];
        _undoDirection = new Stack<>();
        _undoPieces = new Stack<>();
        _gameOver = false;

        setChanged();
        notifyObservers();
    }

    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        //copy over directions
        _direction = b._direction.clone();
        //copy over undoDirections
        _undoDirection = (Stack)b._undoDirection.clone();
        //copy over undoPieces
        _undoPieces = (Stack)b._undoPieces.clone();
        //copy over allPieces
        _allPieces = b._allPieces.clone();
        setWhoseMove(b.whoseMove());
        this._gameOver = b._gameOver;
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        setWhoseMove(nextMove);

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        //FIXME-check for gameOVer? what else?

        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** update _gameOver to either true or false **/
    public void updateGameOver() {
        //FIXME!
    }

    /** Return the current contents of square C R, where 'a' <= C <= 'e',
     *  and '1' <= R <= '5'.  */
    PieceColor get(char c, char r) {
        assert validSquare(c, r);
        return get(index(c, r));
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _allPieces[k];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _allPieces[k] = v;
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {
        //FIXME
        return true;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        if (jumpPossible()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getJumps(moves, k);
            }
        } else {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private void getMoves(ArrayList<Move> moves, int k) {
        MoveList uncheckedMoves = getMovesHelper(k);
        for (Move uncheck : uncheckedMoves) {
            if (legalMove(uncheck)) {
                moves.add(uncheck);
            }
        }
    }

    /** returns possible non capturing moves depending on whoseMove. **/
    private MoveList getMovesHelper(int k) {
        MoveList allMoves = new MoveList();
        int[] allMove = new int[5];
        allMove[0] = k + 1;
        allMove[1] = k - 1;
        if (whoseMove() == WHITE) {
            allMove[2] = k + 4;
            allMove[3] = k + 5;
            allMove[4] = k + 6;
        } else {
            allMove[2] = k - 4;
            allMove[3] = k - 5;
            allMove[4] = k - 6;
        }
        for (int moveIndex : allMove) {
            allMoves.add(Move.moveLinIndex(k, moveIndex));
        }
        return allMoves;
    }

    /** returns true if move is allowed.
     * If position moving to is empty &&
     * If position moving from is your move
     * If position moving to valid square */
    private boolean moveAllowed(int from, int to) {
        boolean oneRowAway = (Math.abs(from / 5 - to / 5) == 1);
        boolean oneColAway = Math.abs(from % 5 - to % 5) == 1;
        return get(to) == EMPTY && get(from) == whoseMove()
                && validSquare(to);
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    private void getJumps(ArrayList<Move> moves, int k) {
        MoveList uncheckedJumps = getJumpsHelper(k, this);
        for (Move unchecked: uncheckedJumps) {
            if (checkJump(unchecked, false)) {
                moves.add(unchecked);
            }
        }
    }

    /** Return a list of all possible jumps given current board state,
     *   a fromIndex.
     */
    MoveList getJumpsHelper(int k, Board currState) {
        MoveList validJumps = new MoveList();
        if (currState.get(k) != currState.whoseMove()) {
            return validJumps;
        }
        MoveList validOneJumps = currState.allOneJumpsFromK(k);
        if (validOneJumps.size() == 0) {
            return validJumps;
        }
        for (Move jump: validOneJumps) {
            Board nextState = new Board(currState);
            //makes the jump
            //change the pieces but don't change anything else
            nextState.makeMoveHelper(jump);
            MoveList restJumps = getJumpsHelper(jump.toIndex(), nextState);
            if (restJumps.size() == 0) {
                validJumps.add(jump);
            }
            for (Move rest: restJumps) {
                validJumps.add(Move.move(jump, rest));
            }
        }
        return validJumps;
    }

    //TRIAL RUN ----------------------------------------
    boolean checkJump(Move mov, boolean allowPartial) {
        Board copy = new Board(this);
        return checkJumpHelper(mov, copy, allowPartial);
    }

    boolean checkJumpHelper(Move mov, Board currBoard, boolean allowPartial) {
        //base case 1
        if (!currBoard.validSingleJump(mov)) {
            return false;
        }
        //base case 2
        if (mov.jumpTail() == null) {
            currBoard.makeMoveHelper(mov);
            boolean canJumpAtK = currBoard.jumpPossible(index(mov.col1(), mov.row1()));
            if (allowPartial) {
                return true;
            } else {
                if (allowPartial == false && canJumpAtK) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        //recursive case
        //our friend will check the rest of the jump
        Board copy = new Board(currBoard);
        Move front = move(mov.col0(), mov.row0(), mov.col1(), mov.row1());
        copy.makeMoveHelper(front);
        return checkJumpHelper(mov.jumpTail(), copy, allowPartial);
    }

    boolean validSingleJump(Move mov) {
        if (mov.isVestigial()) {
            return false;
        }
        //checks for basic requirements
        if (!jumpAllowed(mov)) {
            return false;
        }
        boolean isHorizontal = mov.isHorizontalJump();
        boolean isVertical = mov.isVerticalJump();
        boolean isDiagonal = mov.isDiagonalJump();
        //must be one of three types
        if (!(isHorizontal || isVertical || isDiagonal)) {
            return false;
        }
        //checks for diagonal line jump
        if (isDiagonal && ! mov.onDiagonalLine()) {
            return false;
        }
        return true;
    }
    //TRIAL RUN ----------------------------------------
//    /** Return true iff MOV is a valid jump sequence on the current board.
//     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
//     *  could be continued and are valid as far as they go.  */
//    boolean checkJump(Move mov, boolean allowPartial) {
//        if (!checkJumpHelper(mov, this)) {
//            return false;
//        }
//        Move frontMove = mov;
//        /** check each subsequent jumps to make sure is correct. **/
//        Board copy = new Board(this);
//        while (mov.jumpTail() != null) {
//            copy = new Board(copy);
//            //make fake move
//            copy.makeMoveHelper(move(mov.col0(), mov.row0(), mov.col1(), mov.row1()));
//            mov = mov.jumpTail();
//            if (!checkJumpHelper(mov, copy)) {
//                return false;
//            }
//        }
//        copy = new Board(this);
//        copy.makeMoveHelper(frontMove);
//        if (!allowPartial && copy.jumpPossible(index(mov.col1(), mov.row1()))) {
//            return false;
//        }
//        return true;
//
//    }
//
//    boolean checkJumpHelper(Move mov, Board currBoard) {
//        if (mov.isVestigial()) {
//            return false;
//        }
//        //checks for basic requirements
//        if (!currBoard.jumpAllowed(mov)) {
//            return false;
//        }
//        boolean isHorizontal = mov.isHorizontalJump();
//        boolean isVertical = mov.isVerticalJump();
//        boolean isDiagonal = mov.isDiagonalJump();
//        //must be one of three types
//        if (!(isHorizontal || isVertical || isDiagonal)) {
//            return false;
//        }
//        //checks for diagonal line jump
//        if (isDiagonal && ! mov.onDiagonalLine()) {
//            return false;
//        }
//        return true;
//    }

    /** Return true if intended jump is aljlowed for MOVE. **/
    boolean jumpAllowed(Move jump) {
        return jumpAllowed(index(jump.col0(), jump.row0()), index(jump.col1(), jump.row1()));
    }
    /** Return true if intended jump is allowed for JUMPINDEX, FROMINDEX. **/
    boolean jumpAllowed(int fromIndex, int jumpIndex) {
        return (Move.validSquare(jumpIndex) &&
                get((fromIndex + jumpIndex) / 2) == get(fromIndex).opposite() &&
                get(jumpIndex) == EMPTY) && get(fromIndex) == whoseMove();
    }

    /** given 2 linearized index, return true if is valid horizontal move. **/
    boolean validHorizontalJump(int from, int to) {
        return Math.abs(from % 5 - to % 5) == 2;
    }
    /** given 2 linearized index, return true if is valid diagonal move. **/
    boolean validDiagonalJump(int from, int to) {
        boolean twoRowsAway = (Math.abs(from / 5 - to / 5) == 2);
        boolean twoColsAway = Math.abs(from % 5 - to % 5) == 2;
        boolean onDiagonalLine = (from % 2 == 0);
        return twoRowsAway && twoColsAway && onDiagonalLine;
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        return (allOneJumpsFromK(k).size() >= 1);
    }

    /** given a starting index, return a list of all possible jumps. **/
    MoveList allOneJumpsFromK(int k) {
        MoveList possibleJumps = new MoveList();
        if (get(k) == EMPTY || get(k) != whoseMove()) {
            return possibleJumps;
        }
        int[] verticalJumps = {k + 10, k - 10};
        int[] horizontalJumps = {k + 2, k - 2};
        int[] diagonalJumps = {k + 8, k - 8, k + 12, k - 12};

        //vertical jumps
        for (int jIndex : verticalJumps) {
            if (jumpAllowed(k, jIndex)) {
                possibleJumps.add(Move.moveLinIndex(k, jIndex));
            }
        }
        //horizontal jumps
        for (int jIndex : horizontalJumps) {
            if (jumpAllowed(k, jIndex) && validHorizontalJump(k, jIndex)) {
                possibleJumps.add(Move.moveLinIndex(k, jIndex));
            }
        }
        //diagonal jumps
        for (int jIndex : diagonalJumps) {
            if (jumpAllowed(k, jIndex) && validDiagonalJump(k, jIndex)) {
                possibleJumps.add(Move.moveLinIndex(k, jIndex));
            }
        }
        return possibleJumps;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** sets _whoseMove to PieceColor WHO. **/
    void setWhoseMove(PieceColor who) {
        _whoseMove = who;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        if (legalMove(mov)) {
            _undoDirection.push(_direction.clone());
            _undoPieces.push(_allPieces.clone());
            makeMoveHelper(mov);
            setDirections(mov);
            while (mov.jumpTail() != null) {
                mov = mov.jumpTail();
                setDirections(mov);
                makeMoveHelper(mov);
            }
        } else {
            //throw some kind of error message
            System.out.println("bad move my friend!");
        }
        setWhoseMove(whoseMove().opposite());
        setChanged();
        notifyObservers();
    }

    /** For each move, makes sure that the pieceColor reflect
     *  the current configuration.
     */
    void makeMoveHelper(Move mov) {
        set(mov.toIndex(), get(mov.fromIndex()));
        set(mov.fromIndex(), EMPTY);
        if (mov.isJump()) {
            set(mov.jumpedIndex(), EMPTY);
        }
    }

    /** For each move, makes sure that directions reflect the current
     *  configuration.**/
    void setDirections(Move mov) {

        //sets the toIndex to whatever direction it came from
        if (mov.isLeftMove()) {
            _direction[mov.toIndex()] = -1;
        } else if (mov.isRightMove()) {
            _direction[mov.toIndex()] = 1;
        } else {
            _direction[mov.toIndex()] = 0;
        }
        //sets from index to 0
        _direction[mov.fromIndex()] = 0;
        //if it is a jump, also set jumpedIndex = 0
        if (mov.isJump()) {
            _direction[mov.jumpedIndex()] = 0;
        }
    }

    /** returns a copy of _directions for testing. **/
    public int[] getDirections() {
        return _direction.clone();
    }

    /** Undo the last move, if any. */
    void undo() {
        if (_undoPieces.isEmpty()) {
            return;
        }
        PieceColor[] prevPieces = _undoPieces.pop();
        int[] prevDirections = _undoDirection.pop();
        //reverse whoseMove
        _whoseMove = _whoseMove.opposite();
        //if gameOver is true, then must be false in previous
        _gameOver = false;
        _direction = prevDirections;
        _allPieces = prevPieces;

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        //Formatter out = new Formatter();
        StringBuilder sb = new StringBuilder();
        for (char n = '5'; n >= '1'; n -= 1) {
            if (!(n == '5')) {
                sb.append("\n ");
            } else {
                sb.append(" ");
            }
            for (char c = 'a'; c <= 'e'; c += 1) {
                sb.append(" " + get(c, n).shortName());
            }
        }
        if (legend) {
            sb.append("\n  a b c d e");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object b2) {
        return this.toString().equals(b2.toString()) &&
                this.whoseMove() == ((Board)b2).whoseMove();
    }
//    private static class restorePiece {
//        PieceColor _who;
//        int _from;
//        int _to;
//
//        private restorePiece(PieceColor who, int from, int to) {
//            _who = who;
//            _from = from;
//            _to = to;
//        }
//
//        /** which piece to relocate. **/
//        PieceColor getWho() {
//            return _who;
//        }
//
//        /** the linearized index to move from. **/
//        int getFrom() {
//            return _from;
//        }
//
//        /** the linearized index to return piece back to in order to
//         * restore previous board. **/
//        int getTo() {
//            return _to;
//        }
//    }

    /** keeps track of move history for undo **/
    //private Stack<Board> _undoBoard = new Stack<>();

    /** keeps track of PieceColor history for undo. **/
    private Stack<PieceColor[]> _undoPieces;
    /** keeps track of direction history for undo. **/
    private Stack<int[]> _undoDirection;

    /** If index k has val -1 then current piece at index k moved left to reach
     *  this position. if val is 0, then piece has no particular orientation.
     *  If index val is +1, then piece moved right to reach this position.
      */
    private int[] _direction;

    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        return false;  // FIXME
    }

    /** default string for a cleared board **/
    public static String clearedBoard = "wwwwwwwwwwbb-wwbbbbbbbbbb";

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Linearized representation of a two dimensional board **/
    private PieceColor[] _allPieces;

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}

// Written by Ian A. Gregory

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

class BoardLoc implements Comparable {
    
    int col, row;
    
    BoardLoc(int col, int row) {
        this.col = col;
        this.row = row;
    }
    
    public BoardLoc clone() {
        return new BoardLoc(col, row);
    }
    
    public int compareTo(Object object) {
        if (!(object instanceof BoardLoc)) return -1;
        BoardLoc other = (BoardLoc)object;
        if (col == other.col) {
            if (row == other.row) return 0;
            return (row < other.row) ? -1 : 1;
        }
        return (col < other.col) ? -1 : 1;
    }
    
}

class FallingLoc extends BoardLoc {
    
    int colDeviation, rowDeviation;
    
    public FallingLoc(int col, int row, int colDeviation, int rowDeviation) {
        super(col, row);
        this.colDeviation = colDeviation;
        this.rowDeviation = rowDeviation;
    }
    
    public FallingLoc clone() {
        return new FallingLoc(col, row, colDeviation, rowDeviation);
    }
    
    public void redeviate(int colDeviation, int rowDeviation) {
        this.col -= this.colDeviation;
        this.row -= this.rowDeviation;
        this.colDeviation = colDeviation;
        this.rowDeviation = rowDeviation;
        this.col += this.colDeviation;
        this.row += this.rowDeviation;
    }
    
}

class FallingLocFactory {
    
    private int centerCol, centerRow;
    
    public FallingLocFactory(int centerCol, int centerRow) {
        this.centerCol = centerCol;
        this.centerRow = centerRow;
    }
    
    public FallingLoc create(int colDeviation, int rowDeviation) {
        return new FallingLoc(centerCol + colDeviation, centerRow + rowDeviation, colDeviation, rowDeviation);
    }
    
}

class FallingPiece {
    
    enum Piece {
        L_PIECE, J_PIECE, T_PIECE, STRAIGHT_PIECE, SQUARE_PIECE, LEFT_DOWN_LEFT_PIECE, RIGHT_DOWN_RIGHT_PIECE;
        static Piece[] allValues = Piece.values();
        static Piece fromInt(int i) {
            return allValues[i];
        }
        private static Random random = new Random();
        static Piece random() {
            return fromInt(random.nextInt(7));
        }
    }
    
    Piece piece;
    FallingLoc[] spaces;
    FallingLoc[] savedStateSpaces;
    
    public FallingPiece(Piece piece) {
        this.piece = piece;
        // TMP
        // this.piece = Piece.STRAIGHT_PIECE;
        FallingLocFactory factory;
        switch (piece) {
            case L_PIECE:
                factory = new FallingLocFactory(4, 2);
                spaces = new FallingLoc[] {
                    factory.create(-1, 0),
                    factory.create(0, 0),
                    factory.create(1, 0),
                    factory.create(1, -1)
                };
                break;
            case J_PIECE:
                factory = new FallingLocFactory(5, 2);
                spaces = new FallingLoc[] {
                    factory.create(-1, -1),
                    factory.create(-1, 0),
                    factory.create(0, 0),
                    factory.create(1, 0)
                };
                break;
            case T_PIECE:
                factory = new FallingLocFactory(4, 0);
                spaces = new FallingLoc[] {
                    factory.create(-1, 0),
                    factory.create(0, 0),
                    factory.create(1, 0),
                    factory.create(0, 1)
                };
                break;
            case STRAIGHT_PIECE:
                factory = new FallingLocFactory(4, 1);
                spaces = new FallingLoc[] {
                    factory.create(0, -1),
                    factory.create(0, 0),
                    factory.create(0, 1),
                    factory.create(0, 2)
                };
                break;
            case SQUARE_PIECE:
                factory = new FallingLocFactory(4, 1);
                spaces = new FallingLoc[] {
                    factory.create(1, 0),
                    factory.create(1, -1),
                    factory.create(0, 0),
                    factory.create(0, -1)
                };
                break;
            case LEFT_DOWN_LEFT_PIECE:
                factory = new FallingLocFactory(5, 1);
                spaces = new FallingLoc[] {
                    factory.create(1, -1),
                    factory.create(0, -1),
                    factory.create(0, 0),
                    factory.create(-1, 0)
                };
                break;
            case RIGHT_DOWN_RIGHT_PIECE:
                factory = new FallingLocFactory(5, 1);
                spaces = new FallingLoc[] {
                    factory.create(-1, -1),
                    factory.create(0, -1),
                    factory.create(0, 0),
                    factory.create(1, 0)
                };
                break;
        }
        sortSpaces();
    }
    
    public void saveState() {
        savedStateSpaces = new FallingLoc[spaces.length];
        for (int i = 0; i < savedStateSpaces.length; ++i) {
            savedStateSpaces[i] = spaces[i].clone();
        }
    }
    
    public void restoreState() {
        spaces = new FallingLoc[savedStateSpaces.length];
        for (int i = 0; i < spaces.length; ++i) {
            spaces[i] = savedStateSpaces[i].clone();
        }
    }
    
    public void rotate() {
        for (FallingLoc loc : spaces) {
            if (loc.colDeviation == 0 && loc.rowDeviation == 0) loc.redeviate(0, 0);
            else if (loc.rowDeviation == 0) loc.redeviate(0, loc.colDeviation);
            else if (loc.colDeviation == 0) loc.redeviate(-loc.rowDeviation, 0);
            else if (loc.colDeviation == -2 && loc.rowDeviation == -2) loc.redeviate(2, -2);
            else if (loc.colDeviation == -1 && loc.rowDeviation == -2) loc.redeviate(2, -1);
            else if (loc.colDeviation == 1 && loc.rowDeviation == -2) loc.redeviate(2, 1);
            else if (loc.colDeviation == 2 && loc.rowDeviation == -2) loc.redeviate(2, 2);
            else if (loc.colDeviation == -2 && loc.rowDeviation == -1) loc.redeviate(1, -2);
            else if (loc.colDeviation == -1 && loc.rowDeviation == -1) loc.redeviate(1, -1);
            else if (loc.colDeviation == 1 && loc.rowDeviation == -1) loc.redeviate(1, 1);
            else if (loc.colDeviation == 2 && loc.rowDeviation == -1) loc.redeviate(1, 2);
            else if (loc.colDeviation == -2 && loc.rowDeviation == 1) loc.redeviate(-1, -2);
            else if (loc.colDeviation == -1 && loc.rowDeviation == 1) loc.redeviate(-1, -1);
            else if (loc.colDeviation == 1 && loc.rowDeviation == 1) loc.redeviate(-1, 1);
            else if (loc.colDeviation == 2 && loc.rowDeviation == 1) loc.redeviate(-1, 2);
            else if (loc.colDeviation == -2 && loc.rowDeviation == 2) loc.redeviate(-2, -2);
            else if (loc.colDeviation == -1 && loc.rowDeviation == 2) loc.redeviate(-2, -1);
            else if (loc.colDeviation == 1 && loc.rowDeviation == 2) loc.redeviate(-2, 1);
            else if (loc.colDeviation == 2 && loc.rowDeviation == 2) loc.redeviate(-2, 2);
            else assert false;
        }
        
        sortSpaces();
    }
    
    private void sortSpaces() {
        java.util.Arrays.sort(spaces);
    }
    
}

public class Game implements InputListener {
    
    private Renderer renderer;
    private InputNotifier inputNotifier;
    
    private int[][] board; // Column-wise
    private FallingPiece fallingPiece, fallingPieceShadow;
    private FallingPiece.Piece heldPiece;
    private FallingPiece.Piece lastPiece = FallingPiece.Piece.random();
    private boolean fastFall = false, canHold = true;
    
    private long lastPieceFallTime = System.nanoTime();
    private final int COMMIT_WAIT_COUNTER_RESET = 1;
    private int commitWaitCounter = COMMIT_WAIT_COUNTER_RESET;
    
    private boolean gameOn = true, isPaused = false;
    private boolean shouldQuit = false;
    private boolean boardNeedsRedraw = true;
    
    private int speed = 1;
    private int score = 0, linesCleared = 0;
    
    public Game(Renderer renderer, InputNotifier inputNotifier) {
        this.renderer = renderer;
        inputNotifier.setListener(this);
        inputNotifier.start();
        this.inputNotifier = inputNotifier;
        
        this.board = new int[10][20];
        newFallingPiece();
    }
    
    public void run() {
        while (gameOn) {
            if (isPaused) waitForUnpause();
            handleInput();
            moveFallingPiece();
            render();
            try { Thread.sleep(5); } catch (Exception e) {}
        }
        while (!shouldQuit) {
            try { Thread.sleep(20); } catch (Exception e) {}
        }
    }
    
    private void quit() {
        System.exit(0);
    }
    
    private void waitForUnpause() {
        while (true) {
            try { Thread.sleep(100); } catch (Exception e) {}
            if (unhandledInput.contains("p")) break;
            if (unhandledInput.contains("q")) quit();
        }
        unhandledInput = "";
        isPaused = false;
    }
    
    private String unhandledInput = "";
    
    public void listenInput(String input) {
        unhandledInput += input;
    }
    
    private void handleInput() {
        while (true) {
            if (unhandledInput.contains("q")) {
                quit();
            } else if (takeInput("\u001b[D")) {
                // Left arrow key
                moveFallingPieceLeft();
            } else if (takeInput("\u001b[C")) {
                // Right arrow key
                moveFallingPieceRight();
            } else if (takeInput("\u001b[A")) {
                // Up arrow key
                rotateFallingPiece();
            } else if (takeInput("\u001b[B")) {
                // Down arrow key
                fastFall = !fastFall;
            } else if (takeInput(" ")) {
                instantDropFallingPieceAndCommit();
            } else if (takeInput("c")) {
                holdFallingPiece();
            } else if (takeInput("p")) {
                isPaused = !isPaused;
                boardNeedsRedraw = true;
                render();
            } else if (takeInput("d")) {
                clearRow(0, 1); // Debug: +1 row cleared
            } else if (unhandledInput.length() != 0) {
                unhandledInput = unhandledInput.substring(1);
            } else {
                return;
            }
        }
    }
    
    private boolean takeInput(String toMatch) {
        if (!unhandledInput.startsWith(toMatch)) return false;
        unhandledInput = unhandledInput.substring(toMatch.length());
        return true;
    }
    
    private void moveFallingPiece() {
        long now = System.nanoTime();
        if (now - lastPieceFallTime > (((fastFall ? 80 : 800) / speed)) * 1000 * 1000) {
            lastPieceFallTime = now;
            if (canMoveFallingPieceDown()) {
                moveFallingPieceDown();
            } else {
                tryCommitFallingPiece();
            }
        }
    }
    
    private boolean canMoveFallingPieceDown() {
        return canMoveFallingPieceDown(fallingPiece);
    }
    
    private boolean canMoveFallingPieceDown(FallingPiece fallingPiece) {
        for (int i = fallingPiece.spaces.length - 1; i >= 0; --i) {
            BoardLoc loc = fallingPiece.spaces[i];
            
            int newRow = loc.row + 1;
            if (newRow == board[0].length) return false;
            if (getSpace(loc.col, newRow) != 0) {
                if (!doesSpaceHaveFallingPiece(loc.col, newRow) && !doesSpaceHaveShadowPiece(loc.col, newRow)) return false;
            }
        }
        return true;
    }
    
    private boolean canMoveFallingPieceLeft() {
        for (BoardLoc loc : fallingPiece.spaces) {
            int newCol = loc.col - 1;
            if (newCol < 0) return false;
            if (getSpace(newCol, loc.row) != 0) {
                if (!doesSpaceHaveFallingPiece(newCol, loc.row) && !doesSpaceHaveShadowPiece(newCol, loc.row)) return false;
            }
        }
        return true;
    }
    
    private boolean canMoveFallingPieceRight() {
        for (BoardLoc loc : fallingPiece.spaces) {
            int newCol = loc.col + 1;
            if (newCol == board.length) return false;
            if (getSpace(newCol, loc.row) != 0) {
                if (!doesSpaceHaveFallingPiece(newCol, loc.row) && !doesSpaceHaveShadowPiece(newCol, loc.row)) return false;
            }
        }
        return true;
    }
    
    private boolean doesSpaceHaveFallingPiece(int col, int row) {
        for (BoardLoc loc : fallingPiece.spaces) {
            if (loc.col == col && loc.row == row) return true;
        }
        return false;
    }
    
    private boolean doesSpaceHaveShadowPiece(int col, int row) {
        if (fallingPieceShadow == null) return false;
        for (BoardLoc loc : fallingPieceShadow.spaces) {
            if (loc.col == col && loc.row == row) return true;
        }
        return false;
    }
    
    private void moveFallingPieceDown() {
        moveFallingPieceDown(fallingPiece);
    }
    
    private void moveFallingPieceDown(FallingPiece fallingPiece) {
        if (!canMoveFallingPieceDown(fallingPiece)) return;
        
        for (int i = fallingPiece.spaces.length - 1; i >= 0; --i) {
            BoardLoc loc = fallingPiece.spaces[i];
            moveSpace(loc.col, loc.row, loc.col, loc.row + 1);
            loc.row++;
        }
        resetCommitWaitCounter();
    }
    
    private void moveFallingPieceLeft() {
        if (!canMoveFallingPieceLeft()) return;
        
        for (int i = 0; i < fallingPiece.spaces.length; ++i) {
            BoardLoc loc = fallingPiece.spaces[i];
            moveSpace(loc.col, loc.row, loc.col - 1, loc.row);
            loc.col--;
        }
        resetCommitWaitCounter();
        recalcFallingPieceShadow();
    }
    
    private void moveFallingPieceRight() {
        if (!canMoveFallingPieceRight()) return;
        
        for (int i = fallingPiece.spaces.length - 1; i >= 0; --i) {
            BoardLoc loc = fallingPiece.spaces[i];
            moveSpace(loc.col, loc.row, loc.col + 1, loc.row);
            loc.col++;
        }
        resetCommitWaitCounter();
        recalcFallingPieceShadow();
    }
    
    private void tryCommitFallingPiece() {
        if (commitWaitCounter-- > 0) {
            for (BoardLoc loc : fallingPiece.spaces) {
                setSpace(loc.col, loc.row, 2, colorIDForSpaceValue(getSpace(loc.col, loc.row)));
            }
            render();
            return;
        }
        resetCommitWaitCounter();
        instantCommitFallingPiece();
    }
    
    private void resetCommitWaitCounter() {
        commitWaitCounter = COMMIT_WAIT_COUNTER_RESET * (int)Math.ceil(speed / 2.0);
    }
    
    private void instantDropFallingPieceAndCommit() {
        instantDropFallingPiece();
        instantCommitFallingPiece();
    }
    
    private void instantCommitFallingPiece() {
        fallingPieceShadow = null;
        for (BoardLoc loc : fallingPiece.spaces) {
            setSpace(loc.col, loc.row, 1, colorIDForSpaceValue(getSpace(loc.col, loc.row)));
        }
        checkForCompletedRows();
        newFallingPiece();
        canHold = true;
        fastFall = false;
    }
    
    private void instantDropFallingPiece() {
        instantDropFallingPiece(fallingPiece);
    }
    
    private void instantDropFallingPiece(FallingPiece fallingPiece) {
        while (canMoveFallingPieceDown(fallingPiece)) moveFallingPieceDown(fallingPiece);
    }
    
    private void holdFallingPiece() {
        if (!canHold) return;
        canHold = false;
        
        for (BoardLoc loc : fallingPiece.spaces) {
            setSpace(loc.col, loc.row, 0, 0);
        }
        
        FallingPiece.Piece prevHeldPiece = heldPiece;
        heldPiece = fallingPiece.piece;
        if (prevHeldPiece != null) newFallingPiece(prevHeldPiece);
        else newFallingPiece();
    }
    
    private void rotateFallingPiece() {
        fallingPiece.saveState();
        fallingPiece.rotate();
        int minCol = board.length, minRow = board[0].length, maxCol = 0, maxRow = 0;
        for (BoardLoc loc : fallingPiece.spaces) {
            // System.out.printf("{%d,%d}", loc.col, loc.row);
            minCol = Math.min(loc.col, minCol);
            minRow = Math.min(loc.row, minRow);
            maxCol = Math.max(loc.col, maxCol);
            maxRow = Math.max(loc.row, maxRow);
        }
        for (BoardLoc loc : fallingPiece.spaces) {
            if (minCol < 0) loc.col += 0 - minCol;
            if (maxCol >= board.length) loc.col += board.length - 1 - maxCol;
            if (minRow < 0) loc.row += 0 - minRow;
            if (maxRow >= board[0].length) loc.row += board[0].length - 1 - maxRow;
            
            if (getSpace(loc.col, loc.row) != 0 && !doesSpaceHaveShadowPiece(loc.col, loc.row)) {
                boolean ok = false;
                for (BoardLoc otherLoc : fallingPiece.savedStateSpaces) {
                    if (loc.col == otherLoc.col && loc.row == otherLoc.row) {
                        // Block is part of old falling piece; no worries overwriting it
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    // Reject rotation: would delete blocks not part of falling piece
                    fallingPiece.restoreState();
                    return;
                }
            }
        }
        for (BoardLoc loc : fallingPiece.savedStateSpaces) {
            setSpace(loc.col, loc.row, 0, 0);
        }
        for (BoardLoc loc : fallingPiece.spaces) {
            setSpace(loc.col, loc.row, 1, 0);
        }
        
        recalcFallingPieceShadow();
    }
    
    private void checkForCompletedRows() {
        checkForCompletedRows(1);
    }
    
    private void checkForCompletedRows(int scoreMultiplier) {
        checkRows: for (int row = board[0].length - 1; row >= 0; --row) {
            for (int col = 0; col < board.length; ++col) {
                if (getSpace(col, row) == 0) continue checkRows;
            }
            clearRow(row, scoreMultiplier);
            checkForCompletedRows(scoreMultiplier + 1);
            break;
        }
    }
    
    private void clearRow(int row, int scoreMultiplier) {
        linesCleared++;
        score += 10 * scoreMultiplier;
        if (linesCleared % 10 == 0) speed++;
        
        for (int col = 0; col < board.length; ++col) {
            setSpace(col, row, 0, 0);
        }
        // Apply gravity
        for (int srcRow = row - 1; srcRow >= 0; --srcRow) {
            try { Thread.sleep(25); } catch (Exception e) {}
            render();
            
            int dstRow = srcRow + 1;
            if (dstRow == board[0].length) continue;
            for (int col = 0; col < board.length; ++col) {
                moveSpace(col, srcRow, col, dstRow);
            }
        }
    }
    
    private void newFallingPiece() {
        newFallingPiece(nextPiece());
    }
    
    private FallingPiece.Piece nextPiece() {
        FallingPiece.Piece piece;
        do {
            piece = FallingPiece.Piece.random();
        } while (piece == lastPiece);
        lastPiece = piece;
        return piece;
    }
    
    private void newFallingPiece(FallingPiece.Piece piece) {
        fallingPiece = new FallingPiece(piece);
        placeFallingPieceOnBoard();
        recalcFallingPieceShadow();
    }
    
    private void recalcFallingPieceShadow() {
        removeFallingPieceFromBoard();
        if (fallingPieceShadow != null) removeFallingPieceFromBoard(fallingPieceShadow);
        
        fallingPieceShadow = new FallingPiece(fallingPiece.piece);
        for (int i = 0; i < fallingPieceShadow.spaces.length; ++i) {
            fallingPieceShadow.spaces[i] = fallingPiece.spaces[i].clone();
        }
        instantDropFallingPiece(fallingPieceShadow);
        for (BoardLoc shadowLoc : fallingPieceShadow.spaces) {
            for (BoardLoc loc : fallingPiece.spaces) {
                if (shadowLoc.col == loc.col && shadowLoc.row == loc.row) {
                    // Actual piece is on top of shadow piece, overrides it
                    setSpace(loc.col, loc.row, 0, 0);
                }
            }
        }
        
        placeFallingPieceOnBoard(fallingPieceShadow, 5, false);
        placeFallingPieceOnBoard();
    }
    
    private void placeFallingPieceOnBoard() {
        placeFallingPieceOnBoard(fallingPiece, 1, true);
    }
    
    private void placeFallingPieceOnBoard(FallingPiece fallingPiece, int spaceValue, boolean loseIfImpossible) {
        for (BoardLoc loc : fallingPiece.spaces) {
            if (loseIfImpossible && getSpace(loc.col, loc.row) != 0 && !doesSpaceHaveShadowPiece(loc.col, loc.row)) lose();
            setSpace(loc.col, loc.row, spaceValue, colorIDForPiece(fallingPiece.piece));
        }
    }
    
    private static int colorIDForPiece(FallingPiece.Piece piece) {
        for (int i = 0; i < FallingPiece.Piece.allValues.length; ++i) {
            if (piece == FallingPiece.Piece.allValues[i]) return i + 1;
        }
        return 0;
    }
    
    private void removeFallingPieceFromBoard() {
        removeFallingPieceFromBoard(fallingPiece);
    }
    
    private void removeFallingPieceFromBoard(FallingPiece fallingPiece) {
        for (BoardLoc loc : fallingPiece.spaces) {
            setSpace(loc.col, loc.row, 0);
        }
    }
    
    private void lose() {
        gameOn = false;
        renderer.newFrame();
        renderer.render("\r\n\r\n\r\n");
        for (int i = 0; i < 2; ++i) {
            renderer.render(String.format("%80s\r\n", ""));
        }
        renderer.render(String.format("%36s%30s", "GAME OVER!", ""));
        for (int i = 0; i < 2; ++i) {
            renderer.render(String.format("%80s\r\n", ""));
        }
        try { Thread.sleep(2000); } catch (Exception e) {}
        
        renderer.newFrame();
        shouldQuit = true;
    }
    
    private void swapSpaces(int col1, int row1, int col2, int row2) {
        int tmp = getSpace(col1, row1);
        copySpace(col1, row1, col2, row2);
        setSpace(col2, row2, tmp);
    }
    
    private void moveSpace(int srcCol, int srcRow, int dstCol, int dstRow) {
        copySpace(srcCol, srcRow, dstCol, dstRow);
        setSpace(srcCol, srcRow, 0, 0);
    }
    
    private void copySpace(int srcCol, int srcRow, int dstCol, int dstRow) {
        setSpace(dstCol, dstRow, getSpace(srcCol, srcRow));
    }
    
    private int getSpace(int col, int row) {
        return board[col][row];
    }
    
    private void setSpace(int col, int row, int newBlock, int newColor) {
        setSpace(col, row, makeSpaceValue((short)newBlock, (short)newColor));
    }
    
    private void setSpace(int col, int row, int newValue) {
        board[col][row] = newValue;
        boardNeedsRedraw = true;
    }
    
    private void render() {
        if (!boardNeedsRedraw) return;
        boardNeedsRedraw = false;
        
        renderer.newFrame();
        for (int row = 0; row < board[0].length; ++row) {
            renderer.render(String.format("%20s", ""));
            renderer.render(stringForSpaceValue(3));
            
            for (int col = 0; col < board.length; ++col) {
                int space = getSpace(col, row);
                renderer.setColor(colorForSpaceValue(space));
                renderer.render(stringForSpaceValue(space));
                if (col + 1 < board.length) renderer.render(" ");
            }
            
            renderer.setColor(Renderer.Color.DEFAULT);
            renderer.render(stringForSpaceValue(3));
            String sidebarText = "";
            boolean rightAlign = false;
            if (isPaused) {
                switch (row) {
                    case 1:
                        sidebarText = "Java TETRIS";
                        rightAlign = true;
                        break;
                    case 2:
                        sidebarText = "by Ian Gregory";
                        rightAlign = true;
                        break;
                    case 5:
                        sidebarText = "Objective:";
                        rightAlign = true;
                        break;
                    case 6:
                        sidebarText = "Fill the screen horizontally";
                        break;
                    case 7:
                        sidebarText = "with squares to clear lines.";
                        break;
                    case 8:
                        sidebarText = "More lines at once means more";
                        break;
                    case 9:
                        sidebarText = "points. Whatever you do, don't";
                        break;
                    case 10:
                        sidebarText = "let the squares reach the top!";
                        break;
                    case 12:
                        sidebarText = "◀︎ ▶︎  move piece sideways";
                        break;
                    case 13:
                        sidebarText = "▲    rotate piece";
                        break;
                    case 14:
                        sidebarText = "(space) instant drop";
                        break;
                    case 15:
                        sidebarText = "▼    toggle fast fall";
                        break;
                    case 16:
                        sidebarText = "c    hold piece/release held";
                        break;
                    case 17:
                        sidebarText = "q    quit Java TETRIS";
                        break;
                    case 19:
                        sidebarText = "PAUSED          ";
                        rightAlign = true;
                        break;
                }
                
            } else {
                switch (row) {
                    case 1:
                        sidebarText = "Score: " + score;
                        break;
                    case 2:
                        sidebarText = "Lines cleared: " + linesCleared;
                        break;
                    case 4:
                        sidebarText = "Speed: " + speed + "x";
                        break;
                    case 19:
                        sidebarText = "p to pause and";
                        break;
                }
            }
            renderer.render(String.format("%" + (rightAlign ? "" : "-") + "30s", "  " + sidebarText));
            
            if (row + 1 < board[0].length) renderer.render("\r\n");
        }
        renderer.render("\r\n");
        renderer.render("                    ");
        for (int col = 0; col < board[0].length + 1; ++col) {
            renderer.render(stringForSpaceValue(4));
        }
        if (isPaused) {
            renderer.render(String.format("%30s", "p    resume       "));
        } else {
            renderer.render(String.format("%-30s", "  display controls"));
        }
        renderer.render("\r\n");
    }
    
    private static int makeSpaceValue(short block, short color) {
        return ((int)color << 16) + (int)block;
    }
    
    private static int tileIDForSpaceValue(int spaceValue) {
        return spaceValue & 0xFFFF;
    }
    
    private static int colorIDForSpaceValue(int spaceValue) {
        return (spaceValue & 0xFFFF0000) >> 0x10;
    }
    
    private static String stringForSpaceValue(int spaceValue) {
        switch (tileIDForSpaceValue(spaceValue)) {
            case 0: return " ";
            case 1: return "▣";
            case 2: return "■";
            case 3: return "█";
            case 4: return "▀";
            case 5: return "▢";
            default: return "";
        }
    }
    
    private static Renderer.Color colorForSpaceValue(int spaceValue) {
        switch (colorIDForSpaceValue(spaceValue)) {
            case 0: return Renderer.Color.DEFAULT;
            case 1: return Renderer.Color.DARK_BLUE;
            case 2: return Renderer.Color.DARK_YELLOW;
            case 3: return Renderer.Color.DARK_MAGENTA;
            case 4: return Renderer.Color.BRIGHT_CYAN;
            case 5: return Renderer.Color.BRIGHT_YELLOW;
            case 6: return Renderer.Color.BRIGHT_GREEN;
            case 7: return Renderer.Color.BRIGHT_RED;
            default: return Renderer.Color.DEFAULT;
        }
    }
    
} // end class

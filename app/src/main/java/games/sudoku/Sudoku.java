package games.sudoku;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Sudoku {

    private static final int BOARD_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    public static void main(String[] args) {
        String difficulty = "easy"; // Change difficulty level as needed

        int[][] sudoku = generateSudoku(difficulty);
        printSudoku(sudoku);
    }

    public static int[][] generateSudoku(String difficulty) {
        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
        fillValues(board);
        removeValues(board, difficulty);
        return board;
    }

    private static void fillValues(int[][] board) {
        fillDiagonalSubgrids(board);
        solveSudoku(board);
    }

    private static void fillDiagonalSubgrids(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i += SUBGRID_SIZE) {
            fillSubgrid(board, i, i);
        }
    }

    private static void fillSubgrid(int[][] board, int row, int col) {
        List<Integer> numbers = new ArrayList<>();
        for (int num = 1; num <= BOARD_SIZE; num++) {
            numbers.add(num);
        }
        Collections.shuffle(numbers);

        int index = 0;
        for (int i = row; i < row + SUBGRID_SIZE; i++) {
            for (int j = col; j < col + SUBGRID_SIZE; j++) {
                board[i][j] = numbers.get(index);
                index++;
            }
        }
    }

    private static boolean solveSudoku(int[][] board) {
        int[] emptyCell = findEmptyCell(board);
        if (emptyCell == null) {
            return true; // Puzzle is solved
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int num = 1; num <= BOARD_SIZE; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;

                if (solveSudoku(board)) {
                    return true;
                }

                board[row][col] = 0; // Backtrack
            }
        }

        return false;
    }

    private static int[] findEmptyCell(int[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (board[row][col] == 0) {
                    return new int[]{row, col};
                }
            }
        }
        return null; // No empty cell found
    }

    private static boolean isValid(int[][] board, int row, int col, int num) {
        return isRowValid(board, row, num) && isColumnValid(board, col, num) && isSubgridValid(board, row, col, num);
    }

    private static boolean isRowValid(int[][] board, int row, int num) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    private static boolean isColumnValid(int[][] board, int col, int num) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSubgridValid(int[][] board, int row, int col, int num) {
        int subgridStartRow = row - row % SUBGRID_SIZE;
        int subgridStartCol = col - col % SUBGRID_SIZE;

        for (int i = subgridStartRow; i < subgridStartRow + SUBGRID_SIZE; i++) {
            for (int j = subgridStartCol; j < subgridStartCol + SUBGRID_SIZE; j++) {
                if (board[i][j] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void removeValues(int[][] board, String difficulty) {
        Random random = new Random();
        int targetCount = 0;

        if (difficulty.equals("easy")) {
            targetCount = random.nextInt(20) + 40;
        } else if (difficulty.equals("medium")) {
            targetCount = random.nextInt(10) + 50;
        } else if (difficulty.equals("hard")) {
            targetCount = random.nextInt(10) + 60;
        }

        for (int i = 0; i < targetCount; i++) {
            int row = random.nextInt(BOARD_SIZE);
            int col = random.nextInt(BOARD_SIZE);

            if (board[row][col] != 0) {
                board[row][col] = 0;
            } else {
                i--;
            }
        }
    }

    private static void printSudoku(int[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}

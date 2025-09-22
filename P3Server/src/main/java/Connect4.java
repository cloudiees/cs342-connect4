import java.util.ArrayList;

public class Connect4 {
    public ArrayList<ArrayList<Character>> board;
    int moves;
    Connect4() {
        board = new ArrayList<>();
        moves = 0;
        for (int i = 0; i < 6; i++) {
            board.add(new ArrayList<>());
            for (int j = 0; j < 7; j++) {
                board.get(i).add(' ');
            }
        }
    }
    public void printBoard() {
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                System.out.print(board.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
    public boolean playMove(int column, char symbol) {
        for (int i = board.size() - 1; i >= 0; i--) {
            if (board.get(i).get(column) == ' ') {
                board.get(i).set(column, symbol);
                break;
            }
        }
        moves++;
        System.out.println("Move " + moves);
        return checkWinner(symbol);
    }

    public void resetBoard() {
        moves = 0;
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.get(i).size(); j++) {
                board.get(i).set(j, ' ');
            }
        }
    }

    public boolean checkWinner(Character symbol) {
        // Checking 4 up down
        printBoard();
        for (int row = 0; row < board.size() - 3; row++) {
            for (int col = 0; col < board.get(row).size(); col++) {
                if (board.get(row).get(col) == symbol
                        && board.get(row + 1).get(col) == symbol
                        && board.get(row + 2).get(col) == symbol
                        && board.get(row + 3).get(col) == symbol
                ) return true;
            }
        }
        // checking 4 left right
        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.get(row).size() - 3; col++) {
                if (board.get(row).get(col) == symbol
                        && board.get(row).get(col + 1) == symbol
                        && board.get(row).get(col + 2) == symbol
                        && board.get(row).get(col + 3) == symbol
                ) return true;
            }
        }
        // checking downwards diagonal
        for (int row = 0; row < board.size() - 3; row++) {
            for (int col = 0; col < board.get(row).size() - 3; col++) {
                if (board.get(row).get(col) == symbol
                        && board.get(row + 1).get(col+1) == symbol
                        && board.get(row + 2).get(col+2) == symbol
                        && board.get(row + 3).get(col+3) == symbol
                ) return  true;
            }
        }
        // Checking upwards diagonal
        for(int row = board.size() - 1; row >= 3; row--) {
            for (int col = 0; col < board.get(row).size() - 3; col++) {
                if (board.get(row).get(col) == symbol
                        && board.get(row - 1).get(col+1) == symbol
                        && board.get(row - 2).get(col+2) == symbol
                        && board.get(row - 3).get(col+3) == symbol
                ) return true;
            }
        }
        return false;
    }
}

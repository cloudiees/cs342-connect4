import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GameBoard {
    public HBox board;
    public ArrayList<ArrayList<Circle>> boardPieces;
    public boolean gameOver;
    public HBox pieces;
    public ArrayList<VBox> vBoxes;
    public ArrayList<ArrayList<Character>> textBoard;
    public ArrayList<Button> buttons;
    public int moves;
    public GameBoard() {
        moves = 0;
        gameOver = false;
        textBoard = new ArrayList<>();
        boardPieces = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            boardPieces.add(new ArrayList<>());
            textBoard.add(new ArrayList<>());
            for (int j = 0; j < 6; j++) {
                textBoard.get(i).add('E');
                Circle r = new Circle(25);
                r.getStyleClass().add("boardPiece");
                boardPieces.get(i).add(r);
            }
        }
        vBoxes = new ArrayList<>();
        buttons = new ArrayList<>();
        for (int i = 0; i < boardPieces.size(); i++) {
            buttons.add(new Button());
            buttons.get(i).setMaxHeight(40);
            buttons.get(i).setMinHeight(40);
            buttons.get(i).setPrefHeight(40);
            buttons.get(i).setMaxWidth(40);
            buttons.get(i).setMinWidth(40);
            buttons.get(i).setPrefWidth(40);
            buttons.get(i).getStyleClass().add("c4Button");
            vBoxes.add(new VBox(10, buttons.get(i)));
            for (int j = 0; j < boardPieces.get(i).size(); j++) {
                vBoxes.get(i).getChildren().add(boardPieces.get(i).get(j));
            }
            vBoxes.get(i).setAlignment(Pos.CENTER);
        }
        pieces = new HBox(10);
        for (int i = 0; i < vBoxes.size(); i++) {
            pieces.getChildren().add(vBoxes.get(i));
        }
        board = new HBox(10, pieces);
        pieces.setAlignment(Pos.CENTER);
        board.setAlignment(Pos.CENTER);
        board.getStyleClass().add("board");
    }

    public GameBoard(GameBoard gameBoard) {
        this.textBoard = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            this.textBoard.add(new ArrayList<>());
            for (int j = 0; j < 6; j++) {
                this.textBoard.get(i).add(gameBoard.textBoard.get(i).get(j));
            }
        }
    }

    public void printBoard() {
        for(int i = 0; i < textBoard.size(); i++) {
            for (int j = 0; j < textBoard.get(i).size(); j++) {
                System.out.print("("+textBoard.get(i).get(j) + ")"+  i + "-" + j + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void resetBoard(){
        moves = 0;
        for(int i = 0; i < textBoard.size(); i++) {
            for (int j = 0; j < textBoard.get(i).size(); j++) {
                textBoard.get(i).set(j, 'E');
                boardPieces.get(i).get(j).getStyleClass().clear();
                boardPieces.get(i).get(j).getStyleClass().add("boardPiece");
            }
        }
    }


    public boolean checkWinner(Character symbol) {
        // Checking left right
//        printBoard();
        for (int col = 0; col < textBoard.size() - 3; col++) {
            for (int row = 0; row < textBoard.get(col).size(); row++) {
                if (textBoard.get(col).get(row) == symbol
                        && textBoard.get(col + 1).get(row) == symbol
                        && textBoard.get(col + 2).get(row) == symbol
                        && textBoard.get(col + 3).get(row) == symbol
                ) return true;
            }
        }
        // checking 4 up down
        for (int col = 0; col < textBoard.size(); col++) {
            for (int row = 0; row < textBoard.get(col).size() - 3; row++) {
                if (textBoard.get(col).get(row) == symbol
                        && textBoard.get(col).get(row + 1) == symbol
                        && textBoard.get(col).get(row + 2) == symbol
                        && textBoard.get(col).get(row + 3) == symbol
                ) return true;
            }
        }
        // checking downwards diagonal
        for (int col = 0; col < textBoard.size() - 3; col++) {
            for (int row = 0; row < textBoard.get(col).size() - 3; row++) {
                if (textBoard.get(col).get(row) == symbol
                        && textBoard.get(col + 1).get(row+1) == symbol
                        && textBoard.get(col + 2).get(row+2) == symbol
                        && textBoard.get(col + 3).get(row+3) == symbol
                ) return  true;
            }
        }
        // Checking upwards diagonal
        for (int col = 0; col < textBoard.size() - 3; col++) {
            for (int row = textBoard.get(col).size() - 1; row >= textBoard.get(col).size() - 3; row--) {
                if (textBoard.get(col).get(row) == symbol
                        && textBoard.get(col + 1).get(row-1) == symbol
                        && textBoard.get(col + 2).get(row-2) == symbol
                        && textBoard.get(col + 3).get(row-3) == symbol
                ) return true;
            }
        }
        return false;
    }

    public boolean playMove(int column, char symbol) {
        for (int i = textBoard.get(column).size() - 1; i >= 0; i--) {
            if (textBoard.get(column).get(i) == 'E') {
                textBoard.get(column).set(i, symbol);
                break;
            }
        }
        moves++;
        return checkWinner(symbol);
    }

    public void update(int col, Character symbol) {
        for(int i = textBoard.get(col).size() - 1; i >= 0; i--) {
            if(textBoard.get(col).get(i).equals('E')) {
                textBoard.get(col).set(i, symbol);
                if(symbol == '1') {
                    boardPieces.get(col).get(i).getStyleClass().clear();
                    boardPieces.get(col).get(i).getStyleClass().add("style1piece");
                }
                else if(symbol == '2') {
                    boardPieces.get(col).get(i).getStyleClass().clear();
                    boardPieces.get(col).get(i).getStyleClass().add("style2piece");
                }
                moves++;
                break;
            }
        }
        printBoard();
    }
}


package org.vlasov.miner.model;

import lombok.Data;

import java.util.Random;
import java.util.UUID;

@Data
public class Game {
    private String game_id;
    private int width;
    private int height;
    private int minesCount;
    private String[][] field;
    private boolean[][] mines;
    private boolean completed;

    public Game(int width, int height, int minesCount) {
        this.game_id = UUID.randomUUID().toString();
        this.width = width;
        this.height = height;
        this.minesCount = minesCount;
        this.field = new String[height][width];
        this.mines = new boolean[height][width];
        this.completed = false;
        initializeField();
        placeMines();
    }

    private void initializeField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = " ";
                mines[i][j] = false;
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;
        while (minesPlaced < minesCount) {
            int row = random.nextInt(height);
            int col = random.nextInt(width);
            if (!mines[row][col]) {
                mines[row][col] = true;
                minesPlaced++;
            }
        }
    }

    public void processMove(Move move) {
        if (completed) {
            throw new IllegalStateException("Игра уже завершена");
        }

        int row = move.getRow();
        int col = move.getCol();

        if (row < 0 || row >= height || col < 0 || col >= width) {
            throw new IllegalArgumentException("Ход за пределы (" + row + "," + col + ")");
        }
        if (!" ".equals(field[row][col])) {
            throw new IllegalArgumentException("Клетка (" + row + "," + col + ") уже открыта.");
        }

        if (mines[row][col]) {
            this.completed = true;
            revealAllCells();
        } else {
            openCell(row, col);
            checkCompletion();
        }
    }


    private void openCell(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width || !" ".equals(field[row][col])) return;

        int minesAround = countMinesAround(row, col);
        field[row][col] = minesAround == 0 ? "0" : String.valueOf(minesAround);

        if (minesAround == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) openCell(row + i, col + j);
                }
            }
        }
    }

    private int countMinesAround(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newRow = row + i;
                int newCol = col + j;
                if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width && mines[newRow][newCol]) {
                    count++;
                }
            }
        }
        return count;
    }

    private void checkCompletion() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (" ".equals(field[i][j]) && !mines[i][j]) {
                    return;
                }
            }
        }
        this.completed = true;
        revealAllCells();
    }

    private void revealAllCells() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (mines[i][j]) {
                    field[i][j] = "X";
                } else {
                    int minesAround = countMinesAround(i, j);
                    field[i][j] = Integer.toString(minesAround);
                }
            }
        }
    }

}

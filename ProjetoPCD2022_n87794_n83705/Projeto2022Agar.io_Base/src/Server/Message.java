package Server;

import java.io.Serializable;

import environment.Cell;

public class Message implements Serializable {
	private boolean ended;
    private boolean alive;
	private Cell[][] board;

	public Message(Cell[][] board, boolean a, boolean b) {
		this.board = board;
		this.ended = a;
        this.alive=b;
	}

    public boolean getAlive(){
        return alive;
    }

	public Cell[][] getBoard() {
		return board;
	}

	public boolean getEnd() {
		return ended;
	}

}

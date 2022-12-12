package Server;

import java.io.Serializable;

import environment.Cell;


public class Message implements Serializable {
    private boolean ended;
    private Cell[][] board;

    public Message(Cell[][] board, boolean b){
        this.board=board;
        this.ended=b;
    }

    public Cell[][] getBoard(){
        return board;
    }

    public boolean getEnd(){
        return this.ended;
    }



}

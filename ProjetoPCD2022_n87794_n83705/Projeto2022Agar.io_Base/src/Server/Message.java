package Server;

import java.io.Serializable;
import java.util.ArrayList;

import environment.Cell;
import environment.Direction;
import game.Player;

public class Message implements Serializable {
   // private ArrayList<GameInfo> list;
    private Direction direction;
    private boolean ended;
    private Cell[][] board;

    /*mensagem de comunicacao servidor-cliente
    public Message(ArrayList<GameInfo> players, boolean b){
        this.list=players;
        this.ended=b;
    }*/

    public Message(Cell[][] board, boolean b){
        this.board=board;
        this.ended=b;
    }

    public Cell[][] getBoard(){
        return board;
    }

    /*public ArrayList<GameInfo> getList(){
        return this.list;
    }*/

    public boolean getEnd(){
        return this.ended;
    }



}

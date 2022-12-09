package Server;

import java.io.Serializable;
import java.util.ArrayList;

import environment.Direction;
import game.Player;

public class Message implements Serializable {
    private ArrayList<Player> humans;
    private Direction direction;
    private int id;
    private String msg;

     //mensagem de conexao cliente-servidor
     public Message(int id){
        setPlayerId(id);
    }

    //mensagem de comunicacao cliente-servidor
    public Message(int id, Direction direction){
        setDirection(direction);
        setPlayerId(id);
    }

    //mensagem de comunicacao servidor-cliente
    public Message(ArrayList<Player> players){
        setList(players);
    }

    //mensagem para fim de comunicação cliente-servidor
    public Message(String s){
        setMsg(s);
    }

    public void setMsg(String s){
        this.msg=s;
    }

    public void setDirection(Direction dir){
        this.direction=dir;
    }

    public void setPlayerId(int player){
        this.id=player;
    }

    public void setList(ArrayList<Player> players){
        this.humans=players;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public String getMsg(){
        return this.msg;
    }

    public int getPlayerId(){
        return this.id;
    }
    
    public ArrayList<Player> getList(){
        return this.humans;
    }




}

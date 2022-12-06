package Server;

import java.io.Serializable;

import environment.Direction;

public class Message implements Serializable {
    private Direction direction;
    private int id;

   // public Message(){
   // }

    public void setDirection(Direction dir){
        this.direction=dir;
    }

    public void setPlayerId(int player){
        this.id=player;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public int getPlayerId(){
        return this.id;
    }


}

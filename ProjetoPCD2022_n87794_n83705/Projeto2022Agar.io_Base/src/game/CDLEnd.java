package game;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;


public class CDLEnd implements Serializable{

	boolean finished=false;
	int goal;

    public CDLEnd(Game game){
        this.goal=game.NUM_WINNERS;
    }

    public synchronized void await() throws InterruptedException {
		wait();
	}
    
    public int getWinners() {
    	return goal;
    }

	public synchronized void decrement(int id) {
		synchronized(this) {
			System.out.println("O jogador " + id + " chegou a energia maxima E VENCEU !!\n--------------------------------------------\n");	
			goal--;
			if(goal==0) {
				finished=true;
				notifyAll();
			}	
		}
	}
    
}

package game;

import java.io.Serializable;


public class CDLEnd implements Serializable{

    private Game game;
	boolean finished=false;
	private int goal;

    public CDLEnd(Game game){
        this.game=game;
    }

    public synchronized void await() throws InterruptedException {
		while (!finished) {
			wait();
		}
	}

	public synchronized void countDownLatch(int id) {
		System.out.println("O jogador " + id + " chegou a energia maxima E VENCEU !!\n--------------------------------------------\n");
		goal++;
		synchronized(this){
			if(goal==game.NUM_WINNERS){
				finished=true;
				notifyAll();
			}	
		}				
	}
    
}

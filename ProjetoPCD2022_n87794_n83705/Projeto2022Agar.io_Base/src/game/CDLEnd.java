package game;

import java.io.Serializable;

@SuppressWarnings("serial")
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

    //se um jogador vencer goal � decrementado e quando chegar a 0 notifica a Thread a espera deste objeto
	public synchronized void decrement(int id) {
		synchronized(this) {
			System.out.println("\n O jogador " + id + " chegou a energia maxima E VENCEU !!\n");	
			goal--;
			if(goal==0) {
				finished=true;
				notifyAll();
			}	
		}
	}
    
}

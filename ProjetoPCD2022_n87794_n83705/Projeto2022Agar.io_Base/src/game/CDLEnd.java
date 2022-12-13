package game;

import java.io.Serializable;

public class CDLEnd implements Serializable{

    public int goal=0;
    private Game game;

    public CDLEnd(Game game){
        this.game=game;
        game.setCDL(this);
    }

    /*public synchronized void await() throws InterruptedException {
		while (goal != game.NUM_WINNERS) {
			wait();
		}
	}*/

	public synchronized void countDownLatch(int id) {
		System.out.println("O jogador " + id + " chegou a energia maxima E VENCEU !!\n--------------------------------------------\n");
		goal++;
		if (goal == game.NUM_WINNERS)
			notifyAll();
	}
    
}

package game;

import java.io.Serializable;

public class EndThread extends Thread implements Serializable{
    private Game game;
	private CDLEnd countdownlatch;

    public EndThread(Game game) {
		this.game = game;
		countdownlatch = new CDLEnd(game);
	}

    public synchronized void run() {
        synchronized(countdownlatch){ 
            while(countdownlatch.goal!=game.NUM_WINNERS){
                try {
                    countdownlatch.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                game.endGame();
            }   
        }

	}

}

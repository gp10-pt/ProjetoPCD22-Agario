package game;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("serial")
public class EndThread extends Thread implements Serializable{
    private Game game;
	private CDLEnd countdownlatch;

    public EndThread(Game game) {
		this.game = game;
		this.countdownlatch = new CDLEnd(game);
        game.setCDL(countdownlatch);
	}

    public void run() {
        synchronized(countdownlatch){ 
            while(!countdownlatch.finished){
                try {
                    countdownlatch.await();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }           
            }
            game.endGame();   
        }
        
	}

}

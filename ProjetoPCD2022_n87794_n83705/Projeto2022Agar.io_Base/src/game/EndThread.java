package game;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class EndThread extends Thread implements Serializable{
    private Game game;
	private CDLEnd countdownlatch;
    //public AtomicInteger goal=new AtomicInteger();

    public EndThread(Game game) {
		this.game = game;
		this.countdownlatch = new CDLEnd(game);
        game.setCDL(countdownlatch);
	}

    public void run() {
        synchronized(countdownlatch){ 
            while(!countdownlatch.finished){
                try {
                    //wait();
                    countdownlatch.await();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.out.println("EndThread a finalizar");
                //if(countdownlatch.finished)                 
            }
            game.endGame();   
        }
        
	}

}

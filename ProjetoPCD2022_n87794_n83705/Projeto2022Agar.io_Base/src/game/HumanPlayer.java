package game;

import java.net.UnknownHostException;

public class HumanPlayer extends Player {
	
	public HumanPlayer(int id, Game game) {
		super(id, game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isHumanPlayer() {
		// TODO Auto-generated method stub
		return true;
	}

	public void run(){	
		try {
			game.addHuman(this);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("---------- pronto a correr "+ this.id +"\n");
		setAwake();
		for(;;) {
			while(!game.ended){
				if(this.canRun){
					try {
						game.moveTo(this, this.next);
						checkWin();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					this.canRun=false;
				}
				}
			}
		}
}




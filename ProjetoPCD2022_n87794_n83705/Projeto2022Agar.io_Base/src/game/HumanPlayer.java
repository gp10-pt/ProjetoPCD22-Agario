package game;

import java.io.Serializable;
import java.net.UnknownHostException;

public class HumanPlayer extends Player implements Serializable {
	
	public HumanPlayer(int id, Game game) {
		super(id, game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isHumanPlayer() {
		// TODO Auto-generated method stub
		return true;
	}

	//se o jogador chegar a energia maxima terminar o movimento e a thread
	public void checkWin(){	
		if (getCurrentStrength()>= (byte) win) {
			currentStrength=(byte) win;
			won=true;
			System.out.println("Player "+getIdentification()+" chegou Ã  energia maxima E VENCEU !!\n----_----_----_----_----_----_----_----_----_\n");
			//incrementar contador e verificar se o end goal (3) foi atingido
			if(game.winCondition.incrementAndGet()==5)
				game.endGame();
		}		
	}

	@Override
	public void run(){	
		this.aP.start();
	}
}





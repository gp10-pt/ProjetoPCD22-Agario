package game;

import java.io.Serializable;
import java.net.UnknownHostException;

import environment.Coordinate;
import environment.Direction;

@SuppressWarnings("serial")
public class HumanPlayer extends Player implements Serializable {

	public HumanPlayer(int id, Game game) {
		super(id, game);
	}

	@Override
	public boolean isHumanPlayer() {
		// TODO Auto-generated method stub
		return true;
	}

	// se o jogador chegar a energia maxima terminar o movimento e a thread
	public void checkWin() {
		if (getCurrentStrength() >= (byte) win) {
			currentStrength = (byte) win;
			won = true;
		}
	}

}

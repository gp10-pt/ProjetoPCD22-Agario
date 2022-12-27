package game;

import java.io.Serializable;

/**
 * Class to demonstrate a player being added to the game.
 * 
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public class PhoneyHumanPlayer extends Player implements Serializable {

	public PhoneyHumanPlayer(int id, Game game) {
		super(id, game);
	}

	public boolean isHumanPlayer() {
		return false;
	}
}

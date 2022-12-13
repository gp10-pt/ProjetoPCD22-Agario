package environment;

import java.io.Serializable;

import game.Game;
import game.Player;

public class Cell implements Serializable {
	private Coordinate position;
	private Game game;
	private Player player = null;

	public Cell(Coordinate position, Game g) {
		super();
		this.position = position;
		this.game = g;
	}

	public Coordinate getPosition() {
		return position;
	}

	public boolean isOcupied() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	// Should not be used like this in the initial state: cell might be occupied,
	// must coordinate this operation
	public void setPlayer(Player player) {
		this.player = player;
	}

	public synchronized void removePlayer() {
		synchronized(this){
			this.player= null;
			this.notifyAll();
		}
	}

}

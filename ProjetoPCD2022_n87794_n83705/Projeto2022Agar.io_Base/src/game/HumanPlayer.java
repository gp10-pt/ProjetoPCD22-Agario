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

	//move especifico de player humano
	@SuppressWarnings("static-access")
	public void move(Direction next) {
		// TODO Auto-generated method stub
		Coordinate future = null;
		Coordinate pre = this.getPosition();
		int x = pre.x;
		int y = pre.y;
		if (next != null)
			switch (next) {
			case UP: {
				if (y - 1 >= 0)
					future = pre.translate(Direction.UP.getVector());
				break;
			}
			case DOWN: {
				if (y + 1 < game.DIMY)
					future = pre.translate(Direction.DOWN.getVector());
				break;
			}
			case LEFT: {
				if (x - 1 >= 0)
					future = pre.translate(Direction.LEFT.getVector());
				break;
			}
			case RIGHT: {
				if (x + 1 < game.DIMX)
					future = pre.translate(Direction.RIGHT.getVector());
				break;
			}
			}
		// movimenta se pois a celula esta vazia e nao esta bloqueado, terminando o
		// Unblocker pois nao foi necessario
		if (future != null && !game.getCell(future).isOcupied()) {
			this.setPosition(game.getCell(future));
		} else if (future != null && game.getCell(future).isOcupied()) {
			// fight se o jogador da celula futura esta vivo, ainda nao venceu e nao esta sleeping
			Player futuroP = game.getCell(future).getPlayer();
			if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()) {
				fight(futuroP);
				futuroP.setPosition(game.getCell(future));
			}
		}
		//check se tem energia maxima
		checkWin();
		game.notifyChange();
	}
}

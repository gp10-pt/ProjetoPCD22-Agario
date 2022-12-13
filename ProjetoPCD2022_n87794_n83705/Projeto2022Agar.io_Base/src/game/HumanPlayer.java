package game;

import java.io.Serializable;
import java.net.UnknownHostException;

import environment.Coordinate;
import environment.Direction;

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

	// se o jogador chegar a energia maxima terminar o movimento e a thread
	public void checkWin() {
		if (getCurrentStrength() >= (byte) win) {
			currentStrength = (byte) win;
			won = true;
			
		}
	}

	public void move(Direction next) {
		// TODO Auto-generated method stub
		Coordinate future = null;
		Coordinate pre = this.getPosition();
		int x = pre.x;
		int y = pre.y;
		// System.out.println(entity.getIdentification() + " - origem:
		// "+pre.toString());
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
			// System.out.println(p.getIdentification() + " - destino: "+future.toString()+
			// " - ronda "+ p.ronda);
			this.setPosition(game.getCell(future));
		} else if (future == null) {
			// System.out.println("Posicao de destino out of bounds para o Client:
			// "+p.getIdentification()+"!\n");
		} else if (game.getCell(future).isOcupied()) {
			// fight se o jogador esta vivo, ainda nao venceu e nao esta sleeping
			Player futuroP = game.getCell(future).getPlayer();
			if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()) {
				this.fight(futuroP);
				futuroP.setPosition(game.getCell(future));
			}
		}
		checkWin();
		game.notifyChange();
	}
}

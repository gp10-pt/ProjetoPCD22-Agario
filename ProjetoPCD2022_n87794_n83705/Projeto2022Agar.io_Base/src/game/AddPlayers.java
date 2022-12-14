package game;

import java.io.Serializable;

import environment.Coordinate;
import environment.Direction;

@SuppressWarnings("serial")
public class AddPlayers extends Thread implements Serializable {

	private Game game;
	private Player player;
	Unblocker u;

	public AddPlayers(Player p, Game game) {
		this.player = p;
		this.game = game;
	}

	@SuppressWarnings("static-access")
	public void run() {
		if (player instanceof PhoneyHumanPlayer) {
			synchronized (this) {
				try {
					// add do jogador e sleep inicial
					addPhoneyToGame();
					this.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//player acorda e começa as suas acoes
				player.setAwake();
				while (player.playerIsAlive()) {
					try {
						this.u = new Unblocker(game, player);
						u.start();
						move(player);
						checkWin();
						sleep(game.REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		}
	}

	//add phoney player com wait pela celula inicial se ja ocupada 
	@SuppressWarnings("deprecation")
	public synchronized void addPhoneyToGame() {
		synchronized (player.initialPos) {
			while (player.initialPos.isOcupied()) {
				if (!player.initialPos.getPlayer().playerIsAlive()) { // se dead nao colocar o jogador
					System.out.println(
							"Jogador " + player.getIdentification() + " eliminado pois jogador morto esta na posicao");
					this.stop();
				}
				// se nao, esperar que a posicao fique livre e depois adicionar ao jogo
				System.out.println("Posicao ja ocupada para player " + player.getIdentification() + " pelo Player "
						+ player.initialPos.getPlayer().getIdentification());
				try {
					player.initialPos.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Player "+ player.getIdentification() + " a ser colocado apos espera!");
			}
		}
		player.initialPos.setPlayer(player);
		player.setPosition(player.initialPos);
		// To update GUI
		game.playerAdded(player);
	}

	//lock do phoney pois se movimentou contra player morto
	@SuppressWarnings("static-access")
	public synchronized void lock() throws InterruptedException {
		player.isBlocked = true;
		if (player.isBlocked())
			this.sleep(2000);
	}

	// se o jogador chegar a energia maxima terminar o movimento e a thread
	@SuppressWarnings("deprecation")
	public void checkWin() {
		if (player.getCurrentStrength() >= (byte) player.win) {
			player.currentStrength = (byte) player.win;
			player.won = true;
			this.stop();
		}
	}

	//move especifico de player automatico
	public synchronized void move(Player p) throws InterruptedException {
		if (!p.won && p.playerIsAlive()) {
			// gerar a direcao pa mover se nao tiver ganho e tiver vivo
			if (!p.isHumanPlayer()) {
				Direction[] hipoteses = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
				int d = (int) (Math.random() * hipoteses.length);
				p.next = hipoteses[d];
			}
			moveTo(p, p.next);
		}
	}

	@SuppressWarnings("static-access")
	public synchronized void moveTo(Player p, Direction direction) throws InterruptedException {
		if (p.ronda % p.originalStrength == 0) {
			Coordinate future = null;
			Coordinate pre = p.getPosition();
			int x = pre.x;
			int y = pre.y;
			//proxima direcao com check OutOfBounds
			if (direction != null)
				switch (direction) {
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
			// movimenta se pois a celula esta vazia e nao esta bloqueado, terminando o Unblocker pois nao foi necessario
			if (future != null && !game.getCell(future).isOcupied()) {
				p.setPosition(game.getCell(future));
				u.stopU();
			} else if (future != null && game.getCell(future).isOcupied()) {
				// fight se o jogador esta vivo, ainda nao venceu e nao esta sleeping
				Player futuroP = game.getCell(future).getPlayer();
				if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()) {
					player.fight(futuroP);
					futuroP.setPosition(game.getCell(future));
					u.stopU();
				} else {
					// apenas os phoneys ficam presos (espera q o Unblocker interrompa o sleep e continua o run)
					if (p instanceof PhoneyHumanPlayer) {
						lock();
					}
				}
			}
		}
		p.ronda++;
		game.notifyChange();
	}

}

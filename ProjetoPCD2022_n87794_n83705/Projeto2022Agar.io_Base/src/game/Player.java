package game;

import java.io.Serializable;

import environment.Cell;
import environment.Direction;
import environment.Coordinate;

/**
 * Represents a player.
 *
 */
@SuppressWarnings("serial")
public abstract class Player implements Serializable {

	protected Game game;

	private int id;

	byte currentStrength;
	protected byte originalStrength;
	public boolean isBlocked = false;
	private boolean isDead = false;
	private boolean isSleeping = true;
	private boolean canRun = false;

	private final Cell initialPos;
	private Cell pos;
	public Direction next;
	public int ronda;
	public boolean won=false;
	public final byte win = (byte) 10;

	public AddPlayers aP;

	// TODO: get player position from data in game
	public Cell getCurrentCell() {
		return pos;
	}

	public Player(int id, Game game) {
		super();

		byte strength;
		this.id = id;
		this.game = game;
		if (!this.isHumanPlayer()) {
			strength = (byte) (1 + Math.random() * 3);
		}
		// strength para humanos
		else {
			strength = (byte) 5;
		}
		currentStrength = strength;
		originalStrength = strength;
		this.initialPos = game.getRandomCell();
	}

	// player para visualizacao nos clients
	public Player(byte strength) {
		initialPos = null;
		this.currentStrength = strength;
	}

	public abstract boolean isHumanPlayer();

	public void addHumanToGame() throws InterruptedException {
		getInitialPos().setPlayer(this);
		setPosition(getInitialPos());
		// To update GUI
		game.playerAdded(this);
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", currentStrength=" + currentStrength + ", getCurrentCell()=" + getCurrentCell()
		+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public byte getCurrentStrength() {
		return currentStrength;
	}

	public byte getOriginalStrength() {
		return originalStrength;
	}

	public int getIdentification() {
		return id;
	}

	public Coordinate getPosition() {
		return pos.getPosition();
	}

	public boolean playerIsAlive() {
		return !this.isDead;
	}

	//ao colocar o player na celula nova este e removido da anterior
	public void setPosition(Cell c) throws InterruptedException {
		c.setPlayer(this);
		pos = c;
	}

	public boolean isSleeping() {
		return this.isSleeping;
	}

	public synchronized boolean isBlocked() {
		return this.isBlocked == true;
	}

	//ação fight desencadeia o recebimento da energia do adversario e a sua morte	
	public void absorbs(Player s) {
		this.currentStrength += s.getCurrentStrength();
		if (getCurrentStrength() >= (byte) win) {
			currentStrength = (byte) win;
			won = true;
			game.getCDL().decrement(id);
		}
		s.death();
	}

	//morte do jogador
	public void death() {
		this.currentStrength = 0;
		this.isDead = true;
		getCurrentCell().warn(getIdentification());
	}

	//desbloqueamento do sleep inicial	
	public void setAwake() {
		this.isSleeping = false;
	}

	//fight com o jogador da celula destino
	public void fight(Player b) {
		if (this.getCurrentStrength() == b.getCurrentStrength()) {
			int i = (int) Math.random() * 2;
			if (i == 0) {
				this.absorbs(b);
			} else {
				b.absorbs(this);
			}
		} else if (this.getCurrentStrength() < b.getCurrentStrength())
			b.absorbs(this);
		else
			this.absorbs(b);
	}

	public Cell getInitialPos() {
		return initialPos;
	}

	//procedimento para inicio da movimentaçao
	public synchronized void move() throws InterruptedException {
		if (!this.won && this.playerIsAlive()) {
			// gerar a direcao pa mover se nao tiver ganho e tiver vivo e phoney
			if (!this.isHumanPlayer()) {
				Direction[] hipoteses = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
				int d = (int) (Math.random() * hipoteses.length);
				this.next = hipoteses[d];
			}
			moveTo(this, this.next);
		}
	}

	//procedimento que realiza a movimentaçao do jogador
	@SuppressWarnings("static-access")
	private synchronized void moveTo(Player p, Direction direction) throws InterruptedException {
		// se for humano ou vez do bot mexer
		if (p.isHumanPlayer() || p.ronda % p.originalStrength == 0) {
			Coordinate future = null;
			Coordinate pre = p.getPosition();
			int x = pre.x;
			int y = pre.y;
			//proxima direcao com check OutOfBounds
			if (direction != null) {
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
			}
			aP.u.start();
			// movimenta-se pois a celula esta vazia e nao esta bloqueado, terminando o Unblocker pois nao foi necessario
			if (future != null && !game.getCell(future).isOcupied()) {
				if (p instanceof PhoneyHumanPlayer) {
					aP.u.stopU();}
				p.setPosition(game.getCell(future));				
			} else if (future != null && game.getCell(future).isOcupied()) {
				// fight se o jogador esta vivo, ainda nao venceu e nao esta sleeping
				Player futuroP = game.getCell(future).getPlayer();
				if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()) {
					if (p instanceof PhoneyHumanPlayer) {
						aP.u.stopU();}
					fight(futuroP);
					futuroP.setPosition(game.getCell(future));
				} else {
					// apenas os phoneys ficam presos (espera q o Unblocker interrompa o sleep e continua o run)
					if (p instanceof PhoneyHumanPlayer) {
						aP.lock();
					}
				}
			}
		}
		p.ronda++;
		game.notifyChange();
	}


}

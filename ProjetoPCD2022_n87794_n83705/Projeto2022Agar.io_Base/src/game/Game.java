package game;

import environment.Direction;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

import environment.Cell;
import environment.Coordinate;

public class Game extends Observable implements Serializable {

	public static final int DIMY = 30;
	public static final int DIMX = 30;
	public int NUM_PLAYERS = 100;
	public final int NUM_WINNERS=5;
	public final long REFRESH_INTERVAL = 400;
	public final double MAX_INITIAL_STRENGTH = 3;
	public final double MAX_INITIAL_STRENGTH_HUMANS = 5;
	public final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public final long INITIAL_WAITING_TIME = 10000;

	public Cell[][] board;
	public CDLEnd endCount;
	public EndThread end;
	public AtomicInteger winCondition = new AtomicInteger();
	public Direction keyD;
	public boolean ended = false;
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> humans = new ArrayList<Player>();
	public ArrayList<AddPlayers> threads = new ArrayList<AddPlayers>();

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++)
				board[x][y] = new Cell(new Coordinate(x, y), this);
		//this.endCount= new CDLEnd(this);
		this.end = new EndThread(this);
		end.start();
	}

	public void updateBoard(Cell[][] x) {
		this.board = x;
		notifyChange();
	}

	public Cell[][] getBoard() {
		Cell[][] duplicate = new Cell[Game.DIMX][Game.DIMY];
		for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				duplicate[x][y] = new Cell(new Coordinate(x, y), this);
				if (board[x][y].getPlayer() != null && !board[x][y].getPlayer().isHumanPlayer()) {
					Dummy dummy = new Dummy(board[x][y].getPlayer().getCurrentStrength(), false);
					duplicate[x][y].setPlayer(dummy);
				} else if (board[x][y].getPlayer() != null && board[x][y].getPlayer().isHumanPlayer()) {
					Dummy dummy = new Dummy(board[x][y].getPlayer().getCurrentStrength(), true);
					duplicate[x][y].setPlayer(dummy);
				}
			}
		}
		return duplicate;
	}

	public CDLEnd getCDL(){
		return endCount;
	}

	public void setCDL(CDLEnd cdl){
		this.endCount=cdl;
	}

	/**
	 * @param p - player
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */

	public HumanPlayer addHuman() throws UnknownHostException {
		// start dos humanos com lanÃ§amento do cliente q vai se ligar pelo server
		Player p = new HumanPlayer(NUM_PLAYERS, this);
		humans.add(p);
		p.addHumanToGame();
		NUM_PLAYERS++;
		return (HumanPlayer) p;
	}

	public void startPhoneys() {
		try {
			addPhoneys();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addPhoneys() throws UnknownHostException {
		Player p;
		// start dos humanos com lanÃ§amento do cliente q vai se ligar pelo server
		for (int i = 0; i != NUM_PLAYERS; i++) {
			p = new PhoneyHumanPlayer(i, this);
			players.add(p);
			p.aP = new AddPlayers(p, this);
			p.aP.start();
		}
	}

	public void playerAdded(Player p) {
		// To update GUI
		notifyChange();
		// System.out.println(p.getIdentification()+" lancado\n");
	}

	public void endGame() {// ciclo a correr vetor de players em vez de board
		ended = true;
		System.out.println("\n-Â»\nFIM DO JOGO\nÂ«-\n");
		for (int x = 0; x < DIMX; x++) {
			for (int y = 0; y < DIMY; y++) {
				if (this.board[x][y].isOcupied() && this.board[x][y].getPlayer().playerIsAlive()
						&& !this.board[x][y].getPlayer().won)
					this.board[x][y].getPlayer().death();
			}
		}
		notifyChange();
	}

	public Cell getCell(Coordinate at) {
		return board[at.x][at.y];
	}

	/**
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	public Cell getRandomCell() {
		Cell newCell = getCell(new Coordinate((int) (Math.random() * Game.DIMX), (int) (Math.random() * Game.DIMY)));
		return newCell;
	}

}
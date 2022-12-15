package game;

import environment.Direction;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

import environment.Cell;
import environment.Coordinate;

@SuppressWarnings({ "serial", "deprecation" })
public class Game extends Observable implements Serializable {

	public static final int DIMY = 30;
	public static final int DIMX = 30;
	public int NUM_PLAYERS = 200;
	public final int NUM_WINNERS=5;
	public final long REFRESH_INTERVAL = 400;
	public final double MAX_INITIAL_STRENGTH = 3;
	public final double MAX_INITIAL_STRENGTH_HUMANS = 5;
	public final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public final long INITIAL_WAITING_TIME = 10000;

	public Cell[][] board;
	public CDLEnd endCount;
	public EndThread end;
	public Direction keyD;
	public boolean ended = false;
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> humans = new ArrayList<Player>();
	public ArrayList<AddPlayers> threads = new ArrayList<AddPlayers>();
	private boolean phoneysStarted=false;

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++)
				board[x][y] = new Cell(new Coordinate(x, y), this);
		this.end = new EndThread(this);
		end.start();
	}

	//update board para visualizacao pelo cliente
	public void updateBoard(Cell[][] x) {
		this.board = x;
		notifyChange();
	}

	//get board para envio aos clietes
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

	// start dos humanos que acontece com o lancamento do cliente q vai se ligar pelo server
	public HumanPlayer addHuman() throws UnknownHostException {
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

	//add de NUM_PLAYERS jogadores automaticos ao jogo
	public void addPhoneys() throws UnknownHostException {
		Player p;
		for (int i = 0; i != NUM_PLAYERS; i++) {
			p = new PhoneyHumanPlayer(i, this);
			players.add(p);
			p.aP = new AddPlayers(p, this);
			p.aP.start();
		}
	}
	
	public void setPhoneysStarted() {
		this.phoneysStarted=true;
	}
	
	public boolean getPhoneysStarted() {
		return phoneysStarted;
	}
	

	public void playerAdded(Player p) {
		// To update GUI
		notifyChange();
	}

	//acabar com o jogo ao correr a board e matar todos os players vivos que não venceram
	public void endGame() {
		ended = true;
		System.out.println("\n\n-» FIM DO JOGO «-\n\n");
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
	
	public Cell getRandomCell() {
		Cell newCell = getCell(new Coordinate((int) (Math.random() * Game.DIMX), (int) (Math.random() * Game.DIMY)));
		return newCell;
	}

	/**
	 * Updates GUI. Should be called anytime the game state changes
	 */
	public void notifyChange() {
		setChanged();
		notifyObservers();
	}

	
}
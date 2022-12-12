package game;
import environment.Direction;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

import environment.Cell;
import environment.Coordinate;

public class Game extends Observable implements Serializable{

	
	public static final int DIMY = 30;
	public static final int DIMX = 30;
	public int NUM_PLAYERS = 100;
	public final int NUM_HUMANS = 2;

	public final long REFRESH_INTERVAL = 400;
	public final double MAX_INITIAL_STRENGTH = 3;
	public final double MAX_INITIAL_STRENGTH_HUMANS = 5;
	public final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public final long INITIAL_WAITING_TIME = 10000;

	public Cell[][] board;
	public AtomicInteger winCondition= new AtomicInteger();
	public Direction keyD;
	public boolean ended=false;
	public ArrayList<Player> players= new ArrayList<Player>();
	public ArrayList<Player> humans= new ArrayList<Player>();
	public ArrayList<AddPlayers> threads= new ArrayList<AddPlayers>();

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y),this);
		
	}

	public void updateBoard(Cell[][] x){
		this.board=x;
		notifyChange();
	}

	public Cell[][] getBoard(){
		return this.board;
	}

	/** 
	 * @param p - player
	 * @throws UnknownHostException
	 * @throws InterruptedException
	 */

	public void addHuman(Player p) throws UnknownHostException {
		//start dos humanos com lançamento do cliente q vai se ligar pelo server 
		humans.add(p);
		p.aP=new AddPlayers(p,this);
		p.aP.start();
		//((HumanPlayer) p).addHumanToGame();
	}

	public void addPhoneys() throws UnknownHostException {
		Player p;
		//start dos humanos com lançamento do cliente q vai se ligar pelo server 
		for (int i =0; i!=NUM_PLAYERS; i++) { 
			p=new PhoneyHumanPlayer(i, this);
			players.add(p);
			p.aP=new AddPlayers(p,this);
			p.aP.start();
		}
	}

	public void startPhoneys(){
		try {
			//Thread.sleep(3000);
			addPhoneys();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playerAdded(Player p) {
		// To update GUI 
		notifyChange();
		System.out.println(p.getIdentification()+" lancado\n");
	}
	
	public void endGame() {
		ended=true;
		System.out.println("\n-»\n-»\nFIM DO JOGO\n«-\n«-\n");
		for (int x = 0; x < DIMX; x++){
			for (int y = 0; y < DIMY; y++){ 
				if(this.board[x][y].isOcupied() && this.board[x][y].getPlayer().playerIsAlive() && !this.board[x][y].getPlayer().won)
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
		Cell newCell=getCell(new Coordinate((int)(Math.random()*Game.DIMX),(int)(Math.random()*Game.DIMY)));
		return newCell; 
	}
	

}
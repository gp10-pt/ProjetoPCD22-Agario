package game;
import environment.Direction;
import gui.BoardJComponent;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import environment.Cell;
import environment.Coordinate;

public class Game extends Observable {

	public static final int DIMY = 30;
	public static final int DIMX = 30;
	private static final int NUM_PLAYERS = 100;
	private static final int NUM_FINISHED_PLAYERS_TO_END_GAME=3;

	public static final long REFRESH_INTERVAL = 400;
	public static final double MAX_INITIAL_STRENGTH = 3;
	public static final long MAX_WAITING_TIME_FOR_MOVE = 2000;
	public static final long INITIAL_WAITING_TIME = 10000;

	protected Cell[][] board;
	public int finished;
	public Direction keyD;
	public ArrayList<Player> players= new ArrayList<Player>();
	public ArrayList<Thread> threads= new ArrayList<Thread>();
	//humano teste
	public HumanPlayer human;

	public Game() {
		board = new Cell[Game.DIMX][Game.DIMY];
		this.finished=0;
		for (int x = 0; x < Game.DIMX; x++) 
			for (int y = 0; y < Game.DIMY; y++) 
				board[x][y] = new Cell(new Coordinate(x, y),this);
		
	}

	/** 
	 * @param p - player
	 * @throws InterruptedException
	 */
	public void addPlayers() {
		Player p= new HumanPlayer(0,this);
		this.human=(HumanPlayer) p;
		p.th.start();
		for (int i = 0; i<NUM_PLAYERS; i++) { 
			p=new PhoneyHumanPlayer((i+1), this);
			players.add(p);
			threads.add(p.th);
		}
		for (Thread thread : threads) {
			thread.start();
        }
	}

	public void playerAdded(Player p) {
		// To update GUI 
		notifyChange();
		System.out.println(p.getIdentification()+" lancado\n");
	}
	
	public void endGame() {
		for (int x = 0; x < DIMX; x++){
			for (int y = 0; y < DIMY; y++){ 
				if(this.board[x][y].isOcupied() && !this.board[x][y].getPlayer().won)
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

	public void move(Player p) throws InterruptedException {

		if(!p.won && p.playerIsAlive()){
			// gerar a direcao pa mover se nao tiver ganho e tiver vivo
			if(!p.isHumanPlayer()) {
				Direction[] hipoteses = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
				int d = (int) (Math.random() * hipoteses.length);
				p.next=hipoteses[d];
			}
			//no caso de ser humano o objecto segue a indicação das teclas, guardada no atributo next (nao implementado)
			// mexer o player
			else {
				//			System.out.println("\nHumano a querer mexer na direcao"+keyD+"\n");
				p.next=keyD;
			}
			moveTo(p, p.next);
			notifyChange();
		}
	}

	public void moveTo(Player p, Direction direction) throws InterruptedException { 
		if (p.ronda%p.originalStrength==0) {
			Coordinate future = null; 
			Coordinate pre= p.getPosition();
			int x=pre.x;
			int y=pre.y;
			//			System.out.println(entity.getIdentification() + " - origem: "+pre.toString());
			if(direction!=null)
				switch (direction) {
				case UP: {
					if(y-1 >= 0)
						future = pre.translate(Direction.UP.getVector());
					break;
				}
				case DOWN: {
					if(y+1 < DIMY)
						future = pre.translate(Direction.DOWN.getVector());
					break;
				}
				case LEFT: {
					if(x-1 >= 0)
						future = pre.translate(Direction.LEFT.getVector());
					break;
				}
				case RIGHT: {
					if(x+1 < DIMX)
						future = pre.translate(Direction.RIGHT.getVector());
					break;
				}
				}
				// movimenta se pois a celula esta vazia e nao esta bloqueado, terminando o Unblocker pois nao foi necessario
			if(future!= null && !getCell(future).isOcupied()) {
				//System.out.println(p.getIdentification() + " - destino: "+future.toString()+ " - ronda "+ p.ronda);
				p.setPosition(getCell(future));
				p.u.th.stop();
				//notifyChange();
			} else if(future==null) {
				//System.out.println("Posiçao de destino out of bounds para o player: "+p.getIdentification()+"!\n"); 
			} else if(getCell(future).isOcupied()){
				// fight se o jogador esta vivo, ainda nao venceu e nao esta sleeping
				Player futuroP=getCell(future).getPlayer();
				if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()){
					fight(p,futuroP);
					p.u.th.stop(); 
				}else {// apenas os phoneys ficam presos (espera q o Unblocker interrompa o sleep e continua o Player.run)
					if(p instanceof PhoneyHumanPlayer) {
						p.lock();
					}
				}
			}
		} else{
		//System.out.println("Player "+p.getIdentification()+" apenas mexe em "+(p.originalStrength-p.ronda%p.originalStrength)+" rondas");
		} 	
		p.ronda++;
	}

	private void fight(Player a, Player b) {
		System.out.println("\n"+a.getIdentification()+" entrou em confronto contra "+b.getIdentification()+ " - ronda "+ a.ronda);
		if (a.getCurrentStrength()==b.getCurrentStrength()) {
			int i= (int) Math.random()*2;
			if(i==0) {
				a.absorbs(b);
			}else {
				b.absorbs(a);
			}				
		} else 
			if (a.getCurrentStrength()<b.getCurrentStrength())
				b.absorbs(a);
			else
				a.absorbs(b);
	}

}

package game;


import java.io.Serializable;

import environment.Cell;
import environment.Direction;
import environment.Coordinate;
/**
 * Represents a player.
 *
 */
public abstract class Player implements Runnable, Serializable{


	protected  Game game;

	public int id;

	private byte currentStrength;
	protected byte originalStrength;
	public boolean isBlocked=false;
	public boolean isDead=false;
	private boolean isSleeping=true;
	public boolean canRun=false;

	private final Cell initialPos;
	public Cell pos;
	public Direction next;
	public int ronda;
	public boolean won;
	public final byte win = (byte) 10;

	public Thread th;
	public Unblocker u;

	// TODO: get player position from data in game
	public Cell getCurrentCell() {
		return pos;
	}

	public Player(int id, Game game) {
		super();
		
		byte strength;
		this.id = id;
		this.game=game;
		if(!this.isHumanPlayer()){
			strength= (byte) (1 + Math.random() * 3);
			Thread p=new Thread(this);
			this.th=p;
		}
		//strength para humanos
		else{
			strength=(byte) 5;
		}
		currentStrength=strength;
		originalStrength=strength;
		this.initialPos=game.getRandomCell();
		
		System.out.println("Sou o player " +id+" e tenho "+originalStrength+" de forÃ§a");
	}

	public abstract boolean isHumanPlayer();

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
	
	public void setPosition(Cell c) {
		 if(pos!=null)
			 this.game.getCell(pos.getPosition()).removePlayer();
		 c.setPlayer(this);
		 pos=c;
	}
	
	public boolean isSleeping(){
		return this.isSleeping;
	}

	public synchronized boolean isBlocked(){
		return this.isBlocked==true;
	}
	
	public synchronized void setUnblocked() {
		this.isBlocked=false;
		System.out.println("Player "+this.getIdentification()+" unblocked!");
		this.notify();
	}
	
	public synchronized void addPhoneyToGame(){
		synchronized(initialPos) {
		//System.out.println("posicao original do player "+this.getIdentification()+": "+initialPos.getPosition().toString());
			while(initialPos.isOcupied()){
				if(!initialPos.getPlayer().playerIsAlive()){	//se dead nao colocar o jogador
					th.stop();
					System.out.println("Jogador "+this.getIdentification()+" eliminado pois jogador morto esta na posicao");
				}		
				// se nao, esperar que a posicao fique livre e depois adicionar ao jogo
				System.out.println("Posicao ja ocupada para player " + this.getIdentification()+" pelo Player "+initialPos.getPlayer().getIdentification());			
				try {
					initialPos.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		initialPos.setPlayer(this);
		this.setPosition(initialPos);
		// To update GUI 
		game.playerAdded(this);
	}

	public void addHumanToGame(){
		initialPos.setPlayer(this);
		this.setPosition(initialPos);
		// To update GUI 
		game.playerAdded(this);
	}

	public void absorbs(Player s) {
		this.currentStrength+=s.getCurrentStrength();
		s.death();
		//System.out.println("\nO jogador "+ s.getIdentification() + " morreu contra o jogador "+this.getIdentification()+".\n#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|");
		//System.out.println("O player "+this.getIdentification()+" chegou a "+this.getCurrentStrength()+" de energia na ronda "+this.ronda+" contra o jogador "+s.getIdentification()+".\n#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|");
	}
	
	public void death() {
		this.currentStrength=0;
		this.isDead=true;
		System.out.println("O jogador "+ this.getIdentification() + " morreu na ronda "+this.ronda+".\n#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|#|\n");
	}	

	//se o jogador chegar a energia maxima terminar o movimento e a thread
	public void checkWin(){	
		if (getCurrentStrength()>= (byte) win) {
			this.currentStrength=(byte) win;
			this.won=true;
			System.out.println("Player "+this.getIdentification()+" chegou Ã  energia maxima E VENCEU !!\n----_----_----_----_----_----_----_----_----_\n");
			//incrementar contador e verificar se o end goal (3) foi atingido
			if(this.game.winCondition.incrementAndGet()==5)
				this.game.endGame();
				th.stop();
		}		
	}
	
	public synchronized void lock() throws InterruptedException {
		this.isBlocked=true;
		if(this.isBlocked())
			th.sleep(2000);
			//System.out.println("Player "+this.getIdentification()+" lockado ");
	}

	public void setAwake() {
		this.isSleeping=false;
	}

	
}		



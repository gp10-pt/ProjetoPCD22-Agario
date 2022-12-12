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

	byte currentStrength;
	protected byte originalStrength;
	public boolean isBlocked=false;
	public boolean isDead=false;
	private boolean isSleeping=true;
	public boolean canRun=false;

	public final Cell initialPos;
	public Cell pos;
	public Direction next;
	public int ronda;
	public boolean won;
	public final byte win = (byte) 10;

	//public Thread th;
	public Unblocker u;
	public AddPlayers aP;

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
			//Thread p=new Thread(this);
			//this.th=p;
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

	public void setAwake() {
		this.isSleeping=false;
	}

	
}		



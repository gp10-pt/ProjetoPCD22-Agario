package game;


import environment.Cell;
import environment.Direction;
import environment.Coordinate;
/**
 * Represents a player.
 *
 */
public abstract class Player implements Runnable {


	protected  Game game;

	private int id;

	private byte currentStrength;
	protected byte originalStrength;
	public boolean isBlocked=false;
	private boolean isDead=false;
	private boolean isSleeping=true;

	public Cell pos;
	public Direction next;
	public int ronda;
	public boolean won;
	public final byte win = (byte) 10;

	public Thread th;

	// TODO: get player position from data in game
	public Cell getCurrentCell() {
		return pos;
	}

	public Player(int id, Game game) {
		super();
		this.id = id;
		this.game=game;
		byte strength= (byte) (1 + Math.random() * 3);
		currentStrength=strength;
		originalStrength=strength;
		this.pos=pos;
		Thread p=new Thread(this);
		this.th=p;
		System.out.println("Sou o player " +id+" e tenho "+strength+" de força");
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
	
	public boolean isAlive() {
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
//		System.out.println("Player "+this.getIdentification()+" unblocked!");
		this.notify();
	}
	
	public synchronized void addPlayerToGame(){
		Cell initialPos=this.game.getRandomCell();
		//System.out.println("posicão original do player "+this.getIdentification()+": "+initialPos.getPosition().toString());
		while(initialPos.isOcupied()){
			if(!initialPos.getPlayer().isAlive()){	//se dead nao colocar o jogador
				th.stop();
				System.out.println("Jogador "+this.getIdentification()+" eliminado pois jogador morto esta na posicao");
			}		
			// se nao, esperar  a posicao fique livre e depois adicionar ao jogo ( COM WAIT )			
			try {
//				System.out.println("Posicao ja ocupada para player " + this.getIdentification()+" pelo Player "+initialPos.getPlayer().getIdentification());
				th.sleep(game.REFRESH_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
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
		this.th.stop();
	}	

	//se o jogador chegar a energia maxima terminar o movimento e a thread
	private void checkWin(){	
		if (getCurrentStrength()>= (byte) win) {
			this.currentStrength=(byte) win;
			this.won=true;
			System.out.println("Player "+this.getIdentification()+" chegou à energia maxima E VENCEU !!\n----_----_----_----_----_----_----_----_----_\n");
			//ganhou
			this.game.finished++;
			if(this.game.finished==3)
				this.game.endGame();
			th.stop();
		}		
	}
	
	//Unblocker da call a unlock() e este notifica a thread p voltar a movimentar
//	public synchronized void unlock() throws InterruptedException {
//		this.th.sleep(2000);
//		while(!this.isBlocked()) {
//			notifyAll();
//			System.out.println("Player "+this.getIdentification()+" unlocked ");
//			wait();	
//		}
//		this.isBlocked=true;
//		notifyAll();
//	}
	
	public synchronized void lock() throws InterruptedException {
		this.isBlocked=true;
		while(this.isBlocked()) {
			System.out.println("Player "+this.getIdentification()+" lockado ");
			wait();
//			notifyAll();
		}
//		System.out.println("XXXXXX");
//		setUnblocked();
		this.notify();	
	}
	
	public void run(){		
		try {			
			//sleep after add , se for lancado depois por ter sido bloqueado vai esperar 10segs antes da proxima jogada e n pode ser atacado
			addPlayerToGame();
			th.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("---------- pronto a correr "+ id +"\n");
		this.isSleeping=false;
//		while(true) {
		for(;;) {
			try {	
//				if(!this.isBlocked()) {
					game.move(this);
					checkWin();
					th.sleep(this.game.REFRESH_INTERVAL);
	//				while(this.isBlocked()) {
	//					lock();
//				}	
//				else {th.sleep(this.game.REFRESH_INTERVAL);}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
}		



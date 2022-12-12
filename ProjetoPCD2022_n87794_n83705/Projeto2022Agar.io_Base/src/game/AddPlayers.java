package game;

import java.io.Serializable;

import environment.Coordinate;
import environment.Direction;

public class AddPlayers extends Thread implements Serializable{

    private Game game;
    private Player player;
    Unblocker u;

    public AddPlayers(Player p, Game game){
        this.player=p;
        this.game=game;
    }

    public void addHumanToGame(){
		player.initialPos.setPlayer(player);
		player.setPosition(player.initialPos);
		// To update GUI 
		game.playerAdded(player);
	}

    public synchronized void addPhoneyToGame(){
		synchronized(player.initialPos) {
		//System.out.println("posicao original do player "+this.getIdentification()+": "+initialPos.getPosition().toString());
			while(player.initialPos.isOcupied()){
				if(!player.initialPos.getPlayer().playerIsAlive()){	//se dead nao colocar o jogador
					System.out.println("Jogador "+player.getIdentification()+" eliminado pois jogador morto esta na posicao");
					return;				
				}		
				// se nao, esperar que a posicao fique livre e depois adicionar ao jogo
				System.out.println("Posicao ja ocupada para player " + player.getIdentification()+" pelo Player "+player.initialPos.getPlayer().getIdentification());			
				try {
					player.initialPos.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}
		player.initialPos.setPlayer(player);
		player.setPosition(player.initialPos);
		// To update GUI 
		game.playerAdded(player);
	}

    public synchronized void lock() throws InterruptedException {
		player.isBlocked=true;
		if(player.isBlocked())
			this.sleep(2000);
			//System.out.println("Player "+this.getIdentification()+" lockado ");
	}

    //se o jogador chegar a energia maxima terminar o movimento e a thread
	public void checkWin(){	
		if (player.getCurrentStrength()>= (byte) player.win) {
			player.currentStrength=(byte) player.win;
			player.won=true;
			System.out.println("Player "+player.getIdentification()+" chegou Ã  energia maxima E VENCEU !!\n----_----_----_----_----_----_----_----_----_\n");
			//incrementar contador e verificar se o end goal (3) foi atingido
			if(this.game.winCondition.incrementAndGet()==5)
				this.game.endGame();
				this.stop();
		}		
	}

    
	public void move(Player p) throws InterruptedException {
		if(!p.won && p.playerIsAlive()){
			// gerar a direcao pa mover se nao tiver ganho e tiver vivo
			if(!p.isHumanPlayer()) {
				Direction[] hipoteses = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
				int d = (int) (Math.random() * hipoteses.length);
				p.next=hipoteses[d];
			}
			moveTo(p, p.next);
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
					if(y+1 < game.DIMY)
						future = pre.translate(Direction.DOWN.getVector());
					break;
				}
				case LEFT: {
					if(x-1 >= 0)
						future = pre.translate(Direction.LEFT.getVector());
					break;
				}
				case RIGHT: {
					if(x+1 < game.DIMX)
						future = pre.translate(Direction.RIGHT.getVector());
					break;
				}
				}
				// movimenta se pois a celula esta vazia e nao esta bloqueado, terminando o Unblocker pois nao foi necessario
			if(future!= null && !game.getCell(future).isOcupied()) {
				//System.out.println(p.getIdentification() + " - destino: "+future.toString()+ " - ronda "+ p.ronda);
				p.setPosition(game.getCell(future));
				p.aP.u.stopU();
				//notifyChange();
			} else if(future==null) {
				//System.out.println("PosiÃ§ao de destino out of bounds para o player: "+p.getIdentification()+"!\n"); 
			} else if(game.getCell(future).isOcupied()){
				// fight se o jogador esta vivo, ainda nao venceu e nao esta sleeping
				Player futuroP=game.getCell(future).getPlayer();
				if (futuroP.playerIsAlive() && !futuroP.won && !futuroP.isSleeping()){
					fight(p,futuroP);
					futuroP.setPosition(game.getCell(future));
					p.aP.u.stopU(); 
				}else {// apenas os phoneys ficam presos (espera q o Unblocker interrompa o sleep e continua o Player.run)
					if(p instanceof PhoneyHumanPlayer) {
						p.aP.lock();
					}
				}
			}
		} else{
		//System.out.println("Player "+p.getIdentification()+" apenas mexe em "+(p.originalStrength-p.ronda%p.originalStrength)+" rondas");
		} 	
		p.ronda++;
		game.notifyChange();
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

    public void run(){	
    if(player instanceof PhoneyHumanPlayer){
		synchronized(this) {
			try {			
				//sleep after add , se for lancado depois por ter sido bloqueado vai esperar 10segs antes da proxima jogada e n pode ser atacado
				addPhoneyToGame();
				this.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("---------- pronto a correr "+ player.id +"\n");
			player.setAwake();
			while(player.playerIsAlive()) {
				try {
					this.u= new Unblocker(game,player);
					u.run();
					move(player);
					checkWin();
                    sleep(game.REFRESH_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Player "+player.getIdentification()+" sleep interrupted e nova tentativa de move");
				}
			}
		}
    } else {
		System.out.print("---------- pronto a correr "+ player.id +"\n");
		player.setAwake();
		while(!game.ended){
			if(player.canRun){
				try {
					moveTo(player, player.next);
					checkWin();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				player.canRun=false;
			}
		}
    }
	}

    
}

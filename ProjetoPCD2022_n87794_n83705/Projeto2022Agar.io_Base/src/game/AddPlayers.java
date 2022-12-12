package game;

import java.io.Serializable;
import java.net.UnknownHostException;

public class AddPlayers extends Thread implements Serializable{

    private Game game;
    private Player player;
    private Unblocker u;

    public AddPlayers(Player p, Game game){
        this.player=p;
        this.game=game;
        this.u=player.u;
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

    public void run(){	
    if(player instanceof PhoneyHumanPlayer){
		synchronized(player) {
			try {			
				//sleep after add , se for lancado depois por ter sido bloqueado vai esperar 10segs antes da proxima jogada e n pode ser atacado
				addPhoneyToGame();
				this.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("---------- pronto a correr "+ player.id +"\n");
			player.setAwake();
			while(player.playerIsAlive()) {
				try {
					player.u= new Unblocker(game,player);
					player.u.run();
					game.move(player);
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
        try {
			game.addHuman(player);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print("---------- pronto a correr "+ player.id +"\n");
		player.setAwake();
		while(!game.ended){
			if(player.canRun){
				try {
					game.moveTo(player, player.next);
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

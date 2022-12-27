package game;

import java.io.Serializable;

import environment.Coordinate;
import environment.Direction;

@SuppressWarnings("serial")
public class AddPlayers extends Thread implements Serializable {

	private Game game;
	private Player player;
	Unblocker u;

	public AddPlayers(Player p, Game game) {
		this.player = p;
		this.game = game;
	}

	@SuppressWarnings("static-access")
	public void run() {
		if (player instanceof PhoneyHumanPlayer) {
			synchronized (this) {
				try {
					// add do jogador e sleep inicial
					addPhoneyToGame();
					sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//player acorda e comeca as suas acoes
				player.setAwake();
				while (player.playerIsAlive()) {
					try {
						this.u = new Unblocker(game, player);
						//u.start();
						player.move();
						//u.stopU();
						sleep(game.REFRESH_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		}
	}

	//add phoney player com wait pela celula inicial se ja ocupada 
	@SuppressWarnings("deprecation")
	public synchronized void addPhoneyToGame() throws InterruptedException {
		int occupant;
		//lockar a cell para iniciar interacao de colocaçao inicial
		player.getInitialPos().acquireLock();
		
		while (player.getInitialPos().isOcupied()) {
			// esperar que a posicao fique livre e depois adicionar ao jogo
//			System.out.println("Posicao ja ocupada para player " + player.getIdentification() + " pelo Player "
//					+ player.getInitialPos().getPlayer().getIdentification());
//			release da lock ate ser notificado e concorrer de novo para a obter
			occupant=player.getInitialPos().getPlayer().getIdentification();
			System.out.println("Player "+player.getIdentification()+" a espera que a cell "+player.getInitialPos().getPosition().toString()+"seja desocupada pelo player   "
					+ occupant);
			
			player.getInitialPos().go.await();
			// quando houver movimento ou jogador morto
			//se jogo acabou exit
			if(game.ended) {
				System.out.println("Jogador " + player.getIdentification() + " eliminado pois jogo acabou");
				this.stop();
			}
			//se player na cell isDead, unlock sem ser colocado e exit
			if (player.getInitialPos().getPlayer()!=null && !player.getInitialPos().getPlayer().playerIsAlive()) { 
				System.out.println("Jogador " + player.getIdentification() + " eliminado pois jogador "+ player.getInitialPos().deadPlayer +" esta morto na posicao");
				player.getInitialPos().returnLock();
				this.stop();
			}
			
			System.out.println("Player "+player.getIdentification()+" a receber lock da cell "+player.getInitialPos().getPosition().toString()+" libertada pelo player   "
					+ occupant);
			//se nao houver outro jogador a ser colocado antes é colocado no jogo
			if(!player.getInitialPos().isOcupied())
				System.out.println("Player "+ player.getIdentification() + " a ser colocado apos espera!");
		}
//		como o player tem pos == null nao desencadeia o void gas()		
		player.setPosition(player.getInitialPos());
		// To update GUI
		game.playerAdded(player);
//		unlock no fim da interacao
		player.getInitialPos().returnLock();
	}

	//bloqueio do move pois se movimentou contra player morto com wait
	//apos 2segs o Unblocker vai notificar e o move fica desbloqueado
	@SuppressWarnings("static-access")
	public void lock() throws InterruptedException {
		player.isBlocked=true;
		System.out.println(player.getIdentification() + " a espera do Unblocker");
		player.wait();
	}



}

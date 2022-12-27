package environment;

import java.io.Serializable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.Game;
import game.Player;

public class Cell implements Serializable {
	private Coordinate position;
	//	lock unica por celula para sincronizar o acesso à mesma
	private Lock lock = new ReentrantLock();
	//	condicao para notificar players a espera de ser colocados
	public Condition go = lock.newCondition(); 
	private Game game;
	private Player player = null;
	public int deadPlayer;

	public Cell(Coordinate position, Game g) {
		super();
		this.position = position;
		this.game = g;
	}

	public Coordinate getPosition() {
		return position;
	}

	public boolean isOcupied() {
		return player != null;
	}

	public Player getPlayer() {
		return player;
	}

	//se o player ja estiver na board comunica a disponibilidade da cell anterior
	public void setPlayer(Player player) throws InterruptedException {
		if(player.getCurrentCell()!=null)
			player.getCurrentCell().gas(player.getIdentification());
		this.player = player;
	}

	public synchronized void removePlayer() {
		this.player= null;
	}

	public void acquireLock() {
//		System.out.println("-|-|-|-|-|-\nCell " +position.toString()+" Locked by player " + i);
		lock.lock();
	}

	public void returnLock() {
//		System.out.println("-|-|-|-|-|-\nCell " +position.toString()+" Unlocked by player " + i);
		lock.unlock();
	}

	//remove o player da celula e sinaliza aos players a sua disponibilidade
	private void gas(int i) throws InterruptedException {
		acquireLock();
		removePlayer();
		try {
			go.signalAll();
		} finally {
			returnLock();
		}
	}
	
	//se player da cell morrer avisar as celulas em espera
	public void warn(int id) {
		this.deadPlayer=id;
		acquireLock();
		try {
			go.signalAll();
		} finally {
			returnLock();
		}
	}

}

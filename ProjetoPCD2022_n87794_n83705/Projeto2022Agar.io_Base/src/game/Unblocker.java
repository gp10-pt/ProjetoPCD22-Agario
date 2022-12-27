package game;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Unblocker extends Thread implements Serializable {
	protected Game game;
	private Player p;


	public Unblocker(Game game, Player player) {
		super();
		this.game = game;
		this.p = player;
	}

	@SuppressWarnings("deprecation")
	public void stopU() {
		stop();
	}

	// se player isBlocked vai esperar e dar unlock
	@SuppressWarnings({ "static-access" })
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		synchronized(p) {
			try {
				sleep(2000);
				if (p.isBlocked() && p.playerIsAlive()) {
					p.notifyAll();
					//p.aP.interrupt();
					p.isBlocked = false;
					System.out.println("Unblocker a desbloquear "+ p.getIdentification());
					return;
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}

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
		this.stop();
	}

	// se player isBlocked vai esperar e dar unlock
	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		try {
			this.sleep(2000);
			if (p.isBlocked() && p.playerIsAlive()) {
				p.isBlocked = false;
				p.aP.interrupt();
				this.stop();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

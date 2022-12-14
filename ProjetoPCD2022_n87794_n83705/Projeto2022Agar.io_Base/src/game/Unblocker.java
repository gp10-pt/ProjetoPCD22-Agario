package game;

import java.io.Serializable;

public class Unblocker extends Thread implements Serializable {
	protected Game game;
	private Player p;
//	private int id;

	public Unblocker(Game game, Player player) {
		super();
		this.game = game;
		this.p = player;
		// System.out.println("Unblocker ligado");
	}

	public void stopU() {
		this.stop();
	}

	// se player isBlocked vai esperar e dar unlock
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		try {
			this.sleep(2000);
			if (p.isBlocked() && p.playerIsAlive()) {
				p.isBlocked = false;
				//System.out.println("Player "+p.getIdentification()+ " unblocked");
				p.aP.interrupt();
				this.stop();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// System.out.println(e);
		}
	}
}

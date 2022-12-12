package game;

public class Unblocker implements Runnable{
	protected  Game game;
	public Thread th;
	private Player p;
//	private int id;
	
	public Unblocker(Game game, Player player){
		super();
		this.game=game;
		this.p=player;
		Thread t=new Thread(this);
		this.th=t;
		//System.out.println("Unblocker ligado");
	}

	public void stopU(){
		th.stop();
	}

	// se player isBlocked vai esperar e dar unlock
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		try {
			th.sleep(2000);
			if(p.isBlocked() && p.playerIsAlive()){
				p.isBlocked=false;
				//System.out.println("Player "+p.getIdentification()+ " unblocked");
				p.aP.interrupt();
				th.stop();
			}		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println(e);
		}
	}
	}
	




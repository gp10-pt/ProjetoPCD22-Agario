package game;

public class Unblocker implements Runnable{
	protected  Game game;
	public Thread th;
//	private int id;
	
	public Unblocker(Game game){
		super();
		this.game=game;
		Thread t=new Thread(this);
		this.th=t;
		System.out.println("Unblocker ligado");
	}

	// se player isBlocked vai esperar e dar unlock
	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		while(true) {
		for (int x = 0; x < game.DIMX; x++){
			for (int y = 0; y < game.DIMY; y++){ 
				if(game.board[x][y].isOcupied() && game.board[x][y].getPlayer().isBlocked() && game.board[x][y].getPlayer().playerIsAlive()) {
					game.board[x][y].getPlayer().setUnblocked();
				}
			}
		}
		}
	}
	

}

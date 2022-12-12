package game;

/**
 * Class to demonstrate a player being added to the game.
 * @author luismota
 *
 */
public class PhoneyHumanPlayer extends Player {

	int id;

	public PhoneyHumanPlayer(int id, Game game) {
		super(id, game);
	}

	public boolean isHumanPlayer() {
		return false;
	}

	public void run(){	
		synchronized(this) {
			try {			
				//sleep after add , se for lancado depois por ter sido bloqueado vai esperar 10segs antes da proxima jogada e n pode ser atacado
				addPhoneyToGame();
				th.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("---------- pronto a correr "+ this.id +"\n");
			setAwake();
			for(;;) {
				try {
					u= new Unblocker(game,this);
					u.th.start();
					game.move(this);
					checkWin();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Player "+this.getIdentification()+" sleep interrupted e nova tentativa de move");
				}
			}
		}
	}
}

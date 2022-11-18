package gui;

import java.util.Observable;
import java.util.Observer;
import game.Game;
import game.PhoneyHumanPlayer;
import game.Player;
import game.HumanPlayer;

import javax.swing.JFrame;

public class GameGuiMain implements Observer {
	private JFrame frame = new JFrame("pcd.io");
	private BoardJComponent boardGui;
	private Game game;
	private static final int NUMBER_PLAYERS =100;

	public GameGuiMain() {
		super();
		game = new Game();
		game.addObserver(this);

		buildGui();

	}

	private void buildGui() {
		boardGui = new BoardJComponent(game);
		frame.add(boardGui);


		frame.setSize(800,800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init()  {
		frame.setVisible(true);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Player p= new HumanPlayer(90,game);
		game.human=(HumanPlayer) p;
		p.th.start();
		System.out.println("Jogador Humano "+p.getIdentification()+" lancado com sucesso\n\n");
		for (int i = 0; i<NUMBER_PLAYERS; i++) { 
			p=new PhoneyHumanPlayer((i+1), game);
			p.th.start();
			//System.out.println("Jogador "+(i+1)+" lancado com sucesso");
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

	public static void main(String[] args) {
		GameGuiMain game = new GameGuiMain();
		game.init();
	}

}

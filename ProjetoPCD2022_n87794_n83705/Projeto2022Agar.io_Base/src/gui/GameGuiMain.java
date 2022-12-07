package gui;

import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import game.Game;
import game.PhoneyHumanPlayer;
import game.Player;
import game.Unblocker;
import game.HumanPlayer;

import javax.swing.JFrame;

import Server.Client;
import Server.Server;

public class GameGuiMain implements Observer {
	private JFrame frame = new JFrame("pcd.io");
	private BoardJComponent boardGui;
	public Game game;
	private Server servidor;

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
			game.addPhoneys();
			//start do servidor que fica a esperar o add de todos os jogadores
			servidor= new Server(8080,this);
		} catch (InterruptedException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

	public static void main(String[] args) {
		GameGuiMain gui = new GameGuiMain();
		gui.init();
	}

}

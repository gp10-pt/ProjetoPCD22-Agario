package Client;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import game.Game;

@SuppressWarnings("deprecation")
public class GameGuiMain implements Observer {
	private JFrame frame = new JFrame("pcd.io");
	private BoardJComponent boardGui;
	public Game game;
	private boolean alternativeKeys;

	public GameGuiMain(Game g, boolean alternativeKeys) {
		super();
		this.game = g;
		game.addObserver(this);
		this.alternativeKeys = alternativeKeys;
		buildGui();
	}

	public void buildGui() {
		boardGui = new BoardJComponent(game, alternativeKeys);
		frame.add(boardGui);
		frame.setSize(800, 800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		frame.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}

	public BoardJComponent getBoardGui() {
		return boardGui;
	}

	public Game getGame() {
		return this.game;
	}

}

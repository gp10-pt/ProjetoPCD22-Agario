package Client;

import environment.Coordinate;
import game.Game;
import game.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * Creates a JComponent to display the game state. At the same time, this is
 * also a KeyListener for itself: when a key is pressed, attribute
 * lastPressedDirection is updated accordingly. This feature is a demo to better
 * understand how to deal with keys pressed, useful for the remote client. This
 * feature is not helpful for the main application and should be ignored. This
 * class does not need to be edited.
 * 
 * @author luismota
 *
 */
@SuppressWarnings("serial")
public class BoardJComponent extends JComponent implements KeyListener {

	private Game game;
	private Image obstacleImage = new ImageIcon("obstacle.png").getImage();
	private Image humanPlayerImage = new ImageIcon("abstract-user-flat.png").getImage();
	private String lastPressedDirection = "UP";

	private boolean alternativeKeys;

	public BoardJComponent(Game game, boolean alternativeKeys) {
		this.alternativeKeys = alternativeKeys;
		this.game = game;
		setFocusable(true);
		addKeyListener(this);
	}

	// vai ser preenchido de acordo com a board do game
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double cellHeight = getHeight() / (double) Game.DIMY;
		double cellWidth = getWidth() / (double) Game.DIMX;

		for (int y = 1; y < Game.DIMY; y++) {
			g.drawLine(0, (int) (y * cellHeight), getWidth(), (int) (y * cellHeight));
		}
		for (int x = 1; x < Game.DIMX; x++) {
			g.drawLine((int) (x * cellWidth), 0, (int) (x * cellWidth), getHeight());
		}
		for (int x = 0; x < Game.DIMX; x++)
			for (int y = 0; y < Game.DIMY; y++) {
				Coordinate p = new Coordinate(x, y);

				Player player = game.getCell(p).getPlayer();
				if (player != null) {
					// Fill yellow if there is a dead player
					if (player.getCurrentStrength() == 0) {
						g.setColor(Color.YELLOW);
						g.fillRect((int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
								(int) (cellHeight));
						g.drawImage(obstacleImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
								(int) (cellHeight), null);
						// if player is dead, don'd draw anything else?
						continue;
					}
					// Fill red if it is a phoney
					if (!player.isHumanPlayer()) {
						g.setColor(Color.MAGENTA);
						g.fillRect((int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
								(int) (cellHeight));
						// Custom icon?
						g.drawImage(humanPlayerImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight), null);
					}
					// Fill green if it is a human player
					if (player.isHumanPlayer()) {
						g.setColor(Color.GREEN);
						g.fillRect((int) (p.x * cellWidth), (int) (p.y * cellHeight), (int) (cellWidth),
								(int) (cellHeight));
						// Custom icon?
						g.drawImage(humanPlayerImage, (int) (p.x * cellWidth), (int) (p.y * cellHeight),
								(int) (cellWidth), (int) (cellHeight), null);
					}
					g.setColor(new Color(player.getIdentification() * 1000));
					((Graphics2D) g).setStroke(new BasicStroke(5));
					Font font = g.getFont().deriveFont((float) cellHeight);
					g.setFont(font);
					String strengthMarking = (player.getCurrentStrength() == 10 ? "X"
							: "" + player.getCurrentStrength());
					g.drawString(strengthMarking, (int) ((p.x + .2) * cellWidth), (int) ((p.y + .9) * cellHeight));
				}

			}
	}

	//direction p movement depente das keys selecionadas
	@Override
	public void keyPressed(KeyEvent e) {
		if (alternativeKeys) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				lastPressedDirection = "UP";
				break;
			case KeyEvent.VK_A:
				lastPressedDirection = "LEFT";
				break;
			case KeyEvent.VK_S:
				lastPressedDirection = "DOWN";
				break;
			case KeyEvent.VK_D:
				lastPressedDirection = "RIGHT";
				break;
			}
		} else {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				lastPressedDirection = "UP";
				break;
			case KeyEvent.VK_LEFT:
				lastPressedDirection = "LEFT";
				break;
			case KeyEvent.VK_DOWN:
				lastPressedDirection = "DOWN";
				break;
			case KeyEvent.VK_RIGHT:
				lastPressedDirection = "RIGHT";
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// ignore
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Ignored...
	}

	public String getLastPressedDirection() {
		return lastPressedDirection;
	}

	public void clearLastPressedDirection() {
		lastPressedDirection = null;
	}
}

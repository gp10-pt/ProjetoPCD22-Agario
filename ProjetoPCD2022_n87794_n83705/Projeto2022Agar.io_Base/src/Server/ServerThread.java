package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import environment.Cell;
import game.Game;
import game.HumanPlayer;

public class ServerThread extends Thread {

	private Socket socket;
	private Game game;
	private BufferedReader in;
	private ObjectOutputStream objOut;

	public ServerThread(Socket socket, Game game) {
		super();
		this.socket = socket;
		this.game = game;
	}
	
	public void run() {
		try {
			openComs();
			communication();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openComs() throws IOException {
		objOut = new ObjectOutputStream(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void communication() throws InterruptedException, IOException {
		//player adicionado ao jogo apos conexao do cliente
		HumanPlayer human = game.addHuman();
		System.out.println("Cliente adicionado!\n");
		while (!game.ended) {
			//envio info necessaria		
			Cell[][] board = game.getBoard();
			if (human.playerIsAlive() && !human.won && game.getPhoneysStarted()) {
				Message msg = new Message(board, false, true);
				objOut.writeObject(msg);
				// leitura da mensagem do client e update para o humano mexer se este ainda nao tiver ganho			
				String s = in.readLine();
				if (s != null && !human.won) {
					switch (s) {
					case "RIGHT":
						human.next = environment.Direction.RIGHT;
						human.move(human.next);
						break;
					case "LEFT":
						human.next = environment.Direction.LEFT;
						human.move(human.next);
						break;
					case "DOWN":
						human.next = environment.Direction.DOWN;
						human.move(human.next);
						break;
					case "UP":
						human.next = environment.Direction.UP;
						human.move(human.next);
						break;
					}
				}
				//se morto ou vencedor enviar a board p update visual
			} else {
				Message msg = new Message(board, false, false);
				objOut.writeObject(msg);
			}
			//sleep de 400ms para novo envio da informacao			
			sleep(game.REFRESH_INTERVAL);
		}
		//game acabou
		endComs();
	}

	//envio de mensagem de fim de jogo para o cliente
	public void endComs() throws IOException {
		Message fim = new Message(game.getBoard(), true, false);
		objOut.writeObject(fim);
		objOut.close();
		in.close();
		socket.close();
	}
	
}

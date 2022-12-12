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
	private BufferedReader  in;
	private ObjectOutputStream objOut;

	public ServerThread(Socket socket, Game game){
		super();
		this.socket=socket;
		this.game=game;
	}

	public void openComs() throws IOException{
		objOut = new ObjectOutputStream (socket.getOutputStream());
		in = new BufferedReader (new InputStreamReader ( socket.getInputStream ()));
	}

	public void communication() throws InterruptedException, IOException{
//player é adicionado ao jogo apos conexao do cliente
		HumanPlayer human= game.addHuman();
		System.out.println("Cliente adicionado!");
//mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		while(!game.ended){
//envio info necessaria		
			Cell[][] board= game.getBoard();			
			Message msg= new Message(board,false);
			objOut.writeObject(msg);
			System.out.println("Server sent game info!");
// leitura da mensagem do client e update para o humano mexer			
			String s= in.readLine();
			if (s != null) {
				switch (s) {
					case "RIGHT":
						human.next = environment.Direction.RIGHT;
						human.canRun=true;
						break;
					case "LEFT":
						human.next = environment.Direction.LEFT;
						human.canRun=true;
						break;
					case "DOWN":
						human.next = environment.Direction.DOWN;
						human.canRun=true;
						break;
					case "UP":
						human.next = environment.Direction.UP;
						human.canRun=true;
						break;
				}
				System.out.println("Server processed client info!");
			}
			else{
				System.out.println("No Direction chosen by client!");
			}
//sleep de 400ms para novo envio da informação			
			sleep(game.REFRESH_INTERVAL);
		}
//game acabou
		endComs();
	}

	public void endComs() throws IOException{
		Message fim=new Message(null, true);
		objOut.writeObject(fim);
		objOut.close();
		in.close();
		socket.close();
	}

	public void run() {
//canais de comunicacao abertos	e a serverThread corre
		try {
			openComs();
			communication();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

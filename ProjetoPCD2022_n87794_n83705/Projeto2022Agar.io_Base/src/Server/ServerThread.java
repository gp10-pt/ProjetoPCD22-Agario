package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import environment.Cell;
import game.Game;
import game.HumanPlayer;
import game.Player;

public class ServerThread extends Thread {

	Socket socket=null;
	private Game game;
	public ArrayList<Player> humans= new ArrayList<Player>();
	private BufferedReader  in;
	private ObjectOutputStream objOut;

	public ServerThread(Socket socket, Game game){
		super();
		this.socket=socket;
		this.game=game;
	}

	/*public ArrayList<GameInfo> fillMessage (Game game){
        ArrayList<GameInfo> msg = new ArrayList<>();
        for (int x = 0; x < Game.DIMX; x++) {
			for (int y = 0; y < Game.DIMY; y++) {
				if (game.board[x][y].isOcupied()) {
					int posX = x;
					int posY = y;
                    boolean isHuman = game.board[x][y].getPlayer().isHumanPlayer();
					int strength = game.board[x][y].getPlayer().getCurrentStrength();
					GameInfo data = new GameInfo(posX, posY, isHuman, strength);
					msg.add(data);
				}
			}
		}
        return msg;
    }*/


	public void run() {
	//canais de comunicacao abertos	
		try {
			this.objOut = new ObjectOutputStream (socket.getOutputStream());
			this.in = new BufferedReader (new InputStreamReader ( socket.getInputStream ()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("ServerThread "+this.getId()+" lançada");
	//player é adicionado ao jogo apos conexao do cliente
		HumanPlayer human= new HumanPlayer(game.NUM_PLAYERS, game);
		human.run();
		game.NUM_PLAYERS++;
	//mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		while(!game.ended){
			try{	
	//envio info necessaria		
				Cell[][] board= game.board;			
				//ArrayList<GameInfo> info= fillMessage(game);
				Message msg= new Message(board,false);
				objOut.writeObject(msg);
				System.out.println("Server sent game info!");
	// leitura da mensagem do client e update para o humano mexer			
				String s= in.readLine();
				if (s != null) {
					switch (s) {
					case "LEFT":
						human.next = environment.Direction.LEFT;
						human.canRun=true;
						break;
					case "UP":
						human.next = environment.Direction.UP;
						human.canRun=true;
						break;
					case "DOWN":
						human.next = environment.Direction.DOWN;
						human.canRun=true;
						break;
					case "RIGHT":
						human.next = environment.Direction.RIGHT;
						human.canRun=true;
						break;
					}
				}
	//sleep de 400ms para novo envio da informação
				System.out.println("Server received game info!");
				sleep(game.REFRESH_INTERVAL); 
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	//game is over
		try {
			Message fim=new Message(null, true);
			objOut.writeObject(fim);
			objOut.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Client.GameGuiMain;
import game.Game;

import static java.lang.Integer.parseInt;

public class Server {
	private ServerSocket sSocket;
	private final int PORT;
	public Game game;
	
	public Server (int port, Game g) {
		this.PORT=port;
		this.game=g;
		try {
			runServer();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runServer() throws IOException, ClassNotFoundException {
		sSocket= new ServerSocket(PORT);
		System.out.println("\n-»\n-»\nServidor a correr no porto "+PORT+"\n«-\n«-\n");
		//espera pelo pedido de ligacao dos clientes e lança a thread autonoma p tratar do jogador
		try{
			while(!game.ended){
				System.out.println("Calling accept");
				Socket socket= sSocket.accept();
				new ServerThread(socket,game).start();
			}
		} finally {
			sSocket.close();
		}	
	}

	public InetAddress getAddress(){
		return sSocket.getInetAddress();
	}

	public int getPort(){
		return PORT;
	}

	public static void main(String[] args){
		Game game= new Game();
		game.startPhoneys();
		//start do servidor que fica a esperar o add dos jogadores
		Server servidor= new Server(8080,game);
	}

}

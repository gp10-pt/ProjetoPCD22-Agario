package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import gui.GameGuiMain;

import static java.lang.Integer.parseInt;

public class Server {
	private final int PORT;
	public GameGuiMain ui;
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
//	private public ArrayList<> players;
	
	public Server (int port, GameGuiMain ggm) {
		this.PORT=port;
		this.ui=ggm;
		try {
			runS();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runS() throws IOException, ClassNotFoundException {
		ServerSocket sSocket= new ServerSocket(PORT);
		int i =0;
		System.out.println("\n-»\n-»\nServidor a correr no porto "+PORT+"\n«-\n«-\n");
		//espera pelo pedido de ligacao dos clientes e lança a thread autonoma p tratar do jogador
		while(!sSocket.isClosed()) {
			Socket socket= sSocket.accept();
			new ServerThread(socket,this).start();
			System.out.println("Cliente "+i+" conectado");
			i++;
		}		
		sSocket.close();
	}


}

package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import gui.GameGuiMain;

import static java.lang.Integer.parseInt;

public class Server {
	private ServerSocket sSocket;
	private final int PORT;
	public GameGuiMain ui;
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	
	public Server (int port, GameGuiMain ggm) {
		this.PORT=port;
		this.ui=ggm;
		try {
			runServer();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runServer() throws IOException, ClassNotFoundException {
		sSocket= new ServerSocket(PORT);
		int i =0;
		System.out.println("\n-»\n-»\nServidor a correr no porto "+PORT+"\n«-\n«-\n");
		//espera pelo pedido de ligacao dos clientes e lança a thread autonoma p tratar do jogador
		while(true){
			Socket socket= sSocket.accept();
			new ServerThread(socket,this).start();
			i++;
			//System.out.println("Thread para cliente "+i+" iniciada");
			if(i==ui.game.NUM_HUMANS)
				break;
		}		
		//sSocket.close();
	}

	public InetAddress getAddress(){
		return sSocket.getInetAddress();
	}

	public int getPort(){
		return PORT;
	}


}

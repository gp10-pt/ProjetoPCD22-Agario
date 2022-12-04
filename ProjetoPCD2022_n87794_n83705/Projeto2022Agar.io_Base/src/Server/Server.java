package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import static java.lang.Integer.parseInt;

public class Server {
	private final int PORT;
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
//	private public ArrayList<> players;
	
	public Server (int port) {
		this.PORT=port;
	}

	public void runS() throws IOException, ClassNotFoundException {
		ServerSocket sSocket= new ServerSocket( PORT);
		int i =0;
		System.out.println("\n-»\n-»\n-»\n-»\n-»\nServidor a correr no porto "+PORT+"\n«-\n«-\n");
		while(!sSocket.isClosed()) {
			Socket socket= sSocket.accept();
			new ServerThread(socket).start();
			System.out.println("Cliente "+i+" conectado");i++;
		}		
		sSocket.close();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		new Server(parseInt(args[0])).runS();
	}

}

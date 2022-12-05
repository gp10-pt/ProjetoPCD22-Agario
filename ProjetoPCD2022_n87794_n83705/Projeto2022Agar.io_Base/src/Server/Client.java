package Server;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import static java.lang.Integer.parseInt;

public class Client implements KeyListener{
	
    private PrintWriter out;
    private BufferedReader br;
	private String gps="P  B\na  i\nu  g\n"; //string com a indicaçao do caminho desejado 
    
	public Client (InetAddress address, int port, String up, String left, String down, String right) {
		System.out.println("as teclas selecionadas sao:\n"+up+"\n"+left+"\n"+down+"\n"+right+"\n");
		try {
			this.connectToGame();
		} catch (IOException e) {
			System.out.println("Falha no lancamento );\n");
			e.printStackTrace();
		}
	}

	public void connectToGame() throws IOException {
		System.out.println("Cliente iniciado");
		InetAddress address = InetAddress.getByName(null);
		int PORT = 8080;
		Socket socket = new Socket(address, PORT);
		System.out.println("Cliente conectado a socket: "+socket.getPort());
		ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
		out = new PrintWriter( new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
	    System.out.println("Cliente conectado");
			while(!socket.isClosed()) {
				System.out.println("gps = "+gps);
				System.out.println("estado da print writer: "+ out.checkError());
				out.println(gps);
				out.flush();
			}
	}
	
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			this.gps = "ESQUERDA";
			break;
		case KeyEvent.VK_RIGHT:
			this.gps = "DIREITA";
			break;
		case KeyEvent.VK_UP:
			this.gps = "CIMA";
			break;
		case KeyEvent.VK_DOWN:
			this.gps = "BAIXO";
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		//new Client(parseInt(args[1],args[2],args[3],args[4],args[5]));
//		while(!socket) void c key.Listener

	}

}

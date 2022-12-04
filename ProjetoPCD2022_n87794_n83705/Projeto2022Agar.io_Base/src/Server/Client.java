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
    
	public Client (int port, String up, String left, String down, String right) {
		System.out.println("as teclas selecionadas sao:\n"+up+"\n"+left+"\n"+down+"\n"+right+"\n");
		KeyListener kl = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					gps = "CIMA";
					System.out.println("up "+gps);
					break;
				case KeyEvent.VK_RIGHT:
					gps = "ESQUERDA";
					System.out.println("left "+gps);
					break;
				case KeyEvent.VK_UP:
					gps = "BAIXO";
					System.out.println("down "+gps);
					break;
				case KeyEvent.VK_DOWN:
					gps = "DIREITA";
					System.out.println("right "+gps);
					break;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		};
		try {
			this.connectToGame();
		} catch (IOException e) {
			System.out.println("Falha no lancamento );\n");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		new Client(parseInt(args[0]),args[1],args[2],args[3],args[4]);
//		while(!socket) void c key.Listener

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
			this.gps = "CIMA";
			break;
		case KeyEvent.VK_RIGHT:
			this.gps = "ESQUERDA";break;
		case KeyEvent.VK_UP:
			this.gps = "BAIXO";
			break;
		case KeyEvent.VK_DOWN:
			this.gps = "DIREITA";
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

}

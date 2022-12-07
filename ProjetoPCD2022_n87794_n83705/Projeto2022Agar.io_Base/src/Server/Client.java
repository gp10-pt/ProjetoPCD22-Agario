package Server;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import environment.Direction;
import game.Game;
import game.Player;

public class Client /*extends Thread*/ implements KeyListener {

	private int player;
	private Socket socket;
	private static Game game;
	private Direction gps=null;
	private InetAddress address;
	private int port;

	//cria client e conecta ao jogo
	public Client (InetAddress address, int port, String up, String left, String down, String right) {
		this.address=address;
		this.port=port;
		try {
			//conexao cliente servidor
			connectToGame(address,port);
			//processo de rececao do game do servidor e envio de mensagem com informacao necessaria p move (player e direcao)
			communication();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			System.out.println("Falha no lancamento );\n");
			e.printStackTrace();
		}
		//System.out.println("as teclas selecionadas sao:\n"+up+"\n"+left+"\n"+down+"\n"+right+"\n");
	}

	//para o cliente saber qual player está a controlar é o player com identification=id do array humans
	public void setPlayer(int id){
		this.player=id;
	}

	public void connectToGame(InetAddress address, int port) throws IOException, ClassNotFoundException,InterruptedException{
		//System.out.println("Cliente iniciado");
		this.socket = new Socket(address, port);
		System.out.println("Client "+player+"conectado a socket: "+socket.getPort());
		//envio mensagem com id do player do cliente para add ao jogo
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());;
		Message msg= new Message();
		msg.setPlayerId(player);	
		out.writeObject(msg);
	}
	
	public void communication(){
		ObjectOutputStream out;
		ObjectInputStream in;
		while(true){
			try {
				//client recebe o game do servidor
				System.out.println("\n"+player +" waiting for game info");
				in = new ObjectInputStream(socket.getInputStream());
				game= (Game) in.readObject();
				System.out.println("Client "+player+" received game info!");
				//se player está morto, mata a ligacao com o server
				for(Player p :game.humans){
					if(p.getIdentification()==player && p.isDead){
						out = new ObjectOutputStream(socket.getOutputStream());
						out.writeObject("end");
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					}
				}
				//mensagem com informacao necessaria p move (player e direcao)
				Message msg= new Message();
				msg.setDirection(getLastPressedDirection());	
				msg.setPlayerId(player); 
				out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(msg);
				System.out.println("Client "+player+" sent move info!");
				//clean da direcao e fecho dos canais de comunicacao
				clearLastPressedDirection();
				out.close();
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT :
				gps=environment.Direction.LEFT; System.out.println("left");
				break;
			case KeyEvent.VK_RIGHT:
				gps=environment.Direction.RIGHT; System.out.println("right");
				break;
			case KeyEvent.VK_UP:
				gps=environment.Direction.UP; System.out.println("up");
				break;
			case KeyEvent.VK_DOWN:
				gps=environment.Direction.DOWN; System.out.println("down");
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

	public Direction getLastPressedDirection() {
		return gps;
	}

	public void clearLastPressedDirection() {
		gps=null;
	}

	//inicia NUM_HUMANS clientes para controlar os NUM_HUMANS humanos
	public static void main(String[] args){
		for(int i=0;i!=5;i++){
			Client c=null;
			try {
				c=new Client(InetAddress.getLocalHost(),8080,"up","left","down","right");
				c.setPlayer(i);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

}

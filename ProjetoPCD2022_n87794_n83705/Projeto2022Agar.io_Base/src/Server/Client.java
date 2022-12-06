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

import environment.Direction;
import game.Game;

public class Client extends Thread implements KeyListener {

	private int player;
	private Socket socket;
	private ObjectOutputStream out;
	private Game game;
	private Direction gps=null; 
    
	/* da fix ao server com o main que tinhas que mudei isso sem querer 
	transformei isto numa thread mas n sei se é o melhor, ve essa cena das teclas nos arguments p tentar dar start no game
	acho q para termos a direcao aqui precisamos de por no BoardJComponent como veio original para usar o getLastPressedDirection() e clearLastPressedDirection()
	aqui no cliente quando queremos escrever a mensagem , pergunta ao stor disso
	*/

	public Client (InetAddress address, int port, String up, String left, String down, String right) {
		//System.out.println("as teclas selecionadas sao:\n"+up+"\n"+left+"\n"+down+"\n"+right+"\n");
		try {
			connectToGame(address,port);
			run();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Falha no lancamento );\n");
			e.printStackTrace();
		}
		//quando run termina é porque player está morto por isso termina se o cliente
		this.stop();
	}

	//para o cliente saber qual player está a controlar(, feito on launch pelo game ?)
	public void setPlayer(int id){
		this.player=id;
	}

	//client conecta se ao servidor
	public void connectToGame(InetAddress address, int port) throws IOException, ClassNotFoundException{
		//System.out.println("Cliente iniciado");
		this.socket = new Socket(address, port);
		System.out.println("Cliente conectado a socket: "+socket.getPort());
	}

	//processo de rececao do game do servidor e envio de mensagem mensagem com informacao necessaria p move (player e direcao)
	@Override
	public void run(){
		// TODO Auto-generated method stub
		ObjectInputStream in;
		while(true){
			try {
				//client recebe o game do servidor
				in = new ObjectInputStream(socket.getInputStream());
				game= (Game) in.readObject();
				//se player está morto, mata a ligacao com o server
				if(game.humans[player].isDead){
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject("end");
					//socket.close();
					break;
				}
				//mensagem com informacao necessaria p move (player e direcao)
				Message msg= new Message();
				msg.setDirection(gps);	
				msg.setPlayerId(player); 
				out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(msg);
				out.close();
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

}

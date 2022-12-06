package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import environment.Direction;
import game.Game;
import game.Player;

public class ServerThread extends Thread {

	Socket socket=null;
	Server servidor=null;
	private Game game;

	ServerThread(Socket socket, Server servidor){
		this.socket=socket;
		this.servidor=servidor;
		game=servidor.ui.game;
	}

	public void run() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		System.out.println("ServerThread "+this.getId()+" lançada");
		//mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		while(true){
			try{			
				ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
				objOut.writeObject(servidor.ui);
				System.out.println("Server sent game info!");
				ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
				//se cliente enviar end pois jogador está morto, termina a ligacao senao executa o move do player
				if(((String) objIn.readObject()) == "end"){
					socket.close();
					break;
				}
				else{
					msg=(Message) objIn.readObject();
					//Direction next= ((Direction) msg.getDirection());
					Player p=game.humans[msg.getPlayerId()];
					p.next= ((Direction) msg.getDirection());
					System.out.println("Server received game info!");
					game.moveTo(p,p.next);	
				}
				//sleep de 400ms para novo envio da formação
				sleep(game.REFRESH_INTERVAL); 
			} catch (InterruptedException | IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.stop();
	}
	
}

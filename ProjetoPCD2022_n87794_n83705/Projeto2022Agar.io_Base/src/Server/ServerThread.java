package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import environment.Direction;
import game.Game;
import game.Player;
import game.Unblocker;

public class ServerThread extends Thread {

	Socket socket=null;
	Server servidor=null;
	private Game game;

	ServerThread(Socket socket, Server servidor){
		this.socket=socket;
		this.servidor=servidor;
		game=servidor.ui.game;
	}

	private void addPlayer(int id) throws InterruptedException, UnknownHostException{
		for(Player p :game.humans){
			if(p.getIdentification()==id){
				game.addHuman(p, id);
			}
		}
		//sleep de inicio de jogos
		this.wait(10000);
	}

	public void run() {
		// TODO Auto-generated method stub
		Message msg = new Message();
		System.out.println("ServerThread "+this.getId()+" lançada");
		//cliente envia id do seu player ao conectar se com o server, é lido e player é adicionado ao jogo 
		try{
			ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
			msg=(Message) objIn.readObject();
			int id=msg.getPlayerId();
			addPlayer(id);
		} catch (InterruptedException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		//mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		while(true){
			try{			
				ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
				objOut.writeObject(servidor.ui);
				System.out.println("Server sent game info!");
				ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
				//se cliente enviar end pois jogador está morto, termina a ligacao senao executa o move do player
				if(((String) objIn.readObject()).equals("end")){
					socket.close();
					break;
				}
				//update do move como acontece no player.run (falta o checkWin ainda)
				else{
					msg=(Message) objIn.readObject();
					//Direction next= ((Direction) msg.getDirection());
					for(Player p :game.humans){
						if(p.getIdentification()==msg.getPlayerId()){
							p.next= ((Direction) msg.getDirection());
							System.out.println("Server received game info!");
							game.moveTo(p,p.next);
							//p.checkWin();
						}	
					}
				}
				//sleep de 400ms para novo envio da informação
				sleep(game.REFRESH_INTERVAL); 
			} catch (InterruptedException | IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		this.stop();
	}
	
}

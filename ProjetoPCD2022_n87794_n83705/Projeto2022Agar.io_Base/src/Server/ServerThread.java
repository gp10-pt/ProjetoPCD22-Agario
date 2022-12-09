package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import environment.Direction;
import game.Game;
import game.Player;

public class ServerThread extends Thread {

	Socket socket=null;
	private Game game;
	public ArrayList<Player> humans= new ArrayList<Player>();

	ServerThread(Socket socket, Game game){
		this.socket=socket;
		this.game=game;
	}

	public void run() {
		// TODO Auto-generated method stub
		System.out.println("ServerThread "+this.getId()+" lançada");
		//cliente envia id do seu player ao conectar se com o server, é lido e player é adicionado ao jogo 
		try{
			ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
			Message msg=(Message) objIn.readObject();
			//playerId é lido e human player com esse id é adicionado ao jogo 
			int id=msg.getPlayerId();
			game.addHuman(id);
			this.humans=game.humans;
			//sleep de inicio de jogo
			sleep(10000);
			for(Player p :humans){
				if(p.getIdentification()==id){
					p.setAwake();
				}
			}
			System.out.print("---------- pronto a correr "+ id +"\n");
		} catch (InterruptedException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		//mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		while(true){
			try{			
				this.humans=game.humans;
				ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
				Message msg= new Message(humans);
				objOut.writeObject(msg);
				System.out.println("Server sent game info!");
				ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
				msg= (Message) objIn.readObject();
				//se cliente enviar end pois jogador está morto, termina a ligacao senao executa o move do player
				if(msg.getMsg().equals("end")){
					socket.close();
					break;
				}
				//update do move como acontece no player.run (falta o checkWin ainda)
				else{
					msg= (Message) objIn.readObject();
					//Direction next= ((Direction) msg.getDirection());
					for(Player p :humans){
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

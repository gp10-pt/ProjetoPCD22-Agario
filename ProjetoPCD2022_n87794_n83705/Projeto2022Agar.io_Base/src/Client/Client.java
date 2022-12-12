package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import Server.Message;
import game.Game;
import java.awt.event.KeyEvent;


public class Client {

	private Socket socket;
	private boolean alternateKeys;
    private ObjectInputStream objIn;
	private PrintWriter out;
	private GameGuiMain gui;

	//cria client e conecta ao jogo
	public Client (InetAddress address, int port, boolean alternateKeys) {
		this.alternateKeys=alternateKeys;
		if(alternateKeys) 
			System.out.println("as teclas selecionadas sao: wasd");
		else 
			System.out.println("as teclas selecionadas sao: as setas");

		try {
			//conexao cliente servidor
			connectToGame(address,port);
			//gui start		
			startGame();
			//abrir canais de comunicacao
            openComs();
			//processo de rececao da info do servidor e envio de mensagem com informacao necessaria p move (direcao)
            communication();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			System.out.println("Falha no lancamento );\n");
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void connectToGame(InetAddress address, int port) throws IOException, ClassNotFoundException,InterruptedException{
		this.socket = new Socket(address, port);
		System.out.println("Client conectado a socket: "+socket.getPort());
		/*ClientThread ct= new ClientThread(socket,alternateKeys);
		ct.start();
		ct.join();*/
	}

	public void startGame(){
		Game game=new Game();
		gui= new GameGuiMain(game,alternateKeys);
		gui.init();
	}

	public void openComs() throws IOException{
		this.objIn = new ObjectInputStream(socket.getInputStream());
		this.out= new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		System.out.println("Client thread lançada");
	}

	//processo de rececao da info do servidor e envio de mensagem com informacao necessaria p move (direcao)
    public void communication() throws IOException, ClassNotFoundException{
	//client recebe a game info do servidor e atualiza a gui	
	KeyEvent e = new KeyEvent(gui.getBoardGui(), 1, 20, 1, 10, 'a');
		while(true){
			Message msg= (Message) objIn.readObject();
			if(msg.getEnd()==false){
				gui.getGame().updateBoard(msg.getBoard());
				gui.getBoardGui().keyPressed(e);
				System.out.println("\nClient updated with game info");
	//envio da direcao p server	
				if(gui.getBoardGui().getLastPressedDirection()!=null)			
					sendComs(gui);
			}
			else{
				break;
			}
		}
		objIn.close();
		out.close();
	}

	

	public void sendComs(GameGuiMain gui){
//mensagem com informacao necessaria p move (direcao)
		System.out.println(gui.getBoardGui().getLastPressedDirection());
		out.println(gui.getBoardGui().getLastPressedDirection());
		System.out.println("Client sent move info!");
//clean da direcao
		gui.getBoardGui().clearLastPressedDirection();
	}
	

	//inicia NUM_HUMANS clientes para controlar os NUM_HUMANS humanos
	public static void main(String[] args){
		try {
			Client c = new Client(InetAddress.getByName(null),8080,false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	

}

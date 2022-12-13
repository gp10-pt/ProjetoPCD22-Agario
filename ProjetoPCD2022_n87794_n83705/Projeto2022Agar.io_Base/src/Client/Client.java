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

public class Client {

	private Socket socket;
	private boolean alternateKeys;
	private ObjectInputStream objIn;
	private PrintWriter out;
	private GameGuiMain gui;
	private BoardJComponent listener;

	// cria client e conecta ao jogo
	public Client(InetAddress address, int port, boolean alternateKeys) {
		this.alternateKeys = alternateKeys;
		if (alternateKeys)
			System.out.println("as teclas selecionadas sao: wasd");
		else
			System.out.println("as teclas selecionadas sao: as setas");

		try {
			// conexao cliente servidor
			connectToGame(address, port);
			// gui start
			startGame();
			// processo de rececao da info do servidor e envio de mensagem com informacao
			// necessaria p move (direcao)
			communication();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
			//System.out.println("\n Jogo fechado pelo cliente ");
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void connectToGame(InetAddress address, int port)
			throws IOException, ClassNotFoundException, InterruptedException {
		this.socket = new Socket(address, port);
		System.out.println("Client conectado a socket: " + socket.getPort());
	}

	public void startGame() {
		Game game = new Game();
		gui = new GameGuiMain(game, alternateKeys);
		gui.init();
		listener = gui.getBoardGui();
	}

	public void openComs() throws IOException {
		this.objIn = new ObjectInputStream(socket.getInputStream());
		this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		System.out.println("Client thread lancada");
	}

	// processo de rececao da info do servidor e envio de mensagem com informacao
	// necessaria p move (direcao)
	public void communication() throws IOException, ClassNotFoundException {
		// abrir canais de comunicacao
		openComs();
		// client recebe a game info do servidor e atualiza a gui
		while (true) {
			Message msg = (Message) objIn.readObject();
			gui.getGame().updateBoard(msg.getBoard());
			if (!msg.getEnd()) {
				if(msg.getAlive()){
					if(listener.getLastPressedDirection()!=null){
						System.out.println(listener.getLastPressedDirection());
					}
					// envio da direcao p server
					out.println(listener.getLastPressedDirection());
					// clean da direcao
					listener.clearLastPressedDirection();
				}			
			} else {
				break;
			}
		}
		objIn.close();
		out.close();
	}

	// inicia 1 cliente para controlar 1 humano ( args = localhost 8080 0/1 ) - 0 para setas e 1 para wasd
	public static void main(String[] args) {
		try {
			InetAddress ip=InetAddress.getByName(args[0]);
			int port=Integer.parseInt(args[1]);
			int keys=Integer.parseInt(args[2]);
			if(keys==0) {
				Client c = new Client(ip, port, false);
			}
			else {
				Client c = new Client(ip, port, true);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

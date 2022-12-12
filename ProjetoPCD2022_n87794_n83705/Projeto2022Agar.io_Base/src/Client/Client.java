package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client {

	private Socket socket;
	private boolean alternateKeys;

	//cria client e conecta ao jogo
	public Client (InetAddress address, int port, boolean alternateKeys) {
		this.alternateKeys=alternateKeys;
		if(alternateKeys) System.out.println("as teclas selecionadas sao: wasd");
		else System.out.println("as teclas selecionadas sao: as setas");
		try {
	//conexao cliente servidor
			connectToGame(address,port);
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
		//System.out.println("Cliente iniciado");
		this.socket = new Socket(address, port);
		System.out.println("Client conectado a socket: "+socket.getPort());
		ClientThread ct= new ClientThread(socket,alternateKeys);
		ct.start();

		while(ct.isAlive()){
			Thread.sleep(2000);
		}
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

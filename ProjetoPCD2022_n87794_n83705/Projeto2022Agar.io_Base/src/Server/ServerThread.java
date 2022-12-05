package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread {

	Socket socket=null;

	ServerThread(Socket socket){
		this.socket=socket;
	}

	public void run() {
		// TODO Auto-generated method stub
		Message msg = null;
		// a cada 400ms mandar update do jogo p client, client recebe e responde com informacao p server dar update do move
		try{
			System.out.println("ServerThread "+this.getId()+" lançada");
			ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
//			msg=new Message(update)
			while ( (msg = (Message) objIn.readObject() ) != null) {
//						mudar o atributo next na instancia do player no jogo
//						
//						objOut.writeObject (message); //resposta ao cliente
			}
			sleep(400); //fim da comunicação
//		socket.close();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}

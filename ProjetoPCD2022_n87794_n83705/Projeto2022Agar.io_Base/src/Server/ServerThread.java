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
		try{
			ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("ServerThread "+this.getId()+" lançada");
//		while(while ((message = (Message)objIn.readObject()) != null) {
//						mudar o atributo next na instancia do player no jogo
//						
//						objOut.writeObject (message); //resposta ao cliente
//		} //fim da comunicação
		socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

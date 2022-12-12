package Client;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import Server.Message;
import game.Game;

import java.awt.event.KeyEvent;

public class ClientThread extends Thread{

    private Socket socket;
    private ObjectInputStream objIn;
	private PrintWriter out;
    private boolean alternateKeys;
	private GameGuiMain gui;

    public ClientThread(Socket socket, boolean alternateKeys){
        super();
        this.socket=socket;
        this.alternateKeys=alternateKeys;
    }

    public void run(){
        try {
			startGame();
            openComs();
            communication();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   
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
		while(true){
			Message msg= (Message) objIn.readObject();
			if(msg.getEnd()==false){
				gui.getGame().updateBoard(msg.getBoard());
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
		//stop();
	}

	

	public void sendComs(GameGuiMain gui){
//mensagem com informacao necessaria p move (direcao)
		System.out.println(gui.getBoardGui().getLastPressedDirection());
		out.println(gui.getBoardGui().getLastPressedDirection());
		System.out.println("Client sent move info!");
//clean da direcao
		gui.getBoardGui().clearLastPressedDirection();
	}


}

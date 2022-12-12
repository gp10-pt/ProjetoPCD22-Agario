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
    private Game game;
    private boolean alternateKeys;

    public ClientThread(Socket socket, boolean alternateKeys){
        super();
        this.socket=socket;
        this.alternateKeys=alternateKeys;
    }

    public void run(){
        try {
            this.objIn = new ObjectInputStream(socket.getInputStream());
            this.out= new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            communication();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }   
    }

	//processo de rececao da info do servidor e envio de mensagem com informacao necessaria p move (direcao)
    public void communication() throws IOException, ClassNotFoundException{
		game=new Game();
		GameGuiMain gui= new GameGuiMain(game,alternateKeys);
		gui.init();
		KeyEvent e = new KeyEvent(gui.getBoardGui(), 1, 20, 1, 10, 'a');
		while(true){
	//client recebe a game info do servidor
			Message msg= (Message) objIn.readObject();
			if(msg.getEnd()==false){
                System.out.println("\nClient waiting for game info");
				game.updateBoard(msg.getBoard());
				game.notifyChange();
                gui.buildGui();
				gui.getBoardGui().keyPressed(e);
	//mensagem com informacao necessaria p move (direcao)
				out.println(gui.getBoardGui().getLastPressedDirection());
				System.out.println("Client sent move info!");
	//clean da direcao
				gui.getBoardGui().clearLastPressedDirection();
			}
			else{
				break;
			}
		}
		objIn.close();
		out.close();
        //stop();
	}

}

package game;

@SuppressWarnings("serial")
public class Dummy extends Player {
	
	private boolean human;
	
	//Player para preenchimento de board p envio a clientes
	public Dummy(byte power, boolean human) {
		super(power);
		this.human = human;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isHumanPlayer() {
		// TODO Auto-generated method stub
		return human;
	}

}

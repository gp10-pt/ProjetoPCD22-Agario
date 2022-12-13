package game;

@SuppressWarnings("serial")
public class Dummy extends Player {
	private boolean human;

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

package org.pumatech.ctf;

public enum ScoringPlay {

	MOVE(1), MOVE_ON_OPPONENT_SIDE(2), CAPTURE(50), TAG(20), CARRY(5);
	
	private int value;
	
	private ScoringPlay(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}

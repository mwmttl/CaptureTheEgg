package org.pumatech.teams.sample;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.grid.Location;

public class BeelinePlayer extends AbstractPlayer {

	public BeelinePlayer(Location startLocation) {
		super(startLocation);
	}

	public Location getMoveLocation() {
		if (hasFlag())
			return getTeam().getFlag().getLocation();
		return getTeam().getOpposingTeam().getFlag().getLocation();
	}

}

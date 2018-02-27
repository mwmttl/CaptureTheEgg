package org.pumatech.teams.sample;

import java.util.List;

import org.pumatech.ctf.AbstractPlayer;

import info.gridworld.grid.Location;

public class RandomPlayer extends AbstractPlayer {

	public RandomPlayer(Location startLocation) {
		super(startLocation);
	}

	public Location getMoveLocation() {
		List<Location> possibleMoveLocations = getGrid().getEmptyAdjacentLocations(getLocation()); 
		if (possibleMoveLocations.size() == 0) return null;
		return possibleMoveLocations.get((int) (Math.random() * possibleMoveLocations.size()));
	}

}

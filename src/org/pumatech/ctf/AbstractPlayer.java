package org.pumatech.ctf;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public abstract class AbstractPlayer extends Actor {

	private Team team;
	private boolean hasFlag;
	private Location startLocation;
	private int tagCoolDown;
	private int steps;
	
	public AbstractPlayer(Location startLocation) {
		this.startLocation = startLocation;
	}
	
	public final void act() {
		steps++;
		if (getTeam().hasWon() || getTeam().getOpposingTeam().hasWon())  {
			if (getTeam().hasWon()) {
				if (hasFlag)
					setColor(Color.MAGENTA);
				else
					setColor(Color.YELLOW);
			}
			return;
		}
		
		if (steps >= Team.MAX_GAME_LENGTH) {
			if (getTeam().getScore() > getTeam().getOpposingTeam().getScore()) {
				getTeam().setHasWon(true);
			} else if (getTeam().getScore() == getTeam().getOpposingTeam().getScore()) {
				getTeam().setHasWon(true);
			}
		} else if (tagCoolDown > 0) {
			setColor(Color.BLACK);
			tagCoolDown--;
		} else {
			if (hasFlag) {
				setColor(Color.YELLOW);
				if (getTeam().onSide(getLocation())) {
					getTeam().setHasWon(true);
				}
			} else {
				setColor(team.getColor());
			}
			processNeighbors();
			makeMove(getMoveLocation());
		}
	}
	
	private void processNeighbors() {
		List<Location> neighborLocations = getGrid().getOccupiedAdjacentLocations(getLocation());
		for (int i = neighborLocations.size() - 1; i >= 0; i--) {
			Actor neighbor = getGrid().get(neighborLocations.get(i));
			if (!(neighbor instanceof AbstractPlayer) || ((AbstractPlayer) neighbor).getTeam().equals(team)) {
				neighborLocations.remove(i);
				if (neighbor instanceof Flag && !((Flag) neighbor).getTeam().equals(team)) {
					hasFlag = true;
					getTeam().getOpposingTeam().getFlag().pickUp(this);
					getTeam().scorePlay(ScoringPlay.CARRY);
				}
			}
		}
		if (getTeam().onSide(getLocation())) {
			Collections.shuffle(neighborLocations);
			for (Location neighborLocation : neighborLocations) {
				Actor neighbor = getGrid().get(neighborLocation);
				if (((AbstractPlayer) neighbor).hasFlag() || Math.random() < (1d / neighborLocations.size())) {
					((AbstractPlayer) neighbor).tag();
					getTeam().scorePlay(ScoringPlay.TAG);
				}
			}
		}
	}
	
	private void makeMove(Location loc) {
		if (loc == null) loc=getLocation();
		if (getTeam().onSide(getLocation()) && getGrid().get(getTeam().getFlag().getLocation()) instanceof Flag && getTeam().nearFlag(getLocation())) {
			int dir = getLocation().getDirectionToward(getTeam().getFlag().getLocation()) + Location.HALF_CIRCLE;
			loc = getLocation().getAdjacentLocation(dir);
			while (getGrid().get(loc) != null) {
				System.out.println(loc + "is occupied");
				loc = loc.getAdjacentLocation(dir);
			}
		}
		else {
			loc = getLocation().getAdjacentLocation(getLocation().getDirectionToward(loc));
		}
		if (getGrid().isValid(loc) && getGrid().get(loc) == null) {
			moveTo(loc);
			if (getTeam().onSide(getLocation()))
				getTeam().scorePlay(ScoringPlay.MOVE);
			else
				getTeam().scorePlay(ScoringPlay.MOVE_ON_OPPONENT_SIDE);
		}
	}
	
	public abstract Location getMoveLocation();
	
	public final void announceScores() {
		if (getTeam().hasWon() || getTeam().getOpposingTeam().hasWon()) return;
		String scoreAnnouncement = "s: " + steps + "\t0: ";
		if (getTeam().getSide() == 0)
			scoreAnnouncement += getTeam().getScore();
		else
			scoreAnnouncement += getTeam().getOpposingTeam().getScore();
		scoreAnnouncement += "\t1: ";
		if (getTeam().getSide() == 1)
			scoreAnnouncement += getTeam().getScore();
		else
			scoreAnnouncement += getTeam().getOpposingTeam().getScore();
		System.out.println(scoreAnnouncement);
	}
	
	private void tag() {
		Location oldLoc = getLocation();
		Location nextLoc;
		do {
			nextLoc = getTeam().adjustForSide(new Location((int) (Math.random() * getGrid().getNumRows()), 0), getGrid());
		} while (getGrid().get(nextLoc) != null);
		moveTo(nextLoc);
		tagCoolDown = 10;
		
		if (hasFlag) {
			getTeam().getOpposingTeam().getFlag().putSelfInGrid(getGrid(), oldLoc);
			hasFlag = false;
		}
	}
	
	protected final void putSelfInGridProtected(Grid<Actor> grid, Location loc) {
		if (getGrid() != null)
			super.removeSelfFromGrid();
		hasFlag = false;
		steps = 0;
		tagCoolDown = 0;
		setColor(getTeam().getColor());
		super.putSelfInGrid(grid, loc);
	}
	
	public final void removeSelfFromGrid() {
		System.err.println("Someone has cheated and tried to remove a player from the grid");
	}
	
	protected final void setTeam(Team team) {
		this.team = team;
		setColor(team.getColor());
	}
	
	protected final void setStartLocation(Location startLocation) {
		this.startLocation = startLocation;
	}
	
	public final boolean hasFlag() {
		return hasFlag;
	}
	
	protected final Location getStartLocation() {
		return startLocation;
	}
	
	public final Team getTeam() {
		return team;
	}

	public final int getSteps() {
		return steps;
	}	
}

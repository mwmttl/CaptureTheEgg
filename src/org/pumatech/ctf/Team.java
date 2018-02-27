package org.pumatech.ctf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class Team {
	public static final int MAX_SIZE = 8;
	public static final int MAX_GAME_LENGTH = 1000;
	public static final Location DEFAULT_FLAG_LOCATION = new Location(24, 10);

	private List<AbstractPlayer> players;
	private Grid<Actor> grid;
	private Flag flag;
	private Team opposingTeam;
	private Color color;
	private String name;
	private volatile boolean hasWon;
	private int score;
	private int side;
	
	public Team(String name, Color color) {
		players = new ArrayList<AbstractPlayer>();
		this.name = name;
		this.color = color;
	}
	
	public final void addPlayer(AbstractPlayer player) {
		if (players.size() < MAX_SIZE) {
			players.add(player);
			player.setTeam(this);
		} else {
			throw new RuntimeException("Team is full - MAX_SIZE = " + MAX_SIZE);
		}
	}
	
	protected final void addTeamToGrid(Grid<Actor> grid, int side) {
		this.grid = grid;
		this.side = side;
		score = 0;
		hasWon = false;
		flag = new Flag(this);
		flag.putSelfInGrid(grid, adjustForSide(DEFAULT_FLAG_LOCATION, grid));
		
		for (AbstractPlayer player : players) {
			double dist = Math.sqrt(Math.pow(player.getStartLocation().getRow() - DEFAULT_FLAG_LOCATION.getRow(), 2) + Math.pow(player.getStartLocation().getCol() - DEFAULT_FLAG_LOCATION.getCol(), 2));
			if (player.getStartLocation().getCol() >= grid.getNumCols() / 2 || player.getStartLocation().getCol() < 0 || dist < 10.0) {
				System.err.println("Someone has cheated and given their players an invalid start location");
				player.setStartLocation(new Location((int)(Math.random()*grid.getNumRows()), 0));
			}
			player.putSelfInGridProtected(grid, adjustForSide(player.getStartLocation(), grid));
		}
	}
	
	protected final void scorePlay(ScoringPlay play) {
		score += play.getValue();
	}
	
	public final Location adjustForSide(Location loc, Grid<Actor> grid) {
		return new Location(loc.getRow(), (side == 0 ? loc.getCol() : grid.getNumCols() - 1 - loc.getCol()));
	}
	
	protected final void setOpposingTeam(Team opposingTeam) {
		this.opposingTeam = opposingTeam;
	}
	
	protected final void setHasWon(boolean hasWon) {
		if (!opposingTeam.hasWon()) {
			for (AbstractPlayer player : players) {
				if (player.hasFlag() && onSide(player.getLocation()) || player.getSteps() >= MAX_GAME_LENGTH && score >= opposingTeam.getScore()) {
					JOptionPane.showMessageDialog(null, "Team " + name + " has won\n" + this.getName() + ": " + this.getScore() + "\n" + this.getOpposingTeam().getName() + ": " + this.getOpposingTeam().getScore());
					this.hasWon = true;
					return;
				}
			}
		}
		if (hasWon)
			System.err.println("Someone has cheated and pretended to win without actually winning");
	}
	
	public final boolean onSide(Location loc) {
		return side == 0 && loc.getCol() < grid.getNumCols() / 2 || side == 1 && loc.getCol() >= grid.getNumCols() / 2;
	}
	
	public final boolean nearFlag(Location loc) {
		return Math.sqrt(Math.pow(loc.getRow() - flag.getLocation().getRow(), 2) + Math.pow(loc.getCol() - flag.getLocation().getCol(), 2)) <= 4;
	}
	
	public final List<AbstractPlayer> getPlayers() {
		return players;
	}
	
	public final Flag getFlag() {
		return flag;
	}
	
	public final Team getOpposingTeam() {
		return opposingTeam;
	}
	
	public final Color getColor() {
		return color;
	}
	
	public final String getName() {
		return name;
	}
	
	public final int getScore() {
		return score;
	}
	
	public final int getSide() {
		return side;
	}
	
	public final boolean hasWon() {
		return hasWon;
	}
	
	public final boolean equals(Team team) {
		return team.getSide() == side && team.getColor().equals(color) && team.getClass() == getClass();
	}
}

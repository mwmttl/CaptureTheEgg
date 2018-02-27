package org.pumatech.ctf;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JOptionPane;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Rock;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class Game {
	private static final int NUM_ROCKS = 75;

	private Game gameA, gameB;
	private Team teamA, teamB;

	public Game(List<Team> teams) {
		if (teams.size() > 2) {
			gameA = new Game(teams.subList(0, teams.size() / 2));
			gameB = new Game(teams.subList(teams.size() / 2, teams.size()));
		} else {
			teamA = teams.get(0);
			if (teams.size() == 2)
				teamB = teams.get(1);
		}
	}

	public Team getWinner(Bracket bracket, ActorWorld world) {
		
		
		if (gameA != null)
			teamA = gameA.getWinner(bracket, world);
		if (gameB != null)
			teamB = gameB.getWinner(bracket, world);
		if (teamA == null)
			return teamB;
		if (teamB == null)
			return teamA;
		
		BoundedGrid<Actor> grid = new BoundedGrid<Actor>(50, 100);

		double randomNumber = Math.random();
		teamA.addTeamToGrid(grid, randomNumber < .5 ? 0 : 1);
		teamB.addTeamToGrid(grid, randomNumber < .5 ? 1 : 0);
		teamA.setOpposingTeam(teamB);
		teamB.setOpposingTeam(teamA);
		
		for (int i = 0; i < NUM_ROCKS; i++) {
			Location rockClumpLocation;
			do {
				rockClumpLocation = new Location((int) (Math.random() * (grid.getNumRows())), (int) (Math.random() * ((grid.getNumCols()) - 6)) + 3);
			} while (nearFlag(grid, rockClumpLocation) || grid.get(rockClumpLocation) != null);
			
			if (!nearPlayer(teamA, rockClumpLocation) && !nearPlayer(teamB, rockClumpLocation)) new Rock().putSelfInGrid(grid, rockClumpLocation);
			for (int j = 0; j < 8; j++) {
				int randomDirection = (int) (Math.random() * 8) * Location.HALF_RIGHT;
				Location possibleRockLocation = rockClumpLocation.getAdjacentLocation(randomDirection);
				if (grid.isValid(possibleRockLocation) && !nearFlag(grid, possibleRockLocation) && grid.get(possibleRockLocation) == null)
					if (!nearPlayer(teamA, possibleRockLocation) && !nearPlayer(teamB, possibleRockLocation))new Rock().putSelfInGrid(grid, possibleRockLocation);
			}
		}
		
		world.setGrid(grid);
		
		if (teamA.getSide() == 0) {
			JOptionPane.showMessageDialog(null, teamA.getName() + " vs. " + teamB.getName());
		}
		else {
			JOptionPane.showMessageDialog(null, teamB.getName() + " vs. " + teamA.getName());
		}
		System.out.println("Playing game");
		
		while (!teamA.hasWon() && !teamB.hasWon()) {}

		bracket.repaint();
		
		return teamA.hasWon() ? teamA : teamB;
	}

	private boolean nearPlayer (Team t, Location loc) {
		for (AbstractPlayer p: t.getPlayers()) {
			if (distance(p.getLocation(), loc) < 3) return true;
		}
		return false;
	}
	
	private double distance (Location loc1, Location loc2) {
		return Math.sqrt(Math.pow(loc1.getRow() - loc2.getRow(), 2) + Math.pow(loc1.getCol() - loc2.getCol(), 2));
	}
	public void draw(Graphics g) {
		draw(g, 25, 300, 150, 60);
	}

	public void draw(Graphics g, int x, int y, int width, int height) {
		g.setColor(Color.WHITE);
		g.fillRect(x, y - height / 2, width, height);
		g.setColor(Color.DARK_GRAY);
		g.drawRect(x, y - height / 2, width, height);
		g.drawLine(x, y, x + width, y);

		if (teamA != null)
			g.drawString(teamA.getName(), x + 3, y + height / 2 - 4);
		if (teamB != null)
			g.drawString(teamB.getName(), x + 3, y - 4);

		if (gameA == null)
			return;

		gameA.draw(g, x + width + 50, y - (getWidth() / 2) * height, width, height);
		gameB.draw(g, x + width + 50, y + (getWidth() / 2) * height, width, height);
	}

	public int getWidth() {
		if (gameA == null)
			return 1;
		return gameA.getWidth() + gameB.getWidth();
	}

	private static boolean nearFlag(Grid<Actor> grid, Location loc) {
		for (int i = loc.getCol() - 5; i <= loc.getCol() + 5; i++) {
			for (int j = loc.getRow() - 5; j <= loc.getRow() + 5; j++) {
				Location newloc = new Location(j, i);
				if (grid.isValid(newloc) && (grid.get(newloc) instanceof Flag)) {
					return true;
				}
			}
		}
		return false;
	}
}

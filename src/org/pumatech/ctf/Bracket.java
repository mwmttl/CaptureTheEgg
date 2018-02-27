package org.pumatech.ctf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JPanel;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;

public class Bracket extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Game finalGame;
	private Team winner;
	
	public Bracket(List<Team> teams) {
		finalGame = new Game(teams);
	}
	
	public Team getWinner() {
		if (winner == null) {
			BoundedGrid<Actor> grid = new BoundedGrid<Actor>(50, 100);
			ActorWorld world = new ActorWorld(grid);
			world.show();
			winner = finalGame.getWinner(this, world);
		}
		return winner;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(800, 1000);
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(800, 1000);
	}
	
	public Dimension getMaximumSize() {
		return new Dimension(800, 1000);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(new Font("Consolas", Font.PLAIN, 14));
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
		
		finalGame.draw(g);
		if (winner != null) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Consolas", Font.PLAIN, 50));
			FontMetrics fm = g.getFontMetrics();
			g.drawString(winner.getName() + " has won!", (800 - fm.stringWidth(winner.getName() + " has won!")) / 2, fm.getHeight());
		}
	}	
}

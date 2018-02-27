package org.pumatech.ctf;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.pumatech.teams.sample.SampleTeam;

public class TournamentRunner {

	public static void main(String[] args) {
		List<Team> teams = new ArrayList<Team>();
		teams.add(new SampleTeam(Color.RED));
		teams.add(new SampleTeam(Color.BLUE));
		teams.add(new SampleTeam(Color.YELLOW));
		teams.add(new SampleTeam(Color.WHITE));
		
		Bracket bracket = new Bracket(teams);
		JFrame bracketViewer = new JFrame("Capture The Flag 2017 Bracket");
		bracketViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bracketViewer.setResizable(false);
		bracketViewer.add(bracket);
		bracketViewer.pack();
		bracketViewer.setLocationRelativeTo(null);
		bracketViewer.setVisible(true);
		
		bracket.getWinner();
	}
	
}

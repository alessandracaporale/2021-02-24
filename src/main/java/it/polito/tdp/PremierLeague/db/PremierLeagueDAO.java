package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		//List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(!idMap.containsKey(res.getInt("PlayerID"))) {
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(res.getInt("PlayerID"), player);
				}
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				
				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID "
				+ "ORDER BY m.MatchID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				result.add(match);
			}
			conn.close();
			return result;
			
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Player> listAllPlayersByMatch(Match match, Map<Integer, Player> idMap) {
		String sql = "SELECT PlayerID "
				+ "FROM actions "
				+ "WHERE MatchID = ?";
		List<Player> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, match.getMatchID());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
					Player player = idMap.get(rs.getInt("PlayerID"));
					result.add(player);
			}
			conn.close();
			return result;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Arco> getArchi(Map<Integer, Player> idMap, Match match) {
		String sql = "SELECT a1.PlayerID AS id1, a2.PlayerID AS id2, a1.TotalSuccessfulPassesAll AS sp1, a1.Assists AS a1, a1.TimePlayed AS tp1, a2.TotalSuccessfulPassesAll AS sp2, a2.Assists AS a2, a2.TimePlayed AS tp2  "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.MatchID = a2.MatchID AND a1.MatchID = ? "
				+ "AND a1.TeamID <> a2.TeamID ";
		List<Arco> list = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, match.getMatchID());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Player p1 = idMap.get(rs.getInt("id1"));
				Player p2 = idMap.get(rs.getInt("id2"));
				double sp1 = (double) rs.getInt("sp1");
				double sp2 = (double) rs.getInt("sp2");
				double a1 = (double) rs.getInt("a1");
				double a2 = (double) rs.getInt("a2");
				double tp1 = (double) rs.getInt("tp1");
				double tp2 = (double) rs.getInt("tp2");
				double e1 = (sp1+a1)/tp1;
				double e2 = (sp2+a2)/tp2;
				double peso = Math.abs(e1-e2);
				if(p1!=null && p2!=null) {
					if (e1 > e2) {
						Arco a = new Arco (p1, p2, peso);
						list.add(a);
					}
					else {
						Arco a = new Arco (p2, p1, peso);
						list.add(a);
					}
				}
				
			}
			conn.close();
			return list;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setEfficienze (Player p, Match match) {
		String sql = "SELECT PlayerID, totalSuccessfulPassesAll as sp, assists as a, timePlayed as tp "
				+ "FROM actions "
				+ "WHERE MatchID = ?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, match.getMatchID());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				double e = (double) (rs.getInt("sp")+rs.getInt("a"))/rs.getInt("tp");
					if(p.getPlayerID() == rs.getInt("PlayerID")) {
						p.aggiungiEfficienza(match, e);
					}
			}
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

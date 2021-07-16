package it.polito.tdp.PremierLeague.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import it.polito.tdp.PremierLeague.db.*;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> graph;
	private Map<Integer, Player> idMap;
	
	public Model() {
		dao = new PremierLeagueDAO();
		idMap = new HashMap<>();
		dao.listAllPlayers(idMap);
	}
	
	public List<Player> listAllPlayersByMatch(Match match, Map<Integer, Player> idMap) {
		return dao.listAllPlayersByMatch(match, idMap);
	}
	
	public String creaGrafo(Match match) {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		String s = "";
		
		//vertici
		Graphs.addAllVertices(graph, dao.listAllPlayersByMatch(match, idMap));
		
		//archi
		/*
		for (Player p1 : dao.listAllPlayersByMatch(match, idMap)) {
			dao.setEfficienze(p1, match);
			for (Player p2 : dao.getAvversari(idMap, match, p1)) {
				dao.setEfficienze(p2, match);
				if(!p1.equals(p2)) {
					if(p1.getEfficienza(match) > p2.getEfficienza(match)) {
						double peso = p1.getEfficienza(match)-p2.getEfficienza(match);
						//DefaultWeightedEdge e = graph.addEdge(p1, p2);
						//graph.setEdgeWeight(e, peso);
						Graphs.addEdge(graph, p1, p2, peso);
					}
				}
				
			}
		}*/
		for (Arco a : dao.getArchi(idMap, match)) {
			if(this.graph.containsVertex(a.getP1()) && this.graph.containsVertex(a.getP2())) {
				DefaultWeightedEdge e = this.graph.getEdge(a.getP1(), a.getP2());
				if (e==null) {
					Graphs.addEdge(graph, a.getP1(), a.getP2(), a.getPeso());
				}
			}
		}
		s= "Grafo:\n#vertici: "+graph.vertexSet().size()+"\n#archi: "+graph.edgeSet().size();
		return s;
	}
	
	public String getGiocatoreMigliore (Match m) {
		if(this.graph==null) {
			return "Creare il grafo!";
		}
		Player best = null;
		double max = 0.0;
		
		for (Player p : this.graph.vertexSet()) {
			double val = 0.0;
			for (DefaultWeightedEdge e : this.graph.outgoingEdgesOf(p)) {
				val += this.graph.getEdgeWeight(e);
			}
			for (DefaultWeightedEdge e : this.graph.incomingEdgesOf(p)) {
				val -= this.graph.getEdgeWeight(e);
			}
			if (val > max) {
				max = val;
				best = p;
			}
		}
		String s = "Il giocatore migliore è "+best.getName()+" ("+best.getPlayerID()+") con efficienza complessiva pari a "+max;
		return s;
	}
	
	public List<Match> listAllMatches() {
		return dao.listAllMatches();
	}
}

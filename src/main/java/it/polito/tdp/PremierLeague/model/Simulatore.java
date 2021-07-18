package it.polito.tdp.PremierLeague.model;

import java.util.*;
import it.polito.tdp.PremierLeague.db.*;
import it.polito.tdp.PremierLeague.model.Evento.EventType;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Simulatore {

	//eventi
	private PriorityQueue<Evento> queue;
	
	//parametri di simulazione --> costanti
	int N; //numero di azioni (inserito)
	int giocatoriIniziali = 11;
	Team t1;
	Team t2;
	PremierLeagueDAO dao;
	Model model;
	
	//stato del sistema
	private Graph<Player, DefaultWeightedEdge> graph;
	double probGoal = 0.5;
	double probEspulsione = 0.3;
	double probEspTeamMigliore = 0.6;
	double probInfortunio = 0.2;
	double prob2 = 0.5;
	double prob3 = 0.5;
	int passo;
	
	//misure in uscita
	Map<Team, Integer> mapGoal;
	Map<Team, Integer> mapEspulsi;
	
	public void init(int N, Match match, Graph<Player, DefaultWeightedEdge> graph) {
		this.graph = graph;
		this.N = N;
		this.mapGoal = new HashMap<>();
		this.mapEspulsi = new HashMap<>();
		dao = new PremierLeagueDAO();
		model = new Model();
		passo = 1;
		
		for(Team t : dao.getTeamByMatch(match)) {
			mapGoal.put(t, 0);
			mapEspulsi.put(t, 0);
			t1 = t;
			if(!t.equals(t1)) {
				t2 = t;
			}
		}
		this.queue = new PriorityQueue<Evento>();
		double prob = Math.random();
		if(prob<probGoal) {
			Evento evento = new Evento(passo, EventType.GOAL);
			queue.add(evento);
		}
		else if (prob<0.8) {
			Evento evento = new Evento(passo, EventType.ESPULSIONE);
			queue.add(evento);
		}
		else {
			Evento evento = new Evento(passo, EventType.INFORTUNIO);
			queue.add(evento);
		}
		
	}
	
	public int run(Match match) {
		Evento e;
		while ((e = queue.poll()) != null && passo<N) {
			switch(e.getType()) {
			case GOAL:
				if(mapEspulsi.get(t1) == mapEspulsi.get(t2)) {
					//Team t = dao.getTeamByPlayer(this.model.getGiocatoreMigliore(match));
				}
			break;
			case ESPULSIONE:
			break;
			case INFORTUNIO:
			break;
			}
		}
		return -1;
	}
}

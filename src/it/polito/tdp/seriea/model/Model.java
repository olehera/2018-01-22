package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO adao;
	private Team teamSelezionato;
	private Map<Season, Integer> punteggi;
	private Map<Integer, Season> stagioniIdMap;
	private Map<String, Team> squadreIdMap;
	private List<Team> squadre;
	private List<Season> stagioni;
	
	private Graph<Season, DefaultWeightedEdge> grafo;
	private int max;
	private List<Season> stagioniConsecutive;
	private List<Season> percorsoBest;
	
	public Model() {
		adao = new SerieADAO();
		squadreIdMap = new HashMap<>();
		stagioniIdMap = new HashMap<>();
		squadre = adao.listTeams(squadreIdMap);
		stagioni = adao.listAllSeasons(stagioniIdMap);
	}
	
	public List<Team> getTeams() {
		return squadre;
	}
	
	public List<Season> getSeasons() {
		return stagioni;
	}

	public Team getTeamSelezionato() {
		return teamSelezionato;
	}

	public void setTeamSelezionato(Team teamSelezionato) {
		this.teamSelezionato = teamSelezionato;
	}
	
	public Map<Season, Integer> punteggi(Team squadra) {
		this.punteggi = new HashMap<Season, Integer>();
		setTeamSelezionato(squadra);
		
		List<Match> partite = adao.listMatchesForTeam(squadra, stagioniIdMap, squadreIdMap);
		
		for (Match m: partite) {
			Season stagione = m.getSeason();
			
			int punti = 0;
			
			if ( m.getFtr().equals("D") ) {
				punti = 1;
			} else if ( m.getHomeTeam().equals(squadra) && m.getFtr().equals("H") ||
					    m.getAwayTeam().equals(squadra) && m.getFtr().equals("A") ) {
				punti = 3;
			}
			
			Integer attuale = punteggi.get(stagione);
			if ( attuale == null )
				attuale = 0;
			
			punteggi.put(stagione, attuale+punti);
				
		}
		
		return punteggi;
	}
	
	public Map<Season, Integer> getPunteggi() {
		return punteggi;
	}
	
	public Season calcolaAnnataDOro() {
		
		Season migliore = null;
		max = 0;
		
		this.grafo = new SimpleDirectedWeightedGraph<Season, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, punteggi.keySet());
		
		for ( Season s1: stagioni )
			for ( Season s2: stagioni )
				if ( !s1.equals(s2) ) {
					int punti1 = punteggi.get(s1);
					int punti2 = punteggi.get(s2);
					
					if ( punti1 > punti2 )
						Graphs.addEdge(grafo, s2, s1, punti1-punti2);
					else 
						Graphs.addEdge(grafo, s1, s2, punti2-punti1);
				}
		
		for ( Season s: grafo.vertexSet() ) {
			int valore = pesoStagione(s);
			
			if ( valore > max ) {
				max = valore;
				migliore = s;
			}
			
		}
		
		return migliore;
	}
	
	public int diffPesi() {
		return max;
	}
	
	public int pesoStagione(Season s) {
		int somma = 0;
		
		for ( DefaultWeightedEdge e: grafo.incomingEdgesOf(s) )
			somma += (int) grafo.getEdgeWeight(e);
		
		for ( DefaultWeightedEdge e: grafo.outgoingEdgesOf(s) )
			somma -= (int) grafo.getEdgeWeight(e);
	
		return somma;
	}
	
	public List<Season> camminoVirtuoso() {
		stagioniConsecutive = new ArrayList<Season>(punteggi.keySet());
		Collections.sort(stagioniConsecutive);
		
		List<Season> parziale = new ArrayList<Season>();
		this.percorsoBest = new ArrayList<>();
		
		for (Season s: grafo.vertexSet()) {     // itera al livello 0
			parziale.add(s);
			cerca(1, parziale);
			parziale.remove(0);
		}
		
		return percorsoBest;
	}
	
	/*
	 *                                                     RICORSIONE
	 * Soluzione parziale : lista di season 
	 * Livello : lunghezza della lista 
	 * Casi terminali : non trova altri vertici da aggiungere -> verifica se il cammino ha lunghezza massima tra quelli visti finora
	 * Generazione delle soluzioni : vertici connessi all'ultimo vertice del percorso (con arco orientato nel verso giusto),
	 *                               non ancora parte del percorso, relativi a stagioni consecutive.
	 */
	private void cerca(int livello, List<Season> parziale) {
		boolean trovato = false;
		
		Season ultimo = parziale.get(livello-1);
		
		for ( Season prossimo: Graphs.successorListOf(grafo, ultimo) )
			if ( !parziale.contains(prossimo) ) 
				if ( stagioniConsecutive.indexOf(ultimo)+1 == stagioniConsecutive.lastIndexOf(prossimo) ) {
					trovato = true;
					
					parziale.add(prossimo);
					
					cerca(livello+1, parziale);
					
					parziale.remove(livello);
				}
					
		if ( !trovato )
			if (parziale.size() > percorsoBest.size())
				percorsoBest = new ArrayList<Season>(parziale);  // clona il best
		
	}

}
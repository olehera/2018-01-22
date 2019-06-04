package it.polito.tdp.seriea.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO adao;
	private Team teamSelezionato;
	private Map<Season, Integer> punteggi;
	private Map<Integer, Season> stagioniIdMap;
	private Map<String, Team> squadreIdMap;
	private List<Team> squadre;
	private List<Season> stagioni;
	
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
			if ( attuale==null )
				attuale = 0;
			
			punteggi.put(stagione, attuale+punti);
				
		}
		
		return punteggi;
	}
	
	public void creaGrafo() {
		
		
		
	}

}
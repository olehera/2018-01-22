package it.polito.tdp.seriea.db;

import java.util.HashMap;
import java.util.List;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class TestSerieADAO {

	public static void main(String[] args) {
		SerieADAO dao = new SerieADAO();

		List<Season> seasons = dao.listAllSeasons(new HashMap<>());
		System.out.println(seasons);
		System.out.println("Seasons # rows: " + seasons.size());

		List<Team> teams = dao.listTeams(new HashMap<>());
		System.out.println(teams);
		System.out.println("Teams # rows: " + teams.size());
	}

}

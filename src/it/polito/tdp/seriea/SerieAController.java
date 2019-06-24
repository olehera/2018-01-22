package it.polito.tdp.seriea;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import it.polito.tdp.seriea.model.Model;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class SerieAController {
	
	private Model model;
	
	public void setModel(Model model) {
		this.model = model;
		boxSquadra.getItems().clear();
		boxSquadra.getItems().addAll(model.getTeams());
	}

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxSquadra"
    private ChoiceBox<Team> boxSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="btnSelezionaSquadra"
    private Button btnSelezionaSquadra; // Value injected by FXMLLoader

    @FXML // fx:id="btnTrovaAnnataOro"
    private Button btnTrovaAnnataOro; // Value injected by FXMLLoader

    @FXML // fx:id="btnTrovaCamminoVirtuoso"
    private Button btnTrovaCamminoVirtuoso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doSelezionaSquadra(ActionEvent event) {
    	Team scelto = boxSquadra.getValue();
    	
    	if (scelto == null) {
    		txtResult.setText("Devi selezionare una squadra");
    		return ;
    	}
    	
    	Map<Season, Integer> punteggi = model.punteggi(scelto);
    	List<Season> seasons = new LinkedList<>(punteggi.keySet());
    	Collections.sort(seasons);
    	
    	txtResult.clear();
    	for (Season s: seasons) 
    		txtResult.appendText(s.getSeason()+" -> "+punteggi.get(s)+"\n");
    }

    @FXML
    void doTrovaAnnataOro(ActionEvent event) {
    	
    	Season annata = model.calcolaAnnataDOro();
    	int diff = model.diffPesi();
    	
    	txtResult.appendText("\nAnnata d'Oro: "+annata.getSeason()+"\nDifferenza dei pesi: "+diff);

    }

    @FXML
    void doTrovaCamminoVirtuoso(ActionEvent event) {
    	Map<Season, Integer> p = model.getPunteggi();
    	
    	txtResult.appendText("\n\nCammino Virtuoso:\n");
    	for (Season s: model.camminoVirtuoso())
    		txtResult.appendText(s.getSeason()+" -> "+p.get(s)+"\n");
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxSquadra != null : "fx:id=\"boxSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnSelezionaSquadra != null : "fx:id=\"btnSelezionaSquadra\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaAnnataOro != null : "fx:id=\"btnTrovaAnnataOro\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert btnTrovaCamminoVirtuoso != null : "fx:id=\"btnTrovaCamminoVirtuoso\" was not injected: check your FXML file 'SerieA.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'SerieA.fxml'.";
    }
}
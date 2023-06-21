package main;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Hier befindet sich die Logik fuer das Highscore Modal
 */
public class HighscoreController {

	@FXML
	Button highscoreExitButton;
	@FXML
	TextArea textAreaHighscore;

	/**
	 * Instanz um Zugriff auf die Highscore funktionen zu bekommen
	 */
	HighScoreManager manager = new HighScoreManager();

	/**
	 * Hier werden die Highscores aus dem Textfile gespeichert
	 */
	List<Highscore> scores;

	/**
	 * Bei Klick auf den Schliessen Button wird das Modal wieder geschlossen
	 */
	@FXML
	private void handleCloseButtonAction() {
		// Get the stage of the close button
		Stage stage = (Stage) highscoreExitButton.getScene().getWindow();
		stage.close();
	}

	/**
	 * Hier wird alles geladen was direkt nach oeffnen des Modals angezeigt werden
	 * soll bzw. benoetigt wird
	 */
	@FXML
	public void initialize() {
		textAreaHighscore.setEditable(false);
		textAreaHighscore.setFocusTraversable(false);
		load();

	}

	/**
	 * Funktion die Scores in der TextArea anzuzeigen
	 */
	private void load() {
		// Speichern der Scores aus der Textdatei zur Weiterverarbeitung
		scores = manager.getHighScores();

		// Erstellung eines Strings einzelnen Strings mit mehreren Zeilen
		StringBuilder sb = new StringBuilder();
		for (Highscore highscore : scores) {
			sb.append(highscore).append("\n");
		}
		// Der Mehrzeilige String wird in der TextArea angezeigt
		textAreaHighscore.setText(sb.toString());

	}

}

package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasse zum Laden und Speichern des Textfiles in dem sich die 5 hoechsten
 * Scores befinden
 */
public class HighScoreManager {
	/**
	 * Liste vom Datentyp highscore
	 */
	private List<Highscore> highScores;

	/**
	 * Maximale laenge der zu speichernden scores
	 */
	private static final int MAX_HIGH_SCORES = 5;

	/**
	 * Ort wo die scores gespeichert werden
	 */
	private File file = new File("highscores.txt");

	/**
	 * Konstruktor der Klasse Bei jeder neues Instanz wird eine Arraylist erzeugt In
	 * diese werden dann mit der Methode loadHighScores() die Scores geladen
	 */
	public HighScoreManager() {
		highScores = new ArrayList<>();
		loadHighScores();
	}

	/**
	 * 
	 * @return gibt Liste mit den Highscores zurueck
	 */
	public List<Highscore> getHighScores() {
		return highScores;
	}

	/**
	 * 
	 * @param score der Wert der bei Erzeugung eines neues Highscore Objektes
	 *              uebergeben wird
	 * 
	 *              Funktion fuegt der Liste einen Highscore hinzu
	 */
	public void addHighScore(int score) {
		highScores.add(new Highscore(score));
		sortHighScores();
		limitHighScores();
		saveHighScores();
	}

	/**
	 * Die Highscores werden absteigend soriert
	 */
	private void sortHighScores() {
		Collections.sort(highScores, (hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore()));
	}

	/**
	 * Wenn die Liste laenger als die erlaubte groesse ist, wird die Liste mit einer
	 * Sublist von den ersten 5 Werten ueberschrieben
	 */
	private void limitHighScores() {
		if (highScores.size() > MAX_HIGH_SCORES) {
			highScores = highScores.subList(0, MAX_HIGH_SCORES);
		}
	}

	/**
	 * Die Highscore werden in einem Textfile gespeichert.
	 */
	private void saveHighScores() {
		// Wenn noch keine Datei mit entsprechendem Namen existiert, wird eine neue
		// erstellt
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			for (Highscore highScore : highScores) {
				writer.println(highScore.getScore());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Die Highscores werden aus dem Textfile geladen und einer Liste gespeichert.
	 */
	private void loadHighScores() {
		// Wenn noch keine Datei mit entsprechendem Namen existiert, wird eine neue
		// erstellt
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				int score = Integer.parseInt(line);
				highScores.add(new Highscore(score));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
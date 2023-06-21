package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Hier befindet sich bis auf das Speichern der Highscore die gesamte Spiellogik
 */
public class Game2048Controller {
	/**
	 * wenn gameover = true
	 */
	boolean gameover = false;

	/**
	 * wenn counter >= 1 werden keine scores mehr gespeichert
	 */
	int counter = 0;

	/**
	 * Instanz um Zugriff auf die Highscore funktionen zu bekommen
	 */
	private HighScoreManager highScoreManager = new HighScoreManager();

	@FXML
	GridPane gameGrid;
	@FXML
	Button buttonBeenden;
	@FXML
	Button buttonStart;
	@FXML
	Button highscoreButton;
	@FXML
	TextArea consoleTextArea;
	@FXML
	Label label00;
	@FXML
	Label label01;
	@FXML
	Label label02;
	@FXML
	Label label03;
	@FXML
	Label label10;
	@FXML
	Label label11;
	@FXML
	Label label12;
	@FXML
	Label label13;
	@FXML
	Label label20;
	@FXML
	Label label21;
	@FXML
	Label label22;
	@FXML
	Label label23;
	@FXML
	Label label30;
	@FXML
	Label label31;
	@FXML
	Label label32;
	@FXML
	Label label33;
	@FXML
	Label labelScore;

	/**
	 * Farben der jeweiligen Tiles
	 */
	private static final Color COLOR_EMPTY = Color.rgb(204, 192, 179);
	private static final Color COLOR_2 = Color.rgb(238, 228, 218);
	private static final Color COLOR_4 = Color.rgb(237, 224, 200);
	private static final Color COLOR_8 = Color.rgb(242, 177, 121);
	private static final Color COLOR_16 = Color.rgb(245, 149, 99);
	private static final Color COLOR_32 = Color.rgb(246, 124, 95);
	private static final Color COLOR_64 = Color.rgb(246, 94, 59);
	private static final Color COLOR_128 = Color.rgb(237, 207, 114);
	private static final Color COLOR_256 = Color.rgb(237, 204, 97);
	private static final Color COLOR_512 = Color.rgb(237, 200, 80);
	private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
	private static final Color COLOR_2048 = Color.rgb(237, 194, 46);

	private static final int GRID_SIZE = 4;

	private int[][] grid;
	private Label[][] labels;
	private int score;

	/**
	 * Initialisierung des Scores
	 */
	private void initializeScore() {
		score = 0;
		labelScore.setText(String.valueOf(score));
	}

	/**
	 * @param mergedValue
	 * 
	 *                    Aktualisierung der Punktzahl nach erfolgreichem merge
	 */
	private void updateScore(int mergedValue) {
		score += mergedValue * 2;
	}

	Scene scene;

	/**
	 * 
	 * @param scene
	 * 
	 *              Hier wird eine neue Scene erstellt, wo der Fokus auf das
	 *              Gamegrid gelenkt wird, ansonsten werden die Tasteneingaben fuer
	 *              das Spiel nicht erkannt und ist somit nicht spielbar
	 * 
	 */
	public void setScene(Scene scene) {
		this.scene = scene;
		scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
		scene.getRoot().requestFocus();
	}

	/**
	 * Hier werden funtkionen geladen, die sofort nach Start der Anwendung zur
	 * verfuegung stehen, bevor irgendwelche Benutzeraktionen durchgefuehrt wurden
	 */
	@FXML
	public void initialize() {

		buttonBeenden.setOnAction(event -> {
			System.exit(0);
		});
		initializeScore();
	}

	/**
	 * Bei Klick auf den Start Button wird das Grid mit den Labels initialisiert und
	 * eine Spielschleife gestartet, in der alle Tasteneingaben erfasst werden und
	 * auch das Grid aktualisiert wird
	 */
	@FXML
	public void handleButtonStart() {
		// Initialisieren des Gamegrids
		initBoard();

		// Erstellen der Spieleschleife
		AnimationTimer gameLoop = new AnimationTimer() {
			private long lastUpdateTime = 0;
			private final double updateInterval = 1.0 / 60.0; // 60 Aktualisierungen/Sekunde

			// Aktualisieren des Spiels
			@Override
			public void handle(long now) {
				if (now - lastUpdateTime >= updateInterval * 1_000_000_000) {
					// Prufe ob Spiel vorbei
					gameover = checkGameOver();
					scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
					labelScore.setText(String.valueOf(getScore()));
					// Aktualisiere das Grid
					updateGridPane();

					// wenn Spiel vorbei und der counter <= 1 speichere Score
					// und Nachricht ueber Gameover
					if (gameover && counter <= 1) {
						// Hier wird geprueft ob der letzte erzielte Score ein Highscore ist und
						// gespeichert werden soll bzw. ob die es noch weniger als 5 Highscores gibt
						List<Highscore> highScores = highScoreManager.getHighScores();
						if (highScores.size() < 5 || score > highScores.get(highScores.size() - 1).getScore()) {
							highScoreManager.addHighScore(getScore());
						}

						labelScore.setText(String.valueOf(getScore()));
						consoleTextArea.setText("Game Over! Keine weiteren Züge möglich.");
					}

					lastUpdateTime = now;
				}
			}
		};

		// Starten der Spielschleife
		gameLoop.start();

	}

	/**
	 * Bei Klick auf den Highscore Button oeffnet sich ein Modal, welches die 5
	 * hoechsten Scores anzeigt
	 */
	@FXML
	public void handleHighscoreButton() {
		try {
			// Load the modal stage FXML file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Highscore.fxml"));
			BorderPane modalRoot = loader.load();

			// Create the modal stage
			Stage modalStage = new Stage();
			modalStage.initModality(Modality.APPLICATION_MODAL);
			modalStage.initStyle(StageStyle.UTILITY);
			modalStage.setTitle("Modal Stage");
			modalStage.setScene(new Scene(modalRoot));

			modalStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialisierung des Spielgrids
	 */
	private void initBoard() {
		// Bestimmte groesse des grids
		grid = new int[GRID_SIZE][GRID_SIZE];
		// counter fuer die handle funktion wird auf 0 gesetzt
		// wenn counter > 1 werden keine scores mehr gespeichert
		counter = 0;

		// setze score wieder auf 0
		initializeScore();

		// setze Text zurueck
		consoleTextArea.setText("");

//		// 
//		scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

		labels = new Label[GRID_SIZE][GRID_SIZE];
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				Label label = new Label();
				label.setMinSize(100, 100);
				label.setStyle("-fx-background-color: grey; -fx-font-size: 24;");
				label.setStyle("-fx-border-color: black;");
				label.setTextFill(Color.BLACK);
				gameGrid.setStyle("-fx-border-color: blue; -fx-border-width: 5px;");
				gameGrid.setStyle("-fx-grid-lines-visible: true; -fx-grid-line-color: red; fx-grid-line-width: 10px;");
				label.setContentDisplay(ContentDisplay.CENTER);
				label.setAlignment(Pos.CENTER);
				gameGrid.add(label, col, row);
				labels[row][col] = label;
			}
		}
		generateNewTile();
		generateNewTile();
		updateGridPane();
	}

	/**
	 * Funktion zur Generierung zufaelliger Tiles im Verhaeltnis 9 : 1
	 */
	private void generateNewTile() {
		// Liste fuer Leere Zellen, die aus int arrays besteht
		List<int[]> emptyCells = new ArrayList<>();
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				if (grid[row][col] == 0) {
					// Wenn Zelle leer, wird der Liste ein array mit zwei Werten hinzugefügt,
					// welches die Koordinaten fuer Zeile und Spalte enthaelt
					emptyCells.add(new int[] { row, col });
				}
			}
		}

		// Waehle eine zufaellige Zelle aus der Liste
		// randomCell ist wieder ein int array, welches die Koordinaten fuer
		// Zeile und Spalte enthaelt
		int[] randomCell = emptyCells.get((int) (Math.random() * emptyCells.size()));
		// 90% wahrscheinlichkeit fuer 2, 10% fuer 4
		int randomValue = Math.random() < 0.9 ? 2 : 4;

		// Platzieren des Tiles an einer zufaelligen Stelle aus der Liste
		int row = randomCell[0];
		int col = randomCell[1];
		grid[row][col] = randomValue;
	}

	/**
	 * 
	 * @param keyCode
	 * 
	 *                Werden die jeweiligen Pfeiltasten gedrueckt, werden die
	 *                entsprechenden Aktionen ausgefuehrt Wird eine andere Taste als
	 *                die Pfeiltasten gedrueckt erscheint eine entsprechende
	 *                Ausgabe.
	 */
	private void handleKeyPress(KeyCode keyCode) {
		switch (keyCode) {
		case UP:
			moveUp();
			break;
		case DOWN:
			moveDown();
			break;
		case LEFT:
			moveLeft();
			break;
		case RIGHT:
			moveRight();
			break;
		default:
			consoleTextArea.setText("Ungueltige Tasteneingabe!");
			break;
		}
	}

	/**
	 * Bewegen der Tiles nach Oben nach erfolgter Bewegung der Tiles erfolgt
	 * Pruefung ob gemerged werden kann. Wenn man das in einem Schritt machen
	 * wuerde, kann es zu Fehlern kommen
	 */
	private void moveUp() {
		boolean moved = false;

		for (int col = 0; col < GRID_SIZE; col++) {
			int mergeIndex = 0; // Index zur Verfolgung der Position des zu mergenden Tiles

			// Move the tiles up
			for (int row = 1; row < GRID_SIZE; row++) {
				if (grid[row][col] != 0) {
					int currentTile = grid[row][col];
					int mergeTile = grid[mergeIndex][col];

					if (mergeTile == 0) {
						// Zelle leer, bewege Tile
						grid[mergeIndex][col] = currentTile;
						grid[row][col] = 0;
						moved = true;
					} else if (mergeTile == currentTile) {
						// Merge tiles
						grid[mergeIndex][col] *= 2;
						grid[row][col] = 0;
						moved = true;
						// Update score
						updateScore(mergeTile);
						mergeIndex++;
					} else {
						// Zelle nicht leer, nehme naechste Position
						mergeIndex++;
						if (mergeIndex != row) {
							grid[mergeIndex][col] = currentTile;
							grid[row][col] = 0;
							moved = true;
						}
					}
				}
			}
		}

		/*
		 * Wenn eine Bewegung der Tiles stattfand, wird ein neues zufälliges Tiles auf
		 * dem Grid platziert
		 */
		if (moved) {
			generateNewTile();
		}
	}

	/**
	 * Bewegen der Tiles nach Unten nach erfolgter Bewegung der Tiles erfolgt
	 * Pruefung ob gemerged werden kann. Wenn man das in einem Schritt machen
	 * wuerde, kann es zu Fehlern kommen
	 */
	private void moveDown() {
		boolean moved = false;

		for (int col = 0; col < GRID_SIZE; col++) {
			int mergeIndex = GRID_SIZE - 1; // Index zur Verfolgung der Position des zu mergenden Tiles

			// Bewege Tiles nach unten
			for (int row = GRID_SIZE - 2; row >= 0; row--) {
				if (grid[row][col] != 0) {
					int currentTile = grid[row][col];
					int mergeTile = grid[mergeIndex][col];

					if (mergeTile == 0) {
						// Zelle leer, bewege Tile
						grid[mergeIndex][col] = currentTile;
						grid[row][col] = 0;
						moved = true;
					} else if (mergeTile == currentTile) {
						// Merge tiles
						grid[mergeIndex][col] *= 2;
						grid[row][col] = 0;
						moved = true;
						// Update score
						updateScore(mergeTile);
						mergeIndex--;
					} else {
						// Zelle nicht leer, nehme naechste Position
						mergeIndex--;
						if (mergeIndex != row) {
							grid[mergeIndex][col] = currentTile;
							grid[row][col] = 0;
							moved = true;
						}
					}
				}
			}
		}

		/*
		 * Wenn eine Bewegung der Tiles stattfand, wird ein neues zufälliges Tiles auf
		 * dem Grid platziert
		 */
		if (moved) {
			generateNewTile();
		}
	}

	/**
	 * Bewegen der Tiles nach Links nach erfolgter Bewegung der Tiles erfolgt
	 * Pruefung ob gemerged werden kann. Wenn man das in einem Schritt machen
	 * wuerde, kann es zu Fehlern kommen
	 */
	private void moveLeft() {
		boolean moved = false;

		for (int row = 0; row < GRID_SIZE; row++) {
			int mergeIndex = 0; // Index zur Verfolgung der Position des zu mergenden Tiles

			// Bewege Tiles nach Links
			for (int col = 1; col < GRID_SIZE; col++) {
				if (grid[row][col] != 0) {
					int currentTile = grid[row][col];
					int mergeTile = grid[row][mergeIndex];

					if (mergeTile == 0) {
						// Zelle leer, bewege Tile
						grid[row][mergeIndex] = currentTile;
						grid[row][col] = 0;
						moved = true;
					} else if (mergeTile == currentTile) {
						// Merge tiles
						grid[row][mergeIndex] *= 2;
						grid[row][col] = 0;
						moved = true;
						// Update score
						updateScore(mergeTile);
						mergeIndex++;
					} else {
						// Zelle nicht leer, nehme naechste Position
						mergeIndex++;
						if (mergeIndex != col) {
							grid[row][mergeIndex] = currentTile;
							grid[row][col] = 0;
							moved = true;
						}
					}
				}
			}
		}

		/*
		 * Wenn eine Bewegung der Tiles stattfand, wird ein neues zufälliges Tiles auf
		 * dem Grid platziert
		 */
		if (moved) {
			generateNewTile();
		}

	}

	/**
	 * Bewegen der Tiles nach Rechts nach erfolgter Bewegung der Tiles erfolgt
	 * Pruefung ob gemerged werden kann. Wenn man das in einem Schritt machen
	 * wuerde, kann es zu Fehlern kommen
	 */
	private void moveRight() {
		boolean moved = false;

		for (int row = 0; row < GRID_SIZE; row++) {
			int mergeIndex = GRID_SIZE - 1; // Index zur Verfolgung der Position des zu mergenden Tiles

			// Bewege Tiles nach rechts
			for (int col = GRID_SIZE - 2; col >= 0; col--) {
				if (grid[row][col] != 0) {
					int currentTile = grid[row][col];
					int mergeTile = grid[row][mergeIndex];

					if (mergeTile == 0) {
						// Zelle leer, bewege Tile
						grid[row][mergeIndex] = currentTile;
						grid[row][col] = 0;
						moved = true;
					} else if (mergeTile == currentTile) {
						// Merge tiles
						grid[row][mergeIndex] *= 2;
						grid[row][col] = 0;
						moved = true;
						// Update score
						updateScore(mergeTile);
						mergeIndex--;
					} else {
						// Zelle nicht leer, nehme naechste Position
						mergeIndex--;
						if (mergeIndex != col) {
							grid[row][mergeIndex] = currentTile;
							grid[row][col] = 0;
							moved = true;
						}
					}
				}
			}
		}

		/*
		 * Wenn eine Bewegung der Tiles stattfand, wird ein neues zufälliges Tiles auf
		 * dem Grid platziert
		 */
		if (moved) {
			generateNewTile();
		}

	}

	/**
	 * Aktualisieren der Labels bzw. der Tiles und setzen der Farbe entsprechend des
	 * Scores
	 */
	private void updateGridPane() {

		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				int value = grid[row][col];
				String text = value == 0 ? "" : String.valueOf(value);

				switch (text) {
				case "2":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_2, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "4":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_4, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "8":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_8, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "16":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_16, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "32":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_32, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "64":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_64, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "128":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_128, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "256":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_256, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "512":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_512, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "1024":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_1024, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				case "2048":
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_2048, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				default:
					labels[row][col].setBackground(
							new Background(new BackgroundFill(COLOR_EMPTY, CornerRadii.EMPTY, Insets.EMPTY)));
					break;
				}

				labels[row][col].setText(text);
			}
		}
	}

	/**
	 * @return Boolean value ob Spiel vorbei oder nicht
	 */
	private boolean checkGameOver() {
		// Pruefe leere Zellen
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				if (grid[row][col] == 0) {
					// Leere Zelle gefunden
					return false;
				}
			}
		}

		// Pruefung ob noch Zuege in die jeweilige Richtung moeglich sind
		for (int row = 0; row < GRID_SIZE; row++) {
			for (int col = 0; col < GRID_SIZE; col++) {
				int currentTile = grid[row][col];

				// Check adjacent tiles
				if (row < GRID_SIZE - 1 && grid[row + 1][col] == currentTile) {
					// Zug nach unten moeglich
					return false;
				}

				if (col < GRID_SIZE - 1 && grid[row][col + 1] == currentTile) {
					// Zug nach rechts moeglich
					return false;
				}
				if (row > GRID_SIZE - 1 && grid[row - 1][col] == currentTile) {
					// Zug nach oben moeglich
					return false;
				}

				if (col > GRID_SIZE - 1 && grid[row][col - 1] == currentTile) {
					// Zug nach oben moeglich
					return false;
				}
			}
		}

		// Wenn counter > 1 wird kein oben in handle kein score mehr gespeichert
		counter++;
		// Alle Zellen sind voll
		return true;
	}

	public int getScore() {
		return score;
	}

}

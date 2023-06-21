package main;

/**
 * Klasse um neue Highscores zu erzeugen
 */
public class Highscore{
	int score;

	public Highscore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "" + this.score;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}

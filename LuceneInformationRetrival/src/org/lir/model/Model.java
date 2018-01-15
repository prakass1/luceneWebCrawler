package org.lir.model;

public class Model {

	private String title;
	private int rank;
	private float relScore;
	private String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public float getRelScore() {
		return relScore;
	}

	public void setRelScore(float relScore) {
		this.relScore = relScore;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Model [title=" + title + ", rank=" + rank + ", relScore=" + relScore + ", url=" + url + "]";
	}
	
	

}

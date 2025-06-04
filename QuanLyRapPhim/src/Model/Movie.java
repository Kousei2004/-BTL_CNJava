package Model;

import java.sql.Date;

public class Movie {
	public Movie() {
		super();
	}
	
	
	private int movieId;
    private String title;
    private String genre;
    private int duration;
    private String description;
    private String trailerUrl;
    private Date releaseDate;
    
   

    public Movie(int movieId, String title, String genre, int duration, String description, String trailerUrl, Date releaseDate) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.description = description;
        this.trailerUrl = trailerUrl;
        this.releaseDate = releaseDate;
    }
    
	public int getMovieId() {
		return movieId;
	}
	public void setMovieId(int movieId) {
		this.movieId = movieId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTrailerUrl() {
		return trailerUrl;
	}
	public void setTrailerUrl(String trailerUrl) {
		this.trailerUrl = trailerUrl;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}


}

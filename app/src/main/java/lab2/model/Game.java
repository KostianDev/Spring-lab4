package lab2.model;

import java.time.LocalDateTime;

public class Game {
    private Long id;
    private LocalDateTime dateTime;
    private Team homeTeam;
    private Team awayTeam;

    public Game() {
    }

    public Game(Long id, LocalDateTime dateTime, Team homeTeam, Team awayTeam) {
        this.id = id;
        this.dateTime = dateTime;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    @Override
    public String toString() {
        return "Game{id=" + id + ", dateTime=" + dateTime + 
               ", homeTeam=" + homeTeam + ", awayTeam=" + awayTeam + "}";
    }
}

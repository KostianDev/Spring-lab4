package lab2.model;

public class GameResult {
    private Long id;
    private Game game;
    private Integer homeScore;
    private Integer awayScore;

    public GameResult() {
    }

    public GameResult(Long id, Game game, Integer homeScore, Integer awayScore) {
        this.id = id;
        this.game = game;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public String getScoreString() {
        return homeScore + " : " + awayScore;
    }

    @Override
    public String toString() {
        return "GameResult{id=" + id + ", game=" + game + 
               ", score=" + homeScore + ":" + awayScore + "}";
    }
}

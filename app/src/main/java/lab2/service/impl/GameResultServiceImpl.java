package lab2.service.impl;

import lab2.model.GameResult;
import lab2.repository.GameResultRepository;
import lab2.service.GameResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameResultServiceImpl implements GameResultService {

    private GameResultRepository gameResultRepository;

    @Autowired
    public void setGameResultRepository(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    @Override
    public List<GameResult> getAllResults() {
        return gameResultRepository.findAll();
    }

    @Override
    public Optional<GameResult> getResultById(Long id) {
        return gameResultRepository.findById(id);
    }

    @Override
    public Optional<GameResult> getResultByGameId(Long gameId) {
        return gameResultRepository.findByGameId(gameId);
    }

    @Override
    public GameResult saveResult(GameResult result) {
        return gameResultRepository.save(result);
    }

    @Override
    public void deleteResult(Long id) {
        gameResultRepository.deleteById(id);
    }
}

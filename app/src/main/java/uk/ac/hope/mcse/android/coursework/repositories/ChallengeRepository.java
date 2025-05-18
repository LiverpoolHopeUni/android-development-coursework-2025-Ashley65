package uk.ac.hope.mcse.android.coursework.repositories;

import android.app.Application;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import uk.ac.hope.mcse.android.coursework.database.ChallengeDao;
import uk.ac.hope.mcse.android.coursework.database.AppDatabase;
import uk.ac.hope.mcse.android.coursework.models.Challenge;

public class ChallengeRepository {
    private final ChallengeDao challengeDao;
    private final ExecutorService executorService;

    public ChallengeRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        challengeDao = database.challengeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void addChallenge(Challenge challenge) {
        executorService.execute(() -> challengeDao.insert(challenge));
    }

    public void updateChallenge(Challenge challenge) {
        executorService.execute(() -> challengeDao.update(challenge));
    }

    public void deleteChallenge(String challengeId) {
        executorService.execute(() -> {
            Challenge challenge = challengeDao.getChallengeById(challengeId);
            if (challenge != null) {
                challengeDao.delete(challenge);
            }
        });
    }

    public void getAllChallenges(Consumer<List<Challenge>> callback) {
        executorService.execute(() -> {
            List<Challenge> challenges = challengeDao.getAllChallenges();
            callback.accept(challenges);
        });
    }

    public void getChallenge(String challengeId, Consumer<Challenge> callback) {
        executorService.execute(() -> {
            Challenge challenge = challengeDao.getChallengeById(challengeId);
            callback.accept(challenge);
        });
    }

    public void getActiveChallenges(Consumer<List<Challenge>> callback) {
        executorService.execute(() -> {
            List<Challenge> activeChallenges = challengeDao.getActiveChallenges(new Date().getTime());
            callback.accept(activeChallenges);
        });
    }
}
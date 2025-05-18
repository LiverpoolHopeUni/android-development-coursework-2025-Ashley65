package uk.ac.hope.mcse.android.coursework.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import uk.ac.hope.mcse.android.coursework.models.Challenge;

@Dao
public interface ChallengeDao {
    @Insert
    void insert(Challenge challenge);

    @Update
    void update(Challenge challenge);

    @Delete
    void delete(Challenge challenge);

    @Query("SELECT * FROM challenges WHERE id = :challengeId")
    Challenge getChallengeById(String challengeId);

    @Query("SELECT * FROM challenges ORDER BY startDate DESC")
    List<Challenge> getAllChallenges();

    @Query("SELECT * FROM challenges WHERE endDate >= :currentTime AND isCompleted = 0 ORDER BY startDate ASC")
    List<Challenge> getActiveChallenges(long currentTime);
}
package com.sports.backend.match.v1.infrastructure.adapter.persistence.repository;

import com.sports.backend.match.v1.infrastructure.adapter.persistence.model.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchJpaRepository extends JpaRepository<MatchEntity, Long> {

    Optional<MatchEntity> findByApiId(Long apiId);

    @Query("""
            SELECT m FROM MatchEntity m
            WHERE (m.homeTeamId = :teamId OR m.awayTeamId = :teamId)
            AND m.status = 'FINISHED'
            ORDER BY m.matchDate DESC
            LIMIT :limit
            """)
    List<MatchEntity> findFinishedByTeamId(@Param("teamId") Long teamId, @Param("limit") int limit);

    @Query("""
            SELECT m FROM MatchEntity m
            WHERE ((m.homeTeamId = :team1Id AND m.awayTeamId = :team2Id)
               OR (m.homeTeamId = :team2Id AND m.awayTeamId = :team1Id))
            AND m.status = 'FINISHED'
            ORDER BY m.matchDate DESC
            """)
    List<MatchEntity> findByBothTeams(@Param("team1Id") Long team1Id, @Param("team2Id") Long team2Id);

    List<MatchEntity> findByCompetitionIdAndSeason(Long competitionId, String season);
}

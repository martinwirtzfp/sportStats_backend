package com.sports.backend.competition.v1.application.service;

import com.sports.backend.competition.v1.application.domain.model.Competition;
import com.sports.backend.competition.v1.application.domain.port.CompetitionPort;
import com.sports.backend.shared.v1.application.exception.ApplicationException;
import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompetitionService {

    private final CompetitionPort competitionPort;

    public List<Competition> findAll() {
        log.debug("Fetching all competitions");
        return competitionPort.findAll();
    }

    public Competition findById(final Long id) {
        log.debug("Fetching competition id#{}", id);
        return competitionPort.findById(id)
                .orElseThrow(() -> new ApplicationException(ApplicationError.COMPETITION_NOT_FOUND));
    }
}

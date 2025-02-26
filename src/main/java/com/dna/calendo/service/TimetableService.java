package com.dna.calendo.service;

import com.dna.calendo.domain.Timetable;
import com.dna.calendo.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimetableService {
    private final TimetableRepository timetableRepository;

    public TimetableService(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    public Timetable createTimetable(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    public List<Timetable> getTimetablesByProjectId(Long projectId) {
        return timetableRepository.findByProjectId(projectId);
    }
}

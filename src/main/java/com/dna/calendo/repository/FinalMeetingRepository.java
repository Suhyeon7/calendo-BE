package com.dna.calendo.repository;

import com.dna.calendo.domain.FinalMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinalMeetingRepository extends JpaRepository<FinalMeeting, Long> {}
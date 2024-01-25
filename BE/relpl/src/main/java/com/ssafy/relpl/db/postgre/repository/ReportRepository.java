package com.ssafy.relpl.db.postgre.repository;

import com.ssafy.relpl.db.postgre.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>{



}

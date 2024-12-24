package com.example.udd_security_incidents.repository;

import com.example.udd_security_incidents.model.DummyTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyRepository extends JpaRepository<DummyTable, Integer> {
}

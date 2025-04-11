package assignment_java5.java5.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import assignment_java5.java5.entitys.Shipper;
import assignment_java5.java5.entitys.Shipper.Status;

public interface ShipperDAO extends JpaRepository<Shipper, Long> {
    List<Shipper> findByStatus(Status status);
}

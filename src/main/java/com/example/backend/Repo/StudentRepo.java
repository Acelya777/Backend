package com.example.backend.Repo;

import com.example.backend.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepo extends JpaRepository<Student,Integer> {
    void deleteStudentById(Integer id);

    Optional<Student> findStudentById(Integer id);

}
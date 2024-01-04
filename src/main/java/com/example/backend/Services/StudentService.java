package com.example.backend.Services;

import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.Model.Student;
import com.example.backend.Repo.StudentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {
    private final StudentRepo studentRepo;
    @Autowired
    public StudentService(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public Student addStudent(Student student){
        return studentRepo.save(student);
    }

    public List<Student> findAllStudent(){
        return studentRepo.findAll();
    }



    public Student findStudentById(int id){
        return studentRepo.findStudentById(id)
                .orElseThrow(() -> new UserNotFoundException("User by id " + id + " was not found"));
    }

    public void deleteStudent(Integer id){
        studentRepo.deleteStudentById(id);
    }


    public  Student updateStudent(Student student) {
        return studentRepo.save(student);
    }
}

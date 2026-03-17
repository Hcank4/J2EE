package com.HongCan.course_registration.service;

import com.HongCan.course_registration.dto.RegisterRequest;
import com.HongCan.course_registration.entity.Role;
import com.HongCan.course_registration.entity.Student;
import com.HongCan.course_registration.repository.RoleRepository;
import com.HongCan.course_registration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerStudent(RegisterRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã tồn tại");
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role STUDENT"));

        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setEmail(request.getEmail());

        HashSet<Role> roles = new HashSet<>();
        roles.add(studentRole);
        student.setRoles(roles);

        studentRepository.save(student);
    }

    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
    }

    public Student createGoogleUserIfNotExists(String email, String name) {
        return studentRepository.findByEmail(email).orElseGet(() -> {
            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role STUDENT"));

            Student student = new Student();
            student.setEmail(email);

            String username = email.split("@")[0];
            if (studentRepository.existsByUsername(username)) {
                username = username + System.currentTimeMillis();
            }

            student.setUsername(username);
            student.setPassword(passwordEncoder.encode("google-login-" + System.currentTimeMillis()));

            HashSet<Role> roles = new HashSet<>();
            roles.add(studentRole);
            student.setRoles(roles);

            return studentRepository.save(student);
        });
    }
}
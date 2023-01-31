package fr.takima.training.simpleapi.controller;

import fr.takima.training.simpleapi.entity.Department;
import fr.takima.training.simpleapi.service.DepartmentService;
import fr.takima.training.simpleapi.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final StudentService studentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, StudentService studentService) {
        this.departmentService = departmentService;
        this.studentService = studentService;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getDepartments() {
        return ResponseEntity.ok(departmentService.getDepartments());
    }

    @GetMapping("/{departmentName}/students")
    public ResponseEntity<Object> getDepartmentList(@PathVariable(name="departmentName") String name) {
        Optional<Department> optionalDepartment = Optional.ofNullable(this.departmentService.getDepartmentByName(name));
        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(this.studentService.getStudentsByDepartmentName(name));
    }

    @GetMapping("/{departmentName}")
    public ResponseEntity<Object> getDepartmentByName(@PathVariable(name="departmentName") String name) {
        Optional<Department> optionalDepartment = Optional.ofNullable(this.departmentService.getDepartmentByName(name));
        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(this.departmentService.getDepartmentByName(name));
    }

    @GetMapping("/{departmentName}/count")
    public ResponseEntity<Object> getDepartmentCountByName(@PathVariable(name="departmentName") String name) {
        Optional<Department> optionalDepartment = Optional.ofNullable(this.departmentService.getDepartmentByName(name));
        if (optionalDepartment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(this.studentService.getStudentsNumberByDepartmentName(name));
    }
}

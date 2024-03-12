package com.example.todo.controller;

import com.example.todo.entity.ToDo;
import com.example.todo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
public class ToDoController {

    private final ToDoService toDoService;

    @Autowired
    public ToDoController(ToDoService toDoService) {
        this.toDoService = toDoService;
    }

    @GetMapping("/todos")
    ResponseEntity<List<ToDo>> getAllToDos() {
        return new ResponseEntity<>(toDoService.findAll(), HttpStatus.OK);
    }

    @PostMapping("/todos")
    ResponseEntity<ToDo> create(@RequestBody ToDo toDo) {
        return new ResponseEntity<>(toDoService.save(toDo), HttpStatus.CREATED);
    }
    @GetMapping("/todo/{id}")
    ResponseEntity<ToDo> getToDoById(@PathVariable Long id) {
        return new ResponseEntity<>(toDoService.findById(id), HttpStatus.OK);
    }

    @PutMapping("/todo/{id}")
    public ResponseEntity<ToDo> updateToDo(@PathVariable Long id, @RequestBody ToDo updatedToDo) {
        ToDo existingToDo = toDoService.findById(id);
        if (existingToDo == null) {
            return ResponseEntity.notFound().build();
        }
        existingToDo.setText(updatedToDo.getText());
        existingToDo.setCompleted(updatedToDo.isCompleted());
        toDoService.save(existingToDo);
        return new ResponseEntity<>(existingToDo, HttpStatus.OK);
    }

    @DeleteMapping("/todo/{id}")
    ResponseEntity<ToDo> deleteToDoById(@PathVariable Long id) {
        ToDo existingToDo = toDoService.findById(id);
        if (existingToDo == null) {
            return ResponseEntity.notFound().build();
        }
        toDoService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

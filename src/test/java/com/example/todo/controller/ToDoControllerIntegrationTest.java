package com.example.todo.controller;

import com.example.todo.entity.ToDo;
import com.example.todo.service.ToDoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ToDoController.class)
@AutoConfigureMockMvc
public class ToDoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ToDoService toDoService;

    private ToDo todo1;
    private ToDo todo2;

    @BeforeEach
    void setUp() {
        todo1 = new ToDo(1L, "Task 1", true);
        todo2 = new ToDo(2L, "Task 2", false);
    }

    @Test
    void getAllToDos() throws Exception {
        when(toDoService.findAll()).thenReturn(Arrays.asList(todo1, todo2));

        mockMvc.perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(todo1.getId()))
                .andExpect(jsonPath("$[0].text").value(todo1.getText()))
                .andExpect(jsonPath("$[0].completed").value(todo1.isCompleted()))
                .andExpect(jsonPath("$[1].id").value(todo2.getId()))
                .andExpect(jsonPath("$[1].text").value(todo2.getText()))
                .andExpect(jsonPath("$[1].completed").value(todo2.isCompleted()));
    }

    @Test
    void createToDo() throws Exception {
        ToDo newToDo = new ToDo("New Task", false);
        when(toDoService.save(any(ToDo.class))).thenReturn(newToDo);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newToDo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(newToDo.getText()))
                .andExpect(jsonPath("$.completed").value(newToDo.isCompleted()));
    }

    @Test
    void getToDoById() throws Exception {
        when(toDoService.findById(1L)).thenReturn(todo1);

        mockMvc.perform(get("/todo/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(todo1.getId()))
                .andExpect(jsonPath("$.text").value(todo1.getText()))
                .andExpect(jsonPath("$.completed").value(todo1.isCompleted()));
    }

    @Test
    void updateToDo() throws Exception {
        ToDo updatedToDo = new ToDo(1L, "Updated Task", false);
        when(toDoService.findById(1L)).thenReturn(todo1);
        when(toDoService.save(any(ToDo.class))).thenReturn(updatedToDo);

        mockMvc.perform(put("/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedToDo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(updatedToDo.getText()))
                .andExpect(jsonPath("$.completed").value(updatedToDo.isCompleted()));
    }

    @Test
    void deleteToDoById() throws Exception {
        when(toDoService.findById(1L)).thenReturn(todo1);

        mockMvc.perform(delete("/todo/1"))
                .andExpect(status().isOk());
    }
}

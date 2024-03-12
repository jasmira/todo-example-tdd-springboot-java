package com.example.todo.controller;

import com.example.todo.entity.ToDo;
import com.example.todo.service.ToDoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class ToDoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ToDoService toDoService;

    @Test
    void getAllToDos() throws Exception {
        List<ToDo> toDoList = new ArrayList<ToDo>();
        toDoList.add(new ToDo(1L,"Eat thrice",true));
        toDoList.add(new ToDo(2L,"Sleep Twice",true));
        when(toDoService.findAll()).thenReturn(toDoList);

        mockMvc.perform(MockMvcRequestBuilders.get("/todos")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(jsonPath("$", hasSize(2))).andDo(print());
    }

    @Test
    void createAToDo() throws Exception {
        ToDo eatToDo = new ToDo(1L, "Eat thrice", true);
        when(toDoService.save(any(ToDo.class))).thenReturn(eatToDo);

        String eatToDoJSON = objectMapper.writeValueAsString(eatToDo);
        ResultActions result = mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(eatToDoJSON)
        );

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Eat thrice"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void getToDoById() throws Exception {
        ToDo todoSample = new ToDo("Todo Sample 1",true);
        when(toDoService.findById(1L)).thenReturn(todoSample);

        mockMvc.perform(MockMvcRequestBuilders.get("/todo/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", equalTo(todoSample.getText())))
                .andExpect(jsonPath("$.completed", equalTo(todoSample.isCompleted())))
                .andDo(print());
    }

    @Test
    void updateAToDo() throws Exception {
        // Prepare test data
        Long id = 1L;
        ToDo existingToDo = new ToDo("Existing ToDo", true);
        ToDo updatedToDo = new ToDo("Updated ToDo", false);

        // Mock ToDoService behavior
        when(toDoService.findById(id)).thenReturn(existingToDo);
        when(toDoService.save(any(ToDo.class))).thenReturn(updatedToDo);

        // Perform PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/todo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedToDo)))
                .andExpect(status().isOk());

        // Verify ToDoService interactions
        verify(toDoService).findById(id);
        //verify(toDoService).save(existingToDo);

        // Verify that the ToDoService's save method is called with the updated ToDo
        verify(toDoService).save(argThat(argument -> argument.getText().equals(updatedToDo.getText()) && argument.isCompleted() == updatedToDo.isCompleted()));
    }

    @Test
    void deleteToDoById() throws Exception {
        // Prepare test data
        Long id = 1L;
        ToDo todoToDelete = new ToDo("ToDo to delete", true);

        // Mock ToDoService behavior
        when(toDoService.findById(id)).thenReturn(todoToDelete);

        // Perform DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verify ToDoService interaction
        verify(toDoService).findById(id);
        verify(toDoService).deleteById(id);
    }
}

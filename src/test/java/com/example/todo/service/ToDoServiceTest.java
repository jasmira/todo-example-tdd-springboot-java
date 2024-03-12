package com.example.todo.service;

import com.example.todo.entity.ToDo;
import com.example.todo.repository.ToDoRepository;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atMostOnce;

@SpringBootTest
public class ToDoServiceTest {

    /** Test case with mocking the repository which doesn't guarantee that it actually saves the data in DB **/

    /*@MockBean
    private ToDoRepository toDoRepository;

    @Autowired
    private ToDoService toDoService;


    @Test
    void getAllToDos(){
        // Create a sample ToDo object
        ToDo todoSample = new ToDo("Todo Sample 1", true);

        // Mock the behavior of the ToDoRepository
        when(toDoRepository.findAll()).thenReturn(Collections.singletonList(todoSample));

        // Call the method under test
        List<ToDo> toDoList = toDoService.findAll();

        // Assert that the returned list contains the expected ToDo object
        ToDo lastToDo = toDoList.get(toDoList.size() - 1);
        assertEquals(todoSample.getText(), lastToDo.getText());
        assertEquals(todoSample.isCompleted(), lastToDo.isCompleted());
        assertEquals(todoSample.getId(), lastToDo.getId());
    }*/


    /** Test case with auto-wiring the repository which guarantees that it actually creates and saves the data in DB **/
    @Autowired
    private ToDoRepository toDoRepository;

    @AfterEach
    void tearDown(){
        // Delete all ToDo entities from the database before each test method
        toDoRepository.deleteAll();
    }

    @Test
    void getAllToDos(){
        ToDo todoSample = new ToDo("Todo Sample 1",true);
        toDoRepository.save(todoSample);
        ToDoService toDoService = new ToDoService(toDoRepository);

        ToDo firstResult = toDoService.findAll().get(0);

        assertEquals(todoSample.getText(), firstResult.getText());
        assertEquals(todoSample.isCompleted(), firstResult.isCompleted());
        assertEquals(todoSample.getId(), firstResult.getId());
    }

    @Test
    void saveAToDo() {
        ToDoService toDoService = new ToDoService(toDoRepository);
        ToDo todoSample = new ToDo("Todo Sample 1",true);

        toDoService.save(todoSample);

        assertEquals(1.0, toDoRepository.count());
    }

    @Test
    void getToDoById(){
        ToDo todoSample2 = new ToDo("Todo Sample 2",true);
        ToDo todoSample3 = new ToDo("Todo Sample 3",false);
        toDoRepository.save(todoSample2);
        toDoRepository.save(todoSample3);
        ToDoService toDoService = new ToDoService(toDoRepository);

        // Now retrieve the ToDo object with id=3L
        ToDo searchResult = toDoService.findById(3L);

        assertNotNull(searchResult); // Ensure that searchResult is not null
        assertEquals(todoSample3.getText(), searchResult.getText());
        assertEquals(todoSample3.isCompleted(), searchResult.isCompleted());
        assertEquals(todoSample3.getId(), searchResult.getId());
    }

    @Test
    void deleteToDoById() {
        // Given
        Long id = 1L;

        // When
        ToDoService toDoService = Mockito.mock(ToDoService.class);
        toDoService.deleteById(id);

        // Then
        verify(toDoService).deleteById(id);
        verify(toDoService, atMostOnce()).deleteById(id);
    }
}

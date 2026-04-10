package distributed.systems.starterapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = "*")
/**
 * REST controller for managing Todo items.
 *
 * Provides endpoints to create, retrieve, update, and delete Todo items.
 * This controller uses an in-memory list to store Todo items for demonstration purposes.
 */
public class TodoController {
```
    private final List<TodoItem> todos = new ArrayList<>(
        List.of(
            new TodoItem(1, "Todo 1", "Description 1", false),
            new TodoItem(2, "Todo 2", "Description 2", true)
        )
    );

    @GetMapping
    public ResponseEntity<List<TodoItem>> getTodos() {
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItem> getTodoById(@PathVariable Integer id) {
        for (TodoItem todo : todos) {
            if (todo.getId().equals(id)) {
                return ResponseEntity.ok(todo);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TodoItem> addTodo(@RequestBody TodoItem todo) {
        todos.add(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(todo);
    }


    @PutMapping
    public ResponseEntity<TodoItem> updateTodo(@RequestBody TodoItem updatedTodo) {
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(updatedTodo.getId())) {
                todos.set(i, updatedTodo);
                return ResponseEntity.ok(updatedTodo);
            }
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Integer id) {
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(id)) {
                todos.remove(i);
                return ResponseEntity.ok("Todo item deleted successfully");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

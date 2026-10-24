package de.test.spring;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/todo")
public class ToDoController {

    @Autowired
    private ToDoRepo toDoRepo;

    @GetMapping
    public List<ToDoEntry> getAll() {
        return toDoRepo.findAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ToDoEntry> getById(@PathVariable final long id) {
        return ResponseEntity.ok(toDoRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @PostMapping
    public ResponseEntity<ToDoEntry> create(@Valid @RequestBody final ToDoEntry toDoEntry) {
        toDoEntry.setId(null);
        final ToDoEntry saved = toDoRepo.save(toDoEntry);
        return ResponseEntity.created(URI.create("/api/todo/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ToDoEntry> update(@PathVariable final long id, @Valid @RequestBody final ToDoEntry toDoEntry) {
        final ToDoEntry entry = toDoRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (toDoEntry.getTitle() != null) entry.setTitle(toDoEntry.getTitle());
        if (toDoEntry.getDescription() != null) entry.setDescription(toDoEntry.getDescription());
        entry.setDone(toDoEntry.isDone());
        return ResponseEntity.ok(toDoRepo.save(entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        if (!toDoRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        toDoRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

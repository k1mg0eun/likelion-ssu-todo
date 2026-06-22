package likelion.todo.domain.todo.controller;

import likelion.todo.domain.todo.dto.*;
import likelion.todo.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/members/{memberId}/todos")
    public ResponseEntity<TodoResponseDTO> createTodo(
            @PathVariable Long memberId,
            @RequestBody TodoCreateRequestDTO req
    ) {
        return ResponseEntity.ok(todoService.createTodo(memberId, req));
    }

    @GetMapping("/members/{memberId}/todos")
    public ResponseEntity<List<TodoResponseDTO>> getTodos(@PathVariable Long memberId) {
        return ResponseEntity.ok(todoService.getTodos(memberId));
    }

    @GetMapping("/members/{memberId}/todos/daily")
    public ResponseEntity<List<TodoResponseDTO>> getDailyTodos(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day
    ) {
        return ResponseEntity.ok(todoService.getDailyTodos(memberId, month, day));
    }

    @PatchMapping("/members/{memberId}/todos/{todoId}")
    public ResponseEntity<TodoResponseDTO> updateTodo(
            @PathVariable Long memberId,
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequestDTO req
    ) {
        return ResponseEntity.ok(todoService.updateTodo(memberId, todoId, req));
    }

    @DeleteMapping("/members/{memberId}/todos/{todoId}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable Long memberId,
            @PathVariable Long todoId
    ) {
        todoService.deleteTodo(memberId, todoId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/members/{memberId}/todos/{todoId}/check")
    public ResponseEntity<TodoResponseDTO> completeTodo(
            @PathVariable Long memberId,
            @PathVariable Long todoId
    ) {
        return ResponseEntity.ok(todoService.completeTodo(memberId, todoId));
    }

    @PatchMapping("/members/{memberId}/todos/{todoId}/reviews")
    public ResponseEntity<TodoResponseDTO> reviewTodo(
            @PathVariable Long memberId,
            @PathVariable Long todoId,
            @RequestBody TodoReviewRequestDTO req
    ) {
        return ResponseEntity.ok(todoService.reviewTodo(memberId, todoId, req));
    }
}
package likelion.todo.domain.todo.service;

import likelion.todo.domain.member.entity.Member;
import likelion.todo.domain.member.repository.MemberRepository;
import likelion.todo.domain.todo.dto.*;
import likelion.todo.domain.todo.entity.Todo;
import likelion.todo.domain.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TodoService {

    private static final ZoneId ASIA_SEOUL = ZoneId.of("Asia/Seoul");

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;   // 멤버 확인용으로 둘 다 필요

    @Transactional
    public TodoResponseDTO createTodo(Long memberId, TodoCreateRequestDTO req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."));

        Todo todo = Todo.builder()
                .content(req.content())
                .date(req.date())
                .isChecked(false)
                .emoji("")
                .member(member)
                .build();

        todoRepository.save(todo);
        return TodoResponseDTO.from(todo);
    }

    public List<TodoResponseDTO> getTodos(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다.");
        }
        return todoRepository.findAllByMemberIdOrderByDateAsc(memberId).stream()
                .map(TodoResponseDTO::from)
                .toList();
    }

    public List<TodoResponseDTO> getDailyTodos(Long memberId, Integer month, Integer day) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다.");
        }
        // month/day 둘 다 없으면 오늘 날짜로, 하나만 있으면 잘못된 요청
        if (month == null && day == null) {
            LocalDate today = LocalDate.now(ASIA_SEOUL);
            month = today.getMonthValue();
            day = today.getDayOfMonth();
        } else if (month == null || day == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.");
        }
        return todoRepository.findAllByMemberIdAndMonthAndDay(memberId, month, day).stream()
                .map(TodoResponseDTO::from)
                .toList();
    }

    @Transactional
    public TodoResponseDTO updateTodo(Long memberId, Long todoId, TodoUpdateRequestDTO req) {
        Todo todo = findOwnedTodo(memberId, todoId);
        todo.update(req.content(), req.date());
        if (req.emoji() != null) {
            todo.updateEmoji(req.emoji());
        }
        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Todo todo = findOwnedTodo(memberId, todoId);
        todoRepository.delete(todo);
    }

    @Transactional
    public TodoResponseDTO completeTodo(Long memberId, Long todoId) {
        Todo todo = findOwnedTodo(memberId, todoId);
        todo.complete();
        return TodoResponseDTO.from(todo);
    }

    @Transactional
    public TodoResponseDTO reviewTodo(Long memberId, Long todoId, TodoReviewRequestDTO req) {
        Todo todo = findOwnedTodo(memberId, todoId);
        todo.updateEmoji(req.emoji() == null ? "" : req.emoji());
        return TodoResponseDTO.from(todo);
    }

    // 반복되는 "멤버 확인 + todo 찾기 + 소유권 확인"을 메서드 하나로 묶음
    private Todo findOwnedTodo(Long memberId, Long todoId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다.");
        }
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "투두를 찾을 수 없습니다."));
        if (!todo.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 멤버의 투두가 아닙니다.");
        }
        return todo;
    }
}
package com.dna.calendo.service;

import com.dna.calendo.config.auth.dto.TodoDto;
import com.dna.calendo.domain.todo.Todo;
import com.dna.calendo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    /** ✅ 특정 날짜의 사용자 투두 조회 */
    public List<TodoDto> getTodosByDate(Long userId, String date) {
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.plusDays(1).atStartOfDay();

        return todoRepository.findByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay)
                .stream()
                .map(todo -> new TodoDto(
                        todo.getId(),
                        todo.getTitle(),
                        todo.isChecked(),
                        todo.getUserId()))
                .collect(Collectors.toList());
    }

    /** ✅ 투두 추가 */
    public void addTodo(Long userId, TodoDto todoDto) {
        if (todoDto.getTitle() == null || todoDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("할 일 제목(title)은 필수 입력값입니다.");
        }

        Todo todo = new Todo();
        todo.setTitle(todoDto.getTitle());
        todo.setChecked(false);
        todo.setUserId(userId);
        todo.setCreatedAt(LocalDateTime.now());

        todoRepository.save(todo);
    }

    /** ✅ 체크 상태 변경 */
    @Transactional
    public void toggleTodoChecked(Long todoId) {
        Optional<Todo> optionalTodo = todoRepository.findById(todoId);
        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            todo.toggleChecked();
            todoRepository.save(todo);  // ✅ 변경사항 저장
        } else {
            throw new IllegalArgumentException("해당 투두가 존재하지 않습니다. ID: " + todoId);
        }
    }

    /** ✅ 투두 업데이트 기능 (제목 + 체크 상태 수정 가능) */
    @Transactional
    public void updateTodo(Long id, TodoDto todoDto) {
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            todo.setTitle(todoDto.getTitle()); // ✅ 제목 수정
            todo.setChecked(todoDto.isChecked()); // ✅ 체크 상태도 업데이트 가능하도록 추가
            todoRepository.save(todo);
        } else {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없습니다.");
        }
    }
}

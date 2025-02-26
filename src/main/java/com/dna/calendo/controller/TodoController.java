package com.dna.calendo.controller;

import com.dna.calendo.config.auth.dto.TodoDto;
import com.dna.calendo.config.auth.dto.SessionUser;
import com.dna.calendo.service.TodoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")  // ✅ CORS 문제 방지
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;
    private final HttpSession httpSession;

    /**
     * ✅ 특정 날짜의 사용자 투두 리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<TodoDto>> getTodosByDate(@RequestParam String date) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(todoService.getTodosByDate(user.getId(), date));
    }

    /**
     * ✅ 투두 추가
     */
    @PostMapping("/add")
    public ResponseEntity<String> addTodo(@RequestBody TodoDto todoDto) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        todoService.addTodo(user.getId(), todoDto);
        return ResponseEntity.ok("투두가 추가되었습니다.");
    }

    /**
     * ✅ 투두 체크 상태 변경
     */
    @PutMapping("/toggle/{id}")
    public ResponseEntity<String> toggleTodo(@PathVariable Long id) {
        todoService.toggleTodoChecked(id);
        return ResponseEntity.ok("체크 상태가 변경되었습니다.");
    }


        /**
         * ✅ 투두 수정 (제목 및 체크 상태 변경 가능)
         */
        @PutMapping("/update/{id}")
        public ResponseEntity<String> updateTodo(@PathVariable("id") Long id, @RequestBody TodoDto request) {
            System.out.println("📌 [Todo 수정] 요청 ID: " + id); // ✅ 디버깅 로그 추가
            System.out.println("📌 [Todo 수정] 변경 내용: " + request.getTitle());

            try {
                todoService.updateTodo(id, request);
                return ResponseEntity.ok("투두가 수정되었습니다.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }
    }


package com.onestack.project.controller;

import com.onestack.project.domain.ChatBoardEvent;
import com.onestack.project.service.ChatBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/posts")
public class ChatBoardController {
    
    @Autowired
    private ChatBoardService chatBoardService;
    
    // 게시글 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBoard(@RequestBody ChatBoardEvent board) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatBoardService.createBoard(board);
            response.put("success", true);
            response.put("message", "게시글이 작성되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    // 게시글 목록 조회
    @GetMapping("/{roomId}")
    public ResponseEntity<List<ChatBoardEvent>> getBoardList(@PathVariable String roomId) {
        return ResponseEntity.ok(chatBoardService.getBoardList(roomId));
    }
    
    // 게시글 상세 조회
    @GetMapping("/detail/{boardId}")
    public ResponseEntity<ChatBoardEvent> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(chatBoardService.getBoard(boardId));
    }
    
    // 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> updateBoard(
            @PathVariable Long boardId,
            @RequestBody ChatBoardEvent board) {
        Map<String, Object> response = new HashMap<>();
        try {
            board.setBoardId(boardId);
            chatBoardService.updateBoard(board);
            response.put("success", true);
            response.put("message", "게시글이 수정되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제  
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Map<String, Object>> deleteBoard(@PathVariable Long boardId) {
        Map<String, Object> response = new HashMap<>();
        try {
            chatBoardService.deleteBoard(boardId);
            response.put("success", true);
            response.put("message", "게시글이 삭제되었습니다.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
} 
package com.org.qualitycore.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.qualitycore.board.model.dto.BoardDTO;
import com.org.qualitycore.board.model.service.BoardService;
import com.org.qualitycore.common.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final SimpMessagingTemplate messagingTemplate;

    // 전체조회
    @GetMapping("/board")
    public ResponseEntity<Message> findAllBoard(@PageableDefault Pageable pageable,
                                                @RequestParam(required = false) String searchType,
                                                @RequestParam(required = false) String searchKeyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        Page<BoardDTO> board = boardService.findAllBoard(pageable, searchType, searchKeyword);

        Map<String, Object> res = new HashMap<>();
        res.put("board", board);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "조회성공", res));
    }

    // 메인화면 게시판
    @GetMapping("/boardMain")
    public ResponseEntity<Message> findAllBoardMain(@PageableDefault Pageable pageable,
                                                @RequestParam(required = false) String searchType,
                                                @RequestParam(required = false) String searchKeyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        Page<BoardDTO> board = boardService.findAllBoardMain(pageable, searchType, searchKeyword);

        Map<String, Object> res = new HashMap<>();
        res.put("board", board);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "조회성공", res));
    }

    // 상세조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Message> findByIdBoard(@PathVariable("boardId") String boardId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        BoardDTO board = boardService.findByIdBoard(boardId);

        Map<String, Object> res = new HashMap<>();
        res.put("board", board);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "조회성공", res));
    }

    // 게시글 등록
    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> BoardCreate(
            @RequestPart("boardData") String boardDataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        ObjectMapper objectMapper = new ObjectMapper();
        BoardDTO boardDTO;
        try {
            // URL 디코딩 처리 (UTF-8)
            String decodedJson = URLDecoder.decode(boardDataJson, StandardCharsets.UTF_8.name());

            // JSON 파싱
            boardDTO = objectMapper.readValue(decodedJson, BoardDTO.class);

            // 서비스 호출 (게시글 생성)
            BoardDTO createdBoard = boardService.BoardCreate(boardDTO, file);

            // WebSocket으로 알림 발송 ➔ 추가된 부분
            messagingTemplate.convertAndSend("/topic/newPosts", createdBoard);

            Map<String, Object> res = new HashMap<>();
            res.put("code", 201);
            res.put("message", "등록성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(res);

        } catch (UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body("인코딩 오류");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("JSON 형식 오류");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    // 게시판 수정
    @PutMapping("/board")
    public ResponseEntity<?> updateBoard(@RequestBody BoardDTO board) {

        boardService.updateBoard(board);

        Map<String, Object> res = new HashMap<>();

        res.put("code", 201);
        res.put("message", "수정성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 게시판 삭제
    @DeleteMapping("board/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable("boardId") String boardId) {

        boardService.deleteBoard(boardId);

        Map<String, Object> res = new HashMap<>();

        res.put("code", 200);
        res.put("message", "삭제성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

}

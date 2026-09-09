package com.illoon.board;

import com.illoon.board.dto.BoardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board", description = "메인보드 집계")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "메인보드 데이터 (내 프로젝트·Sprint·오늘 일정·요약 카운트)")
    @GetMapping("/api/me/board")
    public BoardResponse board(@AuthenticationPrincipal Long userId) {
        return boardService.getBoard(userId);
    }
}

package com.org.qualitycore.board.model.repository;

import com.org.qualitycore.board.model.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, String> {

    @Query("SELECT MAX(b.boardId) FROM Board b")
    String findMaxBoardId(); // ✅ 최대 ID 조회 추가
}

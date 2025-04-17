package com.org.qualitycore.board.model.service;

import com.org.qualitycore.board.model.dto.BoardDTO;
import com.org.qualitycore.board.model.entity.Board;
import com.org.qualitycore.board.model.entity.QBoard;
import com.org.qualitycore.board.model.repository.BoardRepository;
import com.org.qualitycore.board.model.repository.EmployeeRepository;
import com.org.qualitycore.common.CloudinaryService;
import com.org.qualitycore.work.model.entity.Employee;
import com.org.qualitycore.work.model.entity.QEmployee;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final JPAQueryFactory queryFactory;
    private final BoardRepository boardRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    // 게시판 전체조회
    public Page<BoardDTO> findAllBoard(Pageable pageable, String searchType, String searchKeyword) {
        QBoard board = QBoard.board;
        QEmployee employee = QEmployee.employee;

        // 검색 조건 생성
        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.hasText(searchKeyword)) {
            if ("boardTitle".equals(searchType)) {
                where.and(board.boardTitle.containsIgnoreCase(searchKeyword));
            } else if ("empName".equals(searchType)) {
                where.and(employee.empName.containsIgnoreCase(searchKeyword));
            } else if ("boardContents".equals(searchType)) {
                where.and(board.boardContents.containsIgnoreCase(searchKeyword));
            } else {
                // 전체 검색 (제목+작성자+내용)
                where.and(
                        board.boardTitle.containsIgnoreCase(searchKeyword)
                                .or(employee.empName.containsIgnoreCase(searchKeyword))
                                .or(board.boardContents.containsIgnoreCase(searchKeyword))
                );
            }
        }

        // 전체 개수 조회 (검색 조건 적용)
        long total = Optional.ofNullable(
                queryFactory
                        .select(board.count())
                        .from(board)
                        .join(board.employee, employee)
                        .where(where)
                        .fetchOne()
                        ).orElse(0L);


        // 데이터 조회 (페이징 적용)
        List<BoardDTO> results = queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.boardId.as("boardId"),
                        board.boardContents.as("boardContents"),
                        board.boardTitle.as("boardTitle"),
                        board.boardDate.as("boardDate"),
                        board.boardCategory.as("boardCategory"),
                        board.fileName.as("fileName"),
                        board.fileUrl.as("fileUrl"),
                        employee.empId.as("empId"),
                        employee.empName.as("empName")
                ))
                .from(board)
                .join(board.employee, employee)
                .where(where)
                .orderBy(
                        board.boardCategory.desc(), // 중요 카테고리 우선
                        board.boardId.desc()        // 최신글 순
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    // 메인화면 중요, 공지 전체조회
    public Page<BoardDTO> findAllBoardMain(Pageable pageable, String searchType, String searchKeyword) {
        QBoard board = QBoard.board;
        QEmployee employee = QEmployee.employee;

        // 검색 조건 생성
        BooleanBuilder where = new BooleanBuilder();
        if (StringUtils.hasText(searchKeyword)) {
            if ("boardTitle".equals(searchType)) {
                where.and(board.boardTitle.containsIgnoreCase(searchKeyword));
            } else if ("empName".equals(searchType)) {
                where.and(employee.empName.containsIgnoreCase(searchKeyword));
            } else if ("boardContents".equals(searchType)) {
                where.and(board.boardContents.containsIgnoreCase(searchKeyword));
            } else {
                // 전체 검색 (제목+작성자+내용)
                where.and(
                        board.boardTitle.containsIgnoreCase(searchKeyword)
                                .or(employee.empName.containsIgnoreCase(searchKeyword))
                                .or(board.boardContents.containsIgnoreCase(searchKeyword))
                );
            }
        }

        // 전체 개수 조회 (검색 조건 적용)
        long total = Optional.ofNullable(
                queryFactory
                        .select(board.count())
                        .from(board)
                        .join(board.employee, employee)
                        .where(where.and(board.boardCategory.eq("중요").or(board.boardCategory.eq("공지")))) // 중요 및 공지 카테고리 필터링
                        .fetchOne()
        ).orElse(0L);


        // 데이터 조회 (페이징 적용)
        List<BoardDTO> results = queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.boardId.as("boardId"),
                        board.boardContents.as("boardContents"),
                        board.boardTitle.as("boardTitle"),
                        board.boardDate.as("boardDate"),
                        board.boardCategory.as("boardCategory"),
                        board.fileName.as("fileName"),
                        board.fileUrl.as("fileUrl"),
                        employee.empId.as("empId"),
                        employee.empName.as("empName")
                ))
                .from(board)
                .join(board.employee, employee)
                .where(board.boardCategory.eq("중요").or(board.boardCategory.eq("공지")))
                .orderBy(
                        board.boardCategory.desc(),
                        board.boardId.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    // 게시판 상세조회
    public BoardDTO findByIdBoard(String boardId) {
        QBoard board = QBoard.board;
        QEmployee employee = QEmployee.employee;

        BoardDTO boardDTO = queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.boardId.as("boardId"),
                        employee.empId.as("empId"),
                        employee.empName.as("empName"),
                        board.boardContents.as("boardContents"),
                        board.boardTitle.as("boardTitle"),
                        board.boardDate.as("boardDate"),
                        board.boardCategory.as("boardCategory"),
                        board.fileName.as("fileName"),
                        board.fileUrl.as("fileUrl")
                ))
                .from(board)
                .join(board.employee, employee)
                .where(board.boardId.eq(boardId)) // 특정 boardId로 조회
                .fetchOne();  // 하나의 결과만 반환

        return boardDTO;
    }

    // 게시글 등록
    @Transactional
    public BoardDTO BoardCreate(BoardDTO boardDTO, MultipartFile file) {
        try {
            // 1. 사원 조회
            Employee employee = employeeRepository.findById(boardDTO.getEmpId())
                    .orElseThrow(() -> new IllegalArgumentException("사원 없음"));

            // 2. ID 생성
            String maxBoardId = boardRepository.findMaxBoardId();
            String newBoardId = generateNewBoardId(maxBoardId);

            // 3. 파일 업로드 (기존 코드 유지)
            if (file != null && !file.isEmpty()) {
                String fileUrl = uploadFileToCloudinary(file);
                boardDTO.setFileUrl(fileUrl);
                boardDTO.setFileName(file.getOriginalFilename());
            }

            // 4. DTO → Entity 변환
            Board board = modelMapper.map(boardDTO, Board.class);
            board.setBoardId(newBoardId);
            board.setBoardDate(new Date());
            board.setEmployee(employee);

            // 5. 저장
            Board savedBoard = boardRepository.save(board);

            // 6. Entity → DTO 변환 후 반환
            return modelMapper.map(savedBoard, BoardDTO.class);

        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage());
        }
    }

    // auto increment 방식
    private String generateNewBoardId(String maxBoardId) {
        if (maxBoardId == null) return "BO001";

        String numericPart = maxBoardId.substring(2); // "BO123" → "123"
        int nextNumber = Integer.parseInt(numericPart) + 1;
        return String.format("BO%03d", nextNumber); // 3자리 숫자로 포맷팅
    }

    // 클라우드너리 업로드 메서드
    private String uploadFileToCloudinary(MultipartFile file) throws IOException {

        return cloudinaryService.uploadFile(file);
    }

    // 게시글수정
    // 게시판 수정
    @Transactional
    public void updateBoard(BoardDTO board) {

        Board board1 = boardRepository.findById(board.getBoardId()).orElseThrow(IllegalArgumentException::new);

        Board updateBoard = board1.toBuilder()
                .boardTitle(board.getBoardTitle())
                .boardContents(board.getBoardContents())
                .boardCategory(board.getBoardCategory())
                .build();

        boardRepository.save(updateBoard);
    }


    // 게시글 삭제
    @Transactional
    public void deleteBoard(String boardId) {

        boardRepository.deleteById(boardId);

        modelMapper.map(boardId, Board.class);
    }


}

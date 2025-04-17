package com.org.qualitycore.board.model.entity;

import com.org.qualitycore.work.model.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "NOTICE_BOARD")
@Builder(toBuilder = true)
public class Board {

    @Id
    @Column(name = "BOARD_ID")
    private String boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @Column(name = "BOARD_TITLE")
    private String boardTitle;

    @Column(name = "BOARD_CONTENTS")
    private String boardContents;

    @Column(name = "BOARD_DATE")
    private Date boardDate;

    @Column(name = "BOARD_CATEGORY")
    private String boardCategory;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_URL")
    private String fileUrl;


}

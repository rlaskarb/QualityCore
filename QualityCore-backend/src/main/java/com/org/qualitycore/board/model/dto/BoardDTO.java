package com.org.qualitycore.board.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BoardDTO {

    private String boardId;
    private String empId;
    private String boardTitle;
    private String empName;
    private String boardContents;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date boardDate = new Date();
    private String boardCategory;
    private String fileName;
    private String fileUrl;

}

package com.org.qualitycore.productionPerformance.model.service;

import com.org.qualitycore.productionPerformance.model.dto.ProductionPerformanceDTO;
import com.org.qualitycore.productionPerformance.model.repository.PackagingAndShipmentRepository;
import com.org.qualitycore.productionPerformance.model.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductionPerformanceService {

    private final PackagingAndShipmentRepository packagingAndShipmentRepository;
    private final WorkOrderRepository workOrderRepository;

    // 월별 생산실적 조회 (디버깅 추가)
    public List<ProductionPerformanceDTO> getMonthlyPerformance(YearMonth yearMonth, String productName) {
        System.out.println("=== 월별 생산실적 조회 시작 ===");
        System.out.println("요청 파라미터 - yearMonth: " + yearMonth + ", productName: " + productName);

        try {
            if (yearMonth == null) {
                throw new IllegalArgumentException("yearMonth는 null이 될 수 없습니다.");
            }

            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            System.out.println("조회 기간: " + startDate + " ~ " + endDate);

            System.out.println("리포지토리 메서드 호출 시작");
            List<ProductionPerformanceDTO> result = packagingAndShipmentRepository.findMonthlyPerformance(yearMonth, productName);
            System.out.println("리포지토리 메서드 호출 완료, 결과 크기: " + (result != null ? result.size() : "null"));

            // NULL 체크
            if (result == null) {
                System.out.println("리포지토리 결과가 null입니다.");
                result = new ArrayList<>();
            }

            // 결과 데이터 출력 (첫 5개만)
            if (!result.isEmpty()) {
                System.out.println("결과 데이터 샘플 (최대 5개):");
                result.stream().limit(5).forEach(System.out::println);
            } else {
                System.out.println("결과 데이터가 없습니다.");
            }

            return result;
        } catch (Exception e) {
            System.out.println("=== 월별 생산실적 조회 오류 ===");
            System.out.println("오류 타입: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 계획 대비 실적 조회 (디버깅 추가)
    public List<Map<String, Object>> getPlanVsActual(YearMonth yearMonth, String productName) {
        System.out.println("=== 계획 대비 실적 조회 시작 ===");
        System.out.println("요청 파라미터 - yearMonth: " + yearMonth + ", productName: " + productName);

        try {
            System.out.println("리포지토리 메서드 호출 시작");
            List<Map<String, Object>> result = packagingAndShipmentRepository.findPlanVsActual(yearMonth, productName);
            System.out.println("리포지토리 메서드 호출 완료, 결과 크기: " + (result != null ? result.size() : "null"));

            return result;
        } catch (Exception e) {
            System.out.println("=== 계획 대비 실적 조회 오류 ===");
            System.out.println("오류 타입: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 제품별 효율성 조회 (디버깅 추가)
    public List<Map<String, Object>> getProductEfficiency() {
        System.out.println("=== 제품별 효율성 조회 시작 ===");

        try {
            System.out.println("리포지토리 메서드 호출 시작");
            List<Map<String, Object>> result = packagingAndShipmentRepository.findProductEfficiency();
            System.out.println("리포지토리 메서드 호출 완료, 결과 크기: " + (result != null ? result.size() : "null"));

            return result;
        } catch (Exception e) {
            System.out.println("=== 제품별 효율성 조회 오류 ===");
            System.out.println("오류 타입: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Excel 보고서 생성
    public byte[] generateExcelReport(String reportType, String yearMonthStr, String productName) throws IOException {
        System.out.println("=== Excel 보고서 생성 시작 ===");
        System.out.println("요청 파라미터 - reportType: " + reportType + ", yearMonth: " + yearMonthStr + ", productName: " + productName);

        try {
            switch (reportType) {
                case "monthly":
                    return generateMonthlyReport(yearMonthStr, productName);
                case "plan-vs-actual":
                    return generatePlanVsActualReport(yearMonthStr, productName);
                case "efficiency":
                    return generateEfficiencyReport();
                default:
                    throw new IllegalArgumentException("지원하지 않는 보고서 유형: " + reportType);
            }
        } catch (Exception e) {
            System.out.println("=== Excel 보고서 생성 오류 ===");
            System.out.println("오류 타입: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 월별 생산실적 Excel 보고서
    private byte[] generateMonthlyReport(String yearMonthStr, String productName) throws IOException {
        System.out.println("=== 월별 생산실적 Excel 보고서 생성 ===");

        YearMonth yearMonth = yearMonthStr != null ? YearMonth.parse(yearMonthStr) : YearMonth.now();
        List<ProductionPerformanceDTO> data = getMonthlyPerformance(yearMonth, productName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("월별 생산실적");

        // 스타일 설정
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"연월", "제품명", "생산량", "양품수량", "품질률(%)"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 입력
        int rowNum = 1;
        for (ProductionPerformanceDTO row : data) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(row.getYearMonth());
            dataRow.createCell(1).setCellValue(row.getProductName());
            dataRow.createCell(2).setCellValue(row.getTotalQuantity());
            dataRow.createCell(3).setCellValue(row.getGoodQuantity());
            dataRow.createCell(4).setCellValue(row.getQualityRate());
        }

        // 열 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] excelBytes = outputStream.toByteArray();
        outputStream.close();
        workbook.close();

        System.out.println("Excel 보고서 생성 완료, 크기: " + excelBytes.length + " bytes");
        return excelBytes;
    }

    // 계획 대비 실적 Excel 보고서
    private byte[] generatePlanVsActualReport(String yearMonthStr, String productName) throws IOException {
        System.out.println("=== 계획 대비 실적 Excel 보고서 생성 ===");

        YearMonth yearMonth = yearMonthStr != null ? YearMonth.parse(yearMonthStr) : YearMonth.now();
        List<Map<String, Object>> data = getPlanVsActual(yearMonth, productName);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("계획 대비 실적");

        // 스타일 설정
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 달성률 100% 미만에 대한 스타일 (빨간색 글씨)
        CellStyle lowAchievementStyle = workbook.createCellStyle();
        Font redFont = workbook.createFont();
        redFont.setColor(IndexedColors.RED.getIndex());
        lowAchievementStyle.setFont(redFont);

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"연월", "제품명", "계획량", "실적량", "달성률(%)"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 입력
        int rowNum = 1;
        for (Map<String, Object> row : data) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(row.get("YEAR_MONTH").toString());
            dataRow.createCell(1).setCellValue(row.get("PRODUCT_NAME").toString());

            Cell plannedCell = dataRow.createCell(2);
            plannedCell.setCellValue(((Number) row.get("PLANNED_QUANTITY")).doubleValue());

            Cell actualCell = dataRow.createCell(3);
            actualCell.setCellValue(((Number) row.get("ACTUAL_QUANTITY")).doubleValue());

            Cell achievementCell = dataRow.createCell(4);
            double achievementRate = ((Number) row.get("ACHIEVEMENT_RATE")).doubleValue();
            achievementCell.setCellValue(achievementRate);

            // 달성률 100% 미만인 경우 스타일 적용
            if (achievementRate < 100.0) {
                achievementCell.setCellStyle(lowAchievementStyle);
            }
        }

        // 열 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] excelBytes = outputStream.toByteArray();
        outputStream.close();
        workbook.close();

        System.out.println("Excel 보고서 생성 완료, 크기: " + excelBytes.length + " bytes");
        return excelBytes;
    }

    // 제품별 효율성 Excel 보고서
    private byte[] generateEfficiencyReport() throws IOException {
        System.out.println("=== 제품별 효율성 Excel 보고서 생성 ===");

        List<Map<String, Object>> data = getProductEfficiency();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("제품별 생산 효율성");

        // 스타일 설정
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"제품명", "평균 생산시간(일)", "평균 배치 크기", "평균 품질률(%)"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 입력
        int rowNum = 1;
        for (Map<String, Object> row : data) {
            Row dataRow = sheet.createRow(rowNum++);
            dataRow.createCell(0).setCellValue(row.get("PRODUCT_NAME").toString());

            // 평균 생산시간 (분 -> 일)
            double timeMinutes = ((Number) row.get("AVG_PRODUCTION_TIME_MINUTES")).doubleValue();
            dataRow.createCell(1).setCellValue(timeMinutes / 60 / 24); // 분 -> 일 변환

            dataRow.createCell(2).setCellValue(((Number) row.get("AVG_BATCH_SIZE")).doubleValue());
            dataRow.createCell(3).setCellValue(((Number) row.get("AVG_QUALITY_RATE")).doubleValue());
        }

        // 열 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 헤더 정보 추가 (날짜, 작성자 등)
        Row infoRow1 = sheet.createRow(data.size() + 2);
        infoRow1.createCell(0).setCellValue("작성일자: " + java.time.LocalDate.now().toString());

        Row infoRow2 = sheet.createRow(data.size() + 3);
        infoRow2.createCell(0).setCellValue("이 보고서는 자동 생성되었습니다.");

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] excelBytes = outputStream.toByteArray();
        outputStream.close();
        workbook.close();

        System.out.println("Excel 보고서 생성 완료, 크기: " + excelBytes.length + " bytes");
        return excelBytes;
    }
}
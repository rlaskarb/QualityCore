package com.org.qualitycore.routing.model.service;

import com.org.qualitycore.routing.model.dto.ProcessTrackingDTO;
import com.org.qualitycore.routing.model.dto.WortVolumeDTO;
import com.org.qualitycore.routing.model.entity.*;
import com.org.qualitycore.routing.model.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutingService {
    private final RoutingProcessTrackingRepository routingProcessTrackingRepository;
    private final RoutingMaterialGrindingRepository routingMaterialGrindingRepository;
    private final RoutingMashingProcessRepositoryRouting routingMashingProcessRepositoryRouting;
    private final RoutingFiltrationProcessRepository routingFiltrationProcessRepository;
    private final RoutingBoilingProcessRepository routingBoilingProcessRepository;
    private final RoutingCoolingProcessRepository routingCoolingProcessRepository;
    private final RoutingFermentationDetailsRepository routingFermentationDetailsRepository;
    private final RoutingMaturationDetailsRepository routingMaturationDetailsRepository;
    private final RoutingPostMaturationFiltrationRepository routingPostMaturationFiltrationRepository;
    private final RoutingCarbonationProcessRepository routingCarbonationProcessRepository;
    private final RoutingPackagingAndShipmentRepository routingPackagingAndShipmentRepository;

    private final ModelMapper modelMapper;


    public List<ProcessTrackingDTO> findProcessTracking(String lotNo, String processStatus) {
        List<ProcessTracking> trackings;

        if (StringUtils.hasText(lotNo) && StringUtils.hasText(processStatus)) {
            trackings = routingProcessTrackingRepository.findByLotNoContainingAndProcessStatus(lotNo, processStatus);
        } else if (StringUtils.hasText(lotNo)) {
            trackings = routingProcessTrackingRepository.findByLotNoContaining(lotNo);
        } else if (StringUtils.hasText(processStatus)) {
            trackings = routingProcessTrackingRepository.findByProcessStatus(processStatus);
        } else {
            trackings = routingProcessTrackingRepository.findAll();
        }

        return trackings.stream()
                .map(tracking -> {
                    ProcessTrackingDTO dto = new ProcessTrackingDTO();
                    dto.setLotNo(tracking.getLotNo());
                    dto.setProcessName(tracking.getProcessName());
                    dto.setProcessStatus(tracking.getProcessStatus());

                    // 각 공정별 시간 정보 조회 로직
                    switch(tracking.getProcessName()) {
                        case "분쇄":
                            MaterialGrinding grinding =
                                    routingMaterialGrindingRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (grinding != null) {
                                dto.setStartTime(grinding.getStartTime());
                                dto.setExpectedEndTime(grinding.getExpectedEndTime());
                            }
                            break;
                        case "당화":
                            MashingProcess mashing =
                                    routingMashingProcessRepositoryRouting.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (mashing != null) {
                                dto.setStartTime(mashing.getStartTime());
                                dto.setExpectedEndTime(mashing.getExpectedEndTime());
                            }
                            break;
                        case "여과":
                            FiltrationProcess filtration =
                                    routingFiltrationProcessRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (filtration != null) {
                                dto.setStartTime(filtration.getStartTime());
                                dto.setExpectedEndTime(filtration.getExpectedEndTime());
                            }
                            break;
                        case "끓임":
                            BoilingProcess boiling =
                                    routingBoilingProcessRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (boiling != null) {
                                dto.setStartTime(boiling.getStartTime());
                                dto.setExpectedEndTime(boiling.getExpectedEndTime());
                            }
                            break;
                        case "냉각":
                            CoolingProcess cooling =
                                    routingCoolingProcessRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (cooling != null) {
                                dto.setStartTime(cooling.getStartTime());
                                dto.setExpectedEndTime(cooling.getExpectedEndTime());
                            }
                            break;
                        case "발효":
                            FermentationDetails fermentation =
                                    routingFermentationDetailsRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (fermentation != null) {
                                dto.setStartTime(fermentation.getStartTime());
                                dto.setExpectedEndTime(fermentation.getExpectedEndTime());
                            }
                            break;
                        case "숙성":
                            MaturationDetails maturation =
                                    routingMaturationDetailsRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (maturation != null) {
                                dto.setStartTime(maturation.getStartTime());
                                dto.setExpectedEndTime(maturation.getExpectedEndTime());
                            }
                            break;
                        case "숙성후여과":
                            PostMaturationFiltration postFiltration =
                                    routingPostMaturationFiltrationRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (postFiltration != null) {
                                dto.setStartTime(postFiltration.getStartTime());
                                dto.setExpectedEndTime(postFiltration.getExpectedEndTime());
                            }
                            break;
                        case "탄산 조정":
                            CarbonationProcess carbonation =
                                    routingCarbonationProcessRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (carbonation != null) {
                                dto.setStartTime(carbonation.getStartTime());
                                dto.setExpectedEndTime(carbonation.getExpectedEndTime());
                            }
                            break;
                        case "패키징":
                            PackagingAndShipment packaging =
                                    routingPackagingAndShipmentRepository.findByLotNo(tracking.getLotNo())
                                            .orElse(null);

                            if (packaging != null) {
                                // 패키징의 경우 시작/종료 시간이 없을 수 있음
                                // 필요에 따라 다른 로직 추가 가능
                            }
                            break;
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 각 공정별 시작/종료 시간 추가 조회 메서드
    private void enrichProcessTrackingDTO(ProcessTrackingDTO dto) {
        switch (dto.getProcessName()) {
            case "분쇄":
                routingMaterialGrindingRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "당화":
                routingMashingProcessRepositoryRouting.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "여과":
                routingFiltrationProcessRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "끓임":
                routingBoilingProcessRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "냉각":
                routingCoolingProcessRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "발효":
                routingFermentationDetailsRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "숙성":
                routingMaturationDetailsRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "숙성후여과":
                routingPostMaturationFiltrationRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "탄산조정":
                routingCarbonationProcessRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    dto.setStartTime(process.getStartTime());
                    dto.setExpectedEndTime(process.getExpectedEndTime());
                });
                break;
            case "패키징":
                routingPackagingAndShipmentRepository.findByLotNo(dto.getLotNo()).ifPresent(process -> {
                    // 패키징의 경우 시작/종료 시간이 없을 수 있음
                });
                break;
        }
    }

    // 추가 메서드들 (필요에 따라)
    public ProcessTrackingDTO findProcessTrackingByLotNo(String lotNo) {
        ProcessTracking tracking = routingProcessTrackingRepository.findByLotNo(lotNo)
                .orElseThrow(() -> new RuntimeException("해당 LOT 번호의 공정 정보를 찾을 수 없습니다."));

        ProcessTrackingDTO dto = modelMapper.map(tracking, ProcessTrackingDTO.class);
        enrichProcessTrackingDTO(dto);
        return dto;
    }
    public List<WortVolumeDTO> findWortVolumes(String lotNo) {
        List<WortVolumeDTO> result = new ArrayList<>();

        // 1. 여과 공정 데이터 조회
        List<FiltrationProcess> filtrationProcesses;
        if (StringUtils.hasText(lotNo)) {
            filtrationProcesses = routingFiltrationProcessRepository.findByLotNoContaining(lotNo);
        } else {
            filtrationProcesses = routingFiltrationProcessRepository.findAll();
        }

        // 여과 공정 데이터를 DTO로 변환
        for (FiltrationProcess process : filtrationProcesses) {
            WortVolumeDTO dto = new WortVolumeDTO();
            dto.setLotNo(process.getLotNo());
            dto.setProcessName("여과");
            // 여과는 초기 워트량이 없으므로 null 또는 0으로 설정
            dto.setInitialWortVolume(0.0);
            dto.setCurrentWortVolume(process.getRecoveredWortVolume());
            dto.setLossVolume(process.getLossVolume());
            dto.setRecordTime(process.getActualEndTime() != null ?
                    process.getActualEndTime() : process.getStartTime());
            dto.setProcessStatus(process.getProcessStatus());

            // 제품 정보 추가 (필요시)
            result.add(dto);
        }

        // 2. 끓임 공정 데이터 조회
        List<BoilingProcess> boilingProcesses;
        if (StringUtils.hasText(lotNo)) {
            boilingProcesses = routingBoilingProcessRepository.findByLotNoContaining(lotNo);
        } else {
            boilingProcesses = routingBoilingProcessRepository.findAll();
        }

        // 끓임 공정 데이터를 DTO로 변환
        for (BoilingProcess process : boilingProcesses) {
            WortVolumeDTO dto = new WortVolumeDTO();
            dto.setLotNo(process.getLotNo());
            dto.setProcessName("끓임");
            dto.setInitialWortVolume(process.getInitialWortVolume());
            dto.setCurrentWortVolume(process.getPostBoilWortVolume());
            dto.setLossVolume(process.getBoilLossVolume());
            dto.setRecordTime(process.getActualEndTime() != null ?
                    process.getActualEndTime() : process.getStartTime());
            dto.setProcessStatus(process.getProcessStatus());
            dto.setProcessStatus(process.getProcessStatus());

            // 효율 계산 (있는 경우)
            if (process.getInitialWortVolume() != null && process.getInitialWortVolume() > 0
                    && process.getPostBoilWortVolume() != null) {
                double efficiency = (process.getPostBoilWortVolume() / process.getInitialWortVolume()) * 100;
                dto.setTotalEfficiency(Math.round(efficiency * 100.0) / 100.0); // 소수점 2자리로 반올림
            }

            result.add(dto);
        }

        return result;
    }
}
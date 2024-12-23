package com.example.clinic.analysis.controller;

import com.example.clinic.analysis.dto.AnalysisCreationDto;
import com.example.clinic.analysis.dto.AnalysisDto;
import com.example.clinic.analysis.entity.Analysis;
import com.example.clinic.analysis.mapper.AnalysisMapper;
import com.example.clinic.analysis.model.PageArgument;
import com.example.clinic.analysis.service.AnalysisService;
import com.example.clinic.analysis.util.HeaderUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final AnalysisMapper analysisMapper;

    @PostMapping
    public ResponseEntity<AnalysisCreationDto> createAnalysis(@Valid @RequestBody AnalysisCreationDto analysisDto,
                                                              @RequestParam Long patientId) {

        Analysis analysis = analysisService.createAnalysis(analysisDto, patientId);

        return ResponseEntity.created(URI.create("/api/analyses/" + analysis.getId()))
                .body(analysisDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnalysisCreationDto> updateAnalysis(@PathVariable Long id, @Valid @RequestBody AnalysisCreationDto analysisDto) {
        analysisService.updateAnalysis(id, analysisDto);
        return ResponseEntity.ok(analysisDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnalysis(@PathVariable Long id) {
        analysisService.deleteAnalysis(id);
        return ResponseEntity.ok("Analysis with id " + id + " successfully deleted.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisDto> getAnalysisById(@PathVariable Long id) {
        Analysis analysis = analysisService.getAnalysisById(id);
        AnalysisDto analysisDto = analysisMapper.entityToAnalysisDto(analysis);
        return ResponseEntity.ok(analysisDto);
    }

    @GetMapping
    public ResponseEntity<List<AnalysisDto>> getAnalyses(
            PageArgument page
    ) {
        Page<Analysis> analysisPage = analysisService.getAnalyses(page.getPageRequest());
        List<AnalysisDto> analysisDtos = analysisPage.getContent().stream()
                .map(analysisMapper::entityToAnalysisDto)
                .collect(Collectors.toList());

        HttpHeaders headers = HeaderUtils.createPaginationHeaders(analysisPage);

        return ResponseEntity.ok().headers(headers).body(analysisDtos);
    }
}

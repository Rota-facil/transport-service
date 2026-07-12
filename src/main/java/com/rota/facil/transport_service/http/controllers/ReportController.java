package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.ReportService;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = "/student-absences", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> studentAbsences(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return pdf(
                reportService.generateStudentAbsences(currentUser.prefectureId(), startDate, endDate),
                "faltas-alunos-" + startDate + "-a-" + endDate + ".pdf"
        );
    }

    @GetMapping(value = "/cancelled-trips", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> cancelledTrips(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return pdf(
                reportService.generateCancelledTrips(currentUser.prefectureId(), startDate, endDate),
                "viagens-canceladas-" + startDate + "-a-" + endDate + ".pdf"
        );
    }

    private ResponseEntity<byte[]> pdf(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}

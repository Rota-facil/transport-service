package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.TripStatusAlreadyRegisteredException;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.entities.TripUserEntity;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import com.rota.facil.transport_service.persistence.repositories.TripUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final PDFont REGULAR = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private final TripUserRepository tripUserRepository;
    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public byte[] generateStudentAbsences(UUID prefectureId, LocalDate startDate, LocalDate endDate) {
        validatePeriod(startDate, endDate);
        List<TripUserEntity> absences = tripUserRepository.findAbsencesForReport(prefectureId, startDate, endDate);

        return createPdf(
                "Relatório de faltas de alunos",
                startDate,
                endDate,
                absences.size(),
                absences.stream().map(this::formatAbsence).toList()
        );
    }

    @Transactional(readOnly = true)
    public byte[] generateCancelledTrips(UUID prefectureId, LocalDate startDate, LocalDate endDate) {
        validatePeriod(startDate, endDate);
        List<TripEntity> cancelledTrips = tripRepository.findCancelledForReport(prefectureId, startDate, endDate);

        return createPdf(
                "Relatório de viagens canceladas",
                startDate,
                endDate,
                cancelledTrips.size(),
                cancelledTrips.stream().map(this::formatCancellation).toList()
        );
    }

    private String formatAbsence(TripUserEntity tripUser) {
        return String.format(
                "%s | %s | Rota: %s | Instituição: %s | Ponto: %s | Data: %s",
                safe(tripUser.getUser().getName()),
                safe(tripUser.getUser().getEmail()),
                safe(tripUser.getTrip().getRoute().getName()),
                safe(tripUser.getInstitution().getName()),
                safe(tripUser.getBoardPoint().getName()),
                tripUser.getTrip().getCreatedAt().format(DATE_FORMAT)
        );
    }

    private String formatCancellation(TripEntity trip) {
        String driver = trip.getBus().getDriver() != null ? trip.getBus().getDriver().getName() : "Sem motorista";
        return String.format(
                "%s | Rota: %s | Motorista: %s | Motivo: %s | Data: %s",
                safe(trip.getName()),
                safe(trip.getRoute().getName()),
                safe(driver),
                safe(trip.getReasonOfCancellation()),
                trip.getCreatedAt().format(DATE_FORMAT)
        );
    }

    private byte[] createPdf(String title, LocalDate startDate, LocalDate endDate, int total, List<String> rows) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(document);
            writer.line(title, BOLD, 18, 24);
            writer.line("Período: " + startDate.format(DATE_FORMAT) + " a " + endDate.format(DATE_FORMAT), REGULAR, 11, 16);
            writer.line("Total de registros: " + total, BOLD, 11, 24);

            if (rows.isEmpty()) {
                writer.line("Nenhum registro encontrado para o período informado.", REGULAR, 11, 16);
            } else {
                for (int index = 0; index < rows.size(); index++) {
                    writer.line((index + 1) + ". " + rows.get(index), REGULAR, 9, 15);
                }
            }

            document.save(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível gerar o relatório PDF", exception);
        }
    }

    private void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new TripStatusAlreadyRegisteredException("A data inicial não pode ser posterior à data final");
        }
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) return "Não informado";
        return value.replaceAll("[\r\n\t]+", " ").trim();
    }

    private static final class PdfWriter {
        private static final float MARGIN = 42;
        private static final float MIN_Y = 48;
        private final PDDocument document;
        private PDPage page;
        private float y;

        private PdfWriter(PDDocument document) {
            this.document = document;
            newPage();
        }

        private void line(String text, PDFont font, float fontSize, float spacing) throws IOException {
            if (y < MIN_Y) newPage();
            String remaining = text;
            int maxCharacters = Math.max(45, (int) (100 * (10 / fontSize)));
            while (!remaining.isEmpty()) {
                int end = Math.min(maxCharacters, remaining.length());
                if (end < remaining.length()) {
                    int breakAt = remaining.lastIndexOf(" ", end);
                    if (breakAt > 20) end = breakAt;
                }
                String chunk = remaining.substring(0, end).trim();
                writeChunk(chunk, font, fontSize);
                remaining = remaining.substring(end).trim();
                if (!remaining.isEmpty()) {
                    y -= spacing;
                    if (y < MIN_Y) newPage();
                }
            }
            y -= spacing;
        }

        private void writeChunk(String text, PDFont font, float fontSize) throws IOException {
            try (PDPageContentStream content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                content.beginText();
                content.setFont(font, fontSize);
                content.newLineAtOffset(MARGIN, y);
                content.showText(text);
                content.endText();
            }
        }

        private void newPage() {
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            y = PDRectangle.A4.getHeight() - MARGIN;
        }
    }
}

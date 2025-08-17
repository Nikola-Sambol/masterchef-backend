package com.mev.recipeapp.controllers;

import com.mev.recipeapp.dtos.ComponentDTO;
import com.mev.recipeapp.dtos.RecipeDTO;
import com.mev.recipeapp.dtos.response.UserInfoResponse;
import com.mev.recipeapp.service.RecipeService;
import com.mev.recipeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class RecipePdfController {

    private final RecipeService recipeService;
    private final UserService userService;

    @GetMapping("/public/{recipeId}")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long recipeId) {
        RecipeDTO recipe = recipeService.getRecipeWithDetails(recipeId);

        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // 🎨 Paleta boja
            BaseColor primaryColor = new BaseColor(52, 152, 219); // plava
            BaseColor secondaryColor = new BaseColor(64,86,161); // tamnija plava
            BaseColor lightGray = new BaseColor(245, 245, 245);

            // Fontovi
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, secondaryColor);
            Font regularFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Font italicFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.DARK_GRAY);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            // 📷 Slika recepta ako postoji
            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(recipe.getImage());
                    Image recipeImage = Image.getInstance(imageBytes);
                    recipeImage.scaleToFit(500, 300);
                    recipeImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(recipeImage);
                    document.add(new Paragraph("\n")); // razmak ispod slike
                } catch (Exception e) {
                    log.warn("Slika recepta nije učitana: {}", e.getMessage());
                }
            }

            // 📝 Naslov
            Paragraph title = new Paragraph("🍽 " + recipe.getRecipeName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // 📄 Opći podaci
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(15f);
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            infoTable.addCell(new Phrase("Datum objave recepta:", subTitleFont));
            infoTable.addCell(new Phrase(formatDate(recipe.getCreationDate()), regularFont));

            infoTable.addCell(new Phrase("Vrijeme pripreme:", subTitleFont));
            infoTable.addCell(new Phrase(recipe.getPreparationTime(), regularFont));

            if (recipe.getCategory() != null) {
                infoTable.addCell(new Phrase("Kategorija:", subTitleFont));
                infoTable.addCell(new Phrase(recipe.getCategory().getCategoryName(), regularFont));
            }

            if (recipe.getUser() != null) {
                infoTable.addCell(new Phrase("Autor:", subTitleFont));
                infoTable.addCell(new Phrase(
                        recipe.getUser().getName() + " " + recipe.getUser().getSurname()));
            }

            document.add(infoTable);

            // 🧂 Sastojci
            Paragraph sastojciTitle = new Paragraph("🧂 Komponente", subTitleFont);
            sastojciTitle.setSpacingBefore(15f);
            sastojciTitle.setSpacingAfter(10f);
            document.add(sastojciTitle);

            List<ComponentDTO> components = recipe.getComponents();
            if (components != null && !components.isEmpty()) {
                boolean hasImages = components.stream().anyMatch(c -> c.getImagePath() != null && !c.getImagePath().isEmpty());

                int columnCount = hasImages ? 4 : 3;
                PdfPTable table = new PdfPTable(columnCount);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(20f);

                if (hasImages) {
                    table.setWidths(new float[]{2f, 2f, 3f, 2f});
                } else {
                    table.setWidths(new float[]{2f, 2f, 3f});
                }

                // Header
                Stream.of("Naziv komponente", "Sastojci", "Upute")
                        .forEach(col -> {
                            PdfPCell cell = new PdfPCell(new Phrase(col, tableHeaderFont));
                            cell.setBackgroundColor(primaryColor);
                            cell.setPadding(8f);
                            table.addCell(cell);
                        });

                if (hasImages) {
                    PdfPCell imgHeader = new PdfPCell(new Phrase("Slika komponente", tableHeaderFont));
                    imgHeader.setBackgroundColor(primaryColor);
                    imgHeader.setPadding(8f);
                    table.addCell(imgHeader);
                }

                // Redovi
                for (ComponentDTO component : components) {
                    PdfPCell nameCell = new PdfPCell(new Phrase(component.getComponentName(), regularFont));
                    PdfPCell qtyCell = new PdfPCell(new Phrase(component.getIngredients(), regularFont));
                    PdfPCell instCell = new PdfPCell(new Phrase(component.getInstruction(), regularFont));

                    Stream.of(nameCell, qtyCell, instCell).forEach(c -> {
                        c.setBackgroundColor(lightGray);
                        c.setPadding(6f);
                    });

                    table.addCell(nameCell);
                    table.addCell(qtyCell);
                    table.addCell(instCell);

                    if (hasImages) {
                        if (component.getImagePath() != null && !component.getImagePath().isEmpty()) {
                            try {
                                byte[] compImgBytes = Base64.getDecoder().decode(component.getImagePath());
                                Image img = Image.getInstance(compImgBytes);
                                img.scaleToFit(60, 60);
                                PdfPCell imgCell = new PdfPCell(img, true);
                                imgCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                imgCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                imgCell.setPadding(4f);
                                table.addCell(imgCell);
                            } catch (Exception e) {
                                PdfPCell emptyCell = new PdfPCell(new Phrase("-", regularFont));
                                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                table.addCell(emptyCell);
                            }
                        } else {
                            PdfPCell emptyCell = new PdfPCell(new Phrase("-", regularFont));
                            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                            table.addCell(emptyCell);
                        }
                    }

                }

                document.add(table);
            } else {
                document.add(new Paragraph("Nema sastojaka.", italicFont));
            }


            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeRecipeName = sanitizeFileName(recipe.getRecipeName());
            String fileName = safeRecipeName + "_" + timestamp + ".pdf";

            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error while generating PDF: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<byte[]> generateUserPdf(@PathVariable Long userId) {
        UserInfoResponse user = userService.getUserById(userId);
        List<RecipeDTO> recipes = recipeService.getRecipesByUserId(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseColor primaryColor = new BaseColor(52, 152, 219);
            BaseColor secondaryColor = new BaseColor(64, 86, 161);
            BaseColor lightGray = new BaseColor(245, 245, 245);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
            Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, secondaryColor);
            Font regularFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

            Paragraph title = new Paragraph("👤 Korisnički podaci", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable userTable = new PdfPTable(2);
            userTable.setWidthPercentage(100);
            userTable.setSpacingAfter(15f);
            userTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            userTable.addCell(new Phrase("ID:", subTitleFont));
            userTable.addCell(new Phrase(user.getId().toString(), regularFont));

            userTable.addCell(new Phrase("Ime:", subTitleFont));
            userTable.addCell(new Phrase(user.getName(), regularFont));

            userTable.addCell(new Phrase("Prezime:", subTitleFont));
            userTable.addCell(new Phrase(user.getSurname(), regularFont));

            userTable.addCell(new Phrase("Email:", subTitleFont));
            userTable.addCell(new Phrase(user.getEmail(), regularFont));

            userTable.addCell(new Phrase("Uloge:", subTitleFont));
            userTable.addCell(new Phrase(String.join(", ", user.getRole()), regularFont));

            userTable.addCell(new Phrase("Status:", subTitleFont));
            userTable.addCell(new Phrase(user.isEnabled() ? "Aktivan" : "Neaktivan", regularFont));

            userTable.addCell(new Phrase("Datum kreiranja:", subTitleFont));
            userTable.addCell(new Phrase(formatDate(user.getCreationDate()), regularFont));

            document.add(userTable);

            Paragraph recipesTitle = new Paragraph("📜 Recepti korisnika", subTitleFont);
            recipesTitle.setSpacingBefore(15f);
            recipesTitle.setSpacingAfter(10f);
            document.add(recipesTitle);

            if (recipes != null && !recipes.isEmpty()) {
                PdfPTable recipeTable = new PdfPTable(2);
                recipeTable.setWidthPercentage(100);
                recipeTable.setSpacingBefore(10f);
                recipeTable.setSpacingAfter(20f);
                recipeTable.setWidths(new float[]{3f, 2f});

                Stream.of("Naziv recepta", "Datum kreiranja")
                        .forEach(col -> {
                            PdfPCell cell = new PdfPCell(new Phrase(col, tableHeaderFont));
                            cell.setBackgroundColor(primaryColor);
                            cell.setPadding(8f);
                            recipeTable.addCell(cell);
                        });

                for (RecipeDTO recipe : recipes) {
                    PdfPCell nameCell = new PdfPCell(new Phrase(recipe.getRecipeName(), regularFont));
                    PdfPCell dateCell = new PdfPCell(new Phrase(formatDate(recipe.getCreationDate()), regularFont));

                    Stream.of(nameCell, dateCell).forEach(c -> {
                        c.setBackgroundColor(lightGray);
                        c.setPadding(6f);
                    });

                    recipeTable.addCell(nameCell);
                    recipeTable.addCell(dateCell);
                }

                document.add(recipeTable);
            } else {
                document.add(new Paragraph("Korisnik nema recepata.", regularFont));
            }

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String safeUserName = sanitizeFileName(user.getName() + "_" + user.getSurname());
            String fileName = safeUserName + "_" + timestamp + ".pdf";

            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error while generating user PDF: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<byte[]> generateAllUsersPdf() {
        List<UserInfoResponse> users = userService.getAllUsers();

        if (users == null || users.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate(), 40, 40, 50, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            BaseColor primaryColor = new BaseColor(52, 152, 219);
            BaseColor lightGray = new BaseColor(245, 245, 245);

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, primaryColor);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            Font regularFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);

            Paragraph title = new Paragraph("📋 Popis svih korisnika", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2.5f, 2.5f, 4f, 3f, 2f, 3f});
            table.setSpacingBefore(10f);

            Stream.of("ID", "Ime", "Prezime", "Email", "Uloge", "Status", "Datum kreiranja")
                    .forEach(col -> {
                        PdfPCell cell = new PdfPCell(new Phrase(col, tableHeaderFont));
                        cell.setBackgroundColor(primaryColor);
                        cell.setPadding(8f);
                        table.addCell(cell);
                    });

            for (UserInfoResponse user : users) {
                PdfPCell idCell = new PdfPCell(new Phrase(user.getId().toString(), regularFont));
                PdfPCell nameCell = new PdfPCell(new Phrase(user.getName(), regularFont));
                PdfPCell surnameCell = new PdfPCell(new Phrase(user.getSurname(), regularFont));
                PdfPCell emailCell = new PdfPCell(new Phrase(user.getEmail(), regularFont));
                PdfPCell rolesCell = new PdfPCell(new Phrase(String.join(", ", user.getRole()), regularFont));
                PdfPCell statusCell = new PdfPCell(new Phrase(user.isEnabled() ? "Aktivan" : "Neaktivan", regularFont));
                PdfPCell dateCell = new PdfPCell(new Phrase(formatDate(user.getCreationDate()), regularFont));

                Stream.of(idCell, nameCell, surnameCell, emailCell, rolesCell, statusCell, dateCell)
                        .forEach(c -> {
                            c.setBackgroundColor(lightGray);
                            c.setPadding(6f);
                        });

                table.addCell(idCell);
                table.addCell(nameCell);
                table.addCell(surnameCell);
                table.addCell(emailCell);
                table.addCell(rolesCell);
                table.addCell(statusCell);
                table.addCell(dateCell);
            }

            document.add(table);
            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "korisnici_" + timestamp + ".pdf";

            headers.setContentDispositionFormData("attachment", fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error while generating all users PDF: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    private String formatDate(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr);
                return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (Exception ex) {
                return dateStr;
            }
        }
    }

    private String sanitizeFileName(String input) {
        return input.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

}

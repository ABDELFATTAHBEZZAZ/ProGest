package com.gestion.service;

import com.gestion.entity.Devis;
import com.gestion.entity.Facture;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfService {

        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185);
        private static final DeviceRgb HEADER_BG = new DeviceRgb(52, 73, 94);

        public byte[] generateDevisPdf(Devis devis) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf, PageSize.A4);
                        document.setMargins(40, 40, 40, 40);

                        // Header
                        addHeader(document, "DEVIS", devis.getNumero());

                        // Client info
                        addClientInfo(document, devis.getClient().getNom(),
                                        devis.getClient().getEmail(),
                                        devis.getClient().getTelephone());

                        // Dates
                        document.add(new Paragraph("Date: " + devis.getDateCreation().format(DATE_FORMAT))
                                        .setMarginTop(20));
                        document.add(new Paragraph("Validité: " + devis.getDateValidite().format(DATE_FORMAT)));

                        // Products table
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 1, 2, 2, 2 }))
                                        .setWidth(UnitValue.createPercentValue(100))
                                        .setMarginTop(20);

                        // Table header
                        addTableHeader(table, "Produit", "Qté", "Prix U.", "TVA", "Total");

                        // Table content
                        devis.getLignes().forEach(ligne -> {
                                table.addCell(new Cell().add(new Paragraph(ligne.getProduit().getNom())));
                                table.addCell(new Cell().add(new Paragraph(String.valueOf(ligne.getQuantite())))
                                                .setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(ligne.getPrixUnitaire() + " MAD"))
                                                .setTextAlignment(TextAlignment.RIGHT));
                                table.addCell(new Cell().add(new Paragraph(ligne.getTva() + "%"))
                                                .setTextAlignment(TextAlignment.CENTER));
                                table.addCell(new Cell().add(new Paragraph(ligne.getTotalLigne() + " MAD"))
                                                .setTextAlignment(TextAlignment.RIGHT));
                        });

                        document.add(table);

                        // Totals
                        addTotals(document, devis.getTotalHT().toString(),
                                        devis.getTotalTVA().toString(),
                                        devis.getTotalTTC().toString());

                        // Notes
                        if (devis.getNotes() != null && !devis.getNotes().isEmpty()) {
                                document.add(new Paragraph("Notes:").setBold().setMarginTop(20));
                                document.add(new Paragraph(devis.getNotes()));
                        }

                        document.close();
                        return baos.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("Erreur génération PDF devis", e);
                }
        }

        public byte[] generateFacturePdf(Facture facture) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        PdfWriter writer = new PdfWriter(baos);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf, PageSize.A4);
                        document.setMargins(40, 40, 40, 40);

                        // Header
                        addHeader(document, "FACTURE", facture.getNumero());

                        // Client info
                        addClientInfo(document, facture.getClient().getNom(),
                                        facture.getClient().getEmail(),
                                        facture.getClient().getTelephone());

                        // Dates
                        document.add(new Paragraph("Date: " + facture.getDateFacture().format(DATE_FORMAT))
                                        .setMarginTop(20));
                        document.add(new Paragraph("Échéance: " + facture.getDateEcheance().format(DATE_FORMAT)));
                        document.add(new Paragraph("Statut: " + facture.getStatut().name())
                                        .setBold());

                        // If from devis, show reference
                        if (facture.getDevis() != null) {
                                document.add(new Paragraph("Référence devis: " + facture.getDevis().getNumero())
                                                .setMarginTop(10));
                        }

                        // Amounts table
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 3, 2 }))
                                        .setWidth(UnitValue.createPercentValue(50))
                                        .setMarginTop(30);

                        table.addCell(new Cell().add(new Paragraph("Montant HT")).setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE));
                        table.addCell(new Cell().add(new Paragraph(facture.getMontantHT() + " MAD"))
                                        .setTextAlignment(TextAlignment.RIGHT));

                        table.addCell(new Cell().add(new Paragraph("TVA")).setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE));
                        table.addCell(new Cell().add(new Paragraph(facture.getMontantTVA() + " MAD"))
                                        .setTextAlignment(TextAlignment.RIGHT));

                        table.addCell(new Cell().add(new Paragraph("Montant TTC")).setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE).setBold());
                        table.addCell(new Cell().add(new Paragraph(facture.getMontantTTC() + " MAD"))
                                        .setTextAlignment(TextAlignment.RIGHT).setBold());

                        table.addCell(new Cell().add(new Paragraph("Montant payé")).setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE));
                        table.addCell(new Cell().add(new Paragraph(facture.getMontantPaye() + " MAD"))
                                        .setTextAlignment(TextAlignment.RIGHT));

                        table.addCell(new Cell().add(new Paragraph("Reste à payer")).setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE).setBold());
                        table.addCell(new Cell().add(new Paragraph(facture.getMontantRestant() + " MAD"))
                                        .setTextAlignment(TextAlignment.RIGHT).setBold());

                        document.add(table);

                        // Payment method
                        if (facture.getModePaiement() != null) {
                                document.add(new Paragraph("Mode de paiement: " + facture.getModePaiement().name())
                                                .setMarginTop(20));
                        }

                        // Notes
                        if (facture.getNotes() != null && !facture.getNotes().isEmpty()) {
                                document.add(new Paragraph("Notes:").setBold().setMarginTop(20));
                                document.add(new Paragraph(facture.getNotes()));
                        }

                        document.close();
                        return baos.toByteArray();
                } catch (Exception e) {
                        throw new RuntimeException("Erreur génération PDF facture", e);
                }
        }

        private void addHeader(Document document, String type, String numero) {
                Paragraph header = new Paragraph(type)
                                .setFontSize(24)
                                .setBold()
                                .setFontColor(PRIMARY_COLOR);
                document.add(header);

                document.add(new Paragraph("N° " + numero)
                                .setFontSize(14)
                                .setFontColor(ColorConstants.GRAY));
        }

        private void addClientInfo(Document document, String nom, String email, String telephone) {
                Div clientDiv = new Div()
                                .setBackgroundColor(new DeviceRgb(236, 240, 241))
                                .setPadding(15)
                                .setMarginTop(20);

                clientDiv.add(new Paragraph("Client").setBold());
                clientDiv.add(new Paragraph(nom));
                if (email != null)
                        clientDiv.add(new Paragraph(email));
                if (telephone != null)
                        clientDiv.add(new Paragraph(telephone));

                document.add(clientDiv);
        }

        private void addTableHeader(Table table, String... headers) {
                for (String header : headers) {
                        table.addHeaderCell(new Cell()
                                        .add(new Paragraph(header))
                                        .setBackgroundColor(HEADER_BG)
                                        .setFontColor(ColorConstants.WHITE)
                                        .setTextAlignment(TextAlignment.CENTER));
                }
        }

        private void addTotals(Document document, String ht, String tva, String ttc) {
                Table totals = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }))
                                .setWidth(UnitValue.createPercentValue(40))
                                .setMarginTop(20)
                                .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

                totals.addCell(new Cell().add(new Paragraph("Total HT")).setBorder(null));
                totals.addCell(new Cell().add(new Paragraph(ht + " MAD"))
                                .setTextAlignment(TextAlignment.RIGHT).setBorder(null));

                totals.addCell(new Cell().add(new Paragraph("TVA")).setBorder(null));
                totals.addCell(new Cell().add(new Paragraph(tva + " MAD"))
                                .setTextAlignment(TextAlignment.RIGHT).setBorder(null));

                totals.addCell(new Cell().add(new Paragraph("Total TTC").setBold())
                                .setBackgroundColor(PRIMARY_COLOR).setFontColor(ColorConstants.WHITE));
                totals.addCell(new Cell().add(new Paragraph(ttc + " MAD").setBold())
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setBackgroundColor(PRIMARY_COLOR).setFontColor(ColorConstants.WHITE));

                document.add(totals);
        }
}

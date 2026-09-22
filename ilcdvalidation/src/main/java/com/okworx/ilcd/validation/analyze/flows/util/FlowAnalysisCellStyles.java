package com.okworx.ilcd.validation.analyze.flows.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

public class FlowAnalysisCellStyles {

    public CellStyle boldStyle;
    public CellStyle boldItalicStyle;
    public CellStyle referenceInputStyle;
    public CellStyle referenceOutputStyle;
    public CellStyle referenceMixedStyle;
    public XSSFCellStyle infoBackgroundStyle;
    public XSSFCellStyle legendStyle;
    public XSSFCellStyle descriptionStyle;
    public XSSFCellStyle infoLegendReferenceInputStyle;
    public XSSFCellStyle infoLegendReferenceOutputStyle;
    public XSSFCellStyle infoLegendReferenceMixedStyle;
    public XSSFCellStyle infoLegendInputStyle;
    public XSSFCellStyle infoLegendOutputStyle;
    public XSSFCellStyle infoLegendMixedStyle;

    public CellStyle inputStyle;
    public CellStyle outputStyle;
    public CellStyle mixedStyle;

    public void init(SXSSFWorkbook workbook) {
        boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        boldItalicStyle = workbook.createCellStyle();
        Font boldItalicFont = workbook.createFont();
        boldItalicFont.setBold(true);
        boldItalicFont.setItalic(true);
        boldItalicStyle.setFont(boldItalicFont);

        referenceInputStyle = workbook.createCellStyle();
        Font referenceInputFont = workbook.createFont();
        referenceInputFont.setColor(Font.COLOR_RED);
        referenceInputStyle.setFont(referenceInputFont);
        referenceInputStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
        referenceInputStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(referenceInputStyle);

        referenceOutputStyle = workbook.createCellStyle();
        Font referenceOutputFont = workbook.createFont();
        referenceOutputFont.setColor(Font.COLOR_RED);
        referenceOutputStyle.setFont(referenceOutputFont);
        referenceOutputStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        referenceOutputStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(referenceOutputStyle);

        referenceMixedStyle = workbook.createCellStyle();
        Font referenceMixedFont = workbook.createFont();
        referenceMixedFont.setColor(Font.COLOR_RED);
        referenceMixedStyle.setFont(referenceMixedFont);
        referenceMixedStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        referenceMixedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(referenceMixedStyle);

        inputStyle = workbook.createCellStyle();
        inputStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
        inputStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(inputStyle);

        outputStyle = workbook.createCellStyle();
        outputStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        outputStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(outputStyle);

        mixedStyle = workbook.createCellStyle();
        mixedStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        mixedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        addBorders(mixedStyle);

        infoBackgroundStyle = (XSSFCellStyle) workbook.createCellStyle();
        XSSFColor colorLightGrey = new XSSFColor(new byte[] {0, (byte) 230, (byte) 230, (byte) 230}, null);
        infoBackgroundStyle.setFillForegroundColor(colorLightGrey);
        infoBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        descriptionStyle = (XSSFCellStyle) workbook.createCellStyle();
        descriptionStyle.cloneStyleFrom(infoBackgroundStyle);
        Font italicFontLg = workbook.createFont();
        italicFontLg.setItalic(true);
        italicFontLg.setFontHeightInPoints((short) (italicFontLg.getFontHeightInPoints()*1.1));
        descriptionStyle.setFont(italicFontLg);
        descriptionStyle.setAlignment(HorizontalAlignment.CENTER);
        descriptionStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        descriptionStyle.setWrapText(true);

        legendStyle = (XSSFCellStyle) workbook.createCellStyle();
        legendStyle.cloneStyleFrom(descriptionStyle);
        legendStyle.setFillForegroundColor(colorLightGrey);
        legendStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        legendStyle.setAlignment(HorizontalAlignment.LEFT);
        Font italicFont = workbook.createFont();
        italicFont.setItalic(true);
        legendStyle.setFont(italicFont);

        infoLegendInputStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendInputStyle.cloneStyleFrom(inputStyle);

        infoLegendOutputStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendOutputStyle.cloneStyleFrom(outputStyle);

        infoLegendMixedStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendMixedStyle.cloneStyleFrom(mixedStyle);

        infoLegendReferenceInputStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendReferenceInputStyle.cloneStyleFrom(referenceInputStyle);

        infoLegendReferenceOutputStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendReferenceOutputStyle.cloneStyleFrom(referenceOutputStyle);

        infoLegendReferenceMixedStyle = (XSSFCellStyle) workbook.createCellStyle();
        infoLegendReferenceMixedStyle.cloneStyleFrom(referenceMixedStyle);
    }

    private void addBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.HAIR);
        style.setBorderBottom(BorderStyle.HAIR);
        style.setBorderLeft(BorderStyle.HAIR);
        style.setBorderRight(BorderStyle.HAIR);
    }
}

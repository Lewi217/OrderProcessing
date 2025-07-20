package com.sanifu.order_processing.service.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrService {
    private final Tesseract tesseract;

    /**
     * Extract text from an image file
     * This is a placeholder for Phase 2 when OCR will be fully implemented
     */
    public String extractTextFromImage(File imageFile) throws TesseractException {
        log.info("Extracting text from image: {}", imageFile.getName());
        try {
            return tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            log.error("OCR extraction failed for image: {}", imageFile.getName(), e);
            throw e;
        }
    }
}

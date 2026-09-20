"""
LEGALENS AI - Multi-format Document Extraction Engine
Supports PDF, DOCX, TXT with page-number preservation and layout segmentation.
"""
import os
import logging
from typing import List, Dict, Any

logger = logging.getLogger("legalens.parser")


class DocumentParser:
    @staticmethod
    def parse_file(file_path: str) -> List[Dict[str, Any]]:
        """
        Parse document from path and extract pages.
        Returns a list of dicts:
        [{"page_number": int, "text": str, "char_count": int}, ...]
        """
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"Document file not found: {file_path}")

        ext = os.path.splitext(file_path)[1].lower().replace(".", "")

        if ext == "pdf":
            return DocumentParser._parse_pdf(file_path)
        elif ext == "docx":
            return DocumentParser._parse_docx(file_path)
        elif ext in ["txt", "md"]:
            return DocumentParser._parse_txt(file_path)
        elif ext in ["png", "jpg", "jpeg"]:
            return DocumentParser._parse_image(file_path)
        else:
            raise ValueError(f"Unsupported document format: .{ext}")

    @staticmethod
    def _parse_pdf(file_path: str) -> List[Dict[str, Any]]:
        pages = []
        try:
            import fitz  # PyMuPDF
            doc = fitz.open(file_path)
            for i, page in enumerate(doc):
                text = page.get_text("text") or ""
                pages.append({
                    "page_number": i + 1,
                    "text": text.strip(),
                    "char_count": len(text)
                })
            doc.close()
            return pages
        except Exception as e:
            logger.warning(f"PyMuPDF failed, attempting pypdf fallback: {e}")

        # Fallback to pypdf
        try:
            from pypdf import PdfReader
            reader = PdfReader(file_path)
            for i, page in enumerate(reader.pages):
                text = page.extract_text() or ""
                pages.append({
                    "page_number": i + 1,
                    "text": text.strip(),
                    "char_count": len(text)
                })
            return pages
        except Exception as e:
            logger.error(f"All PDF parsing methods failed for {file_path}: {e}")
            raise RuntimeError(f"Failed to extract PDF content: {str(e)}")

    @staticmethod
    def _parse_docx(file_path: str) -> List[Dict[str, Any]]:
        try:
            import docx
            doc = docx.Document(file_path)
            full_text = []
            for para in doc.paragraphs:
                if para.text.strip():
                    full_text.append(para.text.strip())

            # Break into simulated pages (~2500 chars per page if single block)
            combined = "\n\n".join(full_text)
            if not combined:
                return [{"page_number": 1, "text": "", "char_count": 0}]

            page_size = 2500
            pages = []
            for i in range(0, len(combined), page_size):
                chunk = combined[i:i + page_size]
                pages.append({
                    "page_number": (i // page_size) + 1,
                    "text": chunk.strip(),
                    "char_count": len(chunk)
                })
            return pages
        except Exception as e:
            logger.error(f"DOCX extraction failed: {e}")
            raise RuntimeError(f"Failed to extract DOCX: {str(e)}")

    @staticmethod
    def _parse_txt(file_path: str) -> List[Dict[str, Any]]:
        try:
            with open(file_path, "r", encoding="utf-8", errors="replace") as f:
                content = f.read()

            page_size = 2500
            pages = []
            for i in range(0, max(len(content), 1), page_size):
                chunk = content[i:i + page_size]
                pages.append({
                    "page_number": (i // page_size) + 1,
                    "text": chunk.strip(),
                    "char_count": len(chunk)
                })
            return pages
        except Exception as e:
            logger.error(f"TXT reading failed: {e}")
            raise RuntimeError(f"Failed to read text file: {str(e)}")

    @staticmethod
    def _parse_image(file_path: str) -> List[Dict[str, Any]]:
        try:
            import pytesseract
            from PIL import Image
            text = pytesseract.image_to_string(Image.open(file_path)) or ""
            if text.strip():
                return [{
                    "page_number": 1,
                    "text": text.strip(),
                    "char_count": len(text),
                    "ocr_used": True,
                }]
        except Exception as e:
            logger.warning(f"OCR engine unavailable or failed: {e}")
        return [{
            "page_number": 1,
            "text": (
                "[OCR architecture ready. Automated text extraction requires an OCR engine such as Tesseract. "
                "No fabricated document text is generated when OCR is unavailable.]"
            ),
            "char_count": 0,
            "ocr_used": False,
        }]

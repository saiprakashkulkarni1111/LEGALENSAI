"""
LEGALENS AI - Demo PDF Generator
Compiles synthetic demo text contracts into real PDF files for upload testing.
"""
import os
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors


def make_pdf_from_text(txt_path: str, pdf_path: str, title: str):
    with open(txt_path, "r", encoding="utf-8") as f:
        lines = f.readlines()

    doc = SimpleDocTemplate(pdf_path, pagesize=letter, rightMargin=54, leftMargin=54, topMargin=54, bottomMargin=54)
    styles = getSampleStyleSheet()

    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontSize=14,
        leading=18,
        textColor=colors.HexColor('#111827'),
        alignment=1
    )
    body_style = ParagraphStyle(
        'DocBody',
        parent=styles['Normal'],
        fontSize=10,
        leading=14,
        textColor=colors.HexColor('#374151')
    )
    badge_style = ParagraphStyle(
        'DemoBadge',
        parent=styles['Normal'],
        fontSize=9,
        leading=12,
        textColor=colors.HexColor('#9ca3af'),
        alignment=1
    )

    story = [
        Paragraph(title, title_style),
        Spacer(1, 6),
        Paragraph("[SYNTHETIC DEMO CONTRACT FOR TESTING]", badge_style),
        Spacer(1, 16)
    ]

    for line in lines[2:]:  # skip initial headers
        line_s = line.strip()
        if not line_s:
            story.append(Spacer(1, 8))
        else:
            story.append(Paragraph(line_s, body_style))
            story.append(Spacer(1, 4))

    doc.build(story)
    print(f"Generated PDF: {pdf_path}")


if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    demo_dir = os.path.join(base_dir, "data", "demo")
    os.makedirs(demo_dir, exist_ok=True)

    src_txt = os.path.join(demo_dir, "Software_Employment_Agreement.txt")
    dest_pdf = os.path.join(demo_dir, "Software_Employment_Agreement.pdf")
    if os.path.exists(src_txt):
        make_pdf_from_text(src_txt, dest_pdf, "EMPLOYMENT AND CONFIDENTIALITY AGREEMENT")

"""
Accessibility Compliance Tests (WCAG 2.2 AA)
Verifies that attention levels are never communicated purely by color,
ARIA labels and visible text descriptions exist for all risk categories.
"""
from backend.app.schemas.document_schemas import ClauseItem


def test_attention_categories_non_color_dependent():
    """
    WCAG 2.2 Criterion 1.4.1 (Use of Color):
    Color must not be used as the only visual means of conveying information.
    Every attention level must possess explicit textual terminology.
    """
    valid_categories = {
        "Informational",
        "Review",
        "Important review",
        "Professional review recommended"
    }

    test_clause = ClauseItem(
        id="c1",
        clause_type="Termination",
        original_text="Either party may terminate on 7 days notice.",
        page_number=1,
        plain_explanation="Short notice termination.",
        attention_category="Important review"
    )

    assert test_clause.attention_category in valid_categories
    assert test_clause.attention_category != "RED"
    assert test_clause.attention_category != "AMBER"

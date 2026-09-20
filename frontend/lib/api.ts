export const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://127.0.0.1:8000";

export async function api<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      ...(init?.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      ...(init?.headers || {}),
    },
  });
  if (!res.ok) {
    const detail = await res.text();
    throw new Error(detail || `Request failed: ${res.status}`);
  }
  return res.json() as Promise<T>;
}

export type Clause = {
  id: string;
  clause_type: string;
  original_text: string;
  page_number: number;
  section_reference?: string | null;
  plain_explanation: string;
  obligation?: string | null;
  attention_category: string;
  potential_concern?: string | null;
  relevant_legal_concept?: string | null;
  suggested_verification?: string | null;
  confidence: number;
};

export type TimelineEvent = {
  id: string;
  date_str: string;
  event_description: string;
  event_type: string;
  page_number: number;
  confidence: number;
  is_manual_override: boolean;
};

export type DocumentDetail = {
  id: string;
  title: string;
  file_name: string;
  file_type: string;
  file_size: number;
  mode: string;
  status: string;
  doc_type: string;
  jurisdiction_detected: string;
  parties_detected: string[];
  effective_date_detected?: string | null;
  pii_detected_count: number;
  pii_redacted: boolean;
  summary?: string | null;
  created_at: string;
  clauses: Clause[];
  timeline_events: TimelineEvent[];
  pages: { page_number: number; text: string; char_count: number }[];
  total_clauses: number;
  total_obligations: number;
  total_deadlines: number;
  items_requiring_review: number;
};

export type SourceHealth = {
  total_sources: number;
  healthy_sources: number;
  stale_sources: number;
  unavailable_sources: number;
  last_global_sync: string;
  system_pulse: string;
  sources: {
    source_id: string;
    source_name: string;
    authority: string;
    authority_tier: string;
    official_url: string;
    freshness_status: string;
    last_checked: string;
    last_successful_sync: string;
    automation_available: boolean;
    latency_ms: number;
  }[];
};

export type ResearchResponse = {
  query: string;
  answer: string;
  what_document_says?: string | null;
  evidence: {
    id: string;
    source_title: string;
    section_or_para: string;
    exact_text: string;
    official_url: string;
    authority: string;
    authority_tier: string;
    effective_period: string;
    relevance_score: number;
  }[];
  claims: {
    claim_text: string;
    is_verified: boolean;
    evidence_ids: string[];
    confidence: string;
    entailment_reasoning: string;
  }[];
  legal_sources: {
    source_name: string;
    authority: string;
    authority_tier: string;
    official_url: string;
    retrieved_at: string;
    freshness_status: string;
  }[];
  what_this_means: string;
  what_to_verify: string[];
  possible_next_steps: string[];
  questions_for_lawyer: string[];
  retrieved_at: string;
  freshness_status: string;
  limitations: string[];
  responsible_ai_notice?: string;
};

"use client";

import { useEffect, useMemo, useState } from "react";
import { useParams } from "next/navigation";
import { AppShell } from "@/components/AppShell";
import { api, type Clause, type DocumentDetail, type ResearchResponse } from "@/lib/api";

function attentionLabel(cat: string) {
  return cat;
}

export default function DocumentAnalysisPage() {
  const params = useParams<{ id: string }>();
  const [doc, setDoc] = useState<DocumentDetail | null>(null);
  const [selected, setSelected] = useState<Clause | null>(null);
  const [research, setResearch] = useState<ResearchResponse | null>(null);
  const [impacts, setImpacts] = useState<any[] | null>(null);
  const [question, setQuestion] = useState("What does the termination clause mean?");

  useEffect(() => {
    if (!params.id) return;
    api<DocumentDetail>(`/api/documents/${params.id}`).then((d) => {
      setDoc(d);
      setSelected(d.clauses[0] || null);
    });
  }, [params.id]);

  const pageText = useMemo(() => {
    if (!doc) return "";
    const page = selected?.page_number || 1;
    return doc.pages.find((p) => p.page_number === page)?.text || doc.pages.map((p) => p.text).join("\n\n");
  }, [doc, selected]);

  async function findLaw() {
    if (!doc || !selected) return;
    const res = await api<ResearchResponse>("/api/research/live", {
      method: "POST",
      body: JSON.stringify({
        query: selected.relevant_legal_concept || selected.clause_type,
        jurisdiction: "India",
        date_context: "CURRENT",
        document_id: doc.id,
        clause_id: selected.id,
      }),
    });
    setResearch(res);
  }

  async function ask() {
    if (!doc) return;
    const res = await api<ResearchResponse>(`/api/documents/${doc.id}/ask`, {
      method: "POST",
      body: JSON.stringify({ query: question, clause_id: selected?.id }),
    });
    setResearch(res);
  }

  async function relatedLawImpact() {
    if (!doc) return;
    const res = await api<{ impacts: any[] }>(`/api/documents/${doc.id}/impact`, { method: "POST", body: "{}" });
    setImpacts(res.impacts);
  }

  const inspector = selected ? (
    <div className="space-y-4 text-sm">
      <p className="text-[11px] uppercase tracking-[0.2em] text-zinc-500">Evidence</p>
      <div>
        <div className="text-zinc-500 text-xs">CLAUSE</div>
        <div className="mt-1">{selected.clause_type}</div>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Plain explanation</div>
        <p className="mt-1 text-zinc-300">{selected.plain_explanation}</p>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Obligation</div>
        <p className="mt-1 text-zinc-300">{selected.obligation || "No explicit duty extracted."}</p>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Potential concern</div>
        <p className="mt-1 text-zinc-300">{selected.potential_concern}</p>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Attention</div>
        <p className="mt-1">{attentionLabel(selected.attention_category)}</p>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Source</div>
        <p className="mt-1">USER DOCUMENT · Uploaded by user{doc?.mode === "SYNTHETIC_DEMO" ? " · [SYNTHETIC DEMO]" : ""}</p>
      </div>
      <div>
        <div className="text-zinc-500 text-xs">Confidence</div>
        <p className="mt-1">{Math.round(selected.confidence * 100)}%</p>
      </div>
      {research && (
        <div className="border-t border-line pt-4 space-y-2">
          <div className="text-xs text-zinc-500">RELEVANT LEGAL SOURCES</div>
          {research.evidence.length === 0 && <p>Unable to verify this claim from the available authoritative sources.</p>}
          {research.evidence.map((ev) => (
            <a key={ev.id} href={ev.official_url} className="block text-emerald text-xs underline" target="_blank" rel="noreferrer">
              {ev.source_title} · {ev.section_or_para}
            </a>
          ))}
          <p className="text-[11px] text-zinc-500">Freshness: {research.freshness_status} · {research.retrieved_at}</p>
        </div>
      )}
    </div>
  ) : undefined;

  if (!doc) {
    return (
      <AppShell>
        <div className="p-8 text-sm text-zinc-400">Loading analysis…</div>
      </AppShell>
    );
  }

  return (
    <AppShell inspector={inspector}>
      <div className="grid grid-cols-[1.1fr_0.9fr] min-h-screen">
        <section className="paper p-8 overflow-y-auto" aria-label="Document">
          <p className="text-[11px] uppercase tracking-[0.2em] text-zinc-500">Document · page {selected?.page_number || 1}</p>
          <h1 className="text-2xl mt-2">{doc.title}</h1>
          <p className="text-xs mt-1 text-zinc-600">
            {doc.mode === "SYNTHETIC_DEMO" ? "[SYNTHETIC DEMO]" : "REAL DATA MODE"} · {doc.doc_type} · {doc.jurisdiction_detected}
          </p>
          <article className="mt-6 whitespace-pre-wrap text-[15px] leading-7 font-serif">
            {pageText.split("\n").map((line, i) => {
              const hit = selected && line.includes(selected.original_text.slice(0, 40));
              return (
                <span key={i} className={hit ? "bg-amber-200/70" : undefined}>
                  {line}
                  {"\n"}
                </span>
              );
            })}
          </article>
        </section>
        <section className="p-6 overflow-y-auto border-l border-line" aria-label="Analysis">
          <h2 className="text-lg">Analysis</h2>
          <p className="text-sm text-zinc-400 mt-2">{doc.summary}</p>
          <div className="grid grid-cols-4 gap-2 mt-4 text-center">
            {[
              [doc.total_clauses, "clauses"],
              [doc.total_obligations, "obligations"],
              [doc.total_deadlines, "dates"],
              [doc.items_requiring_review, "review"],
            ].map(([n, l]) => (
              <div key={String(l)} className="border border-line rounded p-2">
                <div className="text-xl">{n}</div>
                <div className="text-[10px] uppercase tracking-widest text-zinc-500">{l}</div>
              </div>
            ))}
          </div>
          <ul className="mt-6 space-y-2">
            {doc.clauses.map((c) => (
              <li key={c.id}>
                <button
                  type="button"
                  onClick={() => setSelected(c)}
                  className={`w-full text-left border rounded-md p-3 ${selected?.id === c.id ? "border-emerald bg-panel" : "border-line"}`}
                >
                  <div className="flex justify-between text-sm">
                    <span>{c.clause_type}</span>
                    <span className="text-xs text-zinc-400">{c.attention_category}</span>
                  </div>
                  <p className="text-xs text-zinc-500 mt-1 line-clamp-2">{c.plain_explanation}</p>
                </button>
              </li>
            ))}
          </ul>
          <div className="mt-6 space-y-2">
            <button type="button" onClick={findLaw} className="w-full bg-emerald text-ink rounded-md py-2 text-sm">
              Find Relevant Law
            </button>
            <button type="button" onClick={relatedLawImpact} className="w-full border border-line rounded-md py-2 text-sm">
              Document-to-law impact
            </button>
            <label className="block text-xs text-zinc-500" htmlFor="q">
              Ask about this document
            </label>
            <input
              id="q"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              className="w-full bg-ink border border-line rounded-md px-3 py-2 text-sm"
            />
            <button type="button" onClick={ask} className="w-full border border-line rounded-md py-2 text-sm">
              Ask with evidence
            </button>
          </div>
          {impacts && (
            <div className="mt-4 text-xs space-y-2">
              {impacts.map((imp, i) => (
                <p key={i} className="border border-line rounded p-2">
                  {imp.headline || imp.message} {imp.source_title ? `— ${imp.source_title}` : ""}
                </p>
              ))}
            </div>
          )}
        </section>
      </div>
    </AppShell>
  );
}

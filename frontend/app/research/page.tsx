"use client";

import { useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type ResearchResponse } from "@/lib/api";

export default function ResearchPage() {
  const [query, setQuery] = useState("Section 27 Indian Contract Act non-compete");
  const [result, setResult] = useState<ResearchResponse | null>(null);
  const [busy, setBusy] = useState(false);

  async function run() {
    setBusy(true);
    try {
      const res = await api<ResearchResponse>("/api/research/live", {
        method: "POST",
        body: JSON.stringify({ query, jurisdiction: "India", date_context: "CURRENT" }),
      });
      setResult(res);
    } finally {
      setBusy(false);
    }
  }

  const inspector = result ? (
    <div className="space-y-3 text-sm">
      <p className="text-[11px] uppercase tracking-[0.2em] text-zinc-500">Citation validation</p>
      {result.claims.map((c, i) => (
        <div key={i} className="border border-line rounded p-2">
          <div>{c.is_verified ? "Verified" : "Unverified"} · {c.confidence}</div>
          <p className="text-xs text-zinc-400 mt-1">{c.entailment_reasoning}</p>
        </div>
      ))}
      <p className="text-[11px] text-zinc-500">Source freshness: {result.freshness_status}</p>
    </div>
  ) : undefined;

  return (
    <AppShell inspector={inspector}>
      <div className="p-8 max-w-3xl">
        <h1 className="text-2xl">Research</h1>
        <p className="text-sm text-zinc-400 mt-1">Universal search across acts, sections, and verified judgments. Current-law answers never come from model memory alone.</p>
        <label htmlFor="research-q" className="block mt-6 text-sm">
          Query
        </label>
        <div className="mt-2 flex gap-2">
          <input
            id="research-q"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="flex-1 bg-panel border border-line rounded-md px-3 py-2 text-sm"
          />
          <button type="button" onClick={run} disabled={busy} className="bg-emerald text-ink px-4 rounded-md text-sm">
            {busy ? "Retrieving…" : "Search"}
          </button>
        </div>
        {result && (
          <article className="mt-8 space-y-6">
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">Answer</h2>
              <p className="mt-2 text-zinc-200">{result.answer}</p>
            </section>
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">Evidence</h2>
              {result.evidence.length === 0 && <p className="mt-2">Unable to verify this claim from the available authoritative sources.</p>}
              {result.evidence.map((ev) => (
                <blockquote key={ev.id} className="mt-3 border-l-2 border-emerald pl-3 text-sm text-zinc-300">
                  <div className="text-xs text-zinc-500">{ev.source_title} · {ev.section_or_para} · {ev.authority_tier}</div>
                  <p className="mt-1">{ev.exact_text}</p>
                  <a className="text-emerald text-xs" href={ev.official_url} target="_blank" rel="noreferrer">
                    Official source
                  </a>
                </blockquote>
              ))}
            </section>
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">What this means</h2>
              <p className="mt-2 text-sm text-zinc-300">{result.what_this_means}</p>
            </section>
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">What to verify</h2>
              <ul className="mt-2 list-disc pl-5 text-sm text-zinc-300">
                {result.what_to_verify.map((item) => <li key={item}>{item}</li>)}
              </ul>
            </section>
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">Possible next steps</h2>
              <ul className="mt-2 list-disc pl-5 text-sm text-zinc-300">
                {result.possible_next_steps.map((item) => <li key={item}>{item}</li>)}
              </ul>
            </section>
            <section>
              <h2 className="text-xs uppercase tracking-widest text-zinc-500">Questions for a lawyer</h2>
              <ul className="mt-2 list-disc pl-5 text-sm text-zinc-300">
                {result.questions_for_lawyer.map((item) => <li key={item}>{item}</li>)}
              </ul>
            </section>
            <p className="text-xs text-zinc-500">{result.responsible_ai_notice}</p>
          </article>
        )}
      </div>
    </AppShell>
  );
}

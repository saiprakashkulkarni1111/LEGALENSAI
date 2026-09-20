"use client";

import { useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api } from "@/lib/api";

type CaseHit = {
  id: string;
  case_title: string;
  court: string;
  case_number?: string | null;
  decision_date?: string | null;
  relevant_excerpt: string;
  official_url: string;
  retrieved_at: string;
  status: string;
  requires_official_search: boolean;
  official_search_guidance?: string | null;
};

export default function CasesPage() {
  const [query, setQuery] = useState("Kailash Nath");
  const [cases, setCases] = useState<CaseHit[]>([]);

  async function search() {
    const res = await api<CaseHit[]>(`/api/cases/search?query=${encodeURIComponent(query)}`);
    setCases(res);
  }

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Case Explorer</h1>
        <p className="text-sm text-zinc-400 mt-1">Official court sources where available. CAPTCHA is never bypassed.</p>
        <div className="mt-6 flex gap-2">
          <input
            aria-label="Case search"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            className="flex-1 bg-panel border border-line rounded-md px-3 py-2 text-sm"
          />
          <button type="button" onClick={search} className="bg-emerald text-ink px-4 rounded-md text-sm">
            Search cases
          </button>
        </div>
        <ul className="mt-8 space-y-4">
          {cases.map((c) => (
            <li key={c.id} className="border border-line rounded-md p-4">
              <h2 className="text-lg">{c.case_title}</h2>
              <p className="text-xs text-zinc-500 mt-1">
                {c.court} · {c.case_number || "Number on official portal"} · {c.status}
              </p>
              <p className="text-sm text-zinc-300 mt-3">{c.relevant_excerpt || "Continue on official court search."}</p>
              {c.requires_official_search && (
                <p className="text-xs text-amber-200 mt-2">{c.official_search_guidance || "Continue on official court search"}</p>
              )}
              <a className="text-emerald text-sm mt-3 inline-block" href={c.official_url} target="_blank" rel="noreferrer">
                Official source
              </a>
              <p className="text-[11px] text-zinc-500 mt-1">Retrieved {c.retrieved_at}</p>
            </li>
          ))}
        </ul>
      </div>
    </AppShell>
  );
}

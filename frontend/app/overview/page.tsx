"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type DocumentDetail, type SourceHealth } from "@/lib/api";

export default function OverviewPage() {
  const [docs, setDocs] = useState<DocumentDetail[]>([]);
  const [health, setHealth] = useState<SourceHealth | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([api<DocumentDetail[]>("/api/documents"), api<SourceHealth>("/api/sources/health")])
      .then(([d, h]) => {
        setDocs(d);
        setHealth(h);
      })
      .catch((e) => setError(e.message));
  }, []);

  const deadlines = docs.flatMap((d) => d.timeline_events).slice(0, 6);

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Overview</h1>
        <p className="text-sm text-zinc-400 mt-1">Live workspace metrics from the backend. Values are never hardcoded.</p>
        {error && <p className="mt-4 text-sm text-amber-300" role="alert">Backend unavailable: {error}</p>}
        <div className="grid grid-cols-4 gap-4 mt-8">
          {[
            ["Documents analyzed", String(docs.length)],
            ["Active research", String(docs.filter((d) => d.status === "ANALYZED").length)],
            ["Upcoming deadlines", String(docs.reduce((n, d) => n + d.total_deadlines, 0))],
            ["Items requiring review", String(docs.reduce((n, d) => n + d.items_requiring_review, 0))],
          ].map(([label, value]) => (
            <div key={label} className="border border-line rounded-md p-4 bg-panel/40">
              <div className="text-xs uppercase tracking-widest text-zinc-500">{label}</div>
              <div className="text-3xl mt-2">{value}</div>
            </div>
          ))}
        </div>
        <h2 className="mt-10 text-sm uppercase tracking-[0.2em] text-zinc-500">Legal Data Pulse</h2>
        <div className="mt-4 space-y-2">
          {health?.sources.map((s) => (
            <div key={s.source_id} className="flex items-center justify-between border border-line rounded-md px-4 py-3">
              <div>
                <div className="text-sm">{s.source_name}</div>
                <div className="text-xs text-zinc-500">{s.authority} · {s.authority_tier}</div>
              </div>
              <div className="text-right">
                <div className="text-xs">
                  <span className="pulse-dot mr-2 align-middle bg-emerald" aria-hidden />
                  {s.freshness_status}
                  {!s.automation_available && " · OFFICIAL SEARCH"}
                </div>
                <div className="text-[11px] text-zinc-500">Last verified: {new Date(s.last_checked).toLocaleString()}</div>
              </div>
            </div>
          ))}
        </div>
        <h2 className="mt-10 text-sm uppercase tracking-[0.2em] text-zinc-500">Upcoming dates from documents</h2>
        <ul className="mt-3 space-y-2">
          {deadlines.length === 0 && <li className="text-sm text-zinc-500">No extracted dates yet.</li>}
          {deadlines.map((ev) => (
            <li key={ev.id} className="text-sm border-b border-line py-2">
              <span className="text-zinc-300">{ev.date_str}</span>
              <span className="text-zinc-500"> — {ev.event_type}</span>
            </li>
          ))}
        </ul>
      </div>
    </AppShell>
  );
}

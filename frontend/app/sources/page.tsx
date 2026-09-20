"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type SourceHealth } from "@/lib/api";

export default function SourcesPage() {
  const [health, setHealth] = useState<SourceHealth | null>(null);

  useEffect(() => {
    api<SourceHealth>("/api/sources/health").then(setHealth);
  }, []);

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Saved Sources</h1>
        <p className="text-sm text-zinc-400 mt-1">Authority, retrieval method, and freshness from live health checks. LIVE is shown only after a successful check.</p>
        <ul className="mt-6 space-y-3">
          {health?.sources.map((s) => (
            <li key={s.source_id} className="border border-line rounded-md p-4">
              <div className="flex justify-between">
                <div>
                  <div>{s.source_name}</div>
                  <div className="text-xs text-zinc-500">OFFICIAL SOURCE · {s.authority} · {s.authority_tier}</div>
                </div>
                <div className="text-xs">{s.freshness_status}</div>
              </div>
              <a className="text-emerald text-xs mt-2 inline-block" href={s.official_url} target="_blank" rel="noreferrer">
                {s.official_url}
              </a>
              <p className="text-[11px] text-zinc-500 mt-1">
                Last verified {new Date(s.last_checked).toLocaleString()} · automation {s.automation_available ? "available" : "official search required"}
              </p>
            </li>
          ))}
        </ul>
      </div>
    </AppShell>
  );
}

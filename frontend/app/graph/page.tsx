"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api } from "@/lib/api";

export default function GraphPage() {
  const [graph, setGraph] = useState<{ nodes: any[]; edges: any[] } | null>(null);

  useEffect(() => {
    api("/api/graph").then(setGraph);
  }, []);

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Knowledge Graph</h1>
        <p className="text-sm text-zinc-400 mt-1">Acts, sections, concepts, and judgments linked by verified relations.</p>
        <div className="mt-8 grid grid-cols-3 gap-3">
          {graph?.nodes.map((n) => (
            <div key={n.id} className="border border-line rounded-md p-3 text-sm">
              <div className="text-[10px] uppercase tracking-widest text-zinc-500">{n.type}</div>
              <div className="mt-1">{n.label}</div>
            </div>
          ))}
        </div>
        <ul className="mt-6 text-xs text-zinc-500 space-y-1">
          {graph?.edges.map((e, i) => (
            <li key={i}>{e.source} — {e.relation} → {e.target}</li>
          ))}
        </ul>
      </div>
    </AppShell>
  );
}

"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type DocumentDetail } from "@/lib/api";

type Diff = {
  clause_type: string;
  change_type: string;
  doc_a_text?: string | null;
  doc_b_text?: string | null;
  delta_summary: string;
  potential_significance: string;
};

export default function ComparePage() {
  const [docs, setDocs] = useState<DocumentDetail[]>([]);
  const [a, setA] = useState("");
  const [b, setB] = useState("");
  const [result, setResult] = useState<any>(null);

  useEffect(() => {
    api<DocumentDetail[]>("/api/documents").then((list) => {
      setDocs(list);
      setA(list[0]?.id || "");
      setB(list[1]?.id || "");
    });
  }, []);

  async function compare() {
    const res = await api("/api/documents/compare", {
      method: "POST",
      body: JSON.stringify({ doc_a_id: a, doc_b_id: b }),
    });
    setResult(res);
  }

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Compare</h1>
        <p className="text-sm text-zinc-400 mt-1">Side-by-side clause differences. The system does not say which document is better.</p>
        <div className="mt-6 flex gap-3">
          <select aria-label="Document A" value={a} onChange={(e) => setA(e.target.value)} className="bg-panel border border-line rounded px-3 py-2 text-sm">
            {docs.map((d) => <option key={d.id} value={d.id}>{d.title}</option>)}
          </select>
          <select aria-label="Document B" value={b} onChange={(e) => setB(e.target.value)} className="bg-panel border border-line rounded px-3 py-2 text-sm">
            {docs.map((d) => <option key={d.id} value={d.id}>{d.title}</option>)}
          </select>
          <button type="button" onClick={compare} className="bg-emerald text-ink px-4 rounded-md text-sm">Compare</button>
        </div>
        {result && (
          <div className="mt-8 space-y-3">
            <p className="text-sm text-zinc-400">
              {result.total_differences} differences · added {result.added_count} · removed {result.removed_count} · modified {result.modified_count}
            </p>
            {(result.diffs as Diff[]).map((d, i) => (
              <article key={i} className="border border-line rounded-md p-4 grid grid-cols-2 gap-4">
                <div>
                  <div className="text-xs text-zinc-500">VERSION A · {d.change_type}</div>
                  <p className="text-sm mt-2">{d.doc_a_text || "—"}</p>
                </div>
                <div>
                  <div className="text-xs text-zinc-500">VERSION B</div>
                  <p className="text-sm mt-2">{d.doc_b_text || "—"}</p>
                </div>
                <p className="col-span-2 text-xs text-zinc-400">{d.delta_summary} {d.potential_significance}</p>
              </article>
            ))}
          </div>
        )}
      </div>
    </AppShell>
  );
}

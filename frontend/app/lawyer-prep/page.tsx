"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type DocumentDetail } from "@/lib/api";

export default function LawyerPrepPage() {
  const [docs, setDocs] = useState<DocumentDetail[]>([]);
  const [id, setId] = useState("");
  const [pack, setPack] = useState<any>(null);

  useEffect(() => {
    api<DocumentDetail[]>("/api/documents").then((d) => {
      setDocs(d);
      setId(d[0]?.id || "");
    });
  }, []);

  async function generate() {
    const res = await api("/api/lawyer-prep/generate", {
      method: "POST",
      body: JSON.stringify({ document_id: id }),
    });
    setPack(res);
  }

  function exportMarkdown() {
    if (!pack) return;
    const md = `# ${pack.document_title}\n\n${pack.executive_summary}\n\n## Questions\n${pack.questions_for_counsel.map((q: string) => `- ${q}`).join("\n")}\n\n${pack.legal_disclaimer}\n`;
    const blob = new Blob([md], { type: "text/markdown" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "lawyer-prep.md";
    a.click();
  }

  return (
    <AppShell>
      <div className="p-8 max-w-3xl">
        <h1 className="text-2xl">Lawyer Prep</h1>
        <p className="text-sm text-zinc-400 mt-1">Briefing pack for a qualified professional. Not a substitute for legal advice.</p>
        <div className="mt-6 flex gap-2">
          <select aria-label="Document" value={id} onChange={(e) => setId(e.target.value)} className="bg-panel border border-line rounded px-3 py-2 text-sm">
            {docs.map((d) => <option key={d.id} value={d.id}>{d.title}</option>)}
          </select>
          <button type="button" onClick={generate} className="bg-emerald text-ink px-4 rounded-md text-sm">Prepare for lawyer</button>
        </div>
        {pack && (
          <article className="mt-8 space-y-4 text-sm">
            <p>{pack.executive_summary}</p>
            <h2 className="text-xs uppercase tracking-widest text-zinc-500">Missing information</h2>
            <ul className="list-disc pl-5">{pack.missing_information.map((x: string) => <li key={x}>{x}</li>)}</ul>
            <h2 className="text-xs uppercase tracking-widest text-zinc-500">Questions for counsel</h2>
            <ul className="list-disc pl-5">{pack.questions_for_counsel.map((x: string) => <li key={x}>{x}</li>)}</ul>
            <p className="text-xs text-zinc-500">{pack.legal_disclaimer}</p>
            <button type="button" onClick={exportMarkdown} className="border border-line rounded px-3 py-2">Export Markdown</button>
          </article>
        )}
      </div>
    </AppShell>
  );
}

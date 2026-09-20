"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AppShell } from "@/components/AppShell";
import { api, API_URL, type DocumentDetail } from "@/lib/api";

export default function DocumentsPage() {
  const [docs, setDocs] = useState<DocumentDetail[]>([]);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const refresh = () => api<DocumentDetail[]>("/api/documents").then(setDocs).catch((e) => setMessage(e.message));

  useEffect(() => {
    refresh();
  }, []);

  async function onUpload(file: File, mode: string) {
    setBusy(true);
    setMessage(null);
    const body = new FormData();
    body.append("file", file);
    body.append("mode", mode);
    body.append("auto_redact", "true");
    try {
      const res = await fetch(`${API_URL}/api/documents/upload`, { method: "POST", body });
      if (!res.ok) throw new Error(await res.text());
      await refresh();
      setMessage("Document uploaded, sanitized, and analyzed.");
    } catch (e) {
      setMessage(e instanceof Error ? e.message : "Upload failed");
    } finally {
      setBusy(false);
    }
  }

  return (
    <AppShell>
      <div className="p-8">
        <h1 className="text-2xl">Documents</h1>
        <p className="text-sm text-zinc-400 mt-1">PDF, DOCX, TXT, and images. Demo files must stay labelled as synthetic.</p>
        <form
          className="mt-6 border border-dashed border-line rounded-md p-6"
          onSubmit={(e) => e.preventDefault()}
        >
          <label className="text-sm" htmlFor="file">
            Upload a document
          </label>
          <input
            id="file"
            className="mt-3 block text-sm"
            type="file"
            accept=".pdf,.docx,.txt,.png,.jpg,.jpeg"
            disabled={busy}
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) onUpload(file, file.name.toLowerCase().includes("demo") ? "SYNTHETIC_DEMO" : "REAL");
            }}
          />
          <p className="text-xs text-zinc-500 mt-2">Demo mode is applied automatically when the filename contains “demo”.</p>
        </form>
        {message && <p className="mt-4 text-sm text-zinc-300" role="status">{message}</p>}
        <ul className="mt-8 space-y-3">
          {docs.map((d) => (
            <li key={d.id} className="border border-line rounded-md p-4 bg-panel/30">
              <Link href={`/documents/${d.id}`} className="text-lg hover:text-emerald">
                {d.title}
              </Link>
              <div className="text-xs text-zinc-500 mt-1">
                {d.mode === "SYNTHETIC_DEMO" ? "[SYNTHETIC DEMO] · " : "REAL DATA MODE · "}
                {d.total_clauses} clauses · {d.total_obligations} obligations · {d.total_deadlines} dates · {d.items_requiring_review} requiring review
              </div>
            </li>
          ))}
        </ul>
      </div>
    </AppShell>
  );
}

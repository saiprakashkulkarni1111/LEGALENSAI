"use client";

import { useEffect, useState } from "react";
import { AppShell } from "@/components/AppShell";
import { api, type DocumentDetail, type TimelineEvent } from "@/lib/api";

export default function TimelinePage() {
  const [docs, setDocs] = useState<DocumentDetail[]>([]);
  const [selected, setSelected] = useState<TimelineEvent | null>(null);

  useEffect(() => {
    api<DocumentDetail[]>("/api/documents").then(setDocs);
  }, []);

  const events = docs.flatMap((d) => d.timeline_events.map((e) => ({ ...e, title: d.title })));

  return (
    <AppShell
      inspector={
        selected ? (
          <div className="text-sm space-y-2">
            <div className="text-[11px] uppercase tracking-widest text-zinc-500">Event source</div>
            <p>{selected.event_description}</p>
            <p className="text-xs text-zinc-500">Page {selected.page_number} · Confidence {Math.round(selected.confidence * 100)}%</p>
          </div>
        ) : undefined
      }
    >
      <div className="p-8">
        <h1 className="text-2xl">Timeline</h1>
        <ol className="mt-8 border-l border-line ml-3 space-y-6">
          {events.map((ev) => (
            <li key={ev.id} className="ml-6">
              <button type="button" onClick={() => setSelected(ev)} className="text-left">
                <div className="text-xs text-emerald">{ev.date_str}</div>
                <div className="text-sm mt-1">{ev.event_type}</div>
                <div className="text-xs text-zinc-500">{ev.event_description}</div>
              </button>
            </li>
          ))}
        </ol>
      </div>
    </AppShell>
  );
}

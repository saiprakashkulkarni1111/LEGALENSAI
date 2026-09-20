import Link from "next/link";
import { ArrowDown, Scale } from "lucide-react";

const FLOW = ["Document", "Clause", "Legal source", "Evidence", "Explanation", "Next step"];

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-ink text-zinc-100">
      <header className="flex items-center justify-between px-8 py-5 border-b border-line">
        <div className="flex items-center gap-2">
          <Scale className="h-5 w-5 text-emerald" aria-hidden />
          <span className="text-sm tracking-[0.2em] uppercase">Legalens AI</span>
        </div>
        <nav className="flex gap-6 text-sm text-zinc-400" aria-label="Marketing">
          <Link href="/overview">Workspace</Link>
          <Link href="/research">Research the law</Link>
        </nav>
      </header>
      <section className="max-w-5xl mx-auto px-8 pt-24 pb-16">
        <p className="text-emerald text-xs tracking-[0.25em] uppercase mb-4">Evidence-first legal intelligence for India</p>
        <h1 className="text-5xl leading-tight font-medium">Legal information shouldn&apos;t require a law degree.</h1>
        <p className="mt-6 text-lg text-zinc-400 max-w-2xl">
          Understand documents, discover authoritative sources, and prepare for your next legal conversation.
        </p>
        <div className="mt-10 flex gap-4">
          <Link href="/documents" className="bg-emerald text-ink px-5 py-3 rounded-md text-sm font-medium">
            Analyze a Document
          </Link>
          <Link href="/research" className="border border-line px-5 py-3 rounded-md text-sm text-zinc-200">
            Research the Law
          </Link>
        </div>
      </section>
      <section className="max-w-5xl mx-auto px-8 pb-24" aria-label="Evidence flow">
        <ol className="grid grid-cols-6 gap-3">
          {FLOW.map((step, i) => (
            <li key={step} className="border border-line rounded-md p-4 bg-panel/50">
              <div className="text-[10px] uppercase tracking-widest text-zinc-500">0{i + 1}</div>
              <div className="mt-2 text-sm">{step}</div>
              {i < FLOW.length - 1 && <ArrowDown className="mt-3 h-4 w-4 text-zinc-600" aria-hidden />}
            </li>
          ))}
        </ol>
        <p className="mt-10 text-xs text-zinc-500 max-w-3xl">
          Legalens AI provides legal information and document assistance. It does not replace a lawyer, predict court outcomes, or invent statutes, cases, or citations.
        </p>
      </section>
    </div>
  );
}

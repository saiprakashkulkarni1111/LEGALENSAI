"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Scale,
  LayoutDashboard,
  FileText,
  Search,
  Gavel,
  GitCompare,
  Clock3,
  Briefcase,
  Radio,
  Share2,
} from "lucide-react";
import { cn } from "@/lib/cn";

const NAV = [
  { href: "/overview", label: "Overview", icon: LayoutDashboard },
  { href: "/documents", label: "Documents", icon: FileText },
  { href: "/research", label: "Research", icon: Search },
  { href: "/cases", label: "Case Explorer", icon: Gavel },
  { href: "/compare", label: "Compare", icon: GitCompare },
  { href: "/timeline", label: "Timeline", icon: Clock3 },
  { href: "/lawyer-prep", label: "Lawyer Prep", icon: Briefcase },
  { href: "/graph", label: "Knowledge Graph", icon: Share2 },
  { href: "/sources", label: "Saved Sources", icon: Radio },
];

export function AppShell({ children, inspector }: { children: React.ReactNode; inspector?: React.ReactNode }) {
  const pathname = usePathname();
  return (
    <div className="min-h-screen grid grid-cols-[240px_1fr_320px] bg-ink">
      <aside className="border-r border-line px-4 py-5 flex flex-col" aria-label="Primary">
        <Link href="/" className="flex items-center gap-2 mb-8">
          <Scale className="h-5 w-5 text-emerald" aria-hidden />
          <span className="text-sm tracking-[0.18em] uppercase text-zinc-300">Legalens AI</span>
        </Link>
        <nav className="space-y-1" aria-label="Workspace">
          {NAV.map((item) => {
            const active = pathname === item.href || pathname.startsWith(item.href + "/");
            const Icon = item.icon;
            return (
              <Link
                key={item.href}
                href={item.href}
                aria-current={active ? "page" : undefined}
                className={cn(
                  "flex items-center gap-2 rounded-md px-3 py-2 text-sm border border-transparent",
                  active ? "bg-panel text-white border-line" : "text-zinc-400 hover:text-white hover:bg-panel/60"
                )}
              >
                <Icon className="h-4 w-4" aria-hidden />
                {item.label}
              </Link>
            );
          })}
        </nav>
        <p className="mt-auto text-[11px] leading-relaxed text-zinc-500">
          Legal information, not legal advice. AI can make mistakes. Verify important information with a qualified legal professional.
        </p>
      </aside>
      <main className="min-h-screen overflow-y-auto">{children}</main>
      <aside className="border-l border-line bg-panel/40 p-4 overflow-y-auto" aria-label="Evidence inspector">
        {inspector || (
          <div>
            <p className="text-[11px] uppercase tracking-[0.2em] text-zinc-500">Evidence / AI inspector</p>
            <p className="mt-3 text-sm text-zinc-400">
              Select a clause, research hit, or case to inspect evidence, source authority, version, and freshness.
            </p>
            <div className="mt-6 space-y-3 text-xs text-zinc-500">
              <div>CLAIM → EVIDENCE → SOURCE → VERSION → FRESHNESS → CONFIDENCE</div>
              <div>No evidence → no claim.</div>
            </div>
          </div>
        )}
      </aside>
    </div>
  );
}

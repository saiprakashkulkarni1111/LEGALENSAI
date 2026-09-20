import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Legalens AI — Evidence-first legal intelligence",
  description:
    "Understand documents, discover authoritative Indian legal sources, and prepare for your next legal conversation.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="font-sans antialiased">{children}</body>
    </html>
  );
}

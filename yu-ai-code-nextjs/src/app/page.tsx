import Link from 'next/link';
import { ArrowRight, Code2, Sparkles, Zap } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-background to-muted">
      {/* Header */}
      <header className="border-b bg-background/80 backdrop-blur-sm">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-2">
            <Code2 className="h-6 w-6 text-primary" />
            <span className="text-xl font-bold">Yu AI Code Mother</span>
          </div>
          <nav className="flex items-center gap-4">
            <Link
              href="/apps"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Browse Apps
            </Link>
            <Link
              href="/login"
              className="text-sm font-medium text-muted-foreground transition-colors hover:text-foreground"
            >
              Login
            </Link>
            <Link
              href="/register"
              className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90"
            >
              Get Started
            </Link>
          </nav>
        </div>
      </header>

      {/* Hero Section */}
      <main className="container mx-auto px-4 py-20">
        <div className="mx-auto max-w-3xl text-center">
          <div className="mb-8 inline-flex items-center gap-2 rounded-full border bg-background px-4 py-1.5 text-sm">
            <Sparkles className="h-4 w-4 text-primary" />
            <span>Powered by Advanced AI</span>
          </div>

          <h1 className="mb-6 bg-gradient-to-r from-foreground to-foreground/70 bg-clip-text text-5xl font-bold leading-tight tracking-tight text-transparent md:text-6xl">
            Generate Web Applications
            <br />
            with AI Magic
          </h1>

          <p className="mb-10 text-xl text-muted-foreground">
            Describe your app idea in natural language, and watch as our AI transforms it into
            fully functional code. Built with the Australian tech stack: Next.js, PostgreSQL, and
            more.
          </p>

          <div className="flex flex-col items-center justify-center gap-4 sm:flex-row">
            <Link
              href="/apps/new"
              className="group inline-flex items-center gap-2 rounded-lg bg-primary px-6 py-3 font-medium text-primary-foreground transition-all hover:bg-primary/90 hover:shadow-lg"
            >
              Create Your First App
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </Link>
            <Link
              href="/apps"
              className="inline-flex items-center gap-2 rounded-lg border bg-background px-6 py-3 font-medium transition-all hover:bg-accent"
            >
              Explore Examples
            </Link>
          </div>
        </div>

        {/* Features Grid */}
        <div className="mx-auto mt-24 grid max-w-5xl gap-8 md:grid-cols-3">
          <FeatureCard
            icon={<Zap className="h-6 w-6" />}
            title="Lightning Fast"
            description="Generate complete applications in seconds, not hours. AI-powered code generation at its finest."
          />
          <FeatureCard
            icon={<Code2 className="h-6 w-6" />}
            title="Production Ready"
            description="Clean, maintainable code following best practices. TypeScript, Tailwind CSS, and modern tooling."
          />
          <FeatureCard
            icon={<Sparkles className="h-6 w-6" />}
            title="Smart Iteration"
            description="Refine your app through conversation. Add features, fix bugs, and improve design with AI assistance."
          />
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t py-8">
        <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
          <p>© 2025 Yu AI Code Mother. Built with Next.js, TanStack Query, and Tailwind CSS 3.</p>
        </div>
      </footer>
    </div>
  );
}

function FeatureCard({
  icon,
  title,
  description,
}: {
  icon: React.ReactNode;
  title: string;
  description: string;
}) {
  return (
    <div className="rounded-lg border bg-card p-6 transition-all hover:shadow-md">
      <div className="mb-4 inline-flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10 text-primary">
        {icon}
      </div>
      <h3 className="mb-2 text-lg font-semibold">{title}</h3>
      <p className="text-sm text-muted-foreground">{description}</p>
    </div>
  );
}

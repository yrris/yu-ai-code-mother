# Yu AI Code Mother - Next.js Frontend

> Australian Tech Stack Edition - Built with Next.js 15, React 19, TanStack Query, and Tailwind CSS 3

## 🎯 Overview

This is the modern frontend for the Yu AI Code Mother platform, refactored from Vue 3 to Next.js with the Australian technology stack.

## 🚀 Tech Stack

- **Framework**: Next.js 15.1 with App Router
- **Language**: TypeScript 5.7
- **UI Library**: React 19
- **Styling**: Tailwind CSS 3.4
- **Data Fetching**: TanStack Query (React Query) 5.62
- **HTTP Client**: Axios 1.7
- **Forms**: React Hook Form 7.54
- **Validation**: Zod 3.24
- **Icons**: Lucide React
- **Markdown**: React Markdown 9.0
- **Notifications**: Sonner 1.7

## 📦 Installation

```bash
# Install dependencies
npm install

# Copy environment variables
cp .env.local.example .env.local

# Edit .env.local with your configuration
nano .env.local
```

## 🏃‍♂️ Development

```bash
# Run development server
npm run dev

# Open http://localhost:3000
```

## 🏗️ Build

```bash
# Build for production
npm run build

# Start production server
npm start

# Type check
npm run type-check

# Lint code
npm run lint

# Format code
npm run format
```

## 📁 Project Structure

```
src/
├── app/                    # Next.js App Router
│   ├── layout.tsx         # Root layout
│   ├── page.tsx           # Home page
│   ├── providers.tsx      # React Query provider
│   └── globals.css        # Global styles
│
├── components/            # React components
│   ├── ui/               # Reusable UI components
│   └── features/         # Feature-specific components
│
├── api/                  # API client functions
│   ├── user.ts          # User API calls
│   └── app.ts           # App API calls
│
├── hooks/               # Custom React hooks
│   ├── use-queries.ts  # React Query hooks
│   └── use-sse.ts      # Server-Sent Events hook
│
├── lib/                # Utility functions
│   ├── axios.ts       # Axios instance
│   └── utils.ts       # Helper functions
│
└── types/             # TypeScript type definitions
    └── index.ts       # Shared types
```

## 🔌 API Integration

### Environment Variables

```bash
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8123/api
NEXT_PUBLIC_STATIC_URL=http://localhost:8123/api/static
```

### API Client

```typescript
import { apiClient } from '@/lib/axios';

// Automatic authentication with session cookies
const response = await apiClient.get('/user/get/login');
```

### React Query Hooks

```typescript
import { useCurrentUser, useFeaturedApps } from '@/hooks/use-queries';

function MyComponent() {
  const { data: user, isLoading } = useCurrentUser();
  const { data: apps } = useFeaturedApps({ current: 1, pageSize: 10 });

  // ...
}
```

### Server-Sent Events (SSE)

```typescript
import { useSSE } from '@/hooks/use-sse';

function ChatComponent({ appId }: { appId: number }) {
  const { data, isConnected, connect, disconnect } = useSSE({
    onMessage: (data) => {
      console.log('AI response:', data);
    },
  });

  useEffect(() => {
    connect(`/app/chat/gen/code?appId=${appId}&message=Hello`);
    return () => disconnect();
  }, [appId]);

  // ...
}
```

## 🎨 Styling with Tailwind CSS

### Utility Classes

```tsx
<button className="rounded-lg bg-primary px-4 py-2 text-primary-foreground hover:bg-primary/90">
  Click me
</button>
```

### Design Tokens

All design tokens are defined in `tailwind.config.ts`:

- **Colors**: Primary, secondary, accent, muted, destructive
- **Spacing**: Based on rem units
- **Typography**: Geist Sans and Geist Mono fonts
- **Breakpoints**: sm, md, lg, xl, 2xl

### Dark Mode

```tsx
// Dark mode is supported via class strategy
<html className="dark">
```

## 🔐 Authentication

Authentication is handled via session cookies with the backend:

```typescript
import { useLoginMutation, useLogoutMutation } from '@/hooks/use-queries';

function LoginForm() {
  const loginMutation = useLoginMutation();

  const handleSubmit = (data) => {
    loginMutation.mutate(data, {
      onSuccess: () => {
        router.push('/apps');
      },
    });
  };

  // ...
}
```

## 📊 State Management

State management is handled by:

1. **React Query**: Server state (API data)
2. **React Context**: Global UI state (theme, modals)
3. **React Hook Form**: Form state
4. **Local State**: Component-specific state

## 🧪 Code Quality

### TypeScript

All code is written in TypeScript with strict mode enabled:

```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true
  }
}
```

### ESLint

```bash
npm run lint
```

### Prettier

```bash
npm run format
```

## 🚢 Deployment

### Vercel (Recommended)

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel
```

### Docker

```dockerfile
# Dockerfile
FROM node:20-alpine AS base
FROM base AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app
COPY package*.json ./
RUN npm ci

FROM base AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM base AS runner
WORKDIR /app
ENV NODE_ENV production
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs
EXPOSE 3000
ENV PORT 3000

CMD ["node", "server.js"]
```

```bash
# Build and run
docker build -t yu-ai-nextjs .
docker run -p 3000:3000 yu-ai-nextjs
```

### Environment Variables

Set these in your deployment platform:

```bash
NEXT_PUBLIC_API_URL=https://your-api-domain.com/api
NEXT_PUBLIC_STATIC_URL=https://your-api-domain.com/api/static
```

## 🔧 Configuration

### Next.js

```typescript
// next.config.ts
const nextConfig = {
  reactStrictMode: true,
  images: {
    remotePatterns: [
      { protocol: 'https', hostname: '**' },
    ],
  },
};
```

### Tailwind CSS

```typescript
// tailwind.config.ts
const config = {
  darkMode: ['class'],
  content: ['./src/**/*.{js,ts,jsx,tsx,mdx}'],
  // ...
};
```

## 📚 Learn More

- [Next.js Documentation](https://nextjs.org/docs)
- [React Documentation](https://react.dev)
- [TanStack Query](https://tanstack.com/query/latest)
- [Tailwind CSS](https://tailwindcss.com)
- [TypeScript](https://www.typescriptlang.org)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

MIT License - Same as the original project

---

**Built with ❤️ using the Australian Tech Stack**

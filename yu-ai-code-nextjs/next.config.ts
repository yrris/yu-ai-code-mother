import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
  /* Development optimizations */
  reactStrictMode: true,

  /* API configuration */
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8123/api/:path*',
      },
    ];
  },

  /* Image optimization */
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**',
      },
    ],
  },

  /* Experimental features */
  experimental: {
    optimizePackageImports: ['lucide-react', '@tanstack/react-query'],
  },

  /* Build configuration */
  output: 'standalone',

  /* TypeScript configuration */
  typescript: {
    ignoreBuildErrors: false,
  },

  /* ESLint configuration */
  eslint: {
    ignoreDuringBuilds: false,
  },
};

export default nextConfig;

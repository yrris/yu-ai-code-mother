import { useEffect, useRef, useState, useCallback } from 'react';

export interface UseSSEOptions {
  onMessage?: (data: string, event?: string) => void;
  onError?: (error: Event) => void;
  onOpen?: (event: Event) => void;
  onClose?: () => void;
}

export interface UseSSEResult {
  data: string | null;
  error: Event | null;
  isConnected: boolean;
  connect: () => void;
  disconnect: () => void;
}

/**
 * React hook for Server-Sent Events (SSE)
 *
 * @example
 * ```tsx
 * const { data, isConnected, connect, disconnect } = useSSE({
 *   onMessage: (data) => {
 *     console.log('Received:', data);
 *   },
 * });
 *
 * // Connect to SSE endpoint
 * useEffect(() => {
 *   connect('/api/app/chat/gen/code?appId=123&message=Hello');
 *   return () => disconnect();
 * }, []);
 * ```
 */
export function useSSE(options: UseSSEOptions = {}): UseSSEResult {
  const [data, setData] = useState<string | null>(null);
  const [error, setError] = useState<Event | null>(null);
  const [isConnected, setIsConnected] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);
  const urlRef = useRef<string>('');

  const { onMessage, onError, onOpen, onClose } = options;

  const disconnect = useCallback(() => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
      setIsConnected(false);
      onClose?.();
    }
  }, [onClose]);

  const connect = useCallback(
    (url?: string) => {
      // Disconnect existing connection
      disconnect();

      const targetUrl = url || urlRef.current;
      if (!targetUrl) {
        console.error('SSE URL not provided');
        return;
      }

      urlRef.current = targetUrl;

      try {
        const eventSource = new EventSource(targetUrl, {
          withCredentials: true, // Important for session cookies
        });

        eventSource.onopen = (event) => {
          setIsConnected(true);
          setError(null);
          onOpen?.(event);
        };

        eventSource.onmessage = (event) => {
          setData(event.data);
          onMessage?.(event.data, event.type);
        };

        eventSource.onerror = (event) => {
          setError(event);
          setIsConnected(false);
          onError?.(event);

          // Auto-reconnect logic can be added here
          // For now, we close the connection on error
          disconnect();
        };

        eventSourceRef.current = eventSource;
      } catch (err) {
        console.error('Failed to create EventSource:', err);
        setError(err as Event);
      }
    },
    [disconnect, onMessage, onError, onOpen]
  );

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return {
    data,
    error,
    isConnected,
    connect,
    disconnect,
  };
}

/**
 * Parse SSE data as JSON
 */
export function parseSSEData<T = any>(data: string): T | null {
  try {
    return JSON.parse(data) as T;
  } catch {
    return null;
  }
}

/**
 * Hook for SSE with automatic JSON parsing
 */
export function useSSEJSON<T = any>(
  options: UseSSEOptions & {
    onParsedMessage?: (data: T) => void;
  } = {}
) {
  const [parsedData, setParsedData] = useState<T | null>(null);

  const { onParsedMessage, ...sseOptions } = options;

  const sse = useSSE({
    ...sseOptions,
    onMessage: (data, event) => {
      const parsed = parseSSEData<T>(data);
      if (parsed) {
        setParsedData(parsed);
        onParsedMessage?.(parsed);
      }
      sseOptions.onMessage?.(data, event);
    },
  });

  return {
    ...sse,
    parsedData,
  };
}

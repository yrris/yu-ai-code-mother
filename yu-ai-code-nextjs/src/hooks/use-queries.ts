import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as userApi from '@/api/user';
import * as appApi from '@/api/app';
import type {
  AppAddRequest,
  AppQueryRequest,
  AppUpdateRequest,
  UserLoginRequest,
  UserRegisterRequest,
} from '@/types';
import { toast } from 'sonner';

/**
 * Query keys for React Query
 */
export const queryKeys = {
  user: {
    current: ['user', 'current'] as const,
  },
  app: {
    all: ['apps'] as const,
    detail: (id: number) => ['app', id] as const,
    my: (params: AppQueryRequest) => ['apps', 'my', params] as const,
    featured: (params: AppQueryRequest) => ['apps', 'featured', params] as const,
  },
};

/**
 * Get current user query
 */
export function useCurrentUser() {
  return useQuery({
    queryKey: queryKeys.user.current,
    queryFn: userApi.getCurrentUser,
    retry: false,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

/**
 * User login mutation
 */
export function useLoginMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UserLoginRequest) => userApi.userLogin(data),
    onSuccess: (user) => {
      queryClient.setQueryData(queryKeys.user.current, user);
      toast.success('Login successful!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Login failed');
    },
  });
}

/**
 * User register mutation
 */
export function useRegisterMutation() {
  return useMutation({
    mutationFn: (data: UserRegisterRequest) => userApi.userRegister(data),
    onSuccess: () => {
      toast.success('Registration successful! Please login.');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Registration failed');
    },
  });
}

/**
 * User logout mutation
 */
export function useLogoutMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: userApi.userLogout,
    onSuccess: () => {
      queryClient.setQueryData(queryKeys.user.current, null);
      queryClient.clear();
      toast.success('Logged out successfully');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Logout failed');
    },
  });
}

/**
 * Get app by ID query
 */
export function useApp(id: number) {
  return useQuery({
    queryKey: queryKeys.app.detail(id),
    queryFn: () => appApi.getAppById(id),
    enabled: !!id,
  });
}

/**
 * Get my apps query (paginated)
 */
export function useMyApps(params: AppQueryRequest) {
  return useQuery({
    queryKey: queryKeys.app.my(params),
    queryFn: () => appApi.getMyApps(params),
  });
}

/**
 * Get featured apps query (paginated)
 */
export function useFeaturedApps(params: AppQueryRequest) {
  return useQuery({
    queryKey: queryKeys.app.featured(params),
    queryFn: () => appApi.getFeaturedApps(params),
  });
}

/**
 * Create app mutation
 */
export function useCreateAppMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AppAddRequest) => appApi.createApp(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.app.all });
      toast.success('App created successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create app');
    },
  });
}

/**
 * Update app mutation
 */
export function useUpdateAppMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: AppUpdateRequest) => appApi.updateApp(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.app.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.app.all });
      toast.success('App updated successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update app');
    },
  });
}

/**
 * Delete app mutation
 */
export function useDeleteAppMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => appApi.deleteApp(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.app.all });
      toast.success('App deleted successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete app');
    },
  });
}

/**
 * Deploy app mutation
 */
export function useDeployAppMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => appApi.deployApp(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.app.detail(id) });
      toast.success('App deployed successfully!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to deploy app');
    },
  });
}

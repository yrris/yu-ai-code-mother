import { apiClient, ApiResponse, extractData } from '@/lib/axios';
import type {
  AppVO,
  AppAddRequest,
  AppUpdateRequest,
  AppQueryRequest,
  PageResponse,
} from '@/types';

/**
 * Get app by ID
 */
export async function getAppById(id: number): Promise<AppVO> {
  const response = await apiClient.get<ApiResponse<AppVO>>(`/app/get/vo`, {
    params: { id },
  });
  return extractData(response.data);
}

/**
 * Create new app
 */
export async function createApp(data: AppAddRequest): Promise<number> {
  const response = await apiClient.post<ApiResponse<number>>('/app/add', data);
  return extractData(response.data);
}

/**
 * Update app
 */
export async function updateApp(data: AppUpdateRequest): Promise<boolean> {
  const response = await apiClient.post<ApiResponse<boolean>>('/app/update', data);
  return extractData(response.data);
}

/**
 * Delete app
 */
export async function deleteApp(id: number): Promise<boolean> {
  const response = await apiClient.post<ApiResponse<boolean>>('/app/delete', { id });
  return extractData(response.data);
}

/**
 * Get my apps (paginated)
 */
export async function getMyApps(params: AppQueryRequest): Promise<PageResponse<AppVO>> {
  const response = await apiClient.post<ApiResponse<PageResponse<AppVO>>>(
    '/app/my/list/page/vo',
    params
  );
  return extractData(response.data);
}

/**
 * Get featured apps (paginated)
 */
export async function getFeaturedApps(params: AppQueryRequest): Promise<PageResponse<AppVO>> {
  const response = await apiClient.post<ApiResponse<PageResponse<AppVO>>>(
    '/app/good/list/page/vo',
    params
  );
  return extractData(response.data);
}

/**
 * Deploy app
 */
export async function deployApp(id: number): Promise<string> {
  const response = await apiClient.post<ApiResponse<string>>('/app/deploy', { id });
  return extractData(response.data);
}

/**
 * Download app source code
 */
export function getDownloadUrl(appId: number): string {
  return `${apiClient.defaults.baseURL}/app/download/${appId}`;
}

/**
 * Get deployed app URL
 */
export function getDeployedAppUrl(deployKey: string): string {
  return `${apiClient.defaults.baseURL}/static/${deployKey}/index.html`;
}

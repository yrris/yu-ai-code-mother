import { apiClient, ApiResponse, extractData } from '@/lib/axios';
import type { LoginUserVO, UserLoginRequest, UserRegisterRequest } from '@/types';

/**
 * User login
 */
export async function userLogin(data: UserLoginRequest): Promise<LoginUserVO> {
  const response = await apiClient.post<ApiResponse<LoginUserVO>>('/user/login', data);
  return extractData(response.data);
}

/**
 * User register
 */
export async function userRegister(data: UserRegisterRequest): Promise<number> {
  const response = await apiClient.post<ApiResponse<number>>('/user/register', data);
  return extractData(response.data);
}

/**
 * User logout
 */
export async function userLogout(): Promise<boolean> {
  const response = await apiClient.post<ApiResponse<boolean>>('/user/logout');
  return extractData(response.data);
}

/**
 * Get current logged-in user
 */
export async function getCurrentUser(): Promise<LoginUserVO> {
  const response = await apiClient.get<ApiResponse<LoginUserVO>>('/user/get/login');
  return extractData(response.data);
}

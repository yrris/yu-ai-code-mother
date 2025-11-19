/**
 * User role enumeration
 */
export enum UserRole {
  USER = 'user',
  ADMIN = 'admin',
}

/**
 * Code generation type enumeration
 */
export enum CodeGenType {
  HTML = 'html',
  MULTI_FILE = 'multi_file',
  VUE_PROJECT = 'vue_project',
}

/**
 * Message type enumeration
 */
export enum MessageType {
  USER = 'user',
  AI = 'ai',
}

/**
 * User entity
 */
export interface User {
  id: number;
  userAccount: string;
  userName?: string;
  userAvatar?: string;
  userProfile?: string;
  userRole: UserRole;
  createTime: string;
}

/**
 * Login user VO (View Object)
 */
export interface LoginUserVO {
  id: number;
  userAccount: string;
  userName?: string;
  userAvatar?: string;
  userProfile?: string;
  userRole: UserRole;
}

/**
 * App entity
 */
export interface App {
  id: number;
  appName?: string;
  cover?: string;
  initPrompt?: string;
  codeGenType?: CodeGenType;
  deployKey?: string;
  deployedTime?: string;
  priority: number;
  userId: number;
  createTime: string;
  updateTime: string;
  user?: User;
}

/**
 * App View Object
 */
export interface AppVO {
  id: number;
  appName?: string;
  cover?: string;
  initPrompt?: string;
  codeGenType?: CodeGenType;
  deployKey?: string;
  deployedTime?: string;
  priority: number;
  userId: number;
  createTime: string;
  updateTime: string;
  user?: User;
}

/**
 * Chat history entity
 */
export interface ChatHistory {
  id: number;
  message: string;
  messageType: MessageType;
  appId: number;
  userId: number;
  createTime: string;
}

/**
 * Pagination request
 */
export interface PageRequest {
  current?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: 'ascend' | 'descend';
}

/**
 * Pagination response
 */
export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/**
 * User login request
 */
export interface UserLoginRequest {
  userAccount: string;
  userPassword: string;
}

/**
 * User register request
 */
export interface UserRegisterRequest {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}

/**
 * App add request
 */
export interface AppAddRequest {
  appName?: string;
  initPrompt?: string;
  codeGenType?: CodeGenType;
}

/**
 * App update request
 */
export interface AppUpdateRequest {
  id: number;
  appName?: string;
  cover?: string;
  initPrompt?: string;
  codeGenType?: CodeGenType;
}

/**
 * App query request
 */
export interface AppQueryRequest extends PageRequest {
  id?: number;
  appName?: string;
  userId?: number;
  codeGenType?: CodeGenType;
}

/**
 * Stream message types
 */
export enum StreamMessageType {
  AI_RESPONSE = 'ai_response',
  TOOL_REQUEST = 'tool_request',
  TOOL_EXECUTED = 'tool_executed',
}

/**
 * Stream message
 */
export interface StreamMessage {
  type: StreamMessageType;
  content: string;
  timestamp?: number;
}

/**
 * SSE (Server-Sent Events) message
 */
export interface SSEMessage {
  data: string;
  event?: string;
  id?: string;
  retry?: number;
}

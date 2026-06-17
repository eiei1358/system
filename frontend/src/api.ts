import axios from 'axios'

const API_BASE_URL = '/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  userId: number
  name: string
  email: string
  role: number
  department: string
  previousLoginAt: string
  requiresPasswordChange: boolean
  sessionToken: string
}

export interface ItemDTO {
  itemId?: number
  userId: number
  userName?: string
  name: string
  description: string
  condition: number
  price: number
  type: number
  status: number
  categoryId: number
  categoryName?: string
  deadline: string
  createdAt?: string
  place: number
  negotiable: boolean
  reported?: boolean
  imageUrls: string[]
}

export interface TransactionDTO {
  transactionId: number
  itemId: number
  itemName: string
  sellerId: number
  sellerName: string
  buyerId: number
  buyerName: string
  status: number
  completedAt: string | null
  sellerCompleted: boolean
  buyerCompleted: boolean
  transferCode: string
}

export interface MessageDTO {
  messageId: number
  senderId: number
  senderName: string
  receiverId: number
  receiverName: string
  itemId: number | null
  content: string
  createdAt: string
  isAdminMessage: boolean
  status: number
}

export const authAPI = {
  login: (data: LoginRequest) => apiClient.post<LoginResponse>('/auth/login', data),
  changePassword: (userId: number, oldPassword: string, newPassword: string) =>
    apiClient.post('/auth/password-change', null, { params: { userId, oldPassword, newPassword } }),
  resetPasswordRequest: (email: string) =>
    apiClient.post('/auth/password-reset-request', null, { params: { email } }),
}

export const userAPI = {
  getUser: (userId: number) => apiClient.get(`/users/${userId}`),
  importCSV: (csvData: string) => apiClient.post('/users/csv-import', { csvData }),
  updateStatus: (userId: number, status: number) => apiClient.put(`/users/${userId}/status`, { status }),
  updateInfo: (userId: number, name: string, department: string, iconUrl: string) =>
    apiClient.put(`/users/${userId}/info`, { userId, name, department, iconUrl }),
}

export const itemAPI = {
  createItem: (data: ItemDTO) => apiClient.post<ItemDTO>('/items', data),
  getItem: (itemId: number) => apiClient.get<ItemDTO>(`/items/${itemId}`),
  updateItem: (itemId: number, data: ItemDTO) => apiClient.put<ItemDTO>(`/items/${itemId}`, data),
  searchItems: (categoryId?: number, keyword?: string, condition?: number, statusList?: string, sortBy?: string, includeExpired?: boolean) =>
    apiClient.get<ItemDTO[]>('/items/search', { params: { categoryId, keyword, condition, statusList, sortBy, includeExpired } }),
  extendDeadline: (itemId: number, newDeadline: string) =>
    apiClient.post(`/items/${itemId}/extend-deadline`, { newDeadline }),
  reportItem: (itemId: number) => apiClient.post(`/items/${itemId}/report`),
}

export const applicationAPI = {
  createApplication: (itemId: number, userId: number, bidPrice?: number) =>
    apiClient.post('/applications', { itemId, userId, bidPrice }),
  finalizeAuction: (itemId: number) => apiClient.post(`/applications/${itemId}/finalize-auction`),
  getApplicationsByItem: (itemId: number) => apiClient.get(`/applications/item/${itemId}`),
  getApplicationsByUser: (userId: number) => apiClient.get(`/applications/user/${userId}`),
}

export const transactionAPI = {
  getTransaction: (transactionId: number) => apiClient.get<TransactionDTO>(`/transactions/${transactionId}`),
  getTransactionByCode: (transferCode: string) => apiClient.get<TransactionDTO>(`/transactions/code/${transferCode}`),
  markSellerComplete: (transactionId: number, userId: number) =>
    apiClient.post(`/transactions/${transactionId}/seller-complete`, null, { params: { userId } }),
  markBuyerComplete: (transactionId: number, userId: number) =>
    apiClient.post(`/transactions/${transactionId}/buyer-complete`, null, { params: { userId } }),
  getTransactionsBySeller: (sellerId: number) => apiClient.get(`/transactions/seller/${sellerId}`),
  getTransactionsByBuyer: (buyerId: number) => apiClient.get(`/transactions/buyer/${buyerId}`),
  generateTransferCode: (transactionId: number) => apiClient.post(`/transactions/${transactionId}/generate-transfer-code`),
}

export const messageAPI = {
  sendMessage: (senderId: number, receiverId: number, itemId: number | null, content: string) =>
    apiClient.post<MessageDTO>('/messages', { senderId, receiverId, itemId, content }),
  getMessages: (userId: number, otherUserId: number, itemId: number) =>
    apiClient.get<MessageDTO[]>('/messages/thread', { params: { userId, otherUserId, itemId } }),
  reportMessage: (messageId: number) => apiClient.post(`/messages/${messageId}/report`),
  deleteMessage: (messageId: number, userId: number) =>
    apiClient.delete(`/messages/${messageId}`, { params: { userId } }),
}

export const dashboardAPI = {
  getStats: () => apiClient.get('/dashboard/stats'),
  getConditionBreakdown: () => apiClient.get('/dashboard/condition-breakdown'),
  getTypeBreakdown: () => apiClient.get('/dashboard/type-breakdown'),
  getRecentTransactions: (limit?: number) => apiClient.get('/dashboard/recent-transactions', { params: { limit } }),
  getUserActivity: () => apiClient.get('/dashboard/user-activity'),
}

export default apiClient

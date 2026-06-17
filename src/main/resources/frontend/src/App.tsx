import React, { useState, useEffect } from 'react'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import ItemListPage from './pages/ItemListPage'
import ItemDetailPage from './pages/ItemDetailPage'
import CreateItemPage from './pages/CreateItemPage'
import TransactionPage from './pages/TransactionPage'
import TransactionStatusPage from './pages/TransactionStatusPage'
import UserSettingsPage from './pages/UserSettingsPage'

type CurrentPage = 'login' | 'dashboard' | 'items' | 'item-detail' | 'create-item' | 'transaction' | 'transaction-status' | 'settings'

interface User {
  userId: number
  name: string
  email: string
  role: number
  department: string
  requiresPasswordChange: boolean
}

export default function App() {
  const [currentPage, setCurrentPage] = useState<CurrentPage>('login')
  const [user, setUser] = useState<User | null>(null)
  const [selectedItemId, setSelectedItemId] = useState<number | null>(null)
  const [selectedTransactionId, setSelectedTransactionId] = useState<number | null>(null)
  const [transferCode, setTransferCode] = useState<string | null>(null)

  useEffect(() => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      const userData = JSON.parse(savedUser)
      setUser(userData)
      if (userData.requiresPasswordChange) {
        setCurrentPage('settings')
      } else {
        setCurrentPage('dashboard')
      }
    }
  }, [])

  const handleLogin = (userData: User) => {
    setUser(userData)
    localStorage.setItem('user', JSON.stringify(userData))
    if (userData.requiresPasswordChange) {
      setCurrentPage('settings')
    } else {
      setCurrentPage('dashboard')
    }
  }

  const handleLogout = () => {
    setUser(null)
    localStorage.removeItem('user')
    setCurrentPage('login')
  }

  const navigateTo = (page: string, itemId?: number, transactionId?: number, code?: string) => {
    setCurrentPage(page as CurrentPage)
    if (itemId) setSelectedItemId(itemId)
    if (transactionId) setSelectedTransactionId(transactionId)
    if (code) setTransferCode(code)
  }

  if (!user) {
    return <LoginPage onLogin={handleLogin} />
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-lg sticky top-0 z-50">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold text-primary">フリマ</h1>
          <div className="flex gap-4 items-center">
            <button onClick={() => navigateTo('dashboard')} className="hover:text-primary transition">ダッシュボード</button>
            <button onClick={() => navigateTo('items')} className="hover:text-primary transition">物品一覧</button>
            <button onClick={() => navigateTo('create-item')} className="hover:text-primary transition">出品</button>
            <button onClick={() => navigateTo('settings')} className="hover:text-primary transition">設定</button>
            <button onClick={handleLogout} className="px-4 py-2 bg-danger text-white rounded hover:bg-red-600 transition">ログアウト</button>
          </div>
        </div>
      </nav>

      <div className="container mx-auto px-4 py-8">
        {currentPage === 'dashboard' && <DashboardPage user={user} onNavigate={navigateTo} />}
        {currentPage === 'items' && <ItemListPage user={user} onNavigate={navigateTo} />}
        {currentPage === 'item-detail' && selectedItemId && <ItemDetailPage itemId={selectedItemId} user={user} onNavigate={navigateTo} />}
        {currentPage === 'create-item' && <CreateItemPage user={user} onNavigate={navigateTo} />}
        {currentPage === 'transaction' && selectedTransactionId && <TransactionPage transactionId={selectedTransactionId} user={user} onNavigate={navigateTo} />}
        {currentPage === 'transaction-status' && transferCode && <TransactionStatusPage transferCode={transferCode} />}
        {currentPage === 'settings' && <UserSettingsPage user={user} onNavigate={navigateTo} onLogout={handleLogout} />}
      </div>
    </div>
  )
}

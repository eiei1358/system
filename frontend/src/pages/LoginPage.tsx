import React, { useState } from 'react'
import { authAPI, LoginResponse } from '../api'

interface Props {
  onLogin: (user: any) => void
}

export default function LoginPage({ onLogin }: Props) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [showReset, setShowReset] = useState(false)
  const [resetEmail, setResetEmail] = useState('')

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      const response = await authAPI.login({ email, password })
      const data = response.data as LoginResponse
      onLogin({
        userId: data.userId,
        name: data.name,
        email: data.email,
        role: data.role,
        department: data.department,
        requiresPasswordChange: data.requiresPasswordChange,
        sessionToken: data.sessionToken,
      })
    } catch (err: any) {
      setError(err.response?.data?.message || 'ログインに失敗しました')
    } finally {
      setIsLoading(false)
    }
  }

  const handlePasswordReset = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)

    try {
      await authAPI.resetPasswordRequest(resetEmail)
      alert('仮パスワードがメール送信されました')
      setShowReset(false)
      setResetEmail('')
    } catch (err: any) {
      setError(err.response?.data?.message || 'パスワードリセットに失敗しました')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary to-secondary px-4">
      <div className="bg-white rounded-lg shadow-2xl p-8 w-full max-w-md">
        <h1 className="text-3xl font-bold text-center mb-8 text-primary">フリマ社内システム</h1>

        {!showReset ? (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">メールアドレス</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                placeholder="user@sample.com"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">パスワード</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                placeholder="••••••••"
                required
              />
            </div>

            {error && <div className="p-4 bg-red-50 border border-red-200 text-red-700 rounded">{error}</div>}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-primary text-white py-2 rounded-lg hover:bg-purple-700 transition disabled:opacity-50"
            >
              {isLoading ? 'ログイン中...' : 'ログイン'}
            </button>

            <button
              type="button"
              onClick={() => setShowReset(true)}
              className="w-full text-center text-primary hover:underline text-sm"
            >
              パスワードをお忘れですか？
            </button>
          </form>
        ) : (
          <form onSubmit={handlePasswordReset} className="space-y-6">
            <p className="text-gray-600 text-sm">メールアドレスを入力して、パスワードリセットリンクを受け取ります。</p>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">メールアドレス</label>
              <input
                type="email"
                value={resetEmail}
                onChange={(e) => setResetEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                placeholder="user@sample.com"
                required
              />
            </div>

            {error && <div className="p-4 bg-red-50 border border-red-200 text-red-700 rounded">{error}</div>}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full bg-primary text-white py-2 rounded-lg hover:bg-purple-700 transition disabled:opacity-50"
            >
              {isLoading ? 'リセット中...' : 'リセットリンクを送信'}
            </button>

            <button
              type="button"
              onClick={() => setShowReset(false)}
              className="w-full text-center text-primary hover:underline text-sm"
            >
              ログイン画面に戻る
            </button>
          </form>
        )}
      </div>
    </div>
  )
}

import React, { useState } from 'react'
import { authAPI, userAPI } from '../api'

interface Props {
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
  onLogout: () => void
}

export default function UserSettingsPage({ user, onNavigate, onLogout }: Props) {
  const [activeTab, setActiveTab] = useState<'password' | 'profile'>('password')
  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  const [profileForm, setProfileForm] = useState({
    name: user.name,
    department: user.department,
    iconUrl: '',
  })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null)

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault()
    setMessage(null)
    setIsSubmitting(true)

    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setMessage({ type: 'error', text: '新しいパスワードが一致しません' })
      setIsSubmitting(false)
      return
    }

    if (passwordForm.newPassword.length < 6) {
      setMessage({ type: 'error', text: 'パスワードは6文字以上である必要があります' })
      setIsSubmitting(false)
      return
    }

    try {
      await authAPI.changePassword(user.userId, passwordForm.oldPassword, passwordForm.newPassword)
      setMessage({ type: 'success', text: 'パスワードが正常に変更されました' })
      setPasswordForm({ oldPassword: '', newPassword: '', confirmPassword: '' })
      setTimeout(() => onNavigate('dashboard'), 1500)
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'パスワード変更に失敗しました' })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault()
    setMessage(null)
    setIsSubmitting(true)

    try {
      await userAPI.updateInfo(user.userId, profileForm.name, profileForm.department, profileForm.iconUrl)
      setMessage({ type: 'success', text: 'プロフィールが正常に更新されました' })
      setTimeout(() => onNavigate('dashboard'), 1500)
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'プロフィール更新に失敗しました' })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">設定</h1>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="flex border-b">
          <button
            onClick={() => setActiveTab('password')}
            className={`flex-1 py-4 px-6 font-semibold text-center ${activeTab === 'password' ? 'border-b-4 border-primary text-primary' : 'text-gray-600'}`}
          >
            パスワード変更
          </button>
          <button
            onClick={() => setActiveTab('profile')}
            className={`flex-1 py-4 px-6 font-semibold text-center ${activeTab === 'profile' ? 'border-b-4 border-primary text-primary' : 'text-gray-600'}`}
          >
            プロフィール
          </button>
        </div>

        <div className="p-8">
          {activeTab === 'password' && (
            <form onSubmit={handlePasswordChange} className="space-y-6 max-w-md">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">現在のパスワード</label>
                <input
                  type="password"
                  value={passwordForm.oldPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, oldPassword: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">新しいパスワード</label>
                <input
                  type="password"
                  value={passwordForm.newPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">新しいパスワード（確認）</label>
                <input
                  type="password"
                  value={passwordForm.confirmPassword}
                  onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              {message && (
                <div className={`p-4 rounded-lg ${message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
                  {message.text}
                </div>
              )}

              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-primary text-white py-2 rounded-lg hover:bg-purple-700 transition disabled:opacity-50"
              >
                {isSubmitting ? '変更中...' : 'パスワードを変更'}
              </button>
            </form>
          )}

          {activeTab === 'profile' && (
            <form onSubmit={handleProfileUpdate} className="space-y-6 max-w-md">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">氏名</label>
                <input
                  type="text"
                  value={profileForm.name}
                  onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">所属部門</label>
                <input
                  type="text"
                  value={profileForm.department}
                  onChange={(e) => setProfileForm({ ...profileForm, department: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">プロフィール画像URL</label>
                <input
                  type="url"
                  value={profileForm.iconUrl}
                  onChange={(e) => setProfileForm({ ...profileForm, iconUrl: e.target.value })}
                  placeholder="https://example.com/image.jpg"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              {message && (
                <div className={`p-4 rounded-lg ${message.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200'}`}>
                  {message.text}
                </div>
              )}

              <button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-primary text-white py-2 rounded-lg hover:bg-purple-700 transition disabled:opacity-50"
              >
                {isSubmitting ? '更新中...' : 'プロフィールを更新'}
              </button>
            </form>
          )}
        </div>
      </div>

      <div className="mt-8">
        <button
          onClick={onLogout}
          className="w-full px-4 py-2 bg-danger text-white rounded-lg hover:bg-red-600 transition font-semibold"
        >
          ログアウト
        </button>
      </div>
    </div>
  )
}

import React, { useState, useEffect } from 'react'
import { dashboardAPI } from '../api'

interface Props {
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
}

export default function DashboardPage({ user, onNavigate }: Props) {
  const [stats, setStats] = useState<any>(null)
  const [conditionData, setConditionData] = useState<any[]>([])
  const [typeData, setTypeData] = useState<any[]>([])
  const [recentTransactions, setRecentTransactions] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [statsRes, conditionRes, typeRes, txRes] = await Promise.all([
          dashboardAPI.getStats(),
          dashboardAPI.getConditionBreakdown(),
          dashboardAPI.getTypeBreakdown(),
          dashboardAPI.getRecentTransactions(10),
        ])

        setStats(statsRes.data)
        setConditionData(conditionRes.data)
        setTypeData(typeRes.data)
        setRecentTransactions(txRes.data)
      } catch (err) {
        console.error('Failed to fetch stats', err)
      } finally {
        setIsLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (isLoading) {
    return <div className="text-center py-8">読み込み中...</div>
  }

  const totalItems = stats?.totalItems || 0
  const soldItems = stats?.soldItems || 0
  const conversionRate = stats?.conversionRate || 0

  return (
    <div className="space-y-8">
      <h1 className="text-3xl font-bold text-gray-800">ダッシュボード</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500">総出品数</h3>
          <p className="text-3xl font-bold text-primary mt-2">{totalItems}</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500">成約数</h3>
          <p className="text-3xl font-bold text-success mt-2">{soldItems}</p>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-sm font-medium text-gray-500">成約率</h3>
          <p className="text-3xl font-bold text-info mt-2">{conversionRate.toFixed(1)}%</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">物品の状態別分布</h3>
          <div className="space-y-3">
            {conditionData.map((item: any) => (
              <div key={item.condition} className="flex items-center">
                <span className="w-24 text-sm">{item.label}</span>
                <div className="flex-1 bg-gray-200 rounded-full h-4 mx-4">
                  <div
                    className="bg-primary rounded-full h-4"
                    style={{ width: `${(item.count / Math.max(...conditionData.map((d: any) => d.count))) * 100}%` }}
                  />
                </div>
                <span className="w-8 text-right text-sm font-semibold">{item.count}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">物品のタイプ別分布</h3>
          <div className="space-y-3">
            {typeData.map((item: any) => (
              <div key={item.type} className="flex items-center">
                <span className="w-24 text-sm">{item.label}</span>
                <div className="flex-1 bg-gray-200 rounded-full h-4 mx-4">
                  <div
                    className="bg-secondary rounded-full h-4"
                    style={{ width: `${(item.count / Math.max(...typeData.map((d: any) => d.count))) * 100}%` }}
                  />
                </div>
                <span className="w-8 text-right text-sm font-semibold">{item.count}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">最近の取引</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-2 text-left font-medium">物品</th>
                <th className="px-4 py-2 text-left font-medium">出品者</th>
                <th className="px-4 py-2 text-left font-medium">購入者</th>
                <th className="px-4 py-2 text-left font-medium">完了日時</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {recentTransactions.map((tx: any) => (
                <tr key={tx.transactionId} className="hover:bg-gray-50">
                  <td className="px-4 py-2">{tx.itemName}</td>
                  <td className="px-4 py-2">{tx.sellerName}</td>
                  <td className="px-4 py-2">{tx.buyerName}</td>
                  <td className="px-4 py-2">{new Date(tx.completedAt).toLocaleDateString('ja-JP')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

import React, { useState, useEffect } from 'react'
import { transactionAPI, TransactionDTO } from '../api'

interface Props {
  transferCode: string
}

export default function TransactionStatusPage({ transferCode }: Props) {
  const [transaction, setTransaction] = useState<TransactionDTO | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchTransaction = async () => {
      try {
        const res = await transactionAPI.getTransactionByCode(transferCode)
        setTransaction(res.data)
      } catch (err: any) {
        setError(err.response?.data?.message || '取引が見つかりません')
      } finally {
        setIsLoading(false)
      }
    }

    fetchTransaction()
  }, [transferCode])

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <p className="text-lg text-gray-600">読み込み中...</p>
        </div>
      </div>
    )
  }

  if (error || !transaction) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
        <div className="bg-white rounded-lg shadow p-8 max-w-md w-full text-center">
          <p className="text-red-600 font-semibold text-lg">{error}</p>
        </div>
      </div>
    )
  }

  const isCompleted = transaction.status === 3
  const progressPercent = transaction.sellerCompleted && transaction.buyerCompleted ? 100 : (transaction.sellerCompleted || transaction.buyerCompleted) ? 50 : 0

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary to-secondary p-4 flex items-center justify-center">
      <div className="bg-white rounded-lg shadow-2xl p-8 max-w-md w-full">
        <h1 className="text-2xl font-bold text-center mb-8 text-gray-800">取引進捗状況</h1>

        <div className="space-y-6">
          <div>
            <p className="text-sm text-gray-600 mb-2">物品</p>
            <p className="text-lg font-semibold text-gray-900">{transaction.itemName}</p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-xs text-gray-600 mb-1">出品者</p>
              <p className="text-sm font-semibold">{transaction.sellerName}</p>
            </div>
            <div>
              <p className="text-xs text-gray-600 mb-1">購入者</p>
              <p className="text-sm font-semibold">{transaction.buyerName}</p>
            </div>
          </div>

          <div className="bg-gray-200 rounded-full h-2">
            <div
              className="bg-success rounded-full h-2 transition-all"
              style={{ width: `${progressPercent}%` }}
            />
          </div>

          <div className="space-y-3">
            <div className="p-4 rounded-lg border-2" style={{ borderColor: transaction.sellerCompleted ? '#10b981' : '#d1d5db', backgroundColor: transaction.sellerCompleted ? '#ecfdf5' : '#f9fafb' }}>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-sm">出品者が譲渡完了</span>
                {transaction.sellerCompleted && <span className="text-2xl">✓</span>}
              </div>
            </div>

            <div className="p-4 rounded-lg border-2" style={{ borderColor: transaction.buyerCompleted ? '#10b981' : '#d1d5db', backgroundColor: transaction.buyerCompleted ? '#ecfdf5' : '#f9fafb' }}>
              <div className="flex items-center justify-between">
                <span className="font-semibold text-sm">購入者が受取完了</span>
                {transaction.buyerCompleted && <span className="text-2xl">✓</span>}
              </div>
            </div>
          </div>

          {isCompleted && (
            <div className="bg-green-50 border-2 border-green-500 rounded-lg p-4 text-center">
              <p className="text-2xl font-bold text-green-600">✓</p>
              <p className="text-green-700 font-semibold mt-2">取引完了</p>
              <p className="text-sm text-green-600 mt-1">{new Date(transaction.completedAt!).toLocaleString('ja-JP')}</p>
            </div>
          )}

          {!isCompleted && (
            <div className="bg-blue-50 border-2 border-blue-500 rounded-lg p-4 text-center">
              <p className="text-blue-700 font-semibold">取引進行中</p>
              <p className="text-sm text-blue-600 mt-1">双方が完了を確認するまで続きます</p>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

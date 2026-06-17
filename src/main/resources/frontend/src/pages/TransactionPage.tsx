import React, { useState, useEffect } from 'react'
import { transactionAPI, TransactionDTO } from '../api'

interface Props {
  transactionId: number
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
}

export default function TransactionPage({ transactionId, user, onNavigate }: Props) {
  const [transaction, setTransaction] = useState<TransactionDTO | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [qrCode, setQrCode] = useState<string | null>(null)

  useEffect(() => {
    const fetchTransaction = async () => {
      try {
        const res = await transactionAPI.getTransaction(transactionId)
        setTransaction(res.data)

        if (!res.data.transferCode) {
          const codeRes = await transactionAPI.generateTransferCode(transactionId)
          setTransaction(prev => prev ? { ...prev, transferCode: codeRes.data.transferCode } : null)
          generateQRCode(codeRes.data.transferCode)
        } else {
          generateQRCode(res.data.transferCode)
        }
      } catch (err) {
        console.error('Failed to fetch transaction', err)
      } finally {
        setIsLoading(false)
      }
    }

    fetchTransaction()
  }, [transactionId])

  const generateQRCode = (code: string) => {
    const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(`https://furima.local/transaction-status/${code}`)}`
    setQrCode(qrUrl)
  }

  const handleCompleteAsCurrentUser = async () => {
    if (!transaction) return

    setIsSubmitting(true)
    try {
      const isSeller = transaction.sellerId === user.userId
      if (isSeller) {
        await transactionAPI.markSellerComplete(transactionId, user.userId)
      } else {
        await transactionAPI.markBuyerComplete(transactionId, user.userId)
      }

      const res = await transactionAPI.getTransaction(transactionId)
      setTransaction(res.data)
      alert(isSeller ? '出品者が譲渡完了を確認しました' : '購入者が受取完了を確認しました')
    } catch (err: any) {
      alert(err.response?.data?.message || '確認に失敗しました')
    } finally {
      setIsSubmitting(false)
    }
  }

  if (isLoading || !transaction) {
    return <div className="text-center py-8">読み込み中...</div>
  }

  const isSeller = transaction.sellerId === user.userId
  const isBuyer = transaction.buyerId === user.userId
  const isCompleted = transaction.status === 3

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">取引詳細</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="space-y-6">
          <div className="bg-white rounded-lg shadow p-6 space-y-4">
            <h2 className="text-lg font-semibold">取引情報</h2>

            <div>
              <p className="text-gray-600 text-sm">物品</p>
              <p className="text-lg font-semibold">{transaction.itemName}</p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-gray-600 text-sm">出品者</p>
                <p className="font-semibold">{transaction.sellerName}</p>
              </div>
              <div>
                <p className="text-gray-600 text-sm">購入者</p>
                <p className="font-semibold">{transaction.buyerName}</p>
              </div>
            </div>

            <div>
              <p className="text-gray-600 text-sm">ステータス</p>
              <p className="text-lg font-semibold">{transaction.status === 1 ? '成約' : transaction.status === 2 ? '進行中' : '完了'}</p>
            </div>

            {transaction.completedAt && (
              <div>
                <p className="text-gray-600 text-sm">完了日時</p>
                <p className="text-lg font-semibold">{new Date(transaction.completedAt).toLocaleString('ja-JP')}</p>
              </div>
            )}
          </div>

          {!isCompleted && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">2ステップ譲渡確認</h2>

              <div className="space-y-4">
                <div className="p-4 rounded-lg border-2" style={{ borderColor: transaction.sellerCompleted ? '#10b981' : '#d1d5db', backgroundColor: transaction.sellerCompleted ? '#f0fdf4' : '#f9fafb' }}>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-semibold">出品者が譲渡完了</p>
                      <p className="text-sm text-gray-600">{transaction.sellerName}が譲渡を完了した確認</p>
                    </div>
                    {transaction.sellerCompleted && <span className="text-2xl">✓</span>}
                  </div>
                </div>

                <div className="p-4 rounded-lg border-2" style={{ borderColor: transaction.buyerCompleted ? '#10b981' : '#d1d5db', backgroundColor: transaction.buyerCompleted ? '#f0fdf4' : '#f9fafb' }}>
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="font-semibold">購入者が受取完了</p>
                      <p className="text-sm text-gray-600">{transaction.buyerName}が受け取りを完了した確認</p>
                    </div>
                    {transaction.buyerCompleted && <span className="text-2xl">✓</span>}
                  </div>
                </div>
              </div>

              {(isSeller || isBuyer) && (
                <button
                  onClick={handleCompleteAsCurrentUser}
                  disabled={isSubmitting || (isSeller && transaction.sellerCompleted) || (isBuyer && transaction.buyerCompleted)}
                  className="w-full mt-6 px-4 py-3 bg-success text-white rounded-lg hover:bg-green-600 transition disabled:opacity-50"
                >
                  {isSubmitting ? '確認中...' : isSeller ? '譲渡完了を確認' : '受取完了を確認'}
                </button>
              )}
            </div>
          )}

          {isCompleted && (
            <div className="bg-green-50 border-2 border-green-500 rounded-lg p-6 text-center">
              <p className="text-2xl font-bold text-green-600 mb-2">✓ 取引完了</p>
              <p className="text-gray-600">この取引は完了しました。</p>
            </div>
          )}
        </div>

        <div className="space-y-6">
          {qrCode && (
            <div className="bg-white rounded-lg shadow p-6 text-center">
              <h2 className="text-lg font-semibold mb-4">QRコード</h2>
              <img src={qrCode} alt="Transfer QR Code" className="mx-auto" />
              <p className="text-sm text-gray-600 mt-4">このコードをスキャンして進捗を確認</p>
            </div>
          )}

          {transaction.transferCode && (
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-lg font-semibold mb-4">譲渡コード</h2>
              <p className="text-center text-lg font-mono bg-gray-50 p-4 rounded break-all">{transaction.transferCode}</p>
              <p className="text-sm text-gray-600 mt-2 text-center">このコードは他の人と共有しないでください</p>
            </div>
          )}

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold mb-4">連絡先</h2>

            {isBuyer && (
              <div className="space-y-2">
                <p className="text-sm text-gray-600">出品者に連絡</p>
                <p className="font-semibold">{transaction.sellerName}</p>
                <p className="text-sm text-gray-500">メッセージから連絡してください</p>
              </div>
            )}

            {isSeller && (
              <div className="space-y-2">
                <p className="text-sm text-gray-600">購入者に連絡</p>
                <p className="font-semibold">{transaction.buyerName}</p>
                <p className="text-sm text-gray-500">メッセージから連絡してください</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

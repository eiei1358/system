import React, { useState, useEffect } from 'react'
import { itemAPI, applicationAPI, messageAPI, ItemDTO } from '../api'

interface Props {
  itemId: number
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
}

export default function ItemDetailPage({ itemId, user, onNavigate }: Props) {
  const [item, setItem] = useState<ItemDTO | null>(null)
  const [messages, setMessages] = useState<any[]>([])
  const [newMessage, setNewMessage] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [bidPrice, setBidPrice] = useState('')
  const [isApplying, setIsApplying] = useState(false)

  useEffect(() => {
    const fetchItem = async () => {
      try {
        const itemRes = await itemAPI.getItem(itemId)
        setItem(itemRes.data)

        if (itemRes.data.userId !== user.userId) {
          const messagesRes = await messageAPI.getMessages(user.userId, itemRes.data.userId, itemId)
          setMessages(messagesRes.data)
        }
      } catch (err) {
        console.error('Failed to fetch item', err)
      } finally {
        setIsLoading(false)
      }
    }

    fetchItem()
  }, [itemId, user.userId])

  const handleApply = async () => {
    if (!item) return

    setIsApplying(true)
    try {
      await applicationAPI.createApplication(itemId, user.userId, item.type === 3 ? parseFloat(bidPrice) : undefined)
      alert('応募しました')
      onNavigate('items')
    } catch (err: any) {
      alert(err.response?.data?.message || '応募に失敗しました')
    } finally {
      setIsApplying(false)
    }
  }

  const handleSendMessage = async () => {
    if (!item || !newMessage.trim()) return

    try {
      await messageAPI.sendMessage(user.userId, item.userId, itemId, newMessage)
      setNewMessage('')
      const messagesRes = await messageAPI.getMessages(user.userId, item.userId, itemId)
      setMessages(messagesRes.data)
    } catch (err: any) {
      alert(err.response?.data?.message || 'メッセージ送信に失敗しました')
    }
  }

  if (isLoading || !item) {
    return <div className="text-center py-8">読み込み中...</div>
  }

  const conditionLabels = ['', '新品', '美品', '良好', '傷あり', 'ジャンク']
  const typeLabels = ['', '無償', '有償', 'オークション']
  const statusLabels = ['', '出品中', '成約', '完了']

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div className="lg:col-span-2 space-y-6">
        <button onClick={() => onNavigate('items')} className="text-primary hover:underline">← 戻る</button>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="aspect-square bg-gray-200 flex items-center justify-center max-h-96">
            {item.imageUrls && item.imageUrls[0] ? (
              <img src={item.imageUrls[0]} alt={item.name} className="w-full h-full object-cover" />
            ) : (
              <span className="text-gray-400">画像なし</span>
            )}
          </div>

          {item.imageUrls && item.imageUrls.length > 1 && (
            <div className="flex gap-2 p-4 overflow-x-auto">
              {item.imageUrls.map((url, idx) => (
                <img key={idx} src={url} alt={`${item.name} ${idx}`} className="w-16 h-16 rounded object-cover" />
              ))}
            </div>
          )}
        </div>

        <div className="bg-white rounded-lg shadow p-6 space-y-4">
          <h1 className="text-2xl font-bold">{item.name}</h1>
          <p className="text-gray-600">{item.description}</p>

          <div className="grid grid-cols-2 gap-4 py-4 border-y">
            <div>
              <span className="text-gray-600">状態: </span>
              <strong>{conditionLabels[item.condition]}</strong>
            </div>
            <div>
              <span className="text-gray-600">タイプ: </span>
              <strong>{typeLabels[item.type]}</strong>
            </div>
            <div>
              <span className="text-gray-600">ステータス: </span>
              <strong>{statusLabels[item.status]}</strong>
            </div>
            <div>
              <span className="text-gray-600">期限: </span>
              <strong>{new Date(item.deadline).toLocaleDateString('ja-JP')}</strong>
            </div>
          </div>

          <div className="space-y-2">
            <p className="text-gray-600">出品者</p>
            <p className="text-lg font-semibold">{item.userName}</p>
          </div>
        </div>

        {item.userId !== user.userId && (
          <div className="bg-white rounded-lg shadow p-6 space-y-4">
            <h2 className="text-lg font-semibold">メッセージ</h2>
            <div className="max-h-64 overflow-y-auto space-y-3 mb-4">
              {messages.map((msg) => (
                <div key={msg.messageId} className={`p-3 rounded ${msg.senderId === user.userId ? 'bg-blue-50 ml-auto max-w-xs' : 'bg-gray-50'}`}>
                  <p className="text-sm font-semibold">{msg.senderName}</p>
                  <p className="text-sm">{msg.content}</p>
                  <p className="text-xs text-gray-500 mt-1">{new Date(msg.createdAt).toLocaleString('ja-JP')}</p>
                </div>
              ))}
            </div>

            <div className="flex gap-2">
              <input
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="メッセージを入力..."
                className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <button
                onClick={handleSendMessage}
                className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-purple-700 transition"
              >
                送信
              </button>
            </div>
          </div>
        )}
      </div>

      <div className="lg:col-span-1">
        <div className="bg-white rounded-lg shadow p-6 space-y-4 sticky top-20">
          <div className="text-center">
            <p className="text-4xl font-bold text-primary">¥{item.price?.toLocaleString()}</p>
          </div>

          {item.status === 1 && item.userId !== user.userId && (
            <>
              {item.type === 3 && (
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">入札額</label>
                  <input
                    type="number"
                    value={bidPrice}
                    onChange={(e) => setBidPrice(e.target.value)}
                    placeholder={item.price?.toString()}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>
              )}

              <button
                onClick={handleApply}
                disabled={isApplying || (item.type === 3 && !bidPrice)}
                className="w-full bg-success text-white py-3 rounded-lg hover:bg-green-600 transition font-semibold disabled:opacity-50"
              >
                {isApplying ? '応募中...' : item.type === 3 ? '入札する' : '応募する'}
              </button>
            </>
          )}

          {item.status !== 1 && (
            <div className="p-4 bg-gray-50 rounded-lg text-center">
              <p className="text-gray-600">この物品は応募できません</p>
            </div>
          )}

          <div className="pt-4 border-t space-y-2">
            <p className="text-sm text-gray-600">価格交渉: {item.negotiable ? '可' : '不可'}</p>
            <p className="text-sm text-gray-600">出品日: {new Date(item.createdAt!).toLocaleDateString('ja-JP')}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

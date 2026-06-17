import React, { useState, useEffect } from 'react'
import { itemAPI, ItemDTO } from '../api'

interface Props {
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
}

export default function ItemListPage({ user, onNavigate }: Props) {
  const [items, setItems] = useState<ItemDTO[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [keyword, setKeyword] = useState('')
  const [categoryId, setCategoryId] = useState<number | undefined>()
  const [condition, setCondition] = useState<number | undefined>()
  const [statusList, setStatusList] = useState('1,2')
  const [sortBy, setSortBy] = useState('newest')
  const [includeExpired, setIncludeExpired] = useState(false)

  const categories = [
    { id: 1, name: 'IT機器' },
    { id: 2, name: 'スマホ' },
    { id: 3, name: '周辺機器' },
  ]

  const conditions = [
    { id: 1, label: '新品' },
    { id: 2, label: '美品' },
    { id: 3, label: '良好' },
    { id: 4, label: '傷あり' },
    { id: 5, label: 'ジャンク' },
  ]

  useEffect(() => {
    const fetchItems = async () => {
      try {
        setIsLoading(true)
        const res = await itemAPI.searchItems(categoryId, keyword, condition, statusList, sortBy, includeExpired)
        setItems(res.data)
      } catch (err) {
        console.error('Failed to fetch items', err)
      } finally {
        setIsLoading(false)
      }
    }

    fetchItems()
  }, [keyword, categoryId, condition, statusList, sortBy, includeExpired])

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold text-gray-800">物品一覧</h1>

      <div className="bg-white rounded-lg shadow p-6 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">キーワード検索</label>
            <input
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="商品名で検索..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">カテゴリ</label>
            <select
              value={categoryId || ''}
              onChange={(e) => setCategoryId(e.target.value ? parseInt(e.target.value) : undefined)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="">全て</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">状態</label>
            <select
              value={condition || ''}
              onChange={(e) => setCondition(e.target.value ? parseInt(e.target.value) : undefined)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="">全て</option>
              {conditions.map((cond) => (
                <option key={cond.id} value={cond.id}>{cond.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">ソート</label>
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="newest">新着順</option>
              <option value="deadline">期限が近い順</option>
            </select>
          </div>
        </div>

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            checked={includeExpired}
            onChange={(e) => setIncludeExpired(e.target.checked)}
            className="w-4 h-4 text-primary rounded"
          />
          <span className="text-sm text-gray-700">期限切れ物品を含める</span>
        </label>
      </div>

      {isLoading ? (
        <div className="text-center py-8">読み込み中...</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map((item) => (
            <div key={item.itemId} className="bg-white rounded-lg shadow overflow-hidden hover:shadow-lg transition cursor-pointer" onClick={() => onNavigate('item-detail', item.itemId)}>
              <div className="aspect-square bg-gray-200 flex items-center justify-center">
                {item.imageUrls && item.imageUrls[0] ? (
                  <img src={item.imageUrls[0]} alt={item.name} className="w-full h-full object-cover" />
                ) : (
                  <span className="text-gray-400">画像なし</span>
                )}
              </div>
              <div className="p-4 space-y-2">
                <h3 className="font-semibold text-gray-900 line-clamp-2">{item.name}</h3>
                <p className="text-lg font-bold text-primary">¥{item.price?.toLocaleString()}</p>
                <p className="text-sm text-gray-600">{item.userName}</p>
                <div className="flex justify-between text-xs text-gray-500">
                  <span>{item.categoryName}</span>
                  <span>{item.status === 1 ? '出品中' : item.status === 2 ? '成約' : '完了'}</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

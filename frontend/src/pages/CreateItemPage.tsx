import React, { useState } from 'react'
import { itemAPI, ItemDTO } from '../api'

interface Props {
  user: any
  onNavigate: (page: string, itemId?: number, transactionId?: number, code?: string) => void
}

export default function CreateItemPage({ user, onNavigate }: Props) {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    condition: 1,
    price: '',
    type: 1,
    categoryId: 1,
    place: 0,
    negotiable: false,
    deadline: '',
  })
  const [imageUrls, setImageUrls] = useState<string[]>([''])
  const [isSubmitting, setIsSubmitting] = useState(false)

  const categories = [
    { id: 1, name: 'IT機器' },
    { id: 2, name: 'スマホ' },
    { id: 3, name: '周辺機器' },
  ]

  const places = [
    { id: 0, name: '神田' },
    { id: 1, name: '横浜' },
  ]

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, type } = e.target
    const value = type === 'checkbox' ? (e.target as HTMLInputElement).checked : e.target.value

    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleImageUrlChange = (index: number, value: string) => {
    const newUrls = [...imageUrls]
    newUrls[index] = value
    setImageUrls(newUrls)
  }

  const addImageUrl = () => {
    setImageUrls([...imageUrls, ''])
  }

  const removeImageUrl = (index: number) => {
    setImageUrls(imageUrls.filter((_, i) => i !== index))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)

    try {
      const item: ItemDTO = {
        userId: user.userId,
        name: formData.name,
        description: formData.description,
        condition: parseInt(formData.condition.toString()),
        price: parseFloat(formData.price),
        type: parseInt(formData.type.toString()),
        status: 1,
        categoryId: parseInt(formData.categoryId.toString()),
        deadline: formData.deadline,
        createdAt: new Date().toISOString(),
        place: parseInt(formData.place.toString()),
        negotiable: formData.negotiable,
        imageUrls: imageUrls.filter(url => url.trim() !== ''),
      }

      await itemAPI.createItem(item)
      alert('物品を出品しました')
      onNavigate('items')
    } catch (err: any) {
      alert(err.response?.data?.message || '出品に失敗しました')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-800 mb-8">物品を出品する</h1>

      <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-8 space-y-6">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">商品名 *</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">説明 *</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows={4}
            required
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">状態 *</label>
            <select
              name="condition"
              value={formData.condition}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="1">新品</option>
              <option value="2">美品</option>
              <option value="3">良好</option>
              <option value="4">傷あり</option>
              <option value="5">ジャンク</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">カテゴリ *</label>
            <select
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">希望金額 *</label>
            <input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">取引形式 *</label>
            <select
              name="type"
              value={formData.type}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              <option value="1">無償</option>
              <option value="2">有償</option>
              <option value="3">オークション</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">受渡し場所 *</label>
            <select
              name="place"
              value={formData.place}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            >
              {places.map(place => (
                <option key={place.id} value={place.id}>{place.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">登録期限 *</label>
            <input
              type="datetime-local"
              name="deadline"
              value={formData.deadline}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
        </div>

        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            name="negotiable"
            checked={formData.negotiable}
            onChange={handleChange}
            className="w-4 h-4 text-primary rounded"
          />
          <span className="text-sm text-gray-700">価格交渉可</span>
        </label>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">画像URL</label>
          <div className="space-y-2">
            {imageUrls.map((url, idx) => (
              <div key={idx} className="flex gap-2">
                <input
                  type="url"
                  value={url}
                  onChange={(e) => handleImageUrlChange(idx, e.target.value)}
                  placeholder="https://example.com/image.jpg"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary"
                />
                {imageUrls.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeImageUrl(idx)}
                    className="px-4 py-2 bg-danger text-white rounded-lg hover:bg-red-600 transition"
                  >
                    削除
                  </button>
                )}
              </div>
            ))}
          </div>
          <button
            type="button"
            onClick={addImageUrl}
            className="mt-2 px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400 transition"
          >
            画像を追加
          </button>
        </div>

        <div className="flex gap-4 pt-4">
          <button
            type="submit"
            disabled={isSubmitting}
            className="flex-1 bg-primary text-white py-3 rounded-lg hover:bg-purple-700 transition font-semibold disabled:opacity-50"
          >
            {isSubmitting ? '出品中...' : '出品する'}
          </button>
          <button
            type="button"
            onClick={() => onNavigate('items')}
            className="flex-1 bg-gray-300 text-gray-700 py-3 rounded-lg hover:bg-gray-400 transition font-semibold"
          >
            キャンセル
          </button>
        </div>
      </form>
    </div>
  )
}

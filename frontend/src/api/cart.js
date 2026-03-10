import request from '@/utils/request'

/**
 * 获取购物车列表（当前登录用户）
 */
export function getCartList() {
  return request({
    url: '/cart',
    method: 'get'
  })
}

/**
 * 添加到购物车
 */
export function addToCart(params) {
  return request({
    url: '/cart/items',
    method: 'post',
    params
  })
}

/**
 * 更新购物车商品数量
 */
export function updateQuantity(id, params) {
  return request({
    url: `/cart/items/${id}`,
    method: 'put',
    params
  })
}

/**
 * 删除购物车商品
 */
export function deleteCart(id) {
  return request({
    url: `/cart/items/${id}`,
    method: 'delete'
  })
}

/**
 * 清空购物车（当前登录用户）
 */
export function clearCart() {
  return request({
    url: '/cart',
    method: 'delete'
  })
}

import request from '@/utils/request'

/**
 * 获取商品列表
 */
export function getProductList(params) {
  return request({
    url: '/products',
    method: 'get',
    params
  })
}

/**
 * 获取商品详情
 */
export function getProductDetail(id) {
  return request({
    url: `/products/${id}`,
    method: 'get'
  })
}

/**
 * 搜索商品
 */
export function searchProducts(params) {
  return request({
    url: '/products/search',
    method: 'get',
    params
  })
}

/**
 * 获取热销商品
 */
export function getHotProducts(params) {
  return request({
    url: '/products/hot',
    method: 'get',
    params
  })
}

/**
 * 获取当前登录商家的商品列表（商家只看自己的，管理员可按 merchantId 过滤）
 */
export function getMerchantProductList(params) {
  return request({
    url: '/products/mine',
    method: 'get',
    params
  })
}

/**
 * 创建商品
 */
export function createProduct(data) {
  return request({
    url: '/products',
    method: 'post',
    data
  })
}

/**
 * 更新商品
 */
export function updateProduct(id, data) {
  return request({
    url: `/products/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品
 */
export function deleteProduct(id) {
  return request({
    url: `/products/${id}`,
    method: 'delete'
  })
}

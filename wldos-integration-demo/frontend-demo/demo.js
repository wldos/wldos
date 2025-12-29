/**
 * WLDOS 第三方集成前端 Demo
 * 
 * 演示如何调用不同类型的 Controller API
 */

// ========== 普通 Controller 示例（UserController） ==========

/**
 * 获取用户列表
 */
async function getUserList() {
    const result = await get('/api/users/list');
    showResult('userListResult', result, result.success);
}

/**
 * 获取用户详情
 */
async function getUserById() {
    const userId = document.getElementById('userId').value;
    const result = await get(`/api/users/${userId}`);
    showResult('userDetailResult', result, result.success);
}

/**
 * 创建用户
 */
async function createUser() {
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const nickname = document.getElementById('nickname').value;
    
    const result = await post('/api/users', {
        username: username,
        email: email,
        nickname: nickname,
    });
    
    showResult('createUserResult', result, result.success);
    
    // 如果创建成功，刷新用户列表
    if (result.success) {
        setTimeout(() => getUserList(), 1000);
    }
}

// ========== EntityController 示例（ProductController） ==========

/**
 * 查询所有产品
 */
async function getAllProducts() {
    const result = await get('/api/products/all');
    showResult('allProductsResult', result, result.success);
}

/**
 * 根据ID查询产品
 */
async function getProductById() {
    const productId = document.getElementById('productId').value;
    const result = await get(`/api/products/get?id=${productId}`);
    showResult('productDetailResult', result, result.success);
}

/**
 * 新增产品
 */
async function addProduct() {
    const name = document.getElementById('productName').value;
    const description = document.getElementById('productDesc').value;
    const price = parseFloat(document.getElementById('productPrice').value);
    const stock = parseInt(document.getElementById('productStock').value);
    
    const result = await post('/api/products/add', {
        name: name,
        description: description,
        price: price,
        stock: stock,
    });
    
    showResult('addProductResult', result, result.success);
    
    // 如果创建成功，刷新产品列表
    if (result.success) {
        setTimeout(() => getAllProducts(), 1000);
    }
}

// ========== NonEntityController 示例（OrderController） ==========

/**
 * 创建订单
 */
async function createOrder() {
    const productId = document.getElementById('orderProductId').value;
    const quantity = document.getElementById('orderQuantity').value;
    
    const result = await post(`/api/orders/create?productId=${productId}&quantity=${quantity}`);
    showResult('createOrderResult', result, result.success);
}

/**
 * 查询订单列表
 */
async function getOrderList() {
    const result = await get('/api/orders/list');
    showResult('orderListResult', result, result.success);
}

/**
 * 获取当前用户信息
 */
async function getUserInfo() {
    const result = await get('/api/orders/user-info');
    showResult('userInfoResult', result, result.success);
}


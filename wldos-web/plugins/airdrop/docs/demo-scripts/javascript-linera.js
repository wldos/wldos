/**
 * Linera空投任务完整脚本示例
 * 
 * 功能：
 * 1. 访问Linera官网
 * 2. 连接钱包
 * 3. 关注Twitter账号
 * 4. 加入Discord
 * 5. 完成测试网交互
 * 6. 自动Claim奖励
 * 
 * 使用方法：
 * 将此脚本内容复制到任务配置的scriptContent字段
 */

console.log('=== 开始Linera空投任务 ===');

// ========== 步骤1: 访问Linera官网 ==========
console.log('步骤1: 访问Linera官网');
await page.goto('https://linera.io');
await page.waitForTimeout(3000);

// 等待页面加载完成
await page.waitForSelector('body', { state: 'visible' });
console.log('页面加载完成');

// ========== 步骤2: 连接钱包 ==========
console.log('步骤2: 连接钱包');
try {
    // 查找连接钱包按钮
    const connectButton = await page.$('[data-testid="connect-wallet"]');
    if (!connectButton) {
        // 尝试其他选择器
        const altButton = await page.$('button:has-text("Connect Wallet")');
        if (altButton) {
            await altButton.click();
        } else {
            throw new Error('未找到连接钱包按钮');
        }
    } else {
        await connectButton.click();
    }
    
    await page.waitForTimeout(2000);
    
    // 选择钱包类型（MetaMask）
    const metamaskButton = await page.$('button:has-text("MetaMask")');
    if (metamaskButton) {
        await metamaskButton.click();
        await page.waitForTimeout(3000);
        console.log('钱包连接成功');
    } else {
        console.log('未找到MetaMask按钮，可能已连接');
    }
} catch (error) {
    console.log('连接钱包失败:', error.message);
    // 继续执行，可能已经连接
}

// ========== 步骤3: 关注Twitter账号 ==========
console.log('步骤3: 关注Twitter账号');
try {
    // 打开新标签页访问Twitter
    const twitterPage = await page.context().newPage();
    await twitterPage.goto('https://twitter.com/linera_official');
    await twitterPage.waitForTimeout(2000);
    
    // 查找关注按钮
    const followButton = await twitterPage.$('[data-testid="follow"]');
    if (followButton) {
        await followButton.click();
        await twitterPage.waitForTimeout(1000);
        console.log('Twitter关注成功');
    } else {
        console.log('可能已关注或需要登录');
    }
    
    await twitterPage.close();
} catch (error) {
    console.log('关注Twitter失败:', error.message);
}

// ========== 步骤4: 加入Discord ==========
console.log('步骤4: 加入Discord');
try {
    const discordPage = await page.context().newPage();
    await discordPage.goto('https://discord.gg/linera');
    await discordPage.waitForTimeout(2000);
    
    // Discord加入逻辑（根据实际页面调整）
    const joinButton = await discordPage.$('button:has-text("Accept Invite")');
    if (joinButton) {
        await joinButton.click();
        await discordPage.waitForTimeout(2000);
        console.log('Discord加入成功');
    }
    
    await discordPage.close();
} catch (error) {
    console.log('加入Discord失败:', error.message);
}

// ========== 步骤5: 完成测试网交互 ==========
console.log('步骤5: 完成测试网交互');
try {
    await page.goto('https://linera.io/testnet');
    await page.waitForTimeout(2000);
    
    // 查找任务列表
    const tasks = await page.$$('.task-item');
    console.log(`找到 ${tasks.length} 个任务`);
    
    // 完成每个任务
    for (let i = 0; i < tasks.length; i++) {
        const task = tasks[i];
        const completeButton = await task.$('button:has-text("Complete")');
        if (completeButton) {
            await completeButton.click();
            await page.waitForTimeout(1000);
            console.log(`任务 ${i + 1} 完成`);
        }
    }
} catch (error) {
    console.log('完成测试网交互失败:', error.message);
}

// ========== 步骤6: 自动Claim奖励 ==========
console.log('步骤6: 自动Claim奖励');
try {
    await page.goto('https://linera.io/claim');
    await page.waitForTimeout(2000);
    
    // 查找Claim按钮
    const claimButton = await page.$('button:has-text("Claim")');
    if (claimButton) {
        await claimButton.click();
        await page.waitForTimeout(2000);
        
        // 确认交易（如果有）
        const confirmButton = await page.$('button:has-text("Confirm")');
        if (confirmButton) {
            await confirmButton.click();
            await page.waitForTimeout(3000);
        }
        
        console.log('Claim成功');
    } else {
        console.log('未找到Claim按钮，可能已Claim或不符合条件');
    }
} catch (error) {
    console.log('Claim失败:', error.message);
}

console.log('=== Linera空投任务完成 ===');

// 返回执行结果
return {
    success: true,
    message: 'Linera空投任务执行完成',
    timestamp: new Date().toISOString()
};


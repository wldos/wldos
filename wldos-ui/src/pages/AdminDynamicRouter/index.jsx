import React, { Suspense, useMemo, useEffect, useState } from 'react';
import { connect } from 'umi';
import { GridContent } from '@ant-design/pro-layout';
import { Card, Spin, Alert, ConfigProvider } from 'antd';
import NoFoundPage from "@/pages/404";
import { injectPluginStyles } from '@/utils/pluginCoLocatedLoader';
import { getComponentPath } from '@/utils/getComponentPath';

// Normalize component path: './ext/Page1' -> 'ext/Page1'
const normalizeComponentPath = (component) => {
	if (!component) return '';
	return component.replace(/^\.\//, '').replace(/^\//, '');
};

// 管理侧插件组件加载器
const AdminPluginComponentLoader = ({ pluginCode, component, menu, pluginManifest, ...props }) => {
	const [lazyComponent, setLazyComponent] = useState(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState(null);

	useEffect(() => {
		loadPluginComponent(pluginCode, component, pluginManifest);
	}, [pluginCode, component, pluginManifest]);

	const loadPluginComponent = async (code, compName, manifest) => {
		try {
			setLoading(true);

			// 控制台去噪：仅在开发环境打印详细日志
			if (process.env.NODE_ENV === 'development') {
				console.log(`[AdminDynamicRouter] 开始加载插件组件: ${code}, 组件名: ${compName}`);
			}

			// 创建并注入插件专用的 request（自动添加前缀）
			try {
				const { createPluginRequest } = await import('@/utils/pluginRequest');
				const pluginRequest = createPluginRequest(code);

				// 注入到全局，供插件使用
				if (typeof window !== 'undefined') {
					if (!window.__PLUGIN_REQUESTS__) {
						window.__PLUGIN_REQUESTS__ = {};
					}
					window.__PLUGIN_REQUESTS__[code] = pluginRequest;

					if (process.env.NODE_ENV === 'development') {
						console.log(`[AdminDynamicRouter] 插件 request 已注入: ${code}`);
					}
				}
			} catch (requestError) {
				// request 注入失败不影响组件加载，只记录警告
				if (process.env.NODE_ENV === 'development') {
					console.warn(`[AdminDynamicRouter] 插件 request 注入失败: ${code}`, requestError);
				}
			}

			// 开发模式：直接从源码加载插件（用于快速预览，生产环境完全规避）
			if (process.env.NODE_ENV === 'development') {
				try {
					// 动态导入开发模式加载器（生产环境 webpack 会完全移除这段代码）
					const {
						loadDevPluginComponent,
						isPluginInDevMode
					} = await import(/* webpackMode: "weak" */ '@/utils/pluginDevLoader');

					if (isPluginInDevMode(code)) {
						// 从源码直接加载（绕过同部署方案，用于快速开发预览）
						const devComponent = await loadDevPluginComponent(code, compName);
						console.log(`[AdminDynamicRouter] 开发模式加载成功: ${code}`, { component: compName });

						setPluginComponent(() => devComponent);
						setLoading(false);
						return;
					}
				} catch (devError) {
					// 开发模式加载失败（可能是模块不存在），继续使用正常加载流程
					if (process.env.NODE_ENV === 'development') {
						console.warn(`[AdminDynamicRouter] 开发模式加载失败，回退到正常加载: ${code}`, devError);
					}
				}
			}

			// 从manifest中获取插件信息
			const pluginInfo = manifest?.plugins?.[code];
			if (!pluginInfo || !pluginInfo.version) {
				throw new Error(`插件 ${code} 未在 manifest 中找到或版本信息缺失`);
			}

			const cssFiles = pluginInfo.assets?.css || [];

			// 先注入CSS样式
			if (cssFiles && cssFiles.length > 0) {
				injectPluginStyles(code, pluginInfo.version, cssFiles);
			}

			// 优先尝试加载 ESM 格式，失败则回退到 JSONP 格式
			// ESM 文件路径：/plugin-assets/{code}/{version}/esm/index.js
			const esmUrl = `/plugin-assets/${code}/${pluginInfo.version}/esm/index.js`;

			// 尝试加载 ESM 格式
			try {
				const esmModule = await import(/* webpackIgnore: true */ esmUrl);
				const comp = esmModule[compName] || esmModule.default?.Components?.[compName] || esmModule.default?.[compName] || esmModule.default;

				if (comp) {
					const WrappedLazyComponent = React.lazy(() => Promise.resolve({ default: comp }));
					setLazyComponent(() => WrappedLazyComponent);
					setLoading(false);

					if (process.env.NODE_ENV === 'development') {
						console.log(`[AdminDynamicRouter] ESM插件加载成功: ${code}`, {
							version: pluginInfo.version,
							esmUrl: esmUrl,
							component: compName
						});
					}
					return;
				} else {
					// ESM 模块加载成功，但找不到对应的组件
					if (process.env.NODE_ENV === 'development') {
						console.warn(`[AdminDynamicRouter] ESM模块加载成功，但找不到组件 ${compName}，尝试JSONP回退`);
					}
				}
			} catch (esmError) {
				// ESM 加载失败，回退到 JSONP 格式
				if (process.env.NODE_ENV === 'development') {
					console.warn(`[AdminDynamicRouter] ESM格式加载失败，回退到JSONP: ${code}`, esmError);
				}
			}

			// 回退到 JSONP 格式（使用 UmiJS 构建的 chunk）
			// JSONP 加载逻辑在下面的 AdminDynamicRouter 组件中实现
			// 这里不抛出错误，让代码继续执行，由下面的逻辑处理 JSONP 加载
			// 设置一个标记，表示 ESM 加载失败，需要回退到 JSONP
			setLoading(false);
			setError(null); // 清除错误，让下面的 JSONP 加载逻辑处理


		} catch (err) {
			// 控制台去噪：仅在开发环境打印详细错误
			if (process.env.NODE_ENV === 'development') {
				console.error(`[AdminDynamicRouter] 插件加载失败: ${code}`, err);
			}
			setError(err.message || '插件加载失败');
			setLoading(false);
		}
	};

	if (loading) {
		return (
			<div style={{
				display: 'flex',
				justifyContent: 'center',
				alignItems: 'center',
				height: '300px'
			}}>
				<Spin size="large" tip="加载管理插件组件中..." />
			</div>
		);
	}

	if (error) {
		return (
			<GridContent>
				<Card>
					<Alert type="error" message="管理插件加载失败" description={error} />
				</Card>
			</GridContent>
		);
	}

	// ESM 插件使用 React.lazy
	if (lazyComponent) {
		const LazyComp = lazyComponent;
		return (
			<ConfigProvider>
				<Suspense fallback={
					<div style={{
						display: 'flex',
						justifyContent: 'center',
						alignItems: 'center',
						height: '300px'
					}}>
						<Spin size="large" />
					</div>
				}>
					<LazyComp menu={menu} {...props} />
				</Suspense>
			</ConfigProvider>
		);
	}

	if (error) {
		return (
			<GridContent>
				<Card>
					<Alert type="error" message="管理插件加载失败" description={error} />
				</Card>
			</GridContent>
		);
	}

	return null;
};

// Flatten routes tree to an array of { path, component, type }
const flattenRoutes = (routes) => {
	if (!routes) return [];
	const arr = Array.isArray(routes) ? routes : (routes.routes || routes.children || []);
	const out = [];
	const walk = (list) => {
		(list || []).forEach((item) => {
			if (item.path) {
				out.push({
					path: item.path,
					component: item.component,
					name: item.name,
					type: item.type,
					icon: item.icon,
					target: item.target,
					displayOrder: item.displayOrder,
					id: item.id,
					parentId: item.parentId
				});
			}
			if (item.routes || item.children) {
				walk(item.routes || item.children);
			}
		});
	};
	walk(arr);
	return out;
};

const AdminDynamicRouter = ({ dynamicRoutes, pluginManifest, dispatch }) => {
	const location = window.location;

	// 扁平化后端动态路由
	const flat = useMemo(() => flattenRoutes(dynamicRoutes), [dynamicRoutes]);

	// 查找匹配的路由
	// 注意：UmiJS 路由系统已经处理了静态路由优先级
	// 如果 AdminDynamicRouter 被渲染，说明没有静态路由匹配，无需再次检查
	const match = useMemo(() => {
		// 规范化路径（移除末尾斜杠）
		const normalizedPath = location.pathname.replace(/\/$/, '');

		// 在动态路由中查找
		const dynamicMatch = flat.find((r) => {
			const routePath = r.path?.replace(/\/$/, '');
			return routePath === normalizedPath;
		});

		// 调试日志：如果找到了路由但没有组件，记录详细信息
		if (dynamicMatch && !dynamicMatch.component) {
			console.warn('[AdminDynamicRouter] 找到路由但缺少 component 字段:', {
				path: dynamicMatch.path,
				name: dynamicMatch.name,
				type: dynamicMatch.type,
				component: dynamicMatch.component,
				allFields: dynamicMatch
			});
			return null;
		}

		// 调试日志：如果没有找到匹配的路由，记录详细信息
		if (!dynamicMatch && process.env.NODE_ENV === 'development') {
			console.warn('[AdminDynamicRouter] 未找到匹配路由，当前路径:', location.pathname);
		}

		return dynamicMatch;
	}, [flat, location.pathname, dynamicRoutes]);

	// 设置tdk信息，确保footer能正确显示
	useEffect(() => {
		if (match && dispatch) {
			dispatch({
				type: 'user/saveTdk',
				payload: {
					pubType: 'page',
					title: match.name || '动态页面',
					keywords: '',
					description: ''
				}
			});
		}
	}, [match, dispatch]);

	// 如果dynamicRoutes还没加载完成，显示加载状态
	if (!dynamicRoutes || dynamicRoutes.length === 0) {
		return (
			<GridContent>
				<Card>
					<div style={{
						display: 'flex',
						justifyContent: 'center',
						alignItems: 'center',
						height: '300px'
					}}>
						<Spin size="large" />
					</div>
				</Card>
			</GridContent>
		);
	}

	if (!match || !match.component) {
		// 返回404页面
		return (<NoFoundPage key={location.pathname}/>);
	}

	// 组件渲染：统一处理本地组件和插件组件，使用完全相同的加载逻辑
	if (match.component) {
		// 获取组件路径（统一处理本地组件和插件组件）
		const componentPath = getComponentPath(match, pluginManifest);

		if (!componentPath) {
			return (
				<GridContent>
					<Card>
						<Alert type="error" message="组件路径无效" />
					</Card>
				</GridContent>
			);
		}

		// 判断是插件路径还是本地组件路径
		// 插件路径：通过 match.type 判断，getComponentPath 返回的是相对路径（如 "airdrop/1.0.0/dynamic-airdrop-Execution.js"）
		// 本地组件：getComponentPath 返回的是中间部分（如 "sys/plugins"）
		let LazyComp;
		const isPluginRoute = match.type === 'admin_plugin_menu';

		if (isPluginRoute) {
			// 插件路径：从 match.path 提取 pluginCode，从 manifest 获取版本信息
			// componentPath 格式：规范化路径（如 "tasks", "scheduler"）
			// 需要拼装为：/plugin-assets/{pluginCode}/{version}/dynamic-{componentPath}-index.js

			// 从 match.path 提取 pluginCode
			const pathParts = match.path.split('/').filter(part => part.length > 0);
			let pluginCode = null;
			// 管理侧：/admin/pluginCode/xxx
			pluginCode = pathParts.length > 1 && pathParts[0] === 'admin' ? pathParts[1] : null;

			if (!pluginCode) {
				return (
					<GridContent>
						<Card>
							<Alert type="error" message="无法识别插件代码" />
						</Card>
					</GridContent>
				);
			}

			// 先尝试直接查找
			let pluginInfo = pluginManifest?.plugins?.[pluginCode];

			// 如果直接查找失败，遍历所有插件，查找路由路径匹配的插件
			if (!pluginInfo && pluginManifest?.plugins) {
				for (const [key, plugin] of Object.entries(pluginManifest.plugins)) {
					if (plugin.routes && Array.isArray(plugin.routes)) {
						// 检查是否有路由路径匹配当前路径
						const matchingRoute = plugin.routes.find(route => route.path === match.path);
						if (matchingRoute) {
							pluginCode = key;
							pluginInfo = plugin;
							break;
						}
					}
				}
			}

			if (!pluginInfo || !pluginInfo.version) {
				return (
					<GridContent>
						<Card>
							<Alert type="error" message={`插件 ${pluginCode} 未找到或版本信息缺失`} />
						</Card>
					</GridContent>
				);
			}

			// 注入 CSS 样式
			if (pluginInfo?.assets?.css) {
				pluginInfo.assets.css.forEach(cssFile => {
					injectPluginStyles(pluginCode, pluginInfo.version, [cssFile]);
				});
			}

			const pluginPublicPath = `/plugin-assets/${pluginCode}/${pluginInfo.version}/`;

			// 备选方案：如果插件仍然生成了自己的 webpack runtime（向后兼容）
			function loadPluginChunkWithPluginRuntime(chunkUrl, pluginPublicPath) {
				return new Promise(async (resolve, reject) => {
					// 先加载插件的 umi.js（包含插件的 webpack runtime）
					const umiPath = `${pluginPublicPath}umi.js`;
					const umiGlobalKey = `__PLUGIN_UMI_LOADED_${pluginCode}_${pluginInfo.version}`;

					if (!window[umiGlobalKey]) {
						try {
							await new Promise((resolveUmi, rejectUmi) => {
								const script = document.createElement('script');
								script.src = umiPath;
								script.onload = () => {
									window[umiGlobalKey] = true;
									resolveUmi();
								};
								script.onerror = rejectUmi;
								document.head.appendChild(script);
							});
						} catch (e) {
							reject(new Error(`加载插件 umi.js 失败: ${umiPath}`));
							return;
						}
					}

					// 插件的 webpack runtime 已经加载，现在加载 JSONP chunk
					// 插件的 webpack runtime 会处理 JSONP chunk 并注册模块
					// 我们通过拦截 webpackJsonp.push 来获取模块
					let moduleResolved = false;

					// 确保 webpackJsonp 存在
					if (!window.webpackJsonp) {
						window.webpackJsonp = [];
					}

					// 保存原始的 push 方法（插件的 webpack runtime 的 push）
					const originalPushMethod = window.webpackJsonp.push;

					// 重写 push 方法，拦截模块注册
					window.webpackJsonp.push = function(...args) {
						// 先调用原始的 push 方法，让插件的 webpack runtime 处理
						if (originalPushMethod) {
							originalPushMethod.apply(this, args);
						}

						// 检查是否是我们要加载的 chunk
						if (args.length >= 1 && Array.isArray(args[0]) && args[0].length >= 2) {
							const firstArg = args[0];
							if (Array.isArray(firstArg[0]) && typeof firstArg[1] === 'object') {
								const chunkData = firstArg[1];

								// 插件的 webpack runtime 已经处理了 JSONP chunk，模块已经被注册
								// 关键：模块函数中的依赖 ID（如 'q1tI', 'nKUr', 'bx4M'）是插件构建时生成的
								// 主应用的 webpack runtime 无法解析这些 ID
								// 所以，我们需要使用插件的 webpack runtime 的 require 函数来解析依赖
								//
								// 解决方案：直接执行模块函数，使用插件的 webpack runtime 的 require 函数
								// 插件的 webpack runtime 已经将 require 函数暴露到全局变量
								// window.__PLUGIN_REQUIRE__[pluginCode][version] = __webpack_require__

								// 等待插件的 webpack runtime 处理完模块后，直接执行模块函数
								setTimeout(() => {
									if (!moduleResolved) {
										try {
											// 获取插件的 webpack runtime 的 require 函数
											const pluginRequire = window.__PLUGIN_REQUIRE__
												&& window.__PLUGIN_REQUIRE__[pluginCode]
												&& window.__PLUGIN_REQUIRE__[pluginCode][pluginInfo.version];

											if (!pluginRequire) {
												console.warn(`[AdminDynamicRouter] 无法获取插件的 webpack runtime require 函数`);
												return;
											}

											// 从 chunkData 中获取模块并执行
											for (const moduleId in chunkData) {
												if (typeof chunkData[moduleId] === 'function') {
													// 直接执行模块函数，使用插件的 webpack runtime 的 require 函数
													const module = { exports: {} };
													const exports = module.exports;

													// 使用插件的 webpack runtime 的 require 函数
													// 这样模块的依赖（如 React、antd）可以从插件的 webpack runtime 获取
													// 插件的 webpack runtime 能够解析插件的模块 ID（如 'q1tI', 'nKUr', 'bx4M'）
													chunkData[moduleId](module, exports, pluginRequire);

													// 检查是否有 default 导出
													if (exports && exports.default) {
														moduleResolved = true;
														window.webpackJsonp.push = originalPushMethod;
														console.log(`[AdminDynamicRouter] 通过插件 webpack runtime 成功加载插件模块（共享机制）`);
														resolve({ default: exports.default });
														return;
													} else if (exports && typeof exports === 'object' && Object.keys(exports).length > 0) {
														// 如果没有 default，使用第一个导出
														const firstKey = Object.keys(exports)[0];
														moduleResolved = true;
														window.webpackJsonp.push = originalPushMethod;
														console.log(`[AdminDynamicRouter] 通过插件 webpack runtime 成功加载插件模块（使用第一个导出）`);
														resolve({ default: exports[firstKey] });
														return;
													}
												}
											}
										} catch (e) {
											console.error(`[AdminDynamicRouter] 执行模块函数失败:`, e);
										}
									}
								}, 100);
							}
						}
					};

					// 创建 script 标签加载 chunk
					const script = document.createElement('script');
					script.src = chunkUrl;
					script.onload = () => {
						// 等待模块解析
						let attempts = 0;
						const checkInterval = setInterval(() => {
							attempts++;
							if (moduleResolved) {
								clearInterval(checkInterval);
							} else if (attempts > 30) {
								clearInterval(checkInterval);
								window.webpackJsonp.push = originalPushMethod;
								reject(new Error(`插件 chunk 加载超时：无法获取模块。chunkUrl: ${chunkUrl}`));
							}
						}, 100);
					};
					script.onerror = () => {
						window.webpackJsonp.push = originalPushMethod;
						reject(new Error(`加载插件 chunk 失败: ${chunkUrl}`));
					};
					document.head.appendChild(script);
				});
			}

			// 优先尝试加载 ESM 格式，失败则回退到 JSONP 格式
			// ESM 文件路径：与 UmiJS chunk 命名规则一致
			// UmiJS chunk: ./scheduler → p__scheduler.{hash}.async.js
			// ESM 文件: ./scheduler → esm/scheduler.js
			LazyComp = React.lazy(async () => {
				const esmUrl = `${pluginPublicPath}esm/${componentPath}.js`;

				// 尝试加载 ESM 格式（直接加载对应的组件文件）
				try {
					if (process.env.NODE_ENV === 'development') {
						console.log(`[AdminDynamicRouter] 尝试加载ESM: ${esmUrl}`, {
							pluginCode,
							componentPath,
							pluginPublicPath
						});
					}

					const esmModule = await import(/* webpackIgnore: true */ esmUrl);

					// 调试信息：查看 ESM 模块的导出内容
					console.log(`[AdminDynamicRouter] ESM模块加载成功，检查导出: ${pluginCode}`, {
						esmUrl,
						componentPath,
						esmModule,
						hasDefault: 'default' in esmModule,
						defaultValue: esmModule.default,
						moduleKeys: Object.keys(esmModule)
					});

					// ESM 文件直接导出 default，直接使用
					const comp = esmModule.default || esmModule;

					if (comp && typeof comp !== 'undefined') {
						if (process.env.NODE_ENV === 'development') {
							console.log(`[AdminDynamicRouter] ESM插件加载成功: ${pluginCode}`, {
								version: pluginInfo.version,
								esmUrl: esmUrl,
								component: componentPath,
								componentType: typeof comp
							});
						}
						return { default: comp };
					} else {
						throw new Error(
							`ESM模块加载成功，但找不到组件 ${componentPath}\n` +
							`ESM URL: ${esmUrl}\n` +
							`模块导出: ${JSON.stringify(Object.keys(esmModule))}\n` +
							`default 值: ${esmModule.default}\n` +
							`模块内容: ${JSON.stringify(esmModule, null, 2)}`
						);
					}
				} catch (esmError) {
					// ESM 加载失败
					console.error(`[AdminDynamicRouter] ESM格式加载失败: ${pluginCode}`, {
						esmUrl,
						error: esmError.message,
						stack: esmError.stack,
						componentPath
					});

					// 检查是否有 JSONP 文件作为后备（通过检查 asset-manifest.json）
					let hasJsonpFallback = false;
					try {
						const manifestUrl = `${pluginPublicPath}asset-manifest.json`;
						const manifestRes = await fetch(manifestUrl);
						if (manifestRes.ok) {
							const assetManifest = await manifestRes.json();
							const umiChunkName = `p__${componentPath}`;
							const manifestKey = `/${umiChunkName}.js`;
							hasJsonpFallback = !!assetManifest[manifestKey];
						}
					} catch (e) {
						// asset-manifest.json 不存在，说明没有构建 UmiJS JSONP
					}

					// 如果没有 JSONP 后备，直接抛出错误
					if (!hasJsonpFallback) {
						throw new Error(
							`插件组件加载失败: ${pluginCode}/${componentPath}\n` +
							`ESM URL: ${esmUrl}\n` +
							`错误: ${esmError.message}\n` +
							`请检查：\n` +
							`1. ESM 文件是否存在: ${esmUrl}\n` +
							`2. ESM 文件语法是否正确\n` +
							`3. 服务器是否正确配置了 ESM 文件的 MIME 类型`
						);
					}

					// 如果有 JSONP 后备，继续回退到 JSONP 格式
					// 回退到 JSONP 格式（使用 UmiJS 构建的 chunk）
					// componentPath 格式：规范化路径（如 "scheduler", "tasks"）
					// UmiJS 会根据 component 路径生成 chunk 名称：p__{componentPath}
					// 例如：./scheduler → p__scheduler, ./tasks → p__tasks
					// 实际文件名：p__scheduler.{hash}.async.js（需要通过 asset-manifest.json 查找）
					const umiChunkName = `p__${componentPath}`;

					// 获取实际 chunk 文件名
					let chunkPath = null;
					try {
						const manifestUrl = `${pluginPublicPath}asset-manifest.json`;
						const manifestRes = await fetch(manifestUrl);
						if (manifestRes.ok) {
							const assetManifest = await manifestRes.json();
							const manifestKey = `/${umiChunkName}.js`;
							const actualChunk = assetManifest[manifestKey];
							if (actualChunk) {
								chunkPath = actualChunk.startsWith('/') ? actualChunk.slice(1) : actualChunk;
							}
						}
					} catch (e) {
						console.warn(`[AdminDynamicRouter] 无法加载 asset-manifest.json，使用默认命名规则:`, e);
					}

					if (!chunkPath) {
						chunkPath = `${umiChunkName}.async.js`;
					}

					// 构建完整的 chunk URL（相对于插件 publicPath）
					const chunkUrl = `${pluginPublicPath}${chunkPath}`;

					// 检查 webpack runtime 是否可用
					if (typeof __webpack_require__ === 'undefined' || !__webpack_require__.e) {
						console.warn(`[AdminDynamicRouter] webpack runtime 不可用，回退到插件 webpack runtime`);
						return loadPluginChunkWithPluginRuntime(chunkUrl, pluginPublicPath);
					}

					// 使用主应用的 webpack runtime 加载插件 chunk
					// 注意：插件配置了 runtimeChunk(false)，所以不会生成 umi.js
					// 插件的依赖通过 externals 配置，使用主应用的模块（window.React, window.antd等）
					// 所以可以直接使用主应用的 webpack runtime 加载插件的 JSONP chunk

					// 保存原始的 publicPath
					const originalPublicPath = __webpack_require__.p;

					try {
						// 临时修改 publicPath 为插件的子目录
						__webpack_require__.p = pluginPublicPath;

						return new Promise((resolve, reject) => {
							let moduleResolved = false;

							// 确保 webpackJsonp 存在
							if (!window.webpackJsonp) {
								window.webpackJsonp = [];
							}

							// 保存原始的 push 方法（主应用的 webpack runtime 的 push）
							const originalPushMethod = window.webpackJsonp.push;

							// 重写 push 方法，拦截模块注册
							window.webpackJsonp.push = function(...args) {
								// 先调用原始的 push 方法，让主应用的 webpack runtime 处理
								if (originalPushMethod) {
									originalPushMethod.apply(this, args);
								}

								// 检查是否是我们要加载的 chunk
								// webpack JSONP chunk 格式：push([[chunkId], {moduleId: function(...)}])
								if (args.length >= 1 && Array.isArray(args[0]) && args[0].length >= 2) {
									const firstArg = args[0];
									// 第一个元素是 chunk ID 数组，第二个元素是模块对象
									if (Array.isArray(firstArg[0]) && typeof firstArg[1] === 'object') {
										const chunkData = firstArg[1];

										// 调试日志
										if (process.env.NODE_ENV === 'development') {
											console.log('[AdminDynamicRouter] 拦截到 webpackJsonp.push，chunkData:', Object.keys(chunkData));
										}

										// 主应用的 webpack runtime 已经处理了 JSONP chunk，模块已经被注册
										// 关键：插件的依赖（React、antd等）通过 externals 配置，使用主应用的模块
										// 插件的 chunk 中的依赖 ID 就是主应用的模块 ID，主应用的 webpack runtime 可以直接解析
										//
										// 但是，插件的模块函数本身的模块 ID（如 'q1tI', 'nKUr', 'bx4M'）仍然是插件构建时生成的
										// 主应用的 webpack runtime 无法解析这些 ID
										//
										// 解决方案：直接执行插件的模块函数，使用主应用的 webpack runtime 的 require 函数
										// 这样插件的依赖（React、antd等）可以从主应用的 webpack runtime 获取
										// 插件的模块函数本身的模块 ID 不需要解析，因为模块函数已经包含了所有代码

										// 从 chunkData 中获取模块并执行
										for (const moduleId in chunkData) {
											if (typeof chunkData[moduleId] === 'function') {
												try {
													// 直接执行模块函数，使用主应用的 webpack runtime 的 require 函数
													const module = { exports: {} };
													const exports = module.exports;

													// 使用主应用的 webpack runtime 的 require 函数
													// 这样插件的依赖（React、antd等）可以从主应用的 webpack runtime 获取
													// 插件的依赖通过 externals 配置，使用主应用的模块 ID
													const mainAppRequire = typeof __webpack_require__ !== 'undefined'
														? __webpack_require__
														: null;

													if (!mainAppRequire) {
														console.warn(`[AdminDynamicRouter] 主应用的 webpack runtime 不可用`);
														break;
													}

													// 执行插件的模块函数
													// 插件的依赖（通过 externals 配置）会使用主应用的 webpack runtime 的 require 函数解析
													chunkData[moduleId](module, exports, mainAppRequire);

													// 检查是否有 default 导出
													if (exports && exports.default) {
														moduleResolved = true;
														window.webpackJsonp.push = originalPushMethod;
														__webpack_require__.p = originalPublicPath;
														console.log(`[AdminDynamicRouter] 通过主应用 webpack runtime 成功加载插件模块（继承机制）`);
														resolve({ default: exports.default });
														return;
													} else if (exports && typeof exports === 'object' && Object.keys(exports).length > 0) {
														// 如果没有 default，使用第一个导出
														const firstKey = Object.keys(exports)[0];
														moduleResolved = true;
														window.webpackJsonp.push = originalPushMethod;
														__webpack_require__.p = originalPublicPath;
														console.log(`[AdminDynamicRouter] 通过主应用 webpack runtime 成功加载插件模块（使用第一个导出）`);
														resolve({ default: exports[firstKey] });
														return;
													}
												} catch (e) {
													console.error(`[AdminDynamicRouter] 执行插件模块函数失败:`, e);
												}
											}
										}
									}
								}
							};

							// 创建 script 标签加载 chunk
							const script = document.createElement('script');
							script.src = chunkUrl;
							script.onload = () => {
								// 等待模块解析
								let attempts = 0;
								const checkInterval = setInterval(() => {
									attempts++;
									if (moduleResolved) {
										clearInterval(checkInterval);
									} else if (attempts > 30) {
										clearInterval(checkInterval);
										window.webpackJsonp.push = originalPushMethod;
										__webpack_require__.p = originalPublicPath;
										reject(new Error(`插件 chunk 加载超时：无法获取模块。chunkUrl: ${chunkUrl}`));
									}
								}, 100);
							};
							script.onerror = () => {
								window.webpackJsonp.push = originalPushMethod;
								__webpack_require__.p = originalPublicPath;
								reject(new Error(`加载插件 chunk 失败: ${chunkUrl}`));
							};
							document.head.appendChild(script);
						});
					} catch (e) {
						// 恢复原始的 publicPath
						__webpack_require__.p = originalPublicPath;
						console.warn(`[AdminDynamicRouter] 通过主应用 webpack runtime 加载失败，回退到插件 webpack runtime:`, e);
						return loadPluginChunkWithPluginRuntime(chunkUrl, pluginPublicPath);
					}
				}
			});
		} else {
			// 本地组件：使用 @/ 别名，@ 表示 src 目录
			// componentPath 格式：sys/plugins（后端返回的格式）
			// 拼装后：@/pages/sys/plugins/index -> src/pages/sys/plugins/index
			// 注意：当前假设所有组件都在 index.jsx/index.js 中
			// 如果后端返回的 component 不是以 /index 结尾（如 "sys/plugins/Detail"），
			// 可能需要添加 .js 扩展名：`@/pages/${componentPath}.js`
			// 需要根据实际后端返回的数据格式验证后确定
			LazyComp = React.lazy(() => import(/* webpackChunkName: "dynamic-[request]" */ `@/pages/${componentPath}/index`));
		}

		// 注意：插件组件和本地组件的加载方式现在应该一样了
		// 1. 本地组件：webpack 在构建时能够静态分析 `@/pages/xxx/index`，可以正确打包和加载
		// 2. 插件组件：通过临时修改 webpack runtime 的 publicPath，使用主应用的 webpack runtime 加载
		//    如果无法修改 publicPath，则回退到手动处理 JSONP chunk
		//
		// 由于插件的 chunk 格式和主应用完全一样，理论上应该可以用相同的方式加载
		// 通过临时修改 publicPath，主应用的 webpack runtime 就能处理子目录下的 chunk 了


		return (
			<Suspense fallback={
				<div style={{
					display: 'flex',
					justifyContent: 'center',
					alignItems: 'center',
					height: '300px'
				}}>
					<Spin size="large" />
				</div>
			}>
				<LazyComp key={location.pathname} />
			</Suspense>
		);
	}

	return null;
};

export default connect(({ user }) => ({
	dynamicRoutes: user.adminMenu,
	pluginManifest: user.pluginManifest
}))(AdminDynamicRouter);

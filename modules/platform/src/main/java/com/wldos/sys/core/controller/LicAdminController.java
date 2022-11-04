/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.base.controller.NoRepoController;
import com.wldos.base.service.CommonOperation;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.support.issue.enums.IssueVersionEnum;
import com.wldos.support.issue.verify.VerifyEnv;
import com.wldos.sys.core.vo.Lic;
import de.schlichtherle.license.LicenseContent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 许可证注册相关controller。
 *
 * @author 树悉猿
 * @date 2022/9/30
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/license")
public class LicAdminController extends NoRepoController {

	private final VerifyEnv verifyEnv;

	/**
	 * 通用的jdbc和业务操作
	 */
	@Autowired
	@Lazy
	@SuppressWarnings({ "all" })
	protected CommonOperation commonOperate;

	public LicAdminController(VerifyEnv verifyEnv) {

		this.verifyEnv = verifyEnv;
	}

	/**
	 * 获取许可证信息
	 *
	 * @return 许可证信息
	 */
	@GetMapping("")
	public Lic licenseInfo() {
		return Lic.of(this.verifyEnv);
	}

	/**
	 * 取回许可证版本枚举信息
	 *
	 * @return 许可证版本
	 */
	@GetMapping("versionEnum")
	public List<Map<String, String>> fetchIssueVersionEnum() {
		return Arrays.stream(IssueVersionEnum.values()).map(item -> {
			Map<String, String> em = new HashMap<>();
			em.put("label", item.getLabel());
			em.put("value", item.getValue());
			return em;
		}).collect(Collectors.toList());
	}

	/**
	 * 更新license，只有在license未过期前才可以更新，过期后只能手动替换再重启系统
	 *
	 * @param file license文件
	 */
	@PostMapping("upload")
	public String updateLicense(@RequestParam("file") MultipartFile file) {
		// 取出默认license文件
		ClassPathResource resource = new ClassPathResource("/wldos.lic");
		File licFile;
		LicenseContent lc;

		try {
			File lic = resource.getFile();
			String resPath = lic.getAbsolutePath();
			// 上传license文件到临时目录
			this.store.storeFile(file, resPath + "/temp/wldos.lic");
			licFile = new File(resPath + "/temp/wldos.lic");

			// 校验是否合法文件(预安装)，校验通过保存到(ClassPath)，校验失败驳回
			lc = this.commonOperate.preInstall(this.verifyEnv, licFile);

			if (ObjectUtils.isBlank(lc)) {
				return this.resJson.ok("error");
			}

			// 保存、覆盖/wldos.lic，本过程不可逆
			this.store.storeFile(file, resPath+"/wldos.lic");
		}
		catch (IOException e) {
			getLog().error("读取license异常，请检查resources目录下是否存在wldos.lic文件", e);
			return this.resJson.ok("error");
		}

		// 重新安装license、解析到系统会话中
		lc = this.commonOperate.updateLicense(this.verifyEnv);

		// 返回成功信息
		return this.resJson.ok(!ObjectUtils.isBlank(lc) ? "ok" : "error");
	}
}

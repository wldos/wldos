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
	 * 更新license
	 *
	 * @param file license文件
	 */
	@PostMapping("upload")
	public String updateLicense(@RequestParam("file") MultipartFile file) {
		ClassPathResource resource = new ClassPathResource("/wldos.lic");
		File licFile;
		LicenseContent lc;

		try {
			File lic = resource.getFile();
			String resPath = lic.getAbsolutePath();

			this.store.storeFile(file, resPath + "/temp/wldos.lic");
			licFile = new File(resPath + "/temp/wldos.lic");

			lc = this.commonOperate.preInstall(this.verifyEnv, licFile);

			if (ObjectUtils.isBlank(lc)) {
				return this.resJson.ok("error");
			}

			this.store.storeFile(file, resPath+"/wldos.lic");
		}
		catch (IOException e) {
			getLog().error("读取license异常，请检查resources目录下是否存在wldos.lic文件", e);
			return this.resJson.ok("error");
		}

		lc = this.commonOperate.updateLicense(this.verifyEnv);

		return this.resJson.ok(!ObjectUtils.isBlank(lc) ? "ok" : "error");
	}

	// todo 1.通过授权注册页面申请许可证（默认社区版），提交其server硬件信息（系统抓取）和商业授权信息（产品、组织、版本等信息）；

	// todo 4.在注册申请页面查看申请状态为成功，安装许可证，下载许可证到本地server替换社区版license，许可证升级完成。
}
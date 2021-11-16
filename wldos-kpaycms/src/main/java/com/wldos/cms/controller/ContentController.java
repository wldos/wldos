

package com.wldos.cms.controller;

import com.wldos.cms.service.ContentService;
import com.wldos.support.controller.RepoController;
import com.wldos.cms.entity.KModelContent;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("cms/content")
@RestController
public class ContentController extends RepoController<ContentService, KModelContent> {

}

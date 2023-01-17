package cn.lingjiatong.re.service.article.controller;

import cn.lingjiatong.re.common.ResultVO;
import cn.lingjiatong.re.service.article.api.client.FrontendArticleFeignClient;
import cn.lingjiatong.re.service.article.api.dto.FrontendArticleScrollDTO;
import cn.lingjiatong.re.service.article.api.vo.FrontendArticleScrollVO;
import cn.lingjiatong.re.service.article.api.vo.FrontendArticleVO;
import cn.lingjiatong.re.service.article.service.FrontendArticleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端文章模块controller层
 *
 * @author Ling, Jiatong
 * Date: 2022/10/20 21:38
 */
@RestController
public class FrontendArticleController implements FrontendArticleFeignClient {

    @Autowired
    private FrontendArticleService frontendArticleService;

    // ********************************新增类接口********************************
    // ********************************删除类接口********************************
    // ********************************修改类接口********************************
    // ********************************查询类接口********************************

    @Override
    public ResultVO<Page<FrontendArticleScrollVO>> findArticleScroll(FrontendArticleScrollDTO dto) {
        return ResultVO.success(frontendArticleService.findArticleScroll(dto));
    }

    @Override
    @GetMapping("/frontend/api/v1/article/{articleId}")
    public ResultVO<FrontendArticleVO> findArticleById(@PathVariable("articleId") Long articleId) {
        return ResultVO.success(frontendArticleService.findArticle(articleId));
    }
}

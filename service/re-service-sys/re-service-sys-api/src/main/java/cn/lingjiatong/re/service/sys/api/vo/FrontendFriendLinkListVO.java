package cn.lingjiatong.re.service.sys.api.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 前端友情链接列表VO对象
 *
 * @author Ling, Jiatong
 * Date: 2022/10/15 23:26
 */
@Data
@ApiModel(description = "前端友情链接列表VO对象")
public class FrontendFriendLinkListVO {

    /**
     * 链接名
     */
    private String name;

    /**
     * 链接地址
     */
    private String url;

    /**
     * 链接类型
     *
     * 1 官方网站
     * 2 个人网站
     */
    private Byte type;

    /**
     * 网站图标标志
     */
    private String faviconUrl;
}

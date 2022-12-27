package cn.lingjiatong.re.service.article.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 草稿保存DTO对象
 *
 * @author Ling, Jiatong
 * Date: 2022/12/27 15:44
 */
@Data
@ApiModel(description = "草稿保存DTO对象")
public class BackendDraftSaveDTO {

    /**
     * 草稿标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 草稿markdown内容
     */
    @ApiModelProperty("草稿markdown内容")
    private String markdownContent;
}

package cn.net.mall.basic.controller.common;

import cn.net.mall.entity.ResponsePageEntity;
import cn.net.mall.basic.entity.common.CommonAreaConditionEntity;
import cn.net.mall.basic.entity.common.CommonAreaEntity;
import cn.net.mall.basic.service.common.CommonAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 接口层
 *
 * @date 2025-02-07 11:43:55
 */
@Tag(name = "AI接口层", description = "AI接口层")
@RestController
@RequestMapping("/v1/ai")
public class AiController {

	private final CommonAreaService commonAreaService;

	public AiController(CommonAreaService commonAreaService) {
		this.commonAreaService = commonAreaService;
	}


}

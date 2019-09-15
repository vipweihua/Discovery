package com.nepxion.discovery.plugin.strategy.service.sentinel.loader;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Weihua Wang
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.common.util.JsonUtil;
import com.nepxion.discovery.plugin.framework.util.FileContextUtil;
import com.nepxion.discovery.plugin.strategy.service.sentinel.constant.SentinelStrategyConstant;
import com.nepxion.discovery.plugin.strategy.service.sentinel.parser.SentinelAuthorityRuleParser;
import com.nepxion.discovery.plugin.strategy.service.sentinel.parser.SentinelDegradeRuleParser;
import com.nepxion.discovery.plugin.strategy.service.sentinel.parser.SentinelFlowRuleParser;
import com.nepxion.discovery.plugin.strategy.service.sentinel.parser.SentinelParamFlowRuleParser;
import com.nepxion.discovery.plugin.strategy.service.sentinel.parser.SentinelSystemRuleParser;

public class SentinelFileRuleLoader implements SentinelRuleLoader {
    private static final Logger LOG = LoggerFactory.getLogger(SentinelFileRuleLoader.class);

    /**
     * 流控规则文件路径
     */
    @Value("${" + SentinelStrategyConstant.SPRING_APPLICATION_STRATEGY_SENTINEL_FLOW_PATH + ":" + DiscoveryConstant.PREFIX_CLASSPATH + SentinelStrategyConstant.SENTINEL_FLOW_KEY + "." + DiscoveryConstant.JSON_FORMAT + "}")
    protected String flowPath;

    /**
     * 降级规则文件路径
     */
    @Value("${" + SentinelStrategyConstant.SPRING_APPLICATION_STRATEGY_SENTINEL_DEGRADE_PATH + ":" + DiscoveryConstant.PREFIX_CLASSPATH + SentinelStrategyConstant.SENTINEL_DEGRADE_KEY + "." + DiscoveryConstant.JSON_FORMAT + "}")
    protected String degradePath;

    /**
     * 授权规则文件路径
     */
    @Value("${" + SentinelStrategyConstant.SPRING_APPLICATION_STRATEGY_SENTINEL_AUTHORITY_PATH + ":" + DiscoveryConstant.PREFIX_CLASSPATH + SentinelStrategyConstant.SENTINEL_AUTHORITY_KEY + "." + DiscoveryConstant.JSON_FORMAT + "}")
    protected String authorityPath;

    /**
     * 系统规则文件路径
     */
    @Value("${" + SentinelStrategyConstant.SPRING_APPLICATION_STRATEGY_SENTINEL_SYSTEM_PATH + ":" + DiscoveryConstant.PREFIX_CLASSPATH + SentinelStrategyConstant.SENTINEL_SYSTEM_KEY + "." + DiscoveryConstant.JSON_FORMAT + "}")
    protected String systemPath;

    /**
     * 热点参数流控规则文件路径
     */
    @Value("${" + SentinelStrategyConstant.SPRING_APPLICATION_STRATEGY_SENTINEL_PARAM_FLOW_PATH + ":" + DiscoveryConstant.PREFIX_CLASSPATH + SentinelStrategyConstant.SENTINEL_PARAM_FLOW_KEY + "." + DiscoveryConstant.JSON_FORMAT + "}")
    protected String paramFlowPath;

    @Autowired
    protected ApplicationContext applicationContext;

    @Override
    public void load() {
        if (CollectionUtils.isEmpty(FlowRuleManager.getRules())) {
            FlowRuleManager.loadRules(new SentinelFlowRuleParser().convert(getRuleText(applicationContext, flowPath)));
            LOG.info("{} flow rules form file loaded...", FlowRuleManager.getRules().size());
        }

        if (CollectionUtils.isEmpty(DegradeRuleManager.getRules())) {
            DegradeRuleManager.loadRules(new SentinelDegradeRuleParser().convert(getRuleText(applicationContext, degradePath)));
            LOG.info("{} degrade rules form file loaded...", DegradeRuleManager.getRules().size());
        }

        if (CollectionUtils.isEmpty(AuthorityRuleManager.getRules())) {
            AuthorityRuleManager.loadRules(new SentinelAuthorityRuleParser().convert(getRuleText(applicationContext, authorityPath)));
            LOG.info("{} authority rules form file loaded...", AuthorityRuleManager.getRules().size());
        }

        if (CollectionUtils.isEmpty(SystemRuleManager.getRules())) {
            SystemRuleManager.loadRules(new SentinelSystemRuleParser().convert(getRuleText(applicationContext, systemPath)));
            LOG.info("{} system rules form file loaded...", SystemRuleManager.getRules().size());
        }

        if (CollectionUtils.isEmpty(ParamFlowRuleManager.getRules())) {
            ParamFlowRuleManager.loadRules(new SentinelParamFlowRuleParser().convert(getRuleText(applicationContext, paramFlowPath)));
            LOG.info("{} param flow rules form file loaded...", ParamFlowRuleManager.getRules().size());
        }
    }

    public String getFlowPath() {
        return flowPath;
    }

    public String getDegradePath() {
        return degradePath;
    }

    public String getAuthorityPath() {
        return authorityPath;
    }

    public String getSystemPath() {
        return systemPath;
    }

    public String getParamFlowPath() {
        return paramFlowPath;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static String getRuleText(ApplicationContext applicationContext, String path) {
        String text = FileContextUtil.getText(applicationContext, path);
        if (StringUtils.isEmpty(text)) {
            text = SentinelStrategyConstant.SENTINEL_EMPTY_RULE;
        }

        return text;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRuleList(ApplicationContext applicationContext, String path) {
        String text = getRuleText(applicationContext, path);

        return (T) JsonUtil.fromJson(text, new TypeReference<List<T>>() {
        });
    }
}
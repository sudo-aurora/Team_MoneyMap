package com.demo.MoneyMap.service.impl;

import com.demo.MoneyMap.dto.request.MonitoringRuleRequestDTO;
import com.demo.MoneyMap.dto.response.MonitoringRuleResponseDTO;
import com.demo.MoneyMap.dto.response.PagedResponseDTO;
import com.demo.MoneyMap.entity.MonitoringRule;
import com.demo.MoneyMap.entity.enums.RuleType;
import com.demo.MoneyMap.exception.DuplicateResourceException;
import com.demo.MoneyMap.exception.ResourceNotFoundException;
import com.demo.MoneyMap.mapper.MonitoringRuleMapper;
import com.demo.MoneyMap.repository.MonitoringRuleRepository;
import com.demo.MoneyMap.service.MonitoringRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MonitoringRuleService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MonitoringRuleServiceImpl implements MonitoringRuleService {

    private final MonitoringRuleRepository ruleRepository;
    private final MonitoringRuleMapper ruleMapper;

    @Override
    public MonitoringRuleResponseDTO createRule(MonitoringRuleRequestDTO requestDTO) {
        log.info("Creating new monitoring rule: {}", requestDTO.getRuleName());

        // Check for duplicate name
        if (ruleRepository.existsByRuleName(requestDTO.getRuleName())) {
            throw new DuplicateResourceException("Rule with name '" + requestDTO.getRuleName() + "' already exists");
        }

        MonitoringRule rule = ruleMapper.toEntity(requestDTO);
        MonitoringRule savedRule = ruleRepository.save(rule);

        log.info("Successfully created rule with ID: {}", savedRule.getId());
        return ruleMapper.toResponseDTO(savedRule);
    }

    @Override
    @Transactional(readOnly = true)
    public MonitoringRuleResponseDTO getRuleById(Long id) {
        MonitoringRule rule = findRuleById(id);
        return ruleMapper.toResponseDTO(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MonitoringRuleResponseDTO> getAllRules(Pageable pageable) {
        Page<MonitoringRule> rulePage = ruleRepository.findAll(pageable);
        return mapToPagedResponse(rulePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringRuleResponseDTO> getActiveRules() {
        return ruleRepository.findByActiveTrue().stream()
                .map(ruleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<MonitoringRuleResponseDTO> getRulesByType(RuleType ruleType, Pageable pageable) {
        Page<MonitoringRule> rulePage = ruleRepository.findByRuleType(ruleType, pageable);
        return mapToPagedResponse(rulePage);
    }

    @Override
    public MonitoringRuleResponseDTO updateRule(Long id, MonitoringRuleRequestDTO requestDTO) {
        log.info("Updating rule with ID: {}", id);

        MonitoringRule rule = findRuleById(id);

        // Check for duplicate name (excluding current rule)
        if (ruleRepository.existsByRuleNameAndIdNot(requestDTO.getRuleName(), id)) {
            throw new DuplicateResourceException("Rule with name '" + requestDTO.getRuleName() + "' already exists");
        }

        ruleMapper.updateEntityFromDTO(requestDTO, rule);
        MonitoringRule updatedRule = ruleRepository.save(rule);

        log.info("Successfully updated rule with ID: {}", id);
        return ruleMapper.toResponseDTO(updatedRule);
    }

    @Override
    public MonitoringRuleResponseDTO activateRule(Long id) {
        log.info("Activating rule with ID: {}", id);
        MonitoringRule rule = findRuleById(id);
        rule.setActive(true);
        MonitoringRule updatedRule = ruleRepository.save(rule);
        return ruleMapper.toResponseDTO(updatedRule);
    }

    @Override
    public MonitoringRuleResponseDTO deactivateRule(Long id) {
        log.info("Deactivating rule with ID: {}", id);
        MonitoringRule rule = findRuleById(id);
        rule.setActive(false);
        MonitoringRule updatedRule = ruleRepository.save(rule);
        return ruleMapper.toResponseDTO(updatedRule);
    }

    @Override
    public void deleteRule(Long id) {
        log.info("Deleting rule with ID: {}", id);
        MonitoringRule rule = findRuleById(id);
        ruleRepository.delete(rule);
        log.info("Successfully deleted rule with ID: {}", id);
    }

    @Override
    public List<RuleType> getRuleTypes() {
        return Arrays.asList(RuleType.values());
    }

    /**
     * Find rule by ID or throw exception.
     */
    private MonitoringRule findRuleById(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found with ID: " + id));
    }

    /**
     * Map Page to PagedResponseDTO.
     */
    private PagedResponseDTO<MonitoringRuleResponseDTO> mapToPagedResponse(Page<MonitoringRule> page) {
        List<MonitoringRuleResponseDTO> content = page.getContent().stream()
                .map(ruleMapper::toResponseDTO)
                .collect(Collectors.toList());

        return PagedResponseDTO.<MonitoringRuleResponseDTO>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}

package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyDeleteDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyUpdateDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.service.EpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CoinCostPolicyServiceImpl implements CoinCostPolicyService {

    private final CoinCostPolicyRepository coinCostPolicyRepository;

    public CoinCostPolicyServiceImpl(CoinCostPolicyRepository coinCostPolicyRepository, EpisodeService episodeService) {
        this.coinCostPolicyRepository = coinCostPolicyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoinCostPolicy> getPolicyEntity(Long policyId) {
        return coinCostPolicyRepository.findById(policyId);
    }

    @Override
    @Transactional
    public void createPolicy(CostPolicyCreateDto costPolicyCreateDto) {
        try {
            CoinCostPolicy newRecord = CoinCostPolicy.builder()
                    .name(costPolicyCreateDto.getName())
                    .coinCost(costPolicyCreateDto.getCoinCost())
                    .build();

            coinCostPolicyRepository.save(newRecord);
        } catch(Exception ex) {
            throw new ServiceMethodException("createPolicy 메서드 에러", ex.getCause());
        }

    }

    @Override
    @Transactional
    public void updatePolicy(CostPolicyUpdateDto costPolicyUpdateDto) {
        CoinCostPolicy targetRecord = coinCostPolicyRepository.findById(costPolicyUpdateDto.getId())
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy Record 없음"));

        try {
            //Todo 변경할 property만 변경하도록 최적화
            String updateName = (costPolicyUpdateDto.getName() != null) ? costPolicyUpdateDto.getName() : targetRecord.getName();
            Integer updateCoinCost = (costPolicyUpdateDto.getCoinCost() != null) ? costPolicyUpdateDto.getCoinCost() : targetRecord.getCoinCost();
            targetRecord.update(updateName, updateCoinCost);

            coinCostPolicyRepository.save(targetRecord);
        } catch(Exception ex) {
            throw new ServiceMethodException("updatePolicy 메서드 에러", ex.getCause());
        }
    }

    @Override
    @Transactional
    public void deletePolicy(CostPolicyDeleteDto costPolicyDeleteDto) {
        CoinCostPolicy targetRecord = coinCostPolicyRepository.findById(costPolicyDeleteDto.getId())
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy Record 없음"));

        try {
            coinCostPolicyRepository.delete(targetRecord);
        } catch(Exception ex) {
        throw new ServiceMethodException("deletePolicy 메서드 에러", ex.getCause());
        }
    }
}

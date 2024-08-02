package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.data.PolicyDBStatus;
import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyDeleteDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyUpdateDto;
import com.ham.netnovel.common.exception.ServiceMethodException;
import com.ham.netnovel.episode.service.EpisodeService;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CoinCostPolicyServiceImpl implements CoinCostPolicyService {

    private final CoinCostPolicyRepository coinCostPolicyRepository;
    private final EpisodeService episodeService;

    public CoinCostPolicyServiceImpl(CoinCostPolicyRepository coinCostPolicyRepository, EpisodeService episodeService) {
        this.coinCostPolicyRepository = coinCostPolicyRepository;
        this.episodeService = episodeService;
    }

    @Override
    public Optional<CoinCostPolicy> getPolicyEntity(Long policyId) {
        return coinCostPolicyRepository.findById(policyId);
    }

    @Override
    public void createPolicy(CostPolicyCreateDto costPolicyCreateDto) {
        try {
            CoinCostPolicy newRecord = CoinCostPolicy.builder()
                    .name(costPolicyCreateDto.getName())
                    .coinCost(costPolicyCreateDto.getCoinCost())
                    .policyRange(costPolicyCreateDto.getPolicyRange())
                    .rangeValue(costPolicyCreateDto.getRangeValue())
                    .build();

            coinCostPolicyRepository.save(newRecord);
        } catch(Exception ex) {
            throw new ServiceMethodException("createPolicy 메서드 에러", ex.getCause());
        }

    }

    @Override
    public void updatePolicy(CostPolicyUpdateDto costPolicyUpdateDto) {
        CoinCostPolicy targetRecord = coinCostPolicyRepository.findById(costPolicyUpdateDto.getId())
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy Record 없음"));

        try {
            //Todo 변경할 property만 변경하도록 최적화
            String updateName = (costPolicyUpdateDto.getName() != null) ? costPolicyUpdateDto.getName() : targetRecord.getName();
            Integer updateCoinCost = (costPolicyUpdateDto.getCoinCost() != null) ? costPolicyUpdateDto.getCoinCost() : targetRecord.getCoinCost();
            PolicyRange updatePolicyRange = (costPolicyUpdateDto.getPolicyRange() != null) ? costPolicyUpdateDto.getPolicyRange() : targetRecord.getPolicyRange();
            Integer updateRangeValue = (costPolicyUpdateDto.getRangeValue() != null) ? costPolicyUpdateDto.getRangeValue() : targetRecord.getRangeValue();
            targetRecord.update(updateName, updateCoinCost, updatePolicyRange, updateRangeValue);

            coinCostPolicyRepository.save(targetRecord);
        } catch(Exception ex) {
            throw new ServiceMethodException("updatePolicy 메서드 에러", ex.getCause());
        }
    }

    @Override
    public void deletePolicy(CostPolicyDeleteDto costPolicyDeleteDto) {
        CoinCostPolicy targetRecord = coinCostPolicyRepository.findById(costPolicyDeleteDto.getId())
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy Record 없음"));

        try {
            targetRecord.changeStatus(PolicyDBStatus.DELETED_BY_USER);
            coinCostPolicyRepository.save(targetRecord);
        } catch(Exception ex) {
        throw new ServiceMethodException("deletePolicy 메서드 에러", ex.getCause());
        }
    }

    //Todo 예외 처리 방법 설계
    @Override
    public void validatePolicy(Long costPolicyId, Integer episodeNumber) throws ServiceMethodException {
        CoinCostPolicy targetRecord = coinCostPolicyRepository.findById(costPolicyId)
                .orElseThrow(() -> new NoSuchElementException("CoinCostPolicy Record 없음"));

        if (targetRecord.getPolicyRange() == PolicyRange.INITIAL) {
            if (episodeNumber > targetRecord.getRangeValue()) {
                throw new ServiceMethodException("validatePolicy: [INITIAL] episodeNumber > rangeValue");
            }
        }
        else if (targetRecord.getPolicyRange() == PolicyRange.RECENT) {
            if (episodeNumber < targetRecord.getRangeValue()) {
                throw new ServiceMethodException("validatePolicy: [RECENT] episodeNumber < rangeValue");
            }
        }
    }
}

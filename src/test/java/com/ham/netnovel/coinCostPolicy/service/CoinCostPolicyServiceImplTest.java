package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@SpringBootTest
@Transactional
@Slf4j
class CoinCostPolicyServiceImplTest {

    @Autowired
    CoinCostPolicyRepository repository;

    @Autowired
    CoinCostPolicyService service;

    @Test
    public void create() {
        //given
        CostPolicyCreateDto createDto = CostPolicyCreateDto.builder()
                .name("무료")
                .coinCost(1)
                .policyRange(PolicyRange.INITIAL)
                .rangeValue(15)
                .build();

        //when
        Long id = service.createPolicy(createDto);
        log.info("id = {}", id);

        //then
        CoinCostPolicy policyEntity = service.getPolicyEntity(id)
                        .orElseThrow(() -> new NoSuchElementException("없음"));

        Assertions.assertThat(policyEntity.getName()).isEqualTo(createDto.getName());
        Assertions.assertThat(policyEntity.getCoinCost()).isEqualTo(createDto.getCoinCost());
        Assertions.assertThat(policyEntity.getPolicyRange()).isEqualTo(createDto.getPolicyRange());
    }


    @Test
    public void update() {

    }

    @Test
    public void delete() {

    }
}
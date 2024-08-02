package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.data.PolicyDBStatus;
import com.ham.netnovel.coinCostPolicy.data.PolicyRange;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyDeleteDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@SpringBootTest
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
        service.createPolicy(createDto);

        //then
        CoinCostPolicy policyEntity = service.getPolicyEntity(1L)
                        .orElseThrow(() -> new NoSuchElementException("없음"));

        Assertions.assertThat(policyEntity.getName()).isEqualTo(createDto.getName());
        Assertions.assertThat(policyEntity.getCoinCost()).isEqualTo(createDto.getCoinCost());
        Assertions.assertThat(policyEntity.getPolicyRange()).isEqualTo(createDto.getPolicyRange());
    }


    @Test
    public void update() {
        //given
        CostPolicyUpdateDto updateDto = CostPolicyUpdateDto.builder()
                .id(1L)
                .coinCost(5)
                .build();

        //when
        service.updatePolicy(updateDto);

        //then
        CoinCostPolicy policyEntity = service.getPolicyEntity(1L)
                .orElseThrow(() -> new NoSuchElementException("없음"));

        Assertions.assertThat(policyEntity.getName()).isEqualTo("무료");
        Assertions.assertThat(policyEntity.getCoinCost()).isEqualTo(updateDto.getCoinCost());
    }

    @Test
    public void delete() {
        //given
        CostPolicyDeleteDto deleteDto = CostPolicyDeleteDto.builder()
                .id(1L)
                .build();

        //when
        service.deletePolicy(deleteDto);

        //then
        CoinCostPolicy policyEntity = service.getPolicyEntity(1L)
                .orElseThrow(() -> new NoSuchElementException("없음"));

        Assertions.assertThat(policyEntity.getStatus()).isEqualTo(PolicyDBStatus.DELETED_BY_USER);
    }
}
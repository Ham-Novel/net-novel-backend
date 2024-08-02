package com.ham.netnovel.coinCostPolicy.service;

import com.ham.netnovel.coinCostPolicy.CoinCostPolicy;
import com.ham.netnovel.coinCostPolicy.CoinCostPolicyRepository;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyCreateDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyDeleteDto;
import com.ham.netnovel.coinCostPolicy.dto.CostPolicyUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest
@Slf4j
class CoinCostPolicyServiceImplTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CoinCostPolicyRepository repository;

    @Autowired
    CoinCostPolicyService service;

    void setup() {
        //DB records 전부 삭제
        repository.deleteAll();

        //auto_increment id를 1부터 초기화.
        String sql = "ALTER TABLE coin_cost_policy ALTER COLUMN id RESTART WITH 1";
        jdbcTemplate.execute(sql);
    }

    @Test
    public void create() {
        //given
        setup();
        CostPolicyCreateDto createDto = CostPolicyCreateDto.builder()
                .name("무료")
                .coinCost(1)
                .build();

        //when
        service.createPolicy(createDto);

        //then
        CoinCostPolicy policyEntity = service.getPolicyEntity(1L)
                        .orElseThrow(() -> new NoSuchElementException("없음"));

        Assertions.assertThat(policyEntity.getName()).isEqualTo(createDto.getName());
        Assertions.assertThat(policyEntity.getCoinCost()).isEqualTo(createDto.getCoinCost());
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
        Optional<CoinCostPolicy> entity = service.getPolicyEntity(1L);
        Assertions.assertThat(entity.isPresent()).isFalse();

    }
}
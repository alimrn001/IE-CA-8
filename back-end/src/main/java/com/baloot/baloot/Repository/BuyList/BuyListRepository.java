package com.baloot.baloot.Repository.BuyList;

import com.baloot.baloot.models.BuyList.BuyList;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyListRepository extends JpaRepository<BuyList, Long> {

    BuyList getBuyListByBuyListId(long buyListId);

}

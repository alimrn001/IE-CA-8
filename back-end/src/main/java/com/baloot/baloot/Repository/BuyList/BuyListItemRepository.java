package com.baloot.baloot.Repository.BuyList;

import com.baloot.baloot.models.BuyList.BuyList;
import com.baloot.baloot.models.BuyList.BuyListItem;
import com.baloot.baloot.models.Commodity.Commodity;
import com.baloot.baloot.models.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuyListItemRepository extends JpaRepository<BuyListItem, Long> {

    List<BuyListItem> findByUser_Username(String username);

    @Query("SELECT bli FROM BuyListItem bli WHERE bli.buyList = :buyList")
    List<BuyListItem> findByBuyList(@Param("buyList") BuyList buyList);

    BuyListItem getBuyListItemByUserAndCommodityAndBuyList(User user, Commodity commodity, BuyList buyList);

}

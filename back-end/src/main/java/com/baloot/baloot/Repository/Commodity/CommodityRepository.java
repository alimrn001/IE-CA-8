package com.baloot.baloot.Repository.Commodity;

import com.baloot.baloot.models.Commodity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, Integer> {

    Commodity getCommodityById(int commodityId);

    @Query(
            value = "SELECT cc.categories FROM commodity_categories cc WHERE cc.commodity_id = :id",
            nativeQuery = true
    )
    List<String> findCategoriesByCommodityId(@Param("id") int id);

    List<Commodity> findByNameContaining(@Param("name") String name);

    List<Commodity> findAllByOrderByNameAsc();

    List<Commodity> findAllByOrderByNameDesc();

    List<Commodity> findAllByOrderByPriceAsc();

    List<Commodity> findAllByOrderByPriceDesc();

    List<Commodity> findAllByOrderByRatingAsc();

    List<Commodity> findAllByOrderByRatingDesc();

    List<Commodity> findByCategoriesContaining(@Param("category") String category);

    List<Commodity> findByProviderId(@Param("providerId") int ProviderId);

    List<Commodity> findByProviderName(@Param("providerName") String providerName);

}

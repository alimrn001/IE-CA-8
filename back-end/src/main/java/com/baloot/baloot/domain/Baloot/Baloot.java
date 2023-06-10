package com.baloot.baloot.domain.Baloot;

import com.baloot.baloot.domain.Baloot.Comment.*;
import com.baloot.baloot.domain.Baloot.Commodity.Category;
import com.baloot.baloot.domain.Baloot.Commodity.CommoditiesManager;
import com.baloot.baloot.domain.Baloot.Commodity.Commodity;
import com.baloot.baloot.domain.Baloot.DiscountCoupon.DiscountCoupon;
import com.baloot.baloot.domain.Baloot.DiscountCoupon.DiscountCouponsManager;
import com.baloot.baloot.Exceptions.*;
import com.baloot.baloot.domain.Baloot.Provider.Provider;
import com.baloot.baloot.domain.Baloot.Provider.ProvidersManager;
import com.baloot.baloot.domain.Baloot.Rating.*;
import com.baloot.baloot.domain.Baloot.User.User;
import com.baloot.baloot.domain.Baloot.User.UsersManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Baloot {

    private final UsersManager usersManager = new UsersManager();

    private final ProvidersManager providersManager = new ProvidersManager();

    private final CommoditiesManager commoditiesManager = new CommoditiesManager();

    private final CommentsManager commentsManager = new CommentsManager();

    private final DiscountCouponsManager discountCouponsManager = new DiscountCouponsManager();

    private final Map<String, Rating> balootRatings = new HashMap<>();

    private static Baloot instance;

    private Baloot() {}


    public static Baloot getInstance() {
        if(instance == null)
            instance = new Baloot();
        return instance;
    }


    public boolean userIsLoggedIn() {
        return usersManager.loggedInUserExists();
    }


    public boolean searchFilterIsApplied() {
        return commoditiesManager.getFilteringStatus();
    }


    public boolean sortingIsAppliedForCommodities() {
        return commoditiesManager.getSortingStatus();
    }


    public String getLoggedInUsername() {
        return usersManager.getLoggedInUser();
    }


    public String getProviderNameById(int providerId) {
        return providersManager.getProviderNameById(providerId);
    }


    public int getDiscountCouponValueByCode(String discountCode) throws Exception {
        return discountCouponsManager.getDiscountCouponValueByCode(discountCode);
    }


    public void handleLogin(String username, String password) throws Exception {
        usersManager.handleLogin(username, password);
    }


    public void handleLogout() throws Exception {
        usersManager.handleLogout();
        clearSearchFilters();
    }


    public boolean userExists(String username) {
        return usersManager.userExists(username);
    }


    public boolean commodityExists(int commodityId) {
        return commoditiesManager.commodityExists(commodityId);
    }


    public void addUser(User user) throws Exception {
        usersManager.addUser(user);
    }


    public void addProvider(Provider provider) throws Exception {
        providersManager.addProvider(provider);
    }


    public void addCommodity(Commodity commodity) throws Exception {
        if(!providersManager.providerExists(commodity.getProviderId()))
            throw new ProviderNotExistsException();

        commoditiesManager.addCommodity(commodity);
        providersManager.getBalootProviders().get(commodity.getProviderId()).addProvidedCommodity(commodity.getId());
        providersManager.getBalootProviders().get(commodity.getProviderId()).updateCommoditiesData(commodity.getRating());
    }


    public void addComment(Comment comment) throws Exception { //fix this !!!1
        if(!usersManager.userExists(comment.getUsername()))
            throw new UserNotExistsException();
        if(!commoditiesManager.commodityExists(comment.getCommodityId()))
            throw new CommodityNotExistsException();
        commentsManager.addComment(comment);
        usersManager.getBalootUsers().get(comment.getUsername()).addCommentReference(comment.getCommentId());
        commoditiesManager.getBalootCommodities().get(comment.getCommodityId()).addComment(comment.getCommentId());
    }


    public void addCommentByUserInput(String username, int commodityId,  String text) throws Exception {
        if(!usersManager.userExists(username))
            throw new UserNotExistsException();
        if(!commoditiesManager.commodityExists(commodityId))
            throw new CommodityNotExistsException();
        int commentId = commentsManager.addCommentByUserInput(username, commodityId, text);
        usersManager.getBalootUsers().get(username).addCommentReference(commentId);
        commoditiesManager.getBalootCommodities().get(commodityId).addComment(commentId);
    }


    public void addRating(String username, int commodityId, int rate) throws Exception {
        if(!usersManager.userExists(username))
            throw new UserNotExistsException();
        if(!commoditiesManager.commodityExists(commodityId))
            throw new CommodityNotExistsException();
        if(rate > 10 || rate < 1)
            throw new RatingOutOfRangeException();

        String ratingPrimaryKey = username + "_" + commodityId;
        Rating rating = new Rating(username, commodityId, rate);

        if(!balootRatings.containsKey(ratingPrimaryKey)) {
            commoditiesManager.getBalootCommodities().get(commodityId).addNewRating(rate);
            balootRatings.put(ratingPrimaryKey, rating);
            return;
        }
        int previousRate = balootRatings.get(ratingPrimaryKey).getScore();
        commoditiesManager.getBalootCommodities().get(commodityId).updateUserRating(previousRate, rate);
        balootRatings.put(ratingPrimaryKey, rating);
    }


    public void addDiscountCoupon(DiscountCoupon discountCoupon) throws Exception {
        discountCouponsManager.addDiscountCoupon(discountCoupon);
    }


    public void addRemoveBuyList(String username, int commodityId, boolean isAdding) throws Exception {
        User user = usersManager.getBalootUser(username);
        Commodity commodity = commoditiesManager.getBalootCommodity(commodityId);
        if(commodity.getInStock()==0 && isAdding)
            throw new ItemNotAvailableInStockException();
        if(user.itemExistsInBuyList(commodityId)) {
            if(isAdding)
                throw new ItemAlreadyExistsInBuyListException();
            user.removeFromBuyList(commodityId);
        }
        else {
            if(isAdding) {
                user.addToBuyList(commodityId);
                return;
            }
            throw new ItemNotInBuyListForRemovingException();
        }
    }


    public void purchaseUserBuyList(String username, String discountCode) throws Exception {
        if(!usersManager.userExists(username))
            throw new UserNotExistsException();

        int discountAmount = 0;
        if(!discountCode.equals("")) {
            DiscountCoupon discountCoupon = discountCouponsManager.getDiscountCouponByCode(discountCode);
            usersManager.addCouponForUser(username, discountCode);
            discountAmount = discountCoupon.getDiscount();
        }
        ArrayList<Integer> userBuyList = usersManager.getBalootUsers().get(username).getBuyList();
        int totalPurchasePrice = 0;
        for(Integer buyListItemId : userBuyList) {
            totalPurchasePrice += commoditiesManager.getBalootCommodities().get(buyListItemId).getPrice();
            if(commoditiesManager.getBalootCommodities().get(buyListItemId).getInStock()==0)
                throw new ItemNotAvailableInStockException(); //not needed generally but might be good for simultaneous purchasing by diffrent users
        }
        System.out.println("updated price is : " + (totalPurchasePrice*(1-((double)discountAmount/100))));
        if(usersManager.getBalootUsers().get(username).getCredit() < (totalPurchasePrice*(1-((double)discountAmount/100))))
            throw new NotEnoughCreditException();

        for(Integer buyListItemId : userBuyList)
            commoditiesManager.getBalootCommodities().get(buyListItemId).reduceInStock(1);
        usersManager.getBalootUsers().get(username).purchaseBuyList((int)(totalPurchasePrice*(1-((double)discountAmount/100))));

    }


    public void addCreditToUser(String username, int credit) throws Exception {
        usersManager.addCreditToUser(username, credit);
    }


    public void voteComment(String username, int commentId, int vote) throws Exception {
        if(!usersManager.userExists(username))
            throw new UserNotExistsException();
        if(!commentsManager.commentExists(commentId))
            throw new CommentNotExistsException();

        boolean beenLikedBefore = usersManager.getBalootUsers().get(username).userHasLikedComment(commentId);
        boolean beenDislikedBefore = usersManager.getBalootUsers().get(username).userHasDislikedComment(commentId);

        if(vote==1)
            usersManager.getBalootUsers().get(username).addCommentToLikedList(commentId);
        else if(vote==-1)
            usersManager.getBalootUsers().get(username).addCommentToDislikedList(commentId);

        commentsManager.voteComment(commentId, vote, beenLikedBefore, beenDislikedBefore);
    }


    public Map<Integer, Comment> getCommodityComments(int commodityId) throws Exception {
        if(!commoditiesManager.commodityExists(commodityId))
            throw new CommodityNotExistsException();
        return commentsManager.getCommodityComments(commodityId);
    }


    public Map<Integer, Commodity> getCommoditiesByCategory(String category) {
        return commoditiesManager.getCommoditiesByCategory(category);
    }


    public Map<Integer, Commodity> getCommoditiesByPriceRange(int startPrice, int endPrice) {
        return commoditiesManager.getCommoditiesByPriceRange(startPrice, endPrice);
    }


    public Map<Integer, Commodity> getUserBuyListCommodities(String username) throws Exception {
        return commoditiesManager.getCommoditiesByIDList(usersManager.getUserBuyListItems(username));
    }


    public int getUserBuyListTotalPrice(String username) throws Exception {
        return commoditiesManager.getCommoditiesListTotalPrice(usersManager.getUserBuyListItems(username));
    }


    public void getCommoditiesFilteredByName(String commodityName) {
        commoditiesManager.filterCommoditiesByName(commodityName);
    }


    public void getCommoditiesFilteredByCategory(String category) {
        commoditiesManager.filterCommoditiesByCategory(category);
    }


    public List<Commodity> getFilteredCommodities() {
        return commoditiesManager.getFilteredCommodities();
    }


    public List<Commodity> getRecommendedCommodities(int commodityId) throws Exception {
        return new ArrayList<>(commoditiesManager.getRecommendedCommodities(commodityId).values());
    }


    public void getCommoditiesSortedByRating() {
        commoditiesManager.sortCommoditiesByRating();
    }


    public void getCommoditiesSortedByName() {
        commoditiesManager.sortCommoditiesByName();
    }


    public void getCommoditiesSortedByPrice() {
        commoditiesManager.sortCommoditiesByPrice();
    }


    public void clearSearchFilters() {
        commoditiesManager.clearFilters();
    }


    public User getBalootUser(String username) throws Exception {
        return usersManager.getBalootUser(username);
    }


    public Provider getBalootProvider(int providerID) throws Exception {
        return providersManager.getBalootProvider(providerID);
    }


    public Commodity getBalootCommodity(int commodityID) throws Exception {
        return commoditiesManager.getBalootCommodity(commodityID);
    }


    public List<Commodity> getCommoditiesByIDList(ArrayList<Integer> commoditiesID) throws Exception {
        return new ArrayList<>(commoditiesManager.getCommoditiesByIDList(commoditiesID).values());
    }


    public Comment getBalootComment(int commentId) throws Exception {
        return commentsManager.getBalootComment(commentId);
    }


    public Map<String, User> getBalootUsers() {
        return usersManager.getBalootUsers();
    }


    public Map<Integer, Provider> getBalootProviders() {
        return providersManager.getBalootProviders();
    }


    public Map<Integer, Commodity> getBalootCommodities() {
        return commoditiesManager.getBalootCommodities();
    }


    public Map<Integer, Comment> getBalootComments() {
        return commentsManager.getBalootComments();
    }


    public Map<String, Rating> getBalootRatings() {
        return balootRatings;
    }


    public Map<String, Category> getBalootCategorySections() {
        return commoditiesManager.getBalootCategories();
    }


    public Map<String, DiscountCoupon> getBalootDiscountCoupons() {
        return discountCouponsManager.getBalootDiscountCoupons();
    }

}

package com.baloot.baloot.services;

import com.baloot.baloot.Utils.HTTPReqHandler;
import com.baloot.baloot.Repository.BuyList.BuyListItemRepository;
import com.baloot.baloot.Repository.BuyList.BuyListRepository;
import com.baloot.baloot.Repository.Comment.CommentRepository;
import com.baloot.baloot.Repository.Comment.VoteRepository;
import com.baloot.baloot.Repository.Commodity.CommodityRepository;
import com.baloot.baloot.Repository.DiscountCoupon.DiscountCouponRepository;
import com.baloot.baloot.Repository.Provider.ProviderRepository;
import com.baloot.baloot.Repository.Rating.RatingRepository;
import com.baloot.baloot.Repository.User.UserRepository;

import com.baloot.baloot.Exceptions.*;
import com.baloot.baloot.Utils.HashString;
import com.baloot.baloot.Utils.JWTUtils;
import com.baloot.baloot.domain.Baloot.Utilities.EmailParser;
import com.baloot.baloot.domain.Baloot.Utilities.LocalDateAdapter;

import com.baloot.baloot.models.BuyList.BuyList;
import com.baloot.baloot.models.BuyList.BuyListItem;
import com.baloot.baloot.models.DiscountCoupon.DiscountCoupon;
import com.baloot.baloot.models.Comment.*;
import com.baloot.baloot.models.User.User;
import com.baloot.baloot.models.Provider.Provider;
import com.baloot.baloot.models.Commodity.Commodity;
import com.baloot.baloot.models.Rating.Rating;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

@Service
public class BalootService {

    final static String usersURL = "http://5.253.25.110:5000/api/users";

    final static String providersURL = "http://5.253.25.110:5000/api/v2/providers";

    final static String commoditiesURL = "http://5.253.25.110:5000/api/v2/commodities";

    final static String commentsURL = "http://5.253.25.110:5000/api/comments";

    final static String discountCouponsURL = "http://5.253.25.110:5000/api/discount";

    private User loggedInUser;

    private final UserRepository userRepository;

    private final ProviderRepository providerRepository;

    private final CommodityRepository commodityRepository;

    private final CommentRepository commentRepository;

    private final DiscountCouponRepository discountCouponRepository;

    private final RatingRepository ratingRepository;

    private final VoteRepository voteRepository;

    private final BuyListItemRepository buyListItemRepository;

    private final BuyListRepository buyListRepository;

    @Autowired
    private BalootService(UserRepository userRepository_, ProviderRepository providerRepository_,
                          CommodityRepository commodityRepository_, CommentRepository commentRepository_,
                          DiscountCouponRepository discountCouponRepository_ , RatingRepository ratingRepository_,
                          VoteRepository voteRepository_, BuyListRepository buyListRepository_, BuyListItemRepository buyListItemRepository_) {
        userRepository       = userRepository_;
        providerRepository   = providerRepository_;
        commodityRepository  = commodityRepository_;
        commentRepository    = commentRepository_;
        discountCouponRepository = discountCouponRepository_;
        ratingRepository = ratingRepository_;
        voteRepository = voteRepository_;
        buyListItemRepository = buyListItemRepository_;
        buyListRepository = buyListRepository_;
        initializeDataBase(usersURL, providersURL, commoditiesURL, commentsURL, discountCouponsURL);
    }

    public void initializeDataBase(String usersAddr, String providersAddr, String commoditiesAddr, String commentsAddr, String discountCouponsAddr) {
        boolean readData = true;
        if(! commodityRepository.findAll().isEmpty()) {
            readData = false;
        }
        try {
            if(readData) {
                retrieveUsersDataFromAPI(usersAddr);
                retrieveProvidersDataFromAPI(providersAddr);
                System.out.println(providerRepository.getProviderById(1).getName() + " is name");
                retrieveCommoditiesDataFromAPI(commoditiesAddr);
                retrieveCommentsDataFromAPI(commentsAddr);
                retrieveDiscountsDataFromAPI(discountCouponsAddr);
            }
            else {
                //retrieveCommentsDataFromAPI(commentsAddr);
                //retrieveDiscountsDataFromAPI(discountCouponsAddr);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveUsersDataFromAPI(String url) throws Exception {
        String userDataJsonStr = new HTTPReqHandler().httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = gson.fromJson(userDataJsonStr, userListType);
        for (User user : userList) {
            user.setPassword(HashString.hashString(user.getPassword()));
            userRepository.save(user);
        }
    }

    private void retrieveProvidersDataFromAPI(String url) throws Exception {
        String providerDataJsonStr = new HTTPReqHandler().httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type providerListType = new TypeToken<ArrayList<Provider>>(){}.getType();
        List<Provider> providerList = gson.fromJson(providerDataJsonStr, providerListType);
        for(Provider provider : providerList) {
            providerRepository.save(provider);
        }
    }

    private void retrieveCommoditiesDataFromAPI(String url) throws Exception {
        String commodityDataJsonStr = new HTTPReqHandler().httpGetRequest(url);
        Gson gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        Type commodityListType = new TypeToken<ArrayList<Commodity>>(){}.getType();
        List<Commodity> commodityList = gson.fromJson(commodityDataJsonStr, commodityListType);
        System.out.println(commodityList.get(20).getProviderId());
        for(Commodity commodity : commodityList) {
            System.out.println("p id is : " + commodity.getProviderId());
            commodity.setProvider(providerRepository.getProviderById(commodity.getProviderId()));
            commodityRepository.save(commodity);
        }
    }

    private void retrieveCommentsDataFromAPI(String url) throws Exception {
        String commentsDataJsonStr = new HTTPReqHandler().httpGetRequest(url);
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        Type commentListType = new TypeToken<ArrayList<Comment>>(){}.getType();
        List<Comment> commentList = gson.fromJson(commentsDataJsonStr, commentListType);
        for(Comment comment : commentList) {
            comment.setCommodity(commodityRepository.getCommodityById(comment.getCommodityId()));
            comment.setDate("2022-11-11");
            comment.setText("sample");
            comment.setUsername(new EmailParser().getEmailUsername(comment.getUsername()));
            comment.setUser(userRepository.getUserByUsername(comment.getUsername()));
            commentRepository.save(comment);
        }
    }

    private void retrieveDiscountsDataFromAPI(String url) throws Exception {
        String discountsDataJsonStr = new HTTPReqHandler().httpGetRequest(url);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        Type discountListType = new TypeToken<ArrayList<DiscountCoupon>>(){}.getType();
        List<DiscountCoupon> discountList = gson.fromJson(discountsDataJsonStr, discountListType);
        for(DiscountCoupon discountCoupon : discountList) {
            discountCouponRepository.save(discountCoupon);
        }
    }

    public boolean userIsLoggedIn() {
        return loggedInUser != null;
    }

    public boolean usernameExists(String username) {
        return (userRepository.getUserByUsername(username)!=null);
    }

    public boolean emailExists(String email) {
        return (userRepository.getUserByEmail(email)!=null);
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public List<User> getUsersList() {
        return userRepository.findAll();
    }

    public Provider getProviderById(int providerId) {
        return providerRepository.getProviderById(providerId);
    }

    public List<Provider> getProvidersByName(String providerName) {
        return providerRepository.findProvidersByNameContaining(providerName);
    }

    public List<Provider> getProvidersList() {
        return providerRepository.findAll();
    }

    public List<Commodity> getCommoditiesList() {
        return commodityRepository.findAll();
    }

    public Commodity getCommodityById(int commodityId) {
        return commodityRepository.getCommodityById(commodityId);
    }

    public List<String> getCommodityCategories(int commodityId) {
        return commodityRepository.findCategoriesByCommodityId(commodityId);
    }

    public List<Comment> getCommentsList() {
        return commentRepository.findAll();
    }

    public List<Commodity> getBalootCommodities() {
        return commodityRepository.findAll();
    }

    public List<Commodity> getCommoditiesByName(String name) {
        return commodityRepository.findByNameContaining(name);
    }

    public List<Commodity> getCommoditiesByOrderedByName(boolean isAsc) {
        if(isAsc)
            return commodityRepository.findAllByOrderByNameAsc();
        return commodityRepository.findAllByOrderByNameDesc();
    }

    public List<Commodity> getCommoditiesByOrderedByPrice(boolean isAsc) {
        if(isAsc)
            return commodityRepository.findAllByOrderByPriceAsc();
        return commodityRepository.findAllByOrderByPriceDesc();
    }

    public List<Commodity> getCommoditiesByProviderName(String providerName) {
        return commodityRepository.findByProviderName(providerName);
    }

    public List<Commodity> getCommoditiesByCategory(String category) {
        return commodityRepository.findByCategoriesContaining(category);
    }

    public List<Commodity> getCommoditiesByProviderId(int providerId) {
        return commodityRepository.findByProviderId(providerId);
    }

    public Comment getCommentById(int commentId) {
        return commentRepository.getCommentByCommentId(commentId);
    }

    public List<Comment> getUserComments(String username) {
        return commentRepository.getCommentsByUserUsername(username);
    }

    public List<Comment> getCommodityComments(int commodityId) {
        return commentRepository.getCommentsByCommodity_Id(commodityId);
    }

    public List<BuyListItem> getUserBuyList(String username) {
        return buyListItemRepository.findByUser_Username(username);
    }

    public Rating getRating(String username, int commodityId) {
        return ratingRepository.getRatingByUserUsernameAndCommodity_Id(username, commodityId);
    }

    public long getNumberOfCommodityRatings(int commodityId) {
        return ratingRepository.countByCommodity_Id(commodityId);
    }

    public long getNumberOfUserRatings(String username) {
        return ratingRepository.countByUserUsername(username);
    }

    public int getNumberOfCommentLikes(Comment comment) {
        return voteRepository.countByCommentAndVote(comment, 1);
    }

    public int getNumberOfCommentDislikes(Comment comment) {
        return voteRepository.countByCommentAndVote(comment, -1);
    }

    public long getNumberOfCommentLikesById(int commentId) {
        return voteRepository.countByComment_CommentIdAndVote(commentId, 1);
    }

    public long getNumberOfCommentDislikesById(int commentId) {
        return voteRepository.countByComment_CommentIdAndVote(commentId, -1);
    }

    public void checkUsernameValidity(String username) throws Exception {
        boolean containsHalfSpace = username.contains("\u200C");
        boolean containsSpace = username.contains(" ");
        boolean containsAtSymbol = username.contains("@");
        boolean containsHashSymbol = username.contains("#");
        boolean containsExclamationSymbol = username.contains("!");
        if(containsHalfSpace || containsSpace || containsAtSymbol || containsHashSymbol || containsExclamationSymbol)
            throw new UsernameWrongCharacterException();
    }

    public void addUser(String username, String password, String birthDate, String email, String address) throws Exception {
        checkUsernameValidity(username);
        LocalDate birth = LocalDate.parse(birthDate);
        String hashedPassword = HashString.hashString(password);
        User user = new User(username, hashedPassword, birth, email, address, 0);
        userRepository.save(user);
    }

    public void updateUser(String username, String password, String birthDate, String email, String address) throws Exception {
        checkUsernameValidity(username); //or maybe not cause it's actually from github !
        User user = userRepository.getUserByUsername(username); //can also be done by email but username is better
        LocalDate birth = LocalDate.parse(birthDate);
        String hashedPassword = HashString.hashString(password);
        user.setPassword(hashedPassword);
        user.setBirthDate(birth);
        user.setEmail(email); //probably not needed since email must also be unique !
        user.setAddress(address);
        userRepository.save(user);
    }

    public void addCreditToUser(User user, int credit) {
        user.addCredit(credit);
        userRepository.save(user); //exceptions handled in service layer !
    }

    public void reduceCreditFromUser(User user, int credit) {
        user.reduceCredit(credit);
        userRepository.save(user); //exceptions handled in service layer !
    }

    public void login(String username, String password) throws Exception {
        if(username==null || password==null)
            throw new ForbiddenValueException();
        User user = getUserByUsername(username);
        if(user==null)
            throw new LoginFailedException();
        if(!HashString.checkPassword(password, user.getPassword()))
            throw new LoginFailedException();
        loggedInUser = user;
    }

    public void loginWithJwtToken(String userEmail) {
        this.loggedInUser = userRepository.getUserByEmail(userEmail); //handling null exception ??
    }

    public String loginByEmail(String email, String password) throws Exception {
        if(email==null || password==null)
            throw new ForbiddenValueException();
        User user = getUserByEmail(email);
        if(user==null)
            throw new LoginFailedException();
        if(!HashString.checkPassword(password, user.getPassword()))
            throw new LoginFailedException();
        loggedInUser = user;
        return JWTUtils.createJWTToken(email);
    }

    public void logout() {
        this.loggedInUser = null;
    }

    public void addRating(String username, int commodity_id, int score) throws Exception{
        Rating oldRating = ratingRepository.getRatingByUserUsernameAndCommodity_Id(username, commodity_id);
        Commodity commodity = commodityRepository.getCommodityById(commodity_id);
        if(score < 1 || score > 10)
            throw new RatingOutOfRangeException();

        if(oldRating == null) {
            Rating newRating = new Rating(getUserByUsername(username), getCommodityById(commodity_id), score);
            ratingRepository.save(newRating);
            commodity.addNewRating(newRating.getScore());
        }
        else {
            commodity.updateUserRating(oldRating.getScore(), score);
            oldRating.setScore(score);
            ratingRepository.save(oldRating);
        }
        commodityRepository.save(commodity);
    }

    public void addComment(String username, int commodityId, String date, String text) throws Exception {
        Commodity commodity = commodityRepository.getCommodityById(commodityId);
        User user = userRepository.getUserByUsername(username);
        if(text==null || text.equals(""))
            throw new ForbiddenValueException();
        if(user==null)
            throw new UserNotExistsException();
        if(commodity==null)
            throw new CommodityNotExistsException();
        Comment comment = new Comment(username, commodityId, text, date);
        comment.setCommodityId(commodityId);
        comment.setUsername(username);
        comment.setUser(user);
        comment.setCommodity(commodity);
        commentRepository.save(comment);
    }

    public void voteComment(String username, int commentId, int voteValue) throws Exception {
        Vote oldVote = voteRepository.getVoteByCommentCommentIdAndUserUsername(commentId, username);
        Comment comment = commentRepository.getCommentByCommentId(commentId);
        User user = userRepository.getUserByUsername(username);
        if(voteValue != -1 && voteValue != 1)
            throw new WrongVoteValueException();
        if(oldVote == null) {
            Vote vote = new Vote(comment, user, voteValue);
            voteRepository.save(vote);
        }
        else {
            oldVote.setVote(voteValue);
            voteRepository.save(oldVote);
        }
    }

    public void addItemToBuyList(User user, Commodity commodity, int quantity) throws Exception {
        List<BuyListItem> userBuyList = buyListItemRepository.findByUser_Username(user.getUsername());;
        if(userBuyList.size()==0) {
            if(quantity > commodity.getInStock())
                throw new ItemNotAvailableInStockException();
            BuyList buyList = new BuyList();
            buyListRepository.save(buyList);
            BuyListItem buyListItem = new BuyListItem(user, commodity, buyList, quantity);
            buyListItemRepository.save(buyListItem);
        }
        else {
            BuyList userBL = userBuyList.get(0).getBuyList();
            BuyListItem item = buyListItemRepository.getBuyListItemByUserAndCommodityAndBuyList(user, commodity, userBL);
            if(item==null) {
                if(quantity > commodity.getInStock())
                    throw new ItemNotAvailableInStockException();
                BuyListItem buyListItem = new BuyListItem(user, commodity, userBL, quantity);
                buyListItemRepository.save(buyListItem);
            }
            else {
                int totalQuantity = item.getQuantity() + quantity;
                if(totalQuantity > commodity.getInStock())
                    throw new ItemNotAvailableInStockException();
                item.setQuantity(totalQuantity);
                buyListItemRepository.save(item);
            }
        }

    }

}

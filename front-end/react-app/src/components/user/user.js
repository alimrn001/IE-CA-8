import React, { Component } from "react";
import "../../assets/styles/user-styles.css";
import "bootstrap/dist/css/bootstrap.min.css";

import HistoryTable from "./historyTable";
import Nav from "../product/nav";
import UserDetails from "./userDetails";
import phoneImg from "../../assets/img/phone.png";
import spaghettiImg from "../../assets/img/spaghetti.png";
import microphoneImg from "../../assets/img/microphone.png";
import cartImg from "../../assets/img/Vector (2).png";
import historyImg from "../../assets/img/Vector (3).png";
import Footer from "./footer";
import CartTable from "./cartTable";
import Popup from "reactjs-popup";
import "reactjs-popup/dist/index.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
// import { useModal } from 'react-hooks-use-modal';

import PopUp1 from "./popUp";
import axios from "../../api/axios";

class User extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "username",
      userInfo: {
        username: "",
        password: "",
        email: "",
        birthDate: 0,
        address: "",
        credit: 0,
        latestDiscountCode: "",
        buylist: [],
        comments: [],
        purchased: [],
        liked: [],
        disliked: [],
        userCoupons: [],
      },

      AccountDetailsEX: {
        userName: " Marshal",
        EMail: " Marshal.Mathers@gmail.com",
        userBirthday: " 1972/10/17",
        userAddress: " 20785 Schultes Avenue, Warren, MI 48091",
        budget: 10000000,
      },

      cartCommodities2: [],

      historyTableData2: [],

      cartCommoditiesCount2: [],
    };
    this.updateCartCommoditiesCount =
      this.updateCartCommoditiesCount.bind(this);
  }

  getUserData() {
    axios
      .get("/user", {
        headers: { Authorization: localStorage.getItem("userJWT") },
      })
      .then((resp) => {
        console.log(resp.status);
        if (resp.status === 200) {
          let buylistCommoditiesId = [];
          let commentsId = [];
          let purchasedlistCommoditiesId = [];
          let likedCommentsId = [];
          let dislikedCommentsId = [];
          let usedCouponsId = [];
          let userBuyList = [];
          let commodityCntData = [];
          let userHistoryList = [];

          Object.keys(resp.data.cartCommodities).forEach((item) => {
            userBuyList.push(resp.data.cartCommodities[item]);
          });

          Object.keys(resp.data.cartCommoditiesCount).forEach((item) => {
            commodityCntData.push(resp.data.cartCommoditiesCount[item]);
          });

          Object.keys(resp.data.history).forEach((item) => {
            userHistoryList.push(resp.data.history[item]);
          });

          Object.keys(resp.data.userInfo.buyList).forEach((item) => {
            buylistCommoditiesId.push(resp.data.userInfo.buyList[item]);
          });

          Object.keys(resp.data.userInfo.commentsList).forEach((item) => {
            commentsId.push(resp.data.userInfo.commentsList[item]);
          });

          Object.keys(resp.data.userInfo.purchasedList).forEach((item) => {
            purchasedlistCommoditiesId.push(
              resp.data.userInfo.purchasedList[item]
            );
          });

          Object.keys(resp.data.userInfo.likedComments).forEach((item) => {
            likedCommentsId.push(resp.data.userInfo.likedComments[item]);
          });

          Object.keys(resp.data.userInfo.dislikedComments).forEach((item) => {
            dislikedCommentsId.push(resp.data.userInfo.dislikedComments[item]);
          });

          Object.keys(resp.data.userInfo.usedDiscountCoupons).forEach(
            (item) => {
              usedCouponsId.push(resp.data.userInfo.usedDiscountCoupons[item]);
            }
          );

          this.setState(
            {
              userInfo: {
                username: resp.data.userInfo.username,
                password: resp.data.userInfo.password,
                email: resp.data.userInfo.email,
                birthDate: resp.data.userInfo.birthDate,
                address: resp.data.userInfo.address,
                credit: resp.data.userInfo.credit,
                latestDiscountCode:
                  resp.data.userInfo.latestSelectedDiscountCode,
                buylist: buylistCommoditiesId,
                comments: commentsId,
                purchased: purchasedlistCommoditiesId,
                liked: likedCommentsId,
                disliked: dislikedCommentsId,
                userCoupons: usedCouponsId,
              },
              cartCommodities2: userBuyList,
              cartCommoditiesCount2: commodityCntData,
            },
            () => {
              // console.log(this.state.cartCommodities2);
              // console.log(this.state.cartCommoditiesCount2);
            }
          );
        }
      })
      .catch((error) => {
        if (error.response.status === 401) {
          window.location.href = "http://localhost:3000/login";
        }
      });
  }

  componentDidMount() {
    if (localStorage.getItem("userJWT") == null) {
      window.location.reload(false);
      window.location.replace("/login");
    } else {
      this.getUserData();
      toast.configure({
        rtl: true,
        className: "text-center",
        position: toast.POSITION.TOP_CENTER,
        autoClose: 5000,
        closeOnClick: true,
      });
    }
  }

  updateCartCommoditiesCount(commodityID, count) {
    const updatedCartItems = this.state.cartCommoditiesCount2.map((item) => {
      if (item.commodityID === commodityID) {
        return { ...item, cartItemsCount: count };
      }
      return item;
    });

    console.log(commodityID);
    this.setState({ cartCommoditiesCount2: updatedCartItems });
    console.log(this.state.cartCommoditiesCount2);
  }

  addCreditToUser = (event, value) => {
    event.preventDefault();
    axios
      .post(
        "/user/addCredit",
        {
          credit: value,
        },
        { headers: { Authorization: localStorage.getItem("userJWT") } }
      )
      .then((resp) => {
        if (resp.status === 200) {
          this.setState((prevState) => {
            let userInfo = { ...prevState.userInfo };
            userInfo.credit = resp.data;
            return { userInfo };
          });
        }
      })
      .catch((error) => {
        if (error.response.status === 401) {
          window.location.href = "http://localhost:3000/login";
        } else if (error.response.status === 403) {
          toast.error("Please enter a valid value");
        } else if (error.response.status === 400) {
          window.location.href = "http://localhost:3000/badrequest";
        }
      });
  };

  render() {
    return (
      <div>
        <Nav
          username={this.state.userInfo.username}
          cartItemsCount={this.state.cartCommodities2.length}
        />
        <div class="container body-container">
          <UserDetails
            onAddCredit={this.addCreditToUser}
            AccountDetails={this.state.userInfo}
          />
          <h3 class="text-brown">
            <img src={cartImg} class="cart-img" alt="" /> Cart
          </h3>
          <CartTable
            cartCommodities={this.state.cartCommodities2}
            updateItemCount={this.updateCartCommoditiesCount}
          />
          <div class="row">
            <PopUp1
              cartCommodities={this.state.cartCommodities2}
              cartCommoditiesCount={this.state.cartCommoditiesCount2}
            />
          </div>

          <h3 class="text-brown mt-20">
            <img src={historyImg} class="cart-img" alt="" /> History
          </h3>
          <HistoryTable historyTableData={this.state.historyTableData2} />
        </div>

        <Footer />
      </div>
    );
  }
}

export default User;

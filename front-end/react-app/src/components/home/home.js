import React, { Component } from "react";
import Nav from "./nav";
import Footer from "./footer";
import "../../assets/styles/home-styles.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "react-toastify/dist/ReactToastify.css";
import Item from "./item";
import imgURL from "../../assets/img/phone.png";
import Filter from "./filter";
import axios from "../../api/axios";
import "bootstrap/dist/js/bootstrap.bundle.min";
import ReactPaginate from "react-paginate";

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      availabaleFlag: false,
      loggedInUser: "",
      itemsEx: [],
      currentPage: 1,
      commoditiesPerPage: 12,
    };
    this.setAvailabaleFlag = this.setAvailabaleFlag.bind(this);
    this.handlePageChange = this.handlePageChange.bind(this);
  }

  handlePageChange(event) {
    this.setState({
      currentPage: Number(event.target.id),
    });
    // window.location.reload();
  }

  setAvailabaleFlag() {
    this.state.availabaleFlag = !this.state.availabaleFlag;
    this.forceUpdate();
  }

  getBalootCommodities() {
    axios
      .get("/", {
        headers: { Authorization: localStorage.getItem("userJWT") },
      })
      .then((resp) => {
        if (resp.status === 200) {
          console.log("logged in user : ", localStorage.getItem("userEmail"));
          let tmp = [];
          Object.keys(resp.data.commodities).forEach((property) => {
            // console.log(resp.data[property]);
            tmp.push(resp.data.commodities[property]);
          });
          this.setState(
            {
              itemsEx: tmp,
              loggedInUser: resp.data.loggedInUsername,
            },
            () => {
              console.log(this.state);
            }
          );
        }
      })
      .catch((error) => {
        console.log(error.response.data);
        if (error.response.status === 401) {
          window.location.href = "http://localhost:3000/login";
        }
      });
  }

  componentDidMount() {
    console.log(localStorage.getItem("userJWT"));
    if (localStorage.getItem("userJWT") == null) {
      window.location.reload(false);
      window.location.replace("/login");
    } else {
      document.title = "Baloot";
      this.getBalootCommodities();
    }
  }

  handleSort = (task) => {
    console.log("sorting by name");
    axios
      .post(
        "/",
        {
          task: task,
          value: "",
        },
        {
          headers: { Authorization: localStorage.getItem("userJWT") },
        }
      )
      .then((resp) => {
        if (resp.status === 200) {
          let tmp = [];
          resp.data.map((item) => {
            tmp.push(item);
          });
          this.setState({
            itemsEx: tmp,
          });
          console.log(resp.data);
        }
      })
      .catch((error) => {
        console.log(error.response.data);
        if (error.response.status === 400) {
          window.location.href = "http://localhost:3000/badrequest";
        }
      });
  };

  handleSearch = (event, field, value) => {
    event.preventDefault();
    console.log("field : ", field, " - value : ", value);
    let searchField = "";
    if (field === "name") {
      searchField = "searchByName";
    } else if (field === "category") {
      searchField = "searchByCategory";
    }
    axios
      .post(
        "/",
        {
          task: searchField,
          value: value,
        },
        {
          headers: { Authorization: localStorage.getItem("userJWT") },
        }
      )
      .then((resp) => {
        // window.location.href = "http://localhost:3000/badrequest";
        console.log(resp.status);
        if (resp.status === 200) {
          let tmp = [];
          resp.data.map((item) => {
            tmp.push(item);
          });
          this.setState({
            // itemsEx: [],
            itemsEx: tmp,
          });
          console.log(resp.data);
        }
      })
      .catch((error) => {
        // window.location.href = "http://localhost:3000/badrequest";
        console.log("error : " + error.status);
      });
  };

  render() {
    return (
      <div>
        <Nav onSearch={this.handleSearch} />

        <Filter
          setAvailabaleFlag={this.setAvailabaleFlag}
          onSort={this.handleSort}
        />

        <div className="container">
          <section className="container-fluid p-4">
            <div className="row mt-4 gy-4 product-container">
              {this.state.itemsEx.map((item) => (
                <Item item={item} availabaleFlag={this.state.availabaleFlag} />
              ))}
            </div>
            {/* <ul id="page-numbers">{renderPageNumbers}</ul> */}
          </section>
        </div>
        {/* <Footer /> */}
      </div>
    );
  }
}

export default Home;

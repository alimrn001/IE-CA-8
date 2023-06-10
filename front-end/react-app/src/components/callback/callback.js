import React, { useState, useEffect } from "react";
import axios from "../../api/axios";
import { useParams } from "react-router-dom";

class Callback extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loadingState: true,
    };
  }

  componentDidMount() {
    let search = window.location.search;
    let params = new URLSearchParams(search.split("?")[1]);

    if (localStorage.getItem("userJWT") != null) {
      window.location.replace("/");
    }

    const req = { code: params.get("code") };
    console.log(req);

    axios
      .get("/callback", { params: req })
      .then((response) => {
        let userJWT = response.headers.authorization.split(" ")[1];
        let userEmail = response.headers.useremail;
        localStorage.setItem("userJWT", userJWT);
        localStorage.setItem("userEmail", userEmail);
        window.location.replace("/");
      })
      .catch(console.error)
      .finally(() => {
        this.setState({ loadingState: false });
      });
  }

  render() {
    return (
      <div>
        <h1>callback</h1>
        {/* {this.state.loadingState ? (
          <div className="spinner">
            <Default color="#b22024" />
          </div>
        ) : (
          <div></div>
        )} */}
      </div>
    );
  }
}

export default Callback;

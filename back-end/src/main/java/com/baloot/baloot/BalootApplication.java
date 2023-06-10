package com.baloot.baloot;

import com.baloot.baloot.DTO.CommodityDTO;
import com.baloot.baloot.Exceptions.ForbiddenValueException;
import com.baloot.baloot.Exceptions.NoLoggedInUserException;
import com.baloot.baloot.Utils.HashString;
import com.baloot.baloot.domain.Baloot.BalootDataService;
import com.baloot.baloot.services.BalootService;
import com.baloot.baloot.services.buylists.BuyListService;
import com.baloot.baloot.services.commodities.CommodityService;
import com.baloot.baloot.services.commodities.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
//@RestController
@ServletComponentScan
@ComponentScan(basePackages = {"com.baloot.baloot", "com.baloot.baloot.filters"})
public class BalootApplication {

    public static void main(String[] args) {
        try {
            BalootDataService.getInstance().importBalootDataFromAPI();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        SpringApplication.run(BalootApplication.class, args);
    }

}
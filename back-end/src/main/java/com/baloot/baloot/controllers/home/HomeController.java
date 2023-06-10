package com.baloot.baloot.controllers.home;

import com.baloot.baloot.DTO.CommodityDTO;
import com.baloot.baloot.Exceptions.ForbiddenValueException;
import com.baloot.baloot.Exceptions.NoLoggedInUserException;
import com.baloot.baloot.services.BalootService;
import com.baloot.baloot.services.commodities.CommodityService;
import com.baloot.baloot.services.commodities.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {
    @Autowired
    private BalootService balootService;

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private FilterService filterService;

    @GetMapping("/")
    public ResponseEntity getBalootCommoditiesList() throws IOException {
        if(!balootService.userIsLoggedIn())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new NoLoggedInUserException().getMessage());
        Map<Integer, CommodityDTO> allCommodities = commodityService.getAllCommodities();
        Map<String, Object> map = new HashMap<>();
        map.put("loggedInUsername", balootService.getLoggedInUser());
        map.put("commodities", allCommodities);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @PostMapping("/")
    public ResponseEntity filterBalootCommodities(@RequestBody Map<String, Object> payLoad) throws IOException {
        if(!balootService.userIsLoggedIn()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new NoLoggedInUserException().getMessage());
        }
        try {
//            System.out.println(payLoad.get("task").toString() + "," + payLoad.get("value").toString() + " to be searched!");
            return ResponseEntity.status(HttpStatus.OK).body(
                    filterService.filterBalootCommodities(payLoad.get("task").toString(), payLoad.get("value").toString()));
        }
        catch (ForbiddenValueException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

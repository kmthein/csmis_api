
package com.team2.csmis_api.controller;

import com.team2.csmis_api.service.DoorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/api/cost")
public class OperatorCostController {

    @Autowired
    private DoorLogService doorLogService;

    @GetMapping("/weekly/{userId}")
    public ResponseEntity<BigDecimal> getWeeklyTotalCostByUserId(@PathVariable int userId) {
        BigDecimal totalCost = doorLogService.getUserWeeklyTotalCost(userId);

        if (totalCost != null) {
            return ResponseEntity.ok(totalCost);
        } else {
            return ResponseEntity.notFound().build(); // If no data found
        }
    }

    @GetMapping("/monthly/{userId}")
    public ResponseEntity<BigDecimal> getMonthlyTotalCostByUserId(@PathVariable int userId) {
        BigDecimal totalCost = doorLogService.getUserMonthlyTotalCost(userId);

        if (totalCost != null) {
            return ResponseEntity.ok(totalCost);
        } else {
            return ResponseEntity.notFound().build(); // If no data found
        }
    }

    @GetMapping("/yearly/{userId}")
    public ResponseEntity<BigDecimal> getYearlyTotalCostByUserId(@PathVariable int userId) {
        BigDecimal totalCost = doorLogService.getUserYearlyTotalCost(userId);

        if (totalCost != null) {
            return ResponseEntity.ok(totalCost);
        } else {
            return ResponseEntity.notFound().build(); // If no data found
        }
    }


}

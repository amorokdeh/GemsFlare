package com.gemsflare.gemsflare.DbTest;

import com.gemsflare.gemsflare.permission.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("/testDb")
    public String testDbConnection(HttpServletRequest request) {
        if (!permissionService.hasPermission(request, "/testDb")) {
            return "Access denied: Permission required";
        }
        try {
            String sql = "SELECT 1";
            jdbcTemplate.queryForObject(sql, Integer.class);
            return "Database connection successful";
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}

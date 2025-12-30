package com.gemsflare.gemsflare.item.controller;

import com.gemsflare.gemsflare.item.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/getAllItems")
    public ResponseEntity<?> getAllItems(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return itemService.getAllItems(page, size);
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getAllCategories() {
        return itemService.getAllCategories();
    }

    @GetMapping("/getAllUserItems")
    public ResponseEntity<?> getAllUserItems(HttpServletRequest request,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return itemService.getAllUserItems(request,page, size);
    }

    @GetMapping("/getItem/{itemNumber}")
    public ResponseEntity<?> getItemByItemNumber(@PathVariable String itemNumber) {
        return itemService.getItemByItemNumber(itemNumber);
    }

    @GetMapping("/getItemsByName")
    public ResponseEntity<?> getItemsByName(@RequestParam String name,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return itemService.getItemsByName(name, page, size);
    }

    @GetMapping("/getItemsByCategory")
    public ResponseEntity<?> getItemsByCategory(@RequestParam String category,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return itemService.getItemsByCategory(category, page, size);
    }

    @PostMapping(value = "/addItem", consumes = "multipart/form-data")
    public ResponseEntity<?> addItem(HttpServletRequest request,
                                     @RequestParam String name,
                                     @RequestParam (required = false) String description,
                                     @RequestParam String category,
                                     @RequestParam List<String> colorGroups,
                                     @RequestParam BigDecimal price,
                                     @RequestParam Integer amount,
                                     @RequestParam (required = false) MultipartFile image,
                                     @RequestParam (required = false) MultipartFile object) {
        return itemService.addItem(request, name, description, category, colorGroups, price, amount, image, object);
    }

    @PostMapping("/addCategory")
    public ResponseEntity<?> addCategory(HttpServletRequest request,
                                         @RequestParam String name) {
        return itemService.addCategory(request, name);
    }

    @DeleteMapping("/deleteItem")
    public ResponseEntity<?> deleteItem(HttpServletRequest request,
                                     @RequestParam String itemNumber,
                                     @RequestParam String password) {
        return itemService.deleteItem(request, itemNumber, password);
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<?> deleteCategory(HttpServletRequest request,
                                            @RequestParam String name) {
        return itemService.deleteCategory(request, name);
    }

    @PutMapping(value = "/editItem", consumes = "multipart/form-data")
    public ResponseEntity<?> editItem(HttpServletRequest request,
                                      @RequestParam String itemNumber,
                                      @RequestParam (required = false) String name,
                                      @RequestParam (required = false) String description,
                                      @RequestParam (required = false) String category,
                                      @RequestParam (required = false) List<String> colorGroups,
                                      @RequestParam (required = false) BigDecimal price,
                                      @RequestParam (required = false) Integer amount,
                                      @RequestParam (required = false) MultipartFile image,
                                      @RequestParam (required = false) MultipartFile object) {
        return itemService.editItem(request, itemNumber, name, description, category, colorGroups, price, amount, image, object);
    }

    @DeleteMapping("/deleteImageFromItem")
    public ResponseEntity<?> deleteImageFromItem(HttpServletRequest request,
                                        @RequestParam String itemNumber) {
        return itemService.deleteImageFromItem(request, itemNumber);
    }

    @DeleteMapping("/deleteObjectFromItem")
    public ResponseEntity<?> deleteObjectFromItem(HttpServletRequest request,
                                                 @RequestParam String itemNumber) {
        return itemService.deleteObjectFromItem(request, itemNumber);
    }

    @PutMapping("/changeItemAmount")
    public ResponseEntity<?> changeItemAmount(HttpServletRequest request,
                                              @RequestParam String itemNumber,
                                              @RequestParam Integer amount) {
        return itemService.changeItemAmount(request, itemNumber, amount);
    }

}
